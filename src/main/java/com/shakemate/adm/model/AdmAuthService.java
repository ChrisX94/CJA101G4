package com.shakemate.adm.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("admAuthService")
public class AdmAuthService {

	@Autowired
	AdmAuthRepository repository;

	@Autowired
	private SessionFactory sessionFactory;

	public List<AdmAuthVO> getAll() {
		return repository.findAll();
	}

	public void addAdmAuth(AdmAuthVO admAuthVO) {
		repository.save(admAuthVO);
	}

	public void updateAdmAuth(AdmAuthVO admAuthVO) {
		repository.save(admAuthVO);
	}

	public void deleteAdmAuth(Integer admAuthId) {
		if (repository.existsById(admAuthId))
			repository.deleteById(admAuthId);
		// repository.deleteById(admId);
	}

	public AdmAuthVO getOneAdmAuth(Integer admAuthId) {
		Optional<AdmAuthVO> optional = repository.findById(admAuthId);
		// return optional.get();
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<AdmAuthVO> getAuthsByAdmId(Integer admId) {
		return repository.findByAdmVO_AdmId(admId);
	}

	public List<AdmAuthVO> getAuthsByAuthId(Integer authId) {
		return repository.findByAuthFuncVO_AuthId(authId);
	}
}
