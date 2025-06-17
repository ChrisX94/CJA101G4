package com.shakemate.adm.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthFuncRepository extends JpaRepository<AuthFuncVO, Integer> {
	// 查某個 AUTH_NAME
	List<AuthFuncVO> findByAuthNameContaining(String authName);

	// 查某個 AUTH_DES
	List<AuthFuncVO> findByAuthDesContaining(String authDes);

}
