package com.shakemate.adm.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shakemate.adm.util.PasswordUtil;

//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_Emp3;

@Service("admService")
@Transactional
public class AdmService {

	@Autowired
	AdmRepository repository;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	AuthFuncRepository authFuncRepository;

	@Autowired
	AdmAuthRepository admAuthRepository;

	public List<AdmVO> getAll() {
		return repository.findAll();
	}

	public void addAdm(AdmVO admVO) {
		// 取出使用者輸入的明文密碼
		String plainPassword = admVO.getAdmPwd();
		// 呼叫工具類加密（hash）
		String hashedPassword = PasswordUtil.hashPassword(plainPassword);
		// 把加密後的密碼塞回 admVO
		admVO.setAdmPwd(hashedPassword);
		repository.save(admVO);
	}

	public void updateAdm(AdmVO admVO) {
		repository.save(admVO);
	}

	public void deleteAdm(Integer admId) {
		if (repository.existsById(admId))
			repository.deleteById(admId);
	}

	public AdmVO getOneAdm(Integer admId) {
		Optional<AdmVO> optional = repository.findById(admId);
		return optional.orElse(null);
	}

	// 新增管理員（含權限）時根據權限決定 status
	@Transactional
	public void addAdmWithAuth(AdmVO admVO, List<Integer> authFuncIds) {
		// 密碼加密（如有需要）

		if (authFuncIds != null && !authFuncIds.isEmpty()) {
			admVO.setStatus(true); // 有權限才啟用
		} else {
			admVO.setStatus(false); // 沒權限預設停用
		}
		repository.save(admVO);
		if (authFuncIds != null && !authFuncIds.isEmpty()) {
			Set<AuthFuncVO> authFuncs = authFuncRepository.findAllById(authFuncIds).stream()
					.collect(Collectors.toSet());
			admVO.setAuthFuncs(authFuncs);
			repository.save(admVO);
		}
	}

