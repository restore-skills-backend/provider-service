package com.restore.providerservice.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restore.providerservice.service.StaffService;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.app.Staff;
import com.restore.core.dto.app.User;
import com.restore.core.dto.app.enums.Roles;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.StaffEntity;
import com.restore.core.entity.UserEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.core.service.AwsService;
import com.restore.providerservice.client.AdminClient;
import com.restore.providerservice.client.UserClient;
import com.restore.providerservice.repository.StaffRepo;
import com.restore.providerservice.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.common.util.ObjectUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class StaffServiceImpl extends AppService implements StaffService {

    private final StaffRepo staffRepo;
    private final UserClient userClient;
    private final AdminClient adminClient;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final AwsService awsService;
    private final ObjectMapper objectMapper;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public StaffServiceImpl(StaffRepo staffRepo, UserClient userClient, AdminClient adminClient, ModelMapper modelMapper, UserRepo userRepo, AwsService awsService, ObjectMapper objectMapper) {
        this.staffRepo = staffRepo;
        this.userClient = userClient;
        this.adminClient = adminClient;
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
        this.awsService = awsService;
        this.objectMapper = objectMapper;
    }

    private Optional<StaffEntity> getById(UUID uuid) {
        return staffRepo.findByUuid(uuid);
    }
    private StaffEntity getEntity(UUID uuid) throws RestoreSkillsException {
        Optional<StaffEntity> existing = getById(uuid);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User ID : " + uuid);
        }
        return existing.get();
    }

    private StaffEntity getStaffEntityByUser(UserEntity userEntity) throws RestoreSkillsException {
        StaffEntity existingStaff = staffRepo.findByUserId(userEntity);

        if (ObjectUtils.isEmpty(existingStaff)) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User Id ID : " + userEntity.getId());
        }

        return existingStaff;
    }

    private Staff toStaff(StaffEntity staffEntity){
        if(!StringUtils.isEmpty(staffEntity.getAvatar())){
            staffEntity.setAvatar(getProfileImage(staffEntity.getAvatar()));
        }
        return modelMapper.map(staffEntity, Staff.class);
    }
    private UserEntity getUserEntity(UUID uuid) throws RestoreSkillsException {
        Optional<UserEntity> existing = userRepo.findByUuid(uuid);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User ID : " + uuid);
        }

        return existing.get();
    }
    private String getProfileImage(String avtarKey){
        try {
            if(avtarKey == null){
                return null;
            }
            return awsService.getPreSignedUrl(avtarKey);
        }catch(Exception exception){
            log.error("Error while fetching profile image.");
            return null;
        }
    }

    private String uploadProfileImage(String profileImage, UUID userId, String avtarKey) throws RestoreSkillsException {
        try {
            return awsService.uploadStaffProfile(profileImage,userId,avtarKey);
        }catch(Exception exception){
            log.error("Error while storing profile image of user :"+ userId + "Exception : " + exception);
            return null;
        }
    }

    private String deleteProfileImage(String profilePhotoKey, String bucketName){
        try{
            return awsService.deleteProfilePhoto(profilePhotoKey,bucketName);
        }catch(Exception exception){
            log.error("Error while deleting profile image of user" + exception );
            return null;
        }
    }
    private void updateIamUser(StaffEntity staffEntity, String schemaName) throws RestoreSkillsException {
        User user = modelMapper.map(staffEntity.getUserId(), User.class);
        user.setFirstName(staffEntity.getFirstName());
        user.setLastName(staffEntity.getLastName());
        user.setPhone(staffEntity.getPhoneNumber());
        if(StringUtils.isEmpty(schemaName)){
            adminClient.updateProfile(getCurrentUser().getTenantKey(), user);
        }
    }
    @Override
    public void createStaffUser(User user, UUID providerGroupId) throws RestoreSkillsException, IOException {
        ResponseEntity<Response> providerGroupResponse = null;
        user.setRole(Roles.STAFF);
        User currentUser = getCurrentUser();
        if(providerGroupId == null){
            providerGroupResponse = adminClient.getProviderGroupBySubdomain("public", currentUser.getTenantKey());
            if (Objects.isNull(providerGroupResponse.getBody().getData()))
                throw new RestoreSkillsException(ResponseCode.NOT_FOUND, "Provider group not found with id : " + currentUser.getTenantKey());
        }else{
            providerGroupResponse = adminClient.getProviderGroupById("public", providerGroupId);
            if (Objects.isNull(providerGroupResponse.getBody().getData()))
                throw new RestoreSkillsException(ResponseCode.NOT_FOUND, "Provider group not found with id : " + providerGroupId);
        }
        ProviderGroup providerGroup = objectMapper.convertValue(providerGroupResponse.getBody().getData(), ProviderGroup.class);
        ResponseEntity<Response> responseEntity = userClient.addUser(providerGroup.getDbSchema(),user,providerGroup.getUuid());

        if(responseEntity.getStatusCode()!= HttpStatusCode.valueOf(200) || responseEntity.getBody()==null) return;
        else if(responseEntity.getBody().getData() == null) return;

        UserEntity userEntity = modelMapper.map(responseEntity.getBody().getData(), UserEntity.class);
        UserEntity savedUserEntity = userRepo.findById(userEntity.getId()).orElseThrow();
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setUserId(savedUserEntity);
        staffEntity.setEmail(savedUserEntity.getEmail());
        staffEntity.setFirstName(user.getFirstName());
        staffEntity.setLastName(user.getLastName());
        staffEntity.setPhoneNumber(user.getPhone());
        staffEntity.setActive(true);
        staffEntity.setUuid(UUID.randomUUID());
        if(!ObjectUtil.isBlank(user.getAvatar())){
            staffEntity.setAvatar(uploadProfileImage(user.getAvatar(),staffEntity.getUuid(),null));
        }
        staffRepo.save(staffEntity);
    }

    @Override
    public Staff get(UUID staffId) throws RestoreSkillsException {
        StaffEntity staffEntity = getEntity(staffId);
        return toStaff(staffEntity);
    }

    @Override
    public Staff getByUserId(UUID userId) throws RestoreSkillsException {
        UserEntity userEntity = getUserEntity(userId);
        return toStaff(getStaffEntityByUser(userEntity));
    }

    @Override
    public Object getAll(Pageable pageable) throws RestoreSkillsException {
        ResponseEntity<Response> response = userClient.getAllUsers(getCurrentUser().getTenantKey(),getCurrentUser().getTenantKey(), pageable);

        if (!response.getBody().getCode().equals(ResponseCode.OK))
            throwError(ResponseCode.DB_ERROR, "Error occur while fetching all the users from database!");

        return response.getBody().getData();
    }

    @Override
    public void updateStatus(UUID userId, boolean status) throws RestoreSkillsException{
        StaffEntity staffEntity = getEntity(userId);
        staffEntity.setActive(status);
        adminClient.activateUser(getCurrentUser().getTenantKey(), staffEntity.getUserId().getEmail(), status);
        staffRepo.save(staffEntity);
    }

    @Override
    public void remove(UUID userId) throws RestoreSkillsException{
        StaffEntity staffEntity = getEntity(userId);
        UserEntity userEntity = getUserEntity(staffEntity.getUserId().getUuid());
        userEntity.setArchive(true);
        staffEntity.setArchive(true);
        userRepo.save(userEntity);
        staffRepo.save(staffEntity);
    }
    @Override
    public void update(Staff staff, String schemaName) throws RestoreSkillsException{
        if(staff.getUuid()==null){
            throwError(ResponseCode.BAD_REQUEST,"Staff UUID not present");
        }
        StaffEntity existingStaff = getEntity(staff.getUuid());
        existingStaff.setFirstName(staff.getFirstName());
        existingStaff.setLastName(staff.getLastName());
        existingStaff.setPhoneNumber(staff.getPhoneNumber());
        if(StringUtils.isEmpty(staff.getAvatar()) && StringUtils.isEmpty(staff.getNewAvatar())){
            deleteProfileImage(existingStaff.getAvatar(), bucketName);
            existingStaff.setAvatar(null);
        }
        if (!StringUtils.isEmpty(staff.getNewAvatar())) {
            String profilePhotoKey = uploadProfileImage(staff.getNewAvatar(), existingStaff.getUuid(), existingStaff.getAvatar());
            existingStaff.setAvatar(profilePhotoKey);
        }
        staffRepo.save(existingStaff);
        updateIamUser(existingStaff,schemaName);
    }
}
