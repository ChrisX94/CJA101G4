package com.shakemate.servicecase.model;

import com.shakemate.servicecase.model.ServiceCase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceCaseRepository extends JpaRepository<ServiceCase, Integer> {

    /**
     * 原 DAO 裡的 findByPrimaryKey(caseId)
     * --> 可以直接使用 findById(caseId)
     *    (回傳 Optional<ServiceCase>)
     */

    /**
     * 原 DAO 裡的 getAll()
     * --> 可以直接使用 findAll()
     */

    /**
     * 如果你希望保持方法命名跟原本意義最接近，
     * 也可以加個預設方法：
     */
    default ServiceCase findByPrimaryKey(Integer caseId) {
        return findById(caseId).orElse(null);
    }

    /**
     * 自訂查詢：依 userId 篩選
     * 相當於你可以寫 List<ServiceCaseVO> findByUserId(userId)
     */
    List<ServiceCase> findByUserId(Integer userId);

    /**
     * 自訂查詢：依 caseStatus 篩選
     */
    List<ServiceCase> findByCaseStatus(Integer caseStatus);

    // 其他你想要的自訂查詢也都可以直接以方法名稱推導：
    // List<ServiceCase> findByAdmId(Integer admId);
    // List<ServiceCase> findByCaseTypeId(Integer caseTypeId);
}
