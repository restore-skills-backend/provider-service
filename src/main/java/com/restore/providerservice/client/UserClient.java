package com.restore.providerservice.client;

import com.restore.core.dto.app.User;
import com.restore.core.dto.response.Response;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "restore-user-service",path = "/api/user")
public interface UserClient {

    @GetMapping("/get-user-by-email/{email}")
    ResponseEntity<Response> getUserByEmail(@PathVariable String email);

    @PostMapping("/create-user/{providerGroupId}")
    ResponseEntity<Response> addUser(@RequestHeader(name = "X-TENANT-ID") String requester, @RequestBody User user, @PathVariable UUID providerGroupId) throws RestoreSkillsException;

    @GetMapping("/all/{schemaName}")
    ResponseEntity<Response> getAllUsers(@RequestHeader(name = "X-TENANT-ID") String requester, @PathVariable String schemaName, Pageable pageable);
}
