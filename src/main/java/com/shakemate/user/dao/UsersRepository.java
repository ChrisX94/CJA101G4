package com.shakemate.user.dao;


import com.shakemate.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    @Transactional
    Users findByEmail(String email);
}