	public void updateAdmWithAuth(AdmVO admVO, List<Integer> authFuncIds) {
		try {
			Optional<AdmVO> optional = repository.findById(admVO.getAdmId());
			if (optional.isPresent()) {
				AdmVO existing = optional.get();

				// 只更新基本資料，不處理權限關係
				existing.setAdmName(admVO.getAdmName());
				existing.setAdmAcc(admVO.getAdmAcc());
				existing.setAdmPwd(admVO.getAdmPwd());

				// 保存基本資料更新
				repository.save(existing);

				// 如果權限有變化，則單獨處理權限更新
				if (authFuncIds != null && !authFuncIds.isEmpty()) {
					updateAdmAuths(existing.getAdmId(), authFuncIds);
				}
			} else {
				throw new RuntimeException("找不到要修改的管理員，ID: " + admVO.getAdmId());
			}
		} catch (Exception e) {
			System.err.println("更新管理員資料時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("更新管理員資料失敗: " + e.getMessage(), e);
		}
	}

	// 權限更新時自動啟用
	@Transactional
	public void updateAdmAuths(Integer admId, List<Integer> authFuncIds) {
		System.out.println("=== 權限更新開始 ===");
		admAuthRepository.deleteByAdmId(admId);
		System.out.println("已刪除舊權限");
		if (authFuncIds != null && !authFuncIds.isEmpty()) {
			for (Integer authId : authFuncIds) {
				AdmAuthVO admAuth = new AdmAuthVO();
				AdmAuthId id = new AdmAuthId();
				id.setAdmId(admId);
				id.setAuthId(authId);
				admAuth.setId(id);
				admAuth.setAdmVO(repository.findById(admId).get());
				admAuth.setAuthFuncVO(authFuncRepository.findById(authId).get());
				admAuthRepository.save(admAuth);
				System.out.println("新增權限: " + authId);
			}
			// 權限有被分配，若 status 為 false 則自動啟用
			AdmVO adm = repository.findById(admId).get();
			if (adm.getStatus() == null || !adm.getStatus()) {
				adm.setStatus(true);
				repository.save(adm);
			}
		}
		System.out.println("=== 權限更新結束 ===");
	}

	// 修改 updateAdmBasic，結尾加上權限更新
	@Transactional
	public void updateAdmBasicWithAuth(AdmVO admVO, List<Integer> authFuncIds) {
		updateAdmBasic(admVO); // 先更新基本資料
		updateAdmAuths(admVO.getAdmId(), authFuncIds); // 再更新權限
	}

	public List<AdmVO> findByName(String name) {
		return repository.findByAdmNameContaining(name);
	}

	public AdmVO findByAcc(String admAcc) {
		return repository.findByAdmAcc(admAcc); // 在repository 寫這個方法
	}

	public List<AdmVO> findByConditions(String admName, String admAcc, Integer authId) {
		return repository.findByConditions(admName, admAcc, authId);
	}

	public AdmVO validateLogin(String admAcc, String admPwd) {
		AdmVO adm = repository.findByAdmAcc(admAcc); // only search for account
		if (adm != null && adm.getAdmPwd().equals(admPwd)) {
			return adm;
		}
		return null;

	}

	// 完全隔離的更新方法，避免關係問題
	@Transactional
	public void updateAdmIsolated(AdmVO admVO) {
		try {
			System.out.println("=== 使用隔離更新方法 ===");
			System.out.println("更新管理員ID: " + admVO.getAdmId());
			System.out.println("更新姓名: " + admVO.getAdmName());
			System.out.println("更新帳號: " + admVO.getAdmAcc());

			// 使用 JPA 查詢直接更新，避免載入關係
			String jpql = "UPDATE AdmVO a SET a.admName = :name, a.admAcc = :acc, a.admPwd = :pwd WHERE a.admId = :id";
			int updatedRows = sessionFactory.getCurrentSession()
					.createQuery(jpql)
					.setParameter("name", admVO.getAdmName())
					.setParameter("acc", admVO.getAdmAcc())
					.setParameter("pwd", admVO.getAdmPwd())
					.setParameter("id", admVO.getAdmId())
					.executeUpdate();

			System.out.println("隔離更新完成，影響行數: " + updatedRows);
		} catch (Exception e) {
			System.err.println("隔離更新失敗: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	// 更簡單的更新方法
	@Transactional
	public void updateAdmSimple(AdmVO admVO) {
		try {
			System.out.println("=== 使用簡單更新方法 ===");
			System.out.println("更新管理員ID: " + admVO.getAdmId());
			System.out.println("更新姓名: " + admVO.getAdmName());
			System.out.println("更新帳號: " + admVO.getAdmAcc());

			// 直接保存，讓 JPA 處理更新
			repository.saveAndFlush(admVO);
			System.out.println("簡單更新完成");
		} catch (Exception e) {
			System.err.println("簡單更新失敗: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	// 使用 Repository 的更新方法
	@Transactional
	public void updateAdmBasic(AdmVO admVO) {
		try {
			System.out.println("=== 使用原生 SQL 更新方法 ===");
			System.out.println("更新管理員ID: " + admVO.getAdmId());
			System.out.println("更新姓名: " + admVO.getAdmName());
			System.out.println("更新帳號: " + admVO.getAdmAcc());
			System.out.println("更新密碼長度: " + (admVO.getAdmPwd() != null ? admVO.getAdmPwd().length() : "null"));

			// 先檢查要更新的管理員是否存在
			Optional<AdmVO> existing = repository.findById(admVO.getAdmId());
			if (existing.isPresent()) {
				System.out.println("找到要更新的管理員，當前姓名: " + existing.get().getAdmName());
				System.out.println("當前帳號: " + existing.get().getAdmAcc());
			} else {
				System.out.println("警告：找不到要更新的管理員！");
			}

			int updatedRows = repository.updateAdmNative(
					admVO.getAdmId(),
					admVO.getAdmName(),
					admVO.getAdmAcc(),
					admVO.getAdmPwd());

			System.out.println("原生 SQL 更新完成，影響行數: " + updatedRows);

			if (updatedRows == 0) {
				System.out.println("警告：沒有行被更新！");
				throw new RuntimeException("沒有找到要更新的管理員，ID: " + admVO.getAdmId());
			} else {
				System.out.println("成功更新了 " + updatedRows + " 行資料");
			}

			// 再次檢查更新後的資料
			Optional<AdmVO> afterUpdate = repository.findById(admVO.getAdmId());
			if (afterUpdate.isPresent()) {
				System.out.println("更新後的資料 - 姓名: " + afterUpdate.get().getAdmName());
				System.out.println("更新後的資料 - 帳號: " + afterUpdate.get().getAdmAcc());
			}

		} catch (Exception e) {
			System.err.println("原生 SQL 更新失敗: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	// 強制刷新快取的方法
	@Transactional
	public void clearCache() {
		try {
			System.out.println("=== 清除 JPA 快取 ===");
			// 清除所有實體的快取
			repository.flush();
			System.out.println("快取已清除");
		} catch (Exception e) {
			System.err.println("清除快取失敗: " + e.getMessage());
		}
	}

	// 測試方法：檢查資料庫連接和資料
	public void testDatabaseConnection() {
		try {
			System.out.println("=== 測試資料庫連接 ===");
			List<AdmVO> allAdms = repository.findAll();
			System.out.println("資料庫中共有 " + allAdms.size() + " 個管理員");

			for (AdmVO adm : allAdms) {
				System.out.println("管理員ID: " + adm.getAdmId() +
						", 姓名: " + adm.getAdmName() +
						", 帳號: " + adm.getAdmAcc());
			}
			System.out.println("=== 資料庫連接測試完成 ===");
		} catch (Exception e) {
			System.err.println("資料庫連接測試失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
