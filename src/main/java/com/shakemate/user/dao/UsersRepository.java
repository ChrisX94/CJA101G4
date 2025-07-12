package com.shakemate.user.dao;

import com.shakemate.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer>, JpaSpecificationExecutor<Users> {
    @Transactional
    Users findByEmail(String email);

    /**
     * 獲取所有用戶ID，用於廣播通知
     */
    @Query("SELECT u.userId FROM Users u")
    List<Integer> findAllUserIds();

}