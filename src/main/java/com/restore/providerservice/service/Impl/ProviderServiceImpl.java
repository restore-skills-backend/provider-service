package com.restore.providerservice.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restore.providerservice.repository.*;
import com.restore.providerservice.service.CloudFileService;
import com.restore.providerservice.service.ProviderService;
import com.restore.core.dto.app.*;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.*;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.core.service.AwsService;
import com.restore.providerservice.client.AdminClient;
import com.restore.providerservice.repository.*;
import com.restore.providerservice.client.UserClient;
import com.restore.providerservice.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ProviderServiceImpl extends AppService implements ProviderService {
    private final ProviderRepo providerRepo;
    private final CountryStateRepo countryStateRepo;
    private final UserRepo userRepo;
    private final CloudFileService cloudFileService;
    private final ModelMapper modelMapper;
    private final Mapper mapper;
    private final UserClient userClient;
    private final ObjectMapper objectMapper;
    private final SpecialityRepo specialityRepo;
    private final AwsService awsService;
    private final LocationRepo locationRepo;
    private final AdminClient adminClient;
    private final InsurancePayerRepo insurancePayerRepo;
    private final ProviderProfileInfoRepo providerProfileInfoRepo;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public ProviderServiceImpl(ProviderRepo providerRepo, CountryStateRepo countryStateRepo,
                               UserRepo userRepo, CloudFileService cloudFileService,
                               ModelMapper modelMapper, Mapper mapper, UserClient userClient,
                               ObjectMapper objectMapper, SpecialityRepo specialityRepo, AwsService awsService, LocationRepo locationRepo, AdminClient adminClient, InsurancePayerRepo insurancePayerRepo, ProviderProfileInfoRepo providerProfileInfoRepo) {
        this.providerRepo = providerRepo;
        this.countryStateRepo = countryStateRepo;
        this.userRepo = userRepo;
        this.cloudFileService = cloudFileService;
        this.modelMapper = modelMapper;
        this.mapper = mapper;
        this.userClient = userClient;
        this.objectMapper = objectMapper;
        this.specialityRepo = specialityRepo;
        this.awsService = awsService;
        this.locationRepo = locationRepo;
        this.adminClient = adminClient;
        this.insurancePayerRepo = insurancePayerRepo;
        this.providerProfileInfoRepo = providerProfileInfoRepo;
    }

    private Optional<ProviderEntity> getById(UUID uuid) {
        return providerRepo.findByUuid(uuid);
    }
    private ProviderEntity getEntity(UUID uuid) throws RestoreSkillsException {
        Optional<ProviderEntity> existing = getById(uuid);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid Provider ID : " + uuid);
        }

        return existing.get();
    }

    private void getEntityByNpiNumber(Long npiNumber) throws RestoreSkillsException {
        Optional<ProviderEntity> existing = providerRepo.findByNpiNumber(npiNumber);
        if (!existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Npi Number already present. : " + npiNumber);
        }
    }

    private ProviderEntity getProviderEntityByUser(UserEntity userEntity) throws RestoreSkillsException {

        ProviderEntity existingProvider = providerRepo.findByUserId(userEntity);

        if (ObjectUtils.isEmpty(existingProvider)) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid Provider ID : " + userEntity.getId());
        }

        return existingProvider;
    }
    private UserEntity getUserEntityByEmailId(String emailId) throws RestoreSkillsException {
        Optional<UserEntity> existing = userRepo.findByEmail(emailId);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User email ID : " + emailId);
        }

        return existing.get();
    }

    private UserEntity getUserEntity(UUID uuid) throws RestoreSkillsException {
        Optional<UserEntity> existing = userRepo.findByUuid(uuid);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User ID : " + uuid);
        }

        return existing.get();
    }

    private ProviderEntity save(ProviderEntity providerEntity) {
       return providerRepo.save(providerEntity);
    }
    private Set<SpecialityEntity> toSpecialityEntity(Set<Speciality> specialities) throws RestoreSkillsException{
        Set<SpecialityEntity> specialityEntities = new HashSet<>();

        for (Speciality speciality : specialities) {
            Optional<SpecialityEntity> optionalEntity = specialityRepo.findByName(speciality.getName());

            if (optionalEntity.isPresent()) {
                specialityEntities.add(optionalEntity.get());
            }else {
                throwError(ResponseCode.NOT_FOUND,"Speciality Not Present in Provider Group." + speciality.getName());
            }
        }

        return specialityEntities;
    }

    private Set<LocationEntity> toLocationEntity(Set<ProviderLocation> locations) throws RestoreSkillsException {
        Set<LocationEntity> locationEntities = new HashSet<>();

        for (ProviderLocation location : locations) {
            Optional<LocationEntity> optionalEntity = locationRepo.findByUuid(location.getUuid());

            if (optionalEntity.isPresent()) {
                locationEntities.add(optionalEntity.get());
            }else {
                throwError(ResponseCode.NOT_FOUND,"Location Not Present in Provider Group." + location.getUuid());
            }
        }

        return locationEntities;
    }

    private Set<InsurancePayerEntity> toInsurancePayerEntity(Set<InsurancePayer> insurancePayers) throws RestoreSkillsException{
        Set<InsurancePayerEntity> payerEntities = new HashSet<>();

        for (InsurancePayer insurancePayer : insurancePayers) {
            Optional<InsurancePayerEntity> optionalEntity = insurancePayerRepo.findById(insurancePayer.getId());

            if (optionalEntity.isPresent()) {
                payerEntities.add(optionalEntity.get());
            }else {
                throwError(ResponseCode.NOT_FOUND,"Insurance Payer Not Present in Provider Group : For Payer Id : " + insurancePayer.getId());
            }
        }

        return payerEntities;
    }

    private ProviderProfileInfoEntity getProviderProfileEntity(Long id) throws RestoreSkillsException{
        return providerProfileInfoRepo.findById(id).orElseThrow(() -> new RestoreSkillsException(ResponseCode.NOT_FOUND,"Provider Profile not updated because of profile id not found : " + id));
    }

    private Optional<CountryStateEntity> toCountryStateEntity(CountryState countryState){
        if(countryState.getState() != null){
            return countryStateRepo.findByState(countryState.getState());
        }
        return Optional.empty();
    }

    private UserEntity addUser(Provider provider, ProviderGroup providerGroup) throws RestoreSkillsException {
        UserEntity userEntity = null;
        User newUser = mapper.mapProviderRequestToUser(provider);
        newUser.setTenantKey(getCurrentUser().getTenantKey());
        ResponseEntity<Response> userEntityResponse = userClient.addUser(providerGroup.getDbSchema(), newUser, providerGroup.getUuid());
        if (Objects.nonNull(userEntityResponse.getBody())) {
            userEntity = objectMapper.convertValue(userEntityResponse.getBody().getData(), UserEntity.class);
        }
        return userEntity;
    }
    private Provider toProvider(ProviderEntity provider) {
        Provider existingProvider = modelMapper.map(provider, Provider.class);
        existingProvider.setAvatar(getProfileImage(provider.getAvtar()));
        return existingProvider;
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
    private String uploadProfileImage(String profileImage, UUID providerId, String avtarKey) throws RestoreSkillsException {
        try {
            return awsService.uploadProviderProfilePhoto(providerId,profileImage,avtarKey,bucketName);
        }catch(Exception exception){
            log.error("Error while storing profile image of user :"+ providerId + "Exception : " + exception);
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
    private void UpdateProfileInfo(Provider provider, ProviderEntity existingProviderEntity) throws RestoreSkillsException{
        ProviderProfileInfo providerProfileInfo = provider.getProviderProfileInfo();
        ProviderProfileInfoEntity existingProviderProfileInfo = getProviderProfileEntity(providerProfileInfo.getId());
        existingProviderProfileInfo.setSubSpeciality(providerProfileInfo.getSubSpeciality());
        existingProviderProfileInfo.setHospitalAffilation(providerProfileInfo.getHospitalAffilation());
        existingProviderProfileInfo.setAgeGroupSeen(providerProfileInfo.getAgeGroupSeen());
        existingProviderProfileInfo.setLanguageSpoken(providerProfileInfo.getLanguageSpoken());
        existingProviderProfileInfo.setReferralNumber(providerProfileInfo.getReferralNumber());
        existingProviderProfileInfo.setAcceptNewPatients(providerProfileInfo.isAcceptNewPatients());
        existingProviderProfileInfo.setAcceptCashPay(providerProfileInfo.isAcceptCashPay());
        existingProviderProfileInfo.setInsuranceVerification(providerProfileInfo.getInsuranceVerification());
        existingProviderProfileInfo.setPriorAuthorisation(providerProfileInfo.getPriorAuthorisation());
        existingProviderProfileInfo.setSecondOpinion(providerProfileInfo.getSecondOpinion());
        existingProviderProfileInfo.setAcuteSpeciality(providerProfileInfo.getAcuteSpeciality());
        existingProviderProfileInfo.setBio(providerProfileInfo.getBio());
        existingProviderProfileInfo.setExpertiseIn(providerProfileInfo.getExpertiseIn());
        existingProviderProfileInfo.setWorkExperience(providerProfileInfo.getWorkExperience());
        existingProviderEntity.setProviderProfileInfo(providerProfileInfoRepo.save(existingProviderProfileInfo));
    }

    private Speciality toSpeciality(SpecialityEntity specialityEntity){
        return modelMapper.map(specialityEntity, Speciality.class);
    }
    @Override
    public void add(Provider provider, UUID providerGroupId) throws RestoreSkillsException,IOException {
        ProviderEntity providerEntity = modelMapper.map(provider, ProviderEntity.class);
        //check Npi Present
        getEntityByNpiNumber(provider.getNpiNumber());
        toCountryStateEntity(provider.getLicensedState()).ifPresent(providerEntity::setLicensedState);
        providerEntity.setInsuranceAccepted(toInsurancePayerEntity(provider.getInsuranceAccepted()));
        providerEntity.setSpecialities(toSpecialityEntity(provider.getSpecialities()));
        providerEntity.setWorkLocations(toLocationEntity(provider.getWorkLocations()));
        ProviderGroup providerGroup = null;
        User currrentUser = getCurrentUser();
        ResponseEntity<Response> response = null;
        if(providerGroupId == null){
            response = adminClient.getProviderGroupBySubdomain("public", currrentUser.getTenantKey());
            if (Objects.isNull(response.getBody().getData()))
                throw new RestoreSkillsException(ResponseCode.NOT_FOUND, "Provider group not found with id : " + currrentUser.getTenantKey());
        }else{
            response = adminClient.getProviderGroupById("public", providerGroupId);
            if (Objects.isNull(response.getBody().getData()))
                throw new RestoreSkillsException(ResponseCode.NOT_FOUND, "Provider group not found with id : " + providerGroupId);
        }
        providerGroup = objectMapper.convertValue(response.getBody().getData(), ProviderGroup.class);
        UserEntity userEntity = addUser(provider, providerGroup);
        UserEntity createdUser = getUserEntity(userEntity.getUuid());
        providerEntity.setUuid(UUID.randomUUID());
        providerEntity.setUserId(createdUser);
        providerEntity.setCreatedBy(getCurrentUser().getIamId());
        providerEntity.setModifiedBy(getCurrentUser().getIamId());
        providerEntity.setActive(true);
        if (!StringUtils.isEmpty(provider.getAvatar())) {
            String profilePhotoKey = uploadProfileImage(provider.getAvatar(), providerEntity.getUuid(),null);
            providerEntity.setAvtar(profilePhotoKey);
        }
        save(providerEntity);
    }
    @Override
    public Provider get(UUID providerId) throws RestoreSkillsException{
        ProviderEntity providerEntity = getEntity(providerId);
        Provider provider = toProvider(providerEntity);
        if(providerEntity.getUserId()!= null && providerEntity.getUserId().getEmail() != null){
            provider.setEmail(providerEntity.getUserId().getEmail());
        }
        return provider;
    }

    @Override
    public Provider getByUserId(UUID userId) throws RestoreSkillsException {
        UserEntity userEntity = getUserEntity(userId);
        Provider provider= toProvider(getProviderEntityByUser(userEntity));
        if(userEntity.getEmail() != null){
            provider.setEmail(userEntity.getEmail());
        }
        return provider;
    }

    @Override
    public Provider getProfile() throws RestoreSkillsException{
        User user = getCurrentUser();
        UserEntity userEntity = getUserEntityByEmailId(user.getEmail());
        Provider provider= toProvider(getProviderEntityByUser(userEntity));
        if(userEntity.getEmail() != null){
            provider.setEmail(userEntity.getEmail());
        }
        return provider;
    }

    @Override
    public ProviderGroup getProviderGroup() throws RestoreSkillsException,IOException{
        User currrentUser = getCurrentUser();
        ResponseEntity<Response> response = adminClient.getProviderGroupBySubdomain("public", currrentUser.getTenantKey());
        if (Objects.isNull(response.getBody().getData()))
            throw new RestoreSkillsException(ResponseCode.NOT_FOUND, "Provider group not found with id : " + currrentUser.getTenantKey());
        return objectMapper.convertValue(response.getBody().getData(), ProviderGroup.class);
    }
    @Override
    public void updateProviderGroup(ProviderGroup providerGroup) throws RestoreSkillsException, IOException {
        ResponseEntity<Response> response = adminClient.updateProviderGroup("public", providerGroup);
        if (!response.getBody().getCode().equals(ResponseCode.OK)){
            throw new RestoreSkillsException(ResponseCode.NOT_FOUND, "Provider group not updated successfully : " + providerGroup.getUuid());
        }
    }

    @Override
    public void remove(UUID providerId) throws RestoreSkillsException{
        ProviderEntity providerEntity = getEntity(providerId);
        UserEntity userEntity = getUserEntity(providerEntity.getUserId().getUuid());
        userEntity.setArchive(true);
        providerEntity.setArchive(true);
        userRepo.save(userEntity);
        save(providerEntity);
    }
    @Override
    public void update(Provider provider, UUID providerId,String schemaName) throws RestoreSkillsException{
        ProviderEntity existingProviderEntity = getEntity(providerId);

        existingProviderEntity.setName(provider.getName());
        existingProviderEntity.setUserType(provider.getUserType());
        existingProviderEntity.setProviderType(provider.getProviderType());
        existingProviderEntity.setGender(provider.getGender());
        existingProviderEntity.setPhoneNumber(provider.getPhoneNumber());
        existingProviderEntity.setFaxNumber(provider.getFaxNumber());
        existingProviderEntity.setGroupNpiNumber(provider.getGroupNpiNumber());
        existingProviderEntity.setLicenceNumber(provider.getLicenceNumber());
        existingProviderEntity.setYearOfExperience(provider.getYearOfExperience());
        existingProviderEntity.setTaxonomyNumber(provider.getTaxonomyNumber());
        existingProviderEntity.setSpecialities(toSpecialityEntity(provider.getSpecialities()));
        existingProviderEntity.setWorkLocations(toLocationEntity(provider.getWorkLocations()));
        toCountryStateEntity(provider.getLicensedState()).ifPresent(existingProviderEntity::setLicensedState);
        existingProviderEntity.setInsuranceAccepted(toInsurancePayerEntity(provider.getInsuranceAccepted()));
        if(provider.getProviderProfileInfo() != null && provider.getProviderProfileInfo().getId()!=null){
            UpdateProfileInfo(provider,existingProviderEntity);
        }
        if (!StringUtils.isEmpty(provider.getNewAvatar())) {
            String profilePhotoKey = uploadProfileImage(provider.getNewAvatar(), existingProviderEntity.getUuid(),existingProviderEntity.getAvtar());
            existingProviderEntity.setAvtar(profilePhotoKey);
        }
        if(StringUtils.isEmpty(provider.getAvatar()) && StringUtils.isEmpty(provider.getNewAvatar())){
            deleteProfileImage(existingProviderEntity.getAvtar(), bucketName);
            existingProviderEntity.setAvtar(null);
        }
        try {
            save(existingProviderEntity);
            User user = modelMapper.map(existingProviderEntity.getUserId(), User.class);
            user.setFirstName(existingProviderEntity.getName());
            user.setPhone(existingProviderEntity.getPhoneNumber());
            if(StringUtils.isEmpty(schemaName)){
                adminClient.updateProfile(getCurrentUser().getTenantKey(), user);
            }else{
                adminClient.updateProfile(schemaName,user);
            }

            log.info("Updated Provider {}", existingProviderEntity.getName());
        } catch (Exception e) {
            throwError(ResponseCode.DB_ERROR, "Failed to update Provider " + existingProviderEntity.getName());
        }
    }
    @Override
    public void updateStatus(UUID providerId, boolean isActive) throws RestoreSkillsException{
        ProviderEntity providerEntity = getEntity(providerId);
        providerEntity.setActive(isActive);
        adminClient.activateUser(getCurrentUser().getTenantKey(), providerEntity.getUserId().getEmail(), isActive);
    }
    @Override
    public Page<Provider> getAll(Pageable pageable) throws RestoreSkillsException {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "created");
//        Page<Provider> providers;
        List<Provider> providerResponseDTOList = new ArrayList<>();
        Page<ProviderEntity> providerPage = null;
        providerPage = providerRepo.findByArchiveIsFalse(pageable);
//        if(date != null){
//            Instant instant = date.toInstant();
//            Instant today = instant.plus(Duration.ofHours(24)).truncatedTo(ChronoUnit.DAYS);
//            Instant tommorrow = today.plus(Duration.ofHours(24));
//            if(excludeInactive){
//                dbProviders = providerRepo.findByNameContainingIgnoreCaseAndIsActiveAndCreatedBetweenAndSpecialityType(name, excludeInactive, today,tommorrow,providerSpeciality,pageable);
//            }else{
//                dbProviders = providerRepo.findByNameContainingIgnoreCaseAndCreatedBetweenAndSpecialityType(name, today,tommorrow,providerSpeciality,pageable);
//            }
//        }else{
//            if(excludeInactive){
//                dbProviders = providerRepo.findByNameContainingIgnoreCaseAndActiveAndSpecialityTypes(name, excludeInactive,providerSpeciality,pageable);
//            }else {
//                dbProviders = providerRepo.findByNameContainingIgnoreCaseOrSpecialityTypes(name,providerSpeciality,pageable);
//            }
//        }
//        dbProviders.forEach(provider -> {
//            providerResponseDTOList.add(mapper.providerToProviderResponse(provider));
//        });
//        providers = new PageImpl<>(providerResponseDTOList, pageable, dbProviders.getTotalElements());

        return providerPage.map(this::toProvider);

    }

    @Override
    public Page<Speciality> getSpecialityList(Pageable pageable) {
        Page<SpecialityEntity> specialityEntities = specialityRepo.findAll(pageable);
        return specialityEntities.map(this::toSpeciality);
    }

//    @Override
//    public String getProviderProfileImage(UUID providerId) throws RestoreSkillsException, IOException{
//        ProviderEntity providerEntity = getEntity(providerId);
//        String profilePic = "";
//        try{
//            profilePic = awsService.getObjectAsBase64(providerEntity.getAvtar(),bucketName);
//        }catch(Exception exception){
//            new RestoreSkillsException(ResponseCode.NOT_FOUND,"provider profile image not found.");
//        }
//        return profilePic;
//    }
//
//    @Override
//    public void deleteProviderProfileImage(UUID providerId) throws RestoreSkillsException, IOException{
//        ProviderEntity providerEntity = getEntity(providerId);
//        String key = awsService.deleteProvidersProfilePhoto(providerEntity.getAvtar(),bucketName);
//        if(key != null){
//            providerEntity.setAvtar(null);
//            save(providerEntity);
//        }
//    }

}
