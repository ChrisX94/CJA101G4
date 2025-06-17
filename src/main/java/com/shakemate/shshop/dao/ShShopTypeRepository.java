package com.shakemate.shshop.dao;

import com.shakemate.shshop.model.ShProdType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShShopTypeRepository extends JpaRepository<ShProdType, Integer> {



}
