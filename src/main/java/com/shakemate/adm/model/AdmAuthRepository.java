package com.shakemate.adm.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AdmAuthRepository extends JpaRepository<AdmAuthVO, Integer> {
	List<AdmAuthVO> findByAdmVO_AdmId(Integer admId);

	List<AdmAuthVO> findByAuthFuncVO_AuthId(Integer authId);

	@Transactional
	@Modifying
	@Query("DELETE FROM AdmAuthVO a WHERE a.admVO.admId = :admId")
	void deleteByAdmId(@Param("admId") Integer admId);
}
