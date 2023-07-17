package com.restore.providerservice.controller;

import com.restore.providerservice.service.InsurancePayerService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/insurance-payer")
@Slf4j
public class InsurancePayerController extends AppController {
    private final InsurancePayerService insurancePayerService;
    @Autowired
    public InsurancePayerController(InsurancePayerService insurancePayerService) {
        this.insurancePayerService = insurancePayerService;
    }
    @GetMapping("/all")
    public ResponseEntity<Response> getAll(Pageable pageable) throws RestoreSkillsException {
        return data(ResponseCode.OK, "Insurance Payers Found Successfully.",insurancePayerService.getAll(pageable));
    }
}
