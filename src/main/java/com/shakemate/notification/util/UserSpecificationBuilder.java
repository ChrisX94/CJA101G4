package com.shakemate.notification.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakemate.user.model.Users;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class UserSpecificationBuilder {

    // 欄位白名單，防止惡意查詢
    private static final Set<String> ALLOWED_FIELDS = Set.of("location", "gender", "userStatus", "interests", "personality");

    private static final ObjectMapper objectMapper = new ObjectMapper();

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