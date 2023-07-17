package com.restore.providerservice.controller;

import com.restore.providerservice.service.FeeScheduleService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.providerservice.dto.FeeSchedule;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/provider/fee-schedule")
public class FeeScheduleController extends AppController {

    private final FeeScheduleService feeScheduleService;

    @Autowired
    public FeeScheduleController(FeeScheduleService feeScheduleService) {
        this.feeScheduleService = feeScheduleService;
    }

    @PostMapping()
    public ResponseEntity<Response> addFeeSchedule(@RequestBody @Valid FeeSchedule feeSchedule) {
        feeScheduleService.addFeeSchedule(feeSchedule);
        return success(ResponseCode.OK, "Added fee schedule successfully!");
    }

    @GetMapping()
    public ResponseEntity<Response> getFeeScheduleList(@RequestParam(required = false) String cptCode, @RequestParam(required = false) Boolean active, Pageable pageable) {
        return data(feeScheduleService.getFeeScheduleList(cptCode, active, pageable));
    }

    @PutMapping("/{feeScheduleUuid}")
    public ResponseEntity<Response> changeActiveStatus(@PathVariable UUID feeScheduleUuid, @RequestBody FeeSchedule feeSchedule) {
        return data(ResponseCode.OK,"FeeSchedule has been updated successfully!", feeScheduleService.updateFeeSchedule(feeScheduleUuid, feeSchedule));
    }

    @PatchMapping("/{feeScheduleUuid}")
    public ResponseEntity<Response> changeActiveStatus(@RequestParam boolean activeStatus, @PathVariable UUID feeScheduleUuid) {
        feeScheduleService.changeActiveStatus(feeScheduleUuid, activeStatus);
        return success(ResponseCode.OK, "FeeSchedule status has been changed successfully!");
    }

    @DeleteMapping("/{feeScheduleUuid}")
    public ResponseEntity<Response> deleteFeeSchedule(@PathVariable UUID feeScheduleUuid) {
        feeScheduleService.deleteFeeSchedule(feeScheduleUuid);
        return success(ResponseCode.OK, "FeeSchedule has been deleted successfully!");
    }
}
