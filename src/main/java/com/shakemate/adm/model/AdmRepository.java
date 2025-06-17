package com.shakemate.adm.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmRepository extends JpaRepository<AdmVO, Integer> {
    List<AdmVO> findByAdmNameContaining(String admName);
    
    AdmVO findByAdmAcc(String admAcc);



//	@Transactional
//	@Modifying
//	@Query(value = "delete from emp3 where empno =?1", nativeQuery = true)
//	void deleteByEmpno(int empno);
//
//	//● (自訂)條件查詢
//	@Query(value = "from EmpVO where empno=?1 and ename like?2 and hiredate=?3 order by empno")
//	List<AdmVO> findByOthers(int adm_id , String adm_name , java.sql.Date hiredate);
}
