package com.restore.providerservice.service.Impl;

import com.restore.providerservice.repository.*;
import com.restore.providerservice.service.AvailabilityService;
import com.restore.core.dto.app.User;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.ProviderEntity;
import com.restore.core.entity.UserEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.providerservice.dto.Availability;
import com.restore.providerservice.dto.AvailabilityByDate;
import com.restore.providerservice.dto.AvailabilityByDateResponse;
import com.restore.providerservice.entity.AvailabilityByDateEntity;
import com.restore.providerservice.entity.AvailabilityEntity;
import com.restore.providerservice.entity.BlockDayEntity;
import com.restore.providerservice.entity.DayWiseAvailabilityEntity;
import com.restore.providerservice.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl extends AppService implements AvailabilityService {

    private final AvailabilityRepo availabilityRepo;
    private final ModelMapper modelMapper;
    private final LocationRepo locationRepo;
    private final DayWiseAvailabilityRepo dayWiseAvailabilityRepo;
    private final BlockDayRepo blockDayRepo;
    private final UserRepo userRepo;
    private final ProviderRepo providerRepository;
    private final AvailabilityByDateRepo availabilityByDateRepo;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepo availabilityRepo, ModelMapper modelMapper, LocationRepo locationRepo, DayWiseAvailabilityRepo dayWiseAvailabilityRepo, BlockDayRepo blockDayRepo, UserRepo userRepo, ProviderRepo providerRepository, AvailabilityByDateRepo availabilityByDateRepo) {
        this.availabilityRepo = availabilityRepo;
        this.modelMapper = modelMapper;
        this.locationRepo = locationRepo;
        this.dayWiseAvailabilityRepo = dayWiseAvailabilityRepo;
        this.blockDayRepo = blockDayRepo;
        this.userRepo = userRepo;
        this.providerRepository = providerRepository;
        this.availabilityByDateRepo = availabilityByDateRepo;
    }

    private ProviderEntity getCurrentProvider() throws RestoreSkillsException {
        User user = getCurrentUser();
        UserEntity userEntity = userRepo.findByEmail(user.getEmail()).orElseThrow(() -> new RestoreSkillsException(ResponseCode.IAM_ERROR, "Access denied! Logged in user does not exists"));
        ProviderEntity providerEntity = providerRepository.findByUserId(userEntity);
        if(providerEntity == null) throwError(ResponseCode.IAM_ERROR,"Access denied! Logged in user does not exists");
        return providerEntity;
    }


    @Override
    public void addAvailability(Availability availability) throws RestoreSkillsException {
        AvailabilityEntity existAvailabilityEntity = availabilityRepo.findByLocationEntityAndProviderEntity(availability.getLocationEntity(), availability.getProviderEntity());
        if(existAvailabilityEntity != null) throwError(ResponseCode.BAD_REQUEST, "Invalid request! Already availability existed for given location and provider");

        AvailabilityEntity availabilityEntity = modelMapper.map(availability, AvailabilityEntity.class);
        setBookingWindowDates(availability, availabilityEntity);
        availabilityEntity.setUuid(UUID.randomUUID());
        Set<DayWiseAvailabilityEntity> dayWiseAvailabilityEntitySet = availability.getDayWiseAvailabilityEntitySet().stream().map(dayWiseAvailability -> modelMapper.map(dayWiseAvailability, DayWiseAvailabilityEntity.class)).collect(Collectors.toSet());
        Set<BlockDayEntity> blockDayEntitySet = availability.getBlockDayEntitySet().stream().map(blockDay -> modelMapper.map(blockDay, BlockDayEntity.class)).collect(Collectors.toSet());
        availabilityEntity.setDayWiseAvailabilityEntitySet(dayWiseAvailabilityEntitySet);
        availabilityEntity.setBlockDayEntitySet(blockDayEntitySet);
        availabilityRepo.save(availabilityEntity);
    }

    @Override
    public void setBookingWindowDates(Availability availability, AvailabilityEntity availabilityEntity) throws RestoreSkillsException {
        availabilityEntity.setBookingWindowStart(LocalDate.now());
        switch (availability.getBookingWindow()) {
            case ONE_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(1));
            case TWO_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(2));
            case THREE_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(3));
            case FOUR_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(4));
            case FIVE_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(5));
            case SIX_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(6));
            case TWELVE_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(12));
            case TWENTY_SIX_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(26));
            case FIFTY_TWO_WEEK -> availabilityEntity.setBookingWindowEnd(LocalDate.now().plusWeeks(50));
            default -> throwError(ResponseCode.BAD_REQUEST, "Availability booking window must be selected");
        }
    }

    @Override
    public AvailabilityEntity getByLocationUuid(UUID location_uuid) throws RestoreSkillsException {
        Optional<LocationEntity> locationEntity = locationRepo.findByUuid(location_uuid);
        if(locationEntity.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Location UUID does not exist!");
        }
        return availabilityRepo.findByLocationEntity(locationEntity.get());
    }

    @Override
    public void editAvailability(UUID availability_uuid, Availability availability) throws RestoreSkillsException {
        Optional<AvailabilityEntity> availabilityEntityOptional = availabilityRepo.findByUuid(availability_uuid);
        if(availabilityEntityOptional.isEmpty()) throwError(ResponseCode.BAD_REQUEST, "Availability for given UUID does not exists");
        AvailabilityEntity availabilityEntity = availabilityEntityOptional.get();
        setBookingWindowDates(availability, availabilityEntity);
        Set<DayWiseAvailabilityEntity> dayWiseAvailabilityEntitySet = availability.getDayWiseAvailabilityEntitySet().stream().map(dayWiseAvailability -> modelMapper.map(dayWiseAvailability, DayWiseAvailabilityEntity.class)).collect(Collectors.toSet());
        Set<BlockDayEntity> blockDayEntitySet = availability.getBlockDayEntitySet().stream().map(blockDay -> modelMapper.map(blockDay, BlockDayEntity.class)).collect(Collectors.toSet());
        availabilityEntity.setDayWiseAvailabilityEntitySet(dayWiseAvailabilityEntitySet);
        availabilityEntity.setBlockDayEntitySet(blockDayEntitySet);
        availabilityRepo.save(availabilityEntity);
    }

    @Override
    public AvailabilityByDateEntity addAvailabilityByDate(AvailabilityByDate availability) throws RestoreSkillsException {
        User user = getCurrentUser();
        Optional<UserEntity> userEntity = userRepo.findByEmail(user.getEmail());
        if(userEntity.isPresent()) {
            ProviderEntity providerEntity = providerRepository.findByUserId(userEntity.get());
            Optional<LocationEntity> locationEntity = locationRepo.findByUuid(availability.getLocationUuid());
            if (!locationEntity.isPresent()) throwError(ResponseCode.BAD_REQUEST, "Invalid request! Location for given UUID does not exists!");

            AvailabilityByDateEntity availabilityByDateEntity = modelMapper.map(availability, AvailabilityByDateEntity.class);
            availabilityByDateEntity.setLocationEntity(locationEntity.get());
            availabilityByDateEntity.setProviderEntity(providerEntity);
            return availabilityByDateRepo.save(availabilityByDateEntity);
        }
        throwError(ResponseCode.IAM_ERROR, "Access denied! Logged in user does not exists");
        return null;
    }

    @Override
    public List<AvailabilityByDateResponse> getAvailabilityByDateByLocationUuid(UUID locationUuid, LocalDate startDate) throws RestoreSkillsException {
        ProviderEntity providerEntity = getCurrentProvider();
        LocationEntity locationEntity = locationRepo.findByUuid(locationUuid).orElseThrow(() -> new RestoreSkillsException(ResponseCode.BAD_REQUEST, "Location does not exists for given UUID"));
        AvailabilityEntity availabilityEntity = availabilityRepo.findByLocationEntityAndProviderEntity(locationEntity, providerEntity);
        if(availabilityEntity == null) return null;

        LocalDate availabilityStartDate = availabilityEntity.getBookingWindowStart();
        LocalDate availabilityEndDate = availabilityEntity.getBookingWindowEnd();
        Set<BlockDayEntity> blockDayEntitySet = availabilityEntity.getBlockDayEntitySet();
        List<AvailabilityByDateResponse> availabilityByDateResponseList = new ArrayList<>();
        while(startDate.isBefore(startDate.plusDays(36))) {
            if(startDate.isBefore(availabilityStartDate) || startDate.isAfter(availabilityEndDate)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            boolean isBlockDay = false;
            for(BlockDayEntity blockDay: blockDayEntitySet) {
                if((startDate.isBefore(blockDay.getToDate()) &&
                        startDate.isAfter(blockDay.getFromDate())) ||
                        startDate.equals(blockDay.getFromDate()) ||
                        startDate.equals(blockDay.getToDate())) {
                    isBlockDay = true;
                    break;
                }
            }
            if(isBlockDay) {
                startDate = startDate.plusDays(1);
                continue;
            }

            DayOfWeek dayOfWeek = startDate.getDayOfWeek();
            Optional<DayWiseAvailabilityEntity> dayWiseTime = availabilityEntity.getDayWiseAvailabilityEntitySet().stream().filter(day -> day.getDayOfWeek().equals(dayOfWeek)).findFirst();
            if(dayWiseTime.isPresent()) {
                AvailabilityByDateResponse availabilityByDateResponse = new AvailabilityByDateResponse();
                availabilityByDateResponse.setDate(startDate);
                Set<AvailabilityByDateResponse.TimeLog> timeLogSet = new HashSet<>();
                timeLogSet.add(new AvailabilityByDateResponse.TimeLog(dayWiseTime.get().getStartTime(), dayWiseTime.get().getEndTime()));
                availabilityByDateResponse.setTimeLogSet(timeLogSet);
                availabilityByDateResponseList.add(availabilityByDateResponse);
            }
            startDate = startDate.plusDays(1);
        }
        return availabilityByDateResponseList;
    }
}
