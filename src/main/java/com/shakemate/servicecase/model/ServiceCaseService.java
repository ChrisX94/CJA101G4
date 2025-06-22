package com.shakemate.servicecase.model;

import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.model.ServiceCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("ServiceCaseService")
public class ServiceCaseService {

    @Autowired
    private ServiceCaseRepository repository;
    
    public ServiceCase create(ServiceCase sc) {
        return repository.save(sc);
    }
    // ... 其他呼叫 repo.findAll(), repo.findByUserId(...) 等方法
    
// ========================================

    public ServiceCase update(ServiceCase serviceCase) {
        return repository.save(serviceCase);
    }

    public void delete(Integer caseId) {
        if (repository.existsById(caseId)) {
            repository.deleteById(caseId);
        }
    }

    public ServiceCase findById(Integer caseId) {
        Optional<ServiceCase> optional = repository.findById(caseId);
        return optional.orElse(null);
    }

    public List<ServiceCase> getAll() {
        return repository.findAll();
    }

    // 可加入更多查詢條件
    public List<ServiceCase> findByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public List<ServiceCase> findByCaseStatus(Integer status) {
        return repository.findByCaseStatus(status);
    }
}
