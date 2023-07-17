package com.restore.providerservice.service.Impl;

import com.restore.core.dto.app.LocationHour;
import com.restore.core.dto.app.Speciality;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.LocationHoursEntity;
import com.restore.core.entity.ProviderGroupAddressEntity;
import com.restore.core.entity.SpecialityEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.core.service.AwsService;
import com.restore.providerservice.dto.Location;
import com.restore.providerservice.repository.LocationRepo;
import com.restore.providerservice.repository.SpecialityRepo;
import com.restore.providerservice.service.LocationService;
import org.keycloak.common.util.ObjectUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl extends AppService implements LocationService {

    private final LocationRepo locationRepo;
    private final ModelMapper modelMapper;
    private final AwsService awsService;
    private final SpecialityRepo specialityRepo;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public LocationServiceImpl(LocationRepo locationRepo, ModelMapper modelMapper, AwsService awsService, SpecialityRepo specialityRepo) {
        this.locationRepo = locationRepo;
        this.modelMapper = modelMapper;
        this.awsService = awsService;
        this.specialityRepo = specialityRepo;
    }

    private Set<SpecialityEntity> toSpecialityEntity(Set<Speciality> specialities) {
        return specialities.stream().map(speciality ->
                specialityRepo.findByName(speciality.getName()).get()).collect(Collectors.toSet());
    }

    private Set<LocationHoursEntity> toLocationHoursEntity(Set<LocationHour> locationHours) throws RestoreSkillsException {
        return locationHours.stream().map(practiceHour -> modelMapper.map(practiceHour, LocationHoursEntity.class)).collect(Collectors.toSet());
    }

    private LocationEntity getLocation(Long id) throws RestoreSkillsException {
        return locationRepo.findById(id).orElseThrow(() -> new RestoreSkillsException(ResponseCode.DB_ERROR, "Cannot find location"));
    }

    @Override
    public List<Location> getLocationList() throws IOException {
        List<LocationEntity> locationEntityList = locationRepo.findAllByActiveIsTrueAndArchiveIsFalse();
        List<Location> locationList = new ArrayList<>();
        locationEntityList.forEach(locationEntity -> {
            Location location = modelMapper.map(locationEntity, Location.class);
            if (!ObjectUtil.isBlank(location.getAvatar())) {
                try {
                    location.setAvatar(awsService.getPreSignedUrl(location.getAvatar()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            locationList.add(location);
        });
        return locationList;
    }

    @Override
    public void addLocation(com.restore.core.dto.app.Location location) throws RestoreSkillsException {
        LocationEntity locationEntity = modelMapper.map(location, LocationEntity.class);

        try {
            String avatarKey = null;
            if (location.getAvatar() != null && !location.getAvatar().isEmpty())
                avatarKey = awsService.uploadLocationProfile(location.getAvatar());
            locationEntity.setLocationHoursEntities(toLocationHoursEntity(location.getLocationHours()));
            locationEntity.setLocationId(location.getLocationCode());
            locationEntity.setEmailId(location.getEmail());
            locationEntity.setFaxId(location.getFax());
            locationEntity.setAvatar(avatarKey);
            locationEntity.setUuid(UUID.randomUUID());
            locationRepo.save(locationEntity);
        } catch (Exception e) {
            throwError(ResponseCode.DB_ERROR, "Failed to add Location " + location.getName());
        }
    }

    @Override
    public Page<com.restore.core.dto.app.Location> getAllLocationsByProvider(Pageable page) throws RestoreSkillsException {
        page = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.Direction.DESC, "created");
        Page<LocationEntity> locationEntityPage = locationRepo.findAll(page);
        return locationEntityPage.map(locationEntity -> {
            com.restore.core.dto.app.Location location = modelMapper.map(locationEntity, com.restore.core.dto.app.Location.class);
            if (!ObjectUtil.isBlank(location.getAvatar())) {
                try {
                    location.setAvatar(awsService.getPreSignedUrl(location.getAvatar()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return location;
        });
    }

    @Override
    public void updateLocation(Long id, com.restore.core.dto.app.Location location) throws RestoreSkillsException, IOException {
        LocationEntity locationEntity = getLocation(id);

        locationEntity.setName(location.getName());
        locationEntity.setContactNumber(location.getContactNumber());
        locationEntity.setInformation(location.getInformation());

        if (Objects.isNull(location.getAvatar()) && Objects.nonNull(locationEntity.getAvatar())) {
            awsService.deleteProfilePhoto(locationEntity.getAvatar(), bucketName);
            locationEntity.setAvatar(null);
        }
        locationEntity.setPhysicalAddress(modelMapper.map(location.getPhysicalAddress(), ProviderGroupAddressEntity.class));
        locationEntity.setBillingAddress(modelMapper.map(location.getBillingAddress(), ProviderGroupAddressEntity.class));
        locationEntity.setLocationHoursEntities(location.getLocationHours().stream().map(locationHour -> modelMapper.map(locationHour, LocationHoursEntity.class)).collect(Collectors.toSet()));

        try {
            modelMapper.map(locationRepo.save(locationEntity), Location.class);
        } catch (Exception e) {
            throwError(ResponseCode.DB_ERROR, e.getMessage());
        }
    }

    @Override
    public void updateStatus(Long locationId, boolean active) throws RestoreSkillsException {
        LocationEntity locationEntity = getLocation(locationId);
        try {
            locationEntity.setActive(active);
            locationRepo.save(locationEntity);
        } catch (Exception e) {
            throwError(ResponseCode.DB_ERROR, e.getMessage());
        }
    }

    @Override
    public Location getLocationByLocationId(Long locationId) throws RestoreSkillsException {
        return modelMapper.map(getLocation(locationId), Location.class);
    }
}
