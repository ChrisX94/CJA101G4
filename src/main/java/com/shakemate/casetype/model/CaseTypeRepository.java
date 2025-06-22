package com.shakemate.casetype.model;

import com.shakemate.casetype.model.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseTypeRepository extends JpaRepository<CaseType, Integer> {
}
