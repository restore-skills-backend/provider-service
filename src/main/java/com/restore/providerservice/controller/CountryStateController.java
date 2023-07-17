package com.restore.providerservice.controller;

import com.restore.providerservice.service.CountryStateService;
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
@RequestMapping("/api/provider/states")
@Slf4j
public class CountryStateController extends AppController {
    private final CountryStateService countryStateService;
    @Autowired
    public CountryStateController(CountryStateService countryStateService) {
        this.countryStateService = countryStateService;
    }
    @GetMapping("/all")
    public ResponseEntity<Response> getAll(Pageable pageable) throws RestoreSkillsException {
        return data(ResponseCode.OK, "States Found Successfully.",countryStateService.getAll(pageable));
    }
}
