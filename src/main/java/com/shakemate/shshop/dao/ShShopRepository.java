package com.shakemate.shshop.dao;

import com.shakemate.shshop.model.ShProd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShShopRepository extends JpaRepository<ShProd, Integer> {

    @Query("""
            SELECT DISTINCT p FROM ShProd p
             LEFT JOIN FETCH p.prodPics
             LEFT JOIN FETCH p.shProdType
            """)
    List<ShProd> findAllWithPics();

    // 找待審核的商品
    @Query("""
            SELECT DISTINCT p FROM ShProd p
             LEFT JOIN FETCH p.prodPics
             LEFT JOIN FETCH p.shProdType
             WHERE p.prodStatus = 0
            """)
    List<ShProd> findAllWithPendingApproval();

    @Query("""
            SELECT p FROM ShProd p
             LEFT JOIN FETCH p.prodPics
             LEFT JOIN FETCH p.shProdType
             WHERE p.prodId = ?1
            """)
    ShProd getByID(Integer id);

    @Query("""
            SELECT DISTINCT p FROM ShProd p
             LEFT JOIN FETCH p.prodPics
             LEFT JOIN FETCH p.shProdType
             WHERE p.shProdType.prodTypeId = :typeId
            """)
    List<ShProd> getByType(@Param("typeId") Integer typeId);

    @Query("""
            SELECT DISTINCT p FROM ShProd p
             LEFT JOIN FETCH p.prodPics
             LEFT JOIN FETCH p.shProdType
             WHERE p.user.userId = :userId
            """)
    List<ShProd> getByUserId(@Param("userId") Integer userId);

    @Query("""
            SELECT DISTINCT p FROM ShProd p
             LEFT JOIN FETCH p.prodPics
             LEFT JOIN FETCH p.shProdType
             WHERE p.user.userId = :userId
             AND(
                p.prodName LIKE CONCAT('%', :keyStr, '%')
                OR p.prodBrand LIKE CONCAT('%', :keyStr, '%')
                OR p.prodContent LIKE CONCAT('%', :keyStr, '%')
                )
            """)
    List<ShProd> getByUserAndKeyStr(@Param("userId") Integer userId, @Param("keyStr") String keyStr);

    @Query("UPDATE ShProd p SET p.prodViews = p.prodViews + 1 WHERE p.prodId = :id")
    @Modifying
    void incrementViews(@Param("id") Integer id);

    @Query("UPDATE ShProd p SET p.prodStatus = :status WHERE p.prodId = :id")
    @Modifying
    void changeProdStatus(@Param("id") Integer id, @Param("status") Byte status);

    @Query("UPDATE ShProd p SET p.prodCount = (p.prodCount - 1) WHERE p.prodId = :id")
    @Modifying
    void changeDeductProd(@Param("id") Integer id);

}


