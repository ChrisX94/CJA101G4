package com.shakemate.notification.entity;

import com.shakemate.adm.model.AdmVO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEMPLATE_ID")
    private Integer templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_ID", referencedColumnName = "ADM_ID", insertable = false, updatable = false)
    private AdmVO createdBy;
    
    @Column(name = "CREATED_BY_ID")
    private Integer createdById;

    @Column(name = "TEMPLATE_CODE", unique = true, nullable = false, length = 50)
    private String templateCode;

    @Column(name = "TEMPLATE_NAME", nullable = false, length = 100)
    private String templateName;

    @Column(name = "TEMPLATE_TYPE", nullable = false, length = 50)
    private String templateType;

    @Column(name = "TEMPLATE_CATEGORY", nullable = false, length = 50)
    private String templateCategory;

    @Column(name = "SUBJECT", nullable = false, length = 200)
    private String titleTemplate;

    @Column(name = "CONTENT")
    private Boolean hasContent;

    @Column(name = "HTML_CONTENT", columnDefinition = "VARCHAR(255)")
    private String messageTemplate;

    @Column(name = "VARIABLES", columnDefinition = "json", nullable = true)
    private String variables;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

    @Column(name = "IS_SYSTEM")
    private Boolean isSystem;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "PREVIEW_IMAGE")
    private String previewImage;

    // 注意：資料庫表中沒有 link 欄位，如果需要可以添加
    // @Column(name = "link")
    // private String link;
} 