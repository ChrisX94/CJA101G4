package com.shakemate.adm.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("admFuncService")
public class AuthFuncService {

    @Autowired
    AuthFuncRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

    public List<AuthFuncVO> getAll() {
        return repository.findAll();
    }

    public void addAuthFunc(AuthFuncVO authFuncVO) {
        repository.save(authFuncVO);
    }

    // 查單筆
    public AuthFuncVO getOneAdmFunc(Integer authId) {
        Optional<AuthFuncVO> optional = repository.findById(authId);
        return optional.orElse(null);
    }

    // 模糊查詢 name
    public List<AuthFuncVO> searchByAuthName(String authName) {
        return repository.findByAuthNameContaining(authName);
    }

    // 模糊查詢 des
    public List<AuthFuncVO> searchByAuthDes(String authDes) {
        return repository.findByAuthDesContaining(authDes);
    }

}
