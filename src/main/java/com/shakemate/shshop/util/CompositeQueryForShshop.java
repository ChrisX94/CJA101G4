package com.shakemate.shshop.util;

import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdType;
import com.shakemate.user.model.Users;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompositeQueryForShshop {
    public static Predicate getPredicates(CriteriaBuilder builder, Root<ShProd> root, String key, String value) {
        Predicate predicate = null;
        if (value == null || value.trim().isEmpty()) {
            return null; // 空值直接忽略
        }

        switch (key) {
            case "friendIds":
                    String[] ids = value.split(",");
                    List<Integer> friendIdList = new ArrayList<>();
                    for (String id : ids) {
                        friendIdList.add(Integer.parseInt(id.trim()));
                    }
                    predicate = root.get("user").get("userId").in(friendIdList);
                break;
            case "prodName":
            case "prodBrand":
            case "prodContent":
                predicate = builder.like(root.get(key), "%" + value.trim() + "%");
                break;

            case "typeId":
                    int typeId = Integer.parseInt(value.trim());
                    ShProdType type = new ShProdType();
                    type.setProdTypeId(typeId);
                    predicate = builder.equal(root.get("shProdType"), type);
                break;

            case "username":
                predicate = builder.like(root.get("user").get("username"), "%" + value.trim() + "%");
                break;

            case "minPrice":
                try {
                    predicate = builder.greaterThanOrEqualTo(root.get("prodPrice"), Integer.parseInt(value.trim()));
                } catch (NumberFormatException ignored) {
                }
                break;

            case "maxPrice":
                try {
                    predicate = builder.lessThanOrEqualTo(root.get("prodPrice"), Integer.parseInt(value.trim()));
                } catch (NumberFormatException ignored) {
                }
                break;
        }

        return predicate;
    }

    public static List<ShProdDto> getAllComposite(Map<String, String> inputMap, Session session) {

        Transaction tx = session.beginTransaction();
        List<ShProdDto> dtoList = new ArrayList<>();
        List<ShProd> list = null;
        try (session){
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ShProd> cq = builder.createQuery(ShProd.class);
            Root<ShProd> root = cq.from(ShProd.class);
            cq.select(root).distinct(true); // 避免JOIN重複資料
            List<Predicate> predicateList = new ArrayList<Predicate>();
            Set<String> keys = inputMap.keySet();
            for (String key : keys) {
                String value = inputMap.get(key);
                Predicate predicate = getPredicates(builder, root, key, value);
                if (predicate != null) {
                    predicateList.add(predicate);
                }
            }
            cq.where(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.orderBy(builder.asc(root.get("updatedTime")));
            Query query = session.createQuery(cq);
            list = query.getResultList();
            for(ShProd p : list) {
                dtoList.add(new ShProdDto(p));
            }
            tx.commit();

        } catch (RuntimeException ex) {
            if (tx != null)
                tx.rollback();
            throw ex;
        }
        return dtoList;
    }


}
