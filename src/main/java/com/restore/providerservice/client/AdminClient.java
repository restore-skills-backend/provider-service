package com.restore.providerservice.client;


import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.app.User;
import com.restore.core.dto.response.Response;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@FeignClient(name = "restore-admin-service", path = "/api/admin")
public interface AdminClient {

    @GetMapping("/provider-group/{id}")
    ResponseEntity<Response> getProviderGroupById(@RequestHeader(name = "X-TENANT-ID") String requester, @PathVariable("id") UUID uuid) throws RestoreSkillsException;

    @GetMapping("/provider-group/subdomain/{subdomain}")
    ResponseEntity<Response> getProviderGroupBySubdomain(@RequestHeader(name = "X-TENANT-ID") String requester, @PathVariable("subdomain") String subdomain) throws RestoreSkillsException, IOException;

    @PutMapping("/provider-group")
    ResponseEntity<Response> updateProviderGroup(@RequestHeader(name = "X-TENANT-ID") String requester,@RequestBody @Valid ProviderGroup providerGroup) throws RestoreSkillsException, IOException;

    @PutMapping("/user/{email}/active/{active}")
    ResponseEntity<Response> activateUser(@RequestHeader(name = "X-TENANT-ID") String requester,@PathVariable String email, @PathVariable boolean active) throws RestoreSkillsException;

    @PutMapping("/profile")
    ResponseEntity<Response> updateProfile(@RequestHeader(name = "X-TENANT-ID") String requester,@Valid @RequestBody User user) throws RestoreSkillsException;
}