package com.shakemate.servicecase.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceCaseService {

	@Autowired
	private ServiceCaseRepository repository;

	public ServiceCase create(ServiceCase sc) {
		return repository.save(sc);
	}
	// ... 其他呼叫 repo.findAll(), repo.findByUserId(...) 等方法

// ========================================

	public ServiceCase update(ServiceCase serviceCase) {
		return repository.save(serviceCase);
	}

	public void delete(Integer caseId) {
		if (repository.existsById(caseId)) {
			repository.deleteById(caseId);
		}
	}

	public ServiceCase findById(Integer caseId) {
		Optional<ServiceCase> optional = repository.findById(caseId);
		return optional.orElse(null);
	}

	public List<ServiceCase> getAll() {
		return repository.findAll();
	}

	// 可加入更多查詢條件
	public List<ServiceCase> findByUserId(Integer userId) {
		return repository.findByUserId(userId);
	}

	public List<ServiceCase> findByCaseStatus(Integer status) {
		return repository.findByCaseStatus(status);
	}

	/**
	 * 動態查詢：只有非 null 的參數才會被當成條件
	 */
	public List<ServiceCase> search(Integer userId, Integer caseTypeId, Integer caseStatus, LocalDate fromDate,
			LocalDate toDate) {
// 建立一個永遠為 true 的初始 Specification
		Specification<ServiceCase> spec = (root, query, cb) -> cb.conjunction();

		if (userId != null) {
			spec = spec.and((root, cq, cb) -> cb.equal(root.get("userId"), userId));
		}
		if (caseTypeId != null) {
			spec = spec.and((root, cq, cb) -> cb.equal(root.get("caseType").get("caseTypeId"), caseTypeId));
		}
		if (caseStatus != null) {
			spec = spec.and((root, cq, cb) -> cb.equal(root.get("caseStatus"), caseStatus));
		}
		if (fromDate != null) {
			spec = spec.and((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("createTime"), fromDate.atStartOfDay()));
		}
		if (toDate != null) {
			spec = spec.and((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("createTime"), toDate.atTime(23, 59, 59)));
		}

		return repository.findAll(spec);
	}

}
