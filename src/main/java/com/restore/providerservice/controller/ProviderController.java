package com.restore.providerservice.controller;

import com.restore.providerservice.service.ProviderService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.Provider;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/provider")
@Slf4j
public class ProviderController extends AppController {

    private final ProviderService providerService;

    @Autowired
    private ProviderController(ProviderService providerService){
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<Response> createProvider(@RequestParam(value = "providerGroupId", required = false) UUID  providerGroupId, @RequestBody @Valid Provider provider) throws RestoreSkillsException,IOException {
        providerService.add(provider,providerGroupId);
        return success(ResponseCode.CREATED, "Provider Created successfully.");
    }


    @GetMapping("/{providerId}")
    public ResponseEntity<Response> get(@PathVariable UUID providerId) throws RestoreSkillsException{
        return data(ResponseCode.OK, "Provider found Successfully.", providerService.get(providerId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getByUserId(@PathVariable UUID userId) throws RestoreSkillsException{
        return data(ResponseCode.OK, "Provider found Successfully.", providerService.getByUserId(userId));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response> getProfile() throws RestoreSkillsException{
        return data(ResponseCode.OK, "Provider found Successfully.", providerService.getProfile());
    }

    @GetMapping("/provider-group")
    public ResponseEntity<Response> getProviderGroup() throws RestoreSkillsException,IOException{
        return data(ResponseCode.OK, "Provider Group found Successfully.", providerService.getProviderGroup());
    }

    @PutMapping("/provider-group")
    public ResponseEntity<Response> updateProviderGroup(@RequestBody @Valid ProviderGroup providerGroup) throws RestoreSkillsException, IOException {
        providerService.updateProviderGroup(providerGroup);
        return success(ResponseCode.OK, "Provider group updated successfully");
    }

    @DeleteMapping("/{providerId}")
    public ResponseEntity<Response> remove(@PathVariable UUID providerId) throws RestoreSkillsException{
        providerService.remove(providerId);
        return success(ResponseCode.OK, "Provider deleted Successfully.");
    }
    @PutMapping("/{providerId}")
    public ResponseEntity<Response> update(@RequestBody Provider provider, @PathVariable UUID providerId,@RequestParam(name = "schemaName", required = false) String schemaName) throws RestoreSkillsException {
        providerService.update(provider,providerId,schemaName);
        return success(ResponseCode.OK, "Provider Updated Successfully");
    }
    @PutMapping("/status/{providerId}")
    public ResponseEntity<Response> updateStatus(@PathVariable UUID providerId,
                                                         @QueryParam("isActive") boolean isActive) throws RestoreSkillsException{
        providerService.updateStatus(providerId,isActive);
        return success(ResponseCode.OK, "Provider Status Updated Successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllProviders(Pageable pageable) throws RestoreSkillsException{
        return data(ResponseCode.OK, "Providers Found Successfully.",providerService.getAll(pageable));
    }

    @GetMapping("/speciality")
    public ResponseEntity<Response> getSpecialityList(Pageable pageable) {
        return data(ResponseCode.OK, "Successfully fetched specialities list!", providerService.getSpecialityList(pageable));
    }

//    @PostMapping(path = "/profile/{providerId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<Response> uploadProfileImage(@ModelAttribute MultipartFile profileImage, @PathVariable UUID providerId) throws RestoreSkillsException, IOException {
//        providerService.uploadProfileImage(profileImage,providerId);
//        return success(ResponseCode.OK, "Provider profile image save successfully.");
//    }
//
//    @GetMapping(path = "/profile/{providerId}")
//    public ResponseEntity<Response> getProviderProfileImage(@PathVariable UUID providerId) throws RestoreSkillsException, IOException {
//        return data(ResponseCode.OK, "Provider profile image get successfully.",providerService.getProviderProfileImage(providerId));
//    }
//
//    @DeleteMapping(path = "/profile/{providerId}")
//    public ResponseEntity<Response> deleteProviderProfileImage(@PathVariable UUID providerId) throws RestoreSkillsException, IOException {
//        providerService.deleteProviderProfileImage(providerId);
//        return success(ResponseCode.OK, "Provider profile image deleted successfully.");
//    }

}
