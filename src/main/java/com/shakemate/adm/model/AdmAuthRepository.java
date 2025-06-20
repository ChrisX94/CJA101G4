package com.shakemate.adm.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmAuthRepository extends JpaRepository<AdmAuthVO, Integer> {
	List<AdmAuthVO> findByAdmVO_AdmId(Integer admId);

	  List<AdmAuthVO> findByAuthFuncVO_AuthId(Integer authId);

}
