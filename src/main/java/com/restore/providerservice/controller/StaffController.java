package com.restore.providerservice.controller;

import com.restore.providerservice.service.StaffService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.Staff;
import com.restore.core.dto.app.User;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/provider/staff")
public class StaffController extends AppController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<Response> createStaffUser(@RequestBody @Valid User user, @RequestParam(name = "providerGroupId", required = false) UUID providerGroupId) throws RestoreSkillsException, IOException {
        staffService.createStaffUser(user,providerGroupId);
        return success(ResponseCode.CREATED, "User Created successfully.");
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<Response> get(@PathVariable UUID staffId) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Staff found succesfully.",staffService.get(staffId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getByUserId(@PathVariable UUID userId) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Staff found succesfully.",staffService.getByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll(Pageable pageable) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Staff users found succesfully.",staffService.getAll(pageable));
    }

    @PatchMapping("/status/{userId}")
    public ResponseEntity<Response> getAll(@PathVariable UUID userId, @RequestParam("status") boolean status) throws RestoreSkillsException {
        staffService.updateStatus(userId,status);
        return success(ResponseCode.UPDATED, "User Status updated successfully.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response> remove(@PathVariable UUID userId) throws RestoreSkillsException {
        staffService.remove(userId);
        return success(ResponseCode.UPDATED, "User updated successfully.");
    }

    @PutMapping
    public ResponseEntity<Response> update(@RequestBody @Valid Staff staff, @RequestParam(name = "schemaName", required = false) String schemaName ) throws RestoreSkillsException {
        staffService.update(staff,schemaName);
        return success(ResponseCode.UPDATED, "User updated successfully.");
    }
}
