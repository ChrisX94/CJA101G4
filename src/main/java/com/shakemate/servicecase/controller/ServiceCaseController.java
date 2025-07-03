package com.shakemate.servicecase.controller;
//提供 REST API（JSON）。

import com.shakemate.servicecase.dto.ServiceCaseDTO;
import com.shakemate.servicecase.mapper.ServiceCaseMapper;

import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.model.ServiceCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicecase")
public class ServiceCaseController {

	@Autowired
	private ServiceCaseService serviceCaseService;

	// 1. 建立一筆 News (POST /api/servicecase)

	@PostMapping
	public ResponseEntity<ServiceCaseDTO> create(@RequestBody ServiceCaseDTO dto) {
        // DTO 轉實體
        ServiceCase sc = ServiceCaseMapper.toEntity(dto);
        // 呼叫 Service 儲存
        ServiceCase saved = serviceCaseService.create(sc);
        // 將儲存結果再轉回 DTO 回傳
        return ResponseEntity.ok(ServiceCaseMapper.toDTO(saved));
	}

	// 2. 取得所有 News (GET /api/servicecase)

	@GetMapping
	public ResponseEntity<List<ServiceCaseDTO>> getAll() {
		System.out.println("/api/servicecase 被呼叫！");
		List<ServiceCaseDTO> dtos = serviceCaseService.getAll().stream().map(ServiceCaseMapper::toDTO)
				.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	// 3. 根據 ID 取得單一 News (GET /api/servicecase/{id})

	@GetMapping("/{id}")
	public ResponseEntity<ServiceCaseDTO> getById(@PathVariable Integer id) {
		ServiceCase sc = serviceCaseService.findById(id);
		if (sc == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(ServiceCaseMapper.toDTO(sc));
	}

	// 4. 更新一筆 News (PUT /api/servicecase/{id})

	@PutMapping("/{id}")
	public ResponseEntity<ServiceCaseDTO> update(@PathVariable Integer id, @RequestBody ServiceCaseDTO dto) {

		ServiceCase existing = serviceCaseService.findById(id);
		if (existing == null)
			return ResponseEntity.notFound().build();

		ServiceCase toUpdate = ServiceCaseMapper.toEntity(dto);
		toUpdate.setCaseId(id);
		ServiceCase updated = serviceCaseService.update(toUpdate);
		return ResponseEntity.ok(ServiceCaseMapper.toDTO(updated));
	}

	// @PutMapping("/{id}")
	//	public ResponseEntity<ServiceCase> update(@PathVariable Integer id, @RequestBody ServiceCase sc) {

	// 確認該筆資料存在
	//		ServiceCase existing = serviceCaseService.findById(id);
	//		if (existing == null) {
	//			return ResponseEntity.notFound().build();
	//		}

	// 將 ID 設回來，並呼叫 Service 層更新
	//		sc.setCaseId(id);
	//		ServiceCase updated = serviceCaseService.update(sc);
	//		return ResponseEntity.ok(updated);
	//	}

	// 5. 刪除一筆 News (DELETE /api/servicecase/{id})

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		if (serviceCaseService.findById(id) == null)
			return ResponseEntity.notFound().build();
		serviceCaseService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// @DeleteMapping("/{id}")
	//	public ResponseEntity<Void> delete(@PathVariable Integer id) {
	//		ServiceCase existing = serviceCaseService.findById(id);
	//		if (existing == null) {
	//			return ResponseEntity.notFound().build();
	//		}
	//		serviceCaseService.delete(id);
	//		return ResponseEntity.noContent().build();
	//	}
}
