package com.shakemate.adm.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AdmRepository extends JpaRepository<AdmVO, Integer> {
	List<AdmVO> findByAdmNameContaining(String admName);

	// 根據帳號查詢
	AdmVO findByAdmAcc(String admAcc);

	@Query("SELECT DISTINCT a FROM AdmVO a LEFT JOIN a.authFuncs af " +
			"WHERE (:admName IS NULL OR a.admName LIKE CONCAT('%', :admName, '%')) " +
			"AND (:admAcc IS NULL OR a.admAcc LIKE CONCAT('%', :admAcc, '%')) " +
			"AND (:authId IS NULL OR af.authId = :authId)")
	List<AdmVO> findByConditions(@Param("admName") String admName,
			@Param("admAcc") String admAcc,
			@Param("authId") Integer authId);

	boolean existsByAdmAcc(String admAcc);

	// 自定義更新方法
	@Modifying
	@Transactional
	@Query("UPDATE AdmVO a SET a.admName = :name, a.admAcc = :acc, a.admPwd = :pwd WHERE a.admId = :id")
	int updateAdmBasic(@Param("id") Integer id,
			@Param("name") String name,
			@Param("acc") String acc,
			@Param("pwd") String pwd);

	// 使用原生 SQL 的更新方法
	@Modifying
	@Transactional
	@Query(value = "UPDATE ADM SET ADM_NAME = :name, ADM_ACC = :acc, ADM_PWD = :pwd WHERE ADM_ID = :id", nativeQuery = true)
	int updateAdmNative(@Param("id") Integer id,
			@Param("name") String name,
			@Param("acc") String acc,
			@Param("pwd") String pwd);
}
