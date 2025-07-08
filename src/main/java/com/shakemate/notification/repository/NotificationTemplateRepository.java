package com.shakemate.notification.repository;

import com.shakemate.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Integer>, JpaSpecificationExecutor<NotificationTemplate> {
    
    /**
     * 根據模板代碼查找模板
     * @param templateCode 模板代碼（唯一）
     * @return 模板實體
     */
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);
    
    /**
     * 檢查模板代碼是否存在
     * @param templateCode 模板代碼
     * @return 是否存在
     */
    boolean existsByTemplateCode(String templateCode);
} 