package com.shakemate.notification.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakemate.user.model.Users;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用戶標籤篩選規範構建器
 * 用於根據標籤條件構建JPA Specification查詢用戶
 */
@Component
@Slf4j
public class UserSpecificationBuilder {

    // 欄位白名單，防止惡意查詢
    private static final Set<String> ALLOWED_FIELDS = Set.of("location", "gender", "userStatus", "interests", "personality");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根據標籤條件構建用戶查詢規範
     *
     * @param criteria 標籤條件Map
     * @return 用戶查詢規範
     */
    public Specification<Users> buildSpecification(Map<String, Object> criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (criteria == null || criteria.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            // 處理用戶類型
            if (criteria.containsKey("userTypes")) {
                Object userTypesObj = criteria.get("userTypes");
                if (userTypesObj instanceof List) {
                    List<?> userTypes = (List<?>) userTypesObj;
                    
                    for (Object typeObj : userTypes) {
                        String type = typeObj.toString();
                        
                        switch (type) {
                            case "VIP":
                                // VIP會員條件
                                predicates.add(criteriaBuilder.equal(root.get("isVip"), true));
                                break;
                            case "NEW":
                                // 新用戶條件（30天內註冊）
                                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("regTime"), thirtyDaysAgo));
                                break;
                            case "INACTIVE":
                                // 不活躍用戶條件（30天未登入）
                                LocalDateTime thirtyDaysAgoForInactive = LocalDateTime.now().minusDays(30);
                                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                    root.get("lastLoginTime"), thirtyDaysAgoForInactive));
                                break;
                        }
                    }
                }
            }
            
            // 處理年齡範圍
            if (criteria.containsKey("age")) {
                Object ageObj = criteria.get("age");
                if (ageObj instanceof Map) {
                    Map<?, ?> ageMap = (Map<?, ?>) ageObj;
                    
                    if (ageMap.containsKey("min")) {
                        Integer minAge = parseInteger(ageMap.get("min"));
                        if (minAge != null) {
                            // 計算出生日期上限（年齡下限）
                            LocalDateTime birthDateUpperBound = LocalDateTime.now().minusYears(minAge);
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                root.get("birthDate"), birthDateUpperBound));
                        }
                    }
                    
                    if (ageMap.containsKey("max")) {
                        Integer maxAge = parseInteger(ageMap.get("max"));
                        if (maxAge != null) {
                            // 計算出生日期下限（年齡上限）
                            LocalDateTime birthDateLowerBound = LocalDateTime.now().minusYears(maxAge + 1).plusDays(1);
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                root.get("birthDate"), birthDateLowerBound));
                        }
                    }
                }
            }
            
            // 處理性別
            if (criteria.containsKey("genders")) {
                Object gendersObj = criteria.get("genders");
                if (gendersObj instanceof List && !((List<?>) gendersObj).isEmpty()) {
                    List<?> genders = (List<?>) gendersObj;
                    List<Predicate> genderPredicates = new ArrayList<>();
                    
                    for (Object genderObj : genders) {
                        String gender = genderObj.toString();
                        genderPredicates.add(criteriaBuilder.equal(root.get("gender"), gender));
                    }
                    
                    predicates.add(criteriaBuilder.or(genderPredicates.toArray(new Predicate[0])));
                }
            }
            
            // 處理地區
            if (criteria.containsKey("regions")) {
                Object regionsObj = criteria.get("regions");
                if (regionsObj instanceof List && !((List<?>) regionsObj).isEmpty()) {
                    List<?> regions = (List<?>) regionsObj;
                    List<Predicate> regionPredicates = new ArrayList<>();
                    
                    for (Object regionObj : regions) {
                        String region = regionObj.toString();
                        regionPredicates.add(criteriaBuilder.equal(root.get("region"), region));
                    }
                    
                    predicates.add(criteriaBuilder.or(regionPredicates.toArray(new Predicate[0])));
                }
            }
            
            // 組合所有條件
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 安全解析整數
     *
     * @param obj 要解析的對象
     * @return 解析後的整數，解析失敗則返回null
     */
    private Integer parseInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Specification<Users> build(String jsonCriteria) {
        if (jsonCriteria == null || jsonCriteria.isEmpty()) {
            return null;
        }

        try {
            Map<String, Object> criteriaMap = objectMapper.readValue(jsonCriteria, new TypeReference<>() {});

            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                for (Map.Entry<String, Object> entry : criteriaMap.entrySet()) {
                    String field = entry.getKey();
                    Object value = entry.getValue();

                    if (ALLOWED_FIELDS.contains(field)) {
                        try {
                            // 根據 Users entity 的欄位類型進行轉換和比較
                            if (field.equals("gender") || field.equals("userStatus")) {
                                // 處理 byte/Byte 類型
                                predicates.add(criteriaBuilder.equal(root.get(field), Byte.valueOf(value.toString())));
                            } else if (field.equals("interests") || field.equals("personality")) {
                                // 處理 String 包含查詢 (多個興趣可以用逗號分隔)
                                predicates.add(criteriaBuilder.like(root.get(field), "%" + value.toString() + "%"));
                            }
                            else {
                                // 預設處理 String 完全匹配
                                predicates.add(criteriaBuilder.equal(root.get(field), value));
                            }
                        } catch (NumberFormatException e) {
                            log.warn("值 '{}' 無法轉換為欄位 '{}' 所需的數字類型，已忽略此條件。", value, field);
                        }
                    } else {
                        log.warn("檢測到不在白名單中的查詢欄位 '{}'，已忽略。", field);
                    }
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        } catch (IOException e) {
            log.error("解析 TargetCriteria JSON 失敗: {}", jsonCriteria, e);
            // 返回一個永遠為 false 的條件，避免因解析失敗而選中所有使用者
            return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }
    }
} 