package com.shakemate.activity.repository;

import com.shakemate.activity.entity.ActivityReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Integer> {
}
