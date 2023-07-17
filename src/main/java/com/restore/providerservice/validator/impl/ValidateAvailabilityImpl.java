package com.restore.providerservice.validator.impl;

import com.restore.providerservice.service.AvailabilityService;
import com.restore.core.dto.app.User;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.ProviderEntity;
import com.restore.core.entity.UserEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.providerservice.dto.Availability;
import com.restore.providerservice.entity.AvailabilityEntity;
import com.restore.providerservice.entity.BlockDayEntity;
import com.restore.providerservice.entity.DayWiseAvailabilityEntity;
import com.restore.providerservice.repository.AvailabilityRepo;
import com.restore.providerservice.repository.LocationRepo;
import com.restore.providerservice.repository.ProviderRepo;
import com.restore.providerservice.repository.UserRepo;
import com.restore.providerservice.validator.ValidateAvailability;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValidateAvailabilityImpl extends AppService implements ConstraintValidator<ValidateAvailability, Availability> {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProviderRepo providerRepo;
    @Autowired
    private LocationRepo locationRepo;
    @Autowired
    private AvailabilityRepo availabilityRepo;
    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private ModelMapper modelMapper;


    @SneakyThrows
    @Override
    public boolean isValid(Availability availability, ConstraintValidatorContext constraintValidatorContext) {
        User user = getCurrentUser();
        Optional<UserEntity> userEntity = userRepo.findByEmail(user.getEmail());
        if(userEntity.isEmpty()) {
            createViolation(constraintValidatorContext, "Access denied! Logged in user does not exists in system! Please contact support team");
            return false;
        }
        ProviderEntity providerEntity = providerRepo.findByUserId(userEntity.get());
        if(providerEntity==null) {
            createViolation(constraintValidatorContext, "Invalid request! Provider does not exists");
            return false;
        }
        availability.setProviderEntity(providerEntity);
        Optional<LocationEntity> locationEntity = locationRepo.findByUuid(availability.getLocationUuid());
        if(locationEntity.isEmpty()) {
            createViolation(constraintValidatorContext, "Invalid request! Location does not exists");
            return false;
        }
        availability.setLocationEntity(locationEntity.get());
        return !checkTimeLineConstraint(availability, constraintValidatorContext);
    }

    private boolean checkTimeLineConstraint(Availability availability, ConstraintValidatorContext constraintValidatorContext) throws RestoreSkillsException {
        AvailabilityEntity availabilityEntity = new AvailabilityEntity();
        availabilityService.setBookingWindowDates(availability, availabilityEntity);
        availabilityEntity.setBookingWindow(availability.getBookingWindow());
        availabilityEntity.setBookingWindowTimeZone(availability.getBookingWindowTimeZone());
        Set<DayWiseAvailabilityEntity> dayWiseAvailabilityEntitySet = availability.getDayWiseAvailabilityEntitySet().stream().map(dayWiseAvailability -> modelMapper.map(dayWiseAvailability, DayWiseAvailabilityEntity.class)).collect(Collectors.toSet());
        Set<BlockDayEntity> blockDayEntitySet = availability.getBlockDayEntitySet().stream().map(blockDay -> modelMapper.map(blockDay, BlockDayEntity.class)).collect(Collectors.toSet());
        availabilityEntity.setDayWiseAvailabilityEntitySet(dayWiseAvailabilityEntitySet);
        availabilityEntity.setBlockDayEntitySet(blockDayEntitySet);
        if(!validateOverlapCondition(availability, availabilityEntity)) {
            createViolation(constraintValidatorContext, "Invalid request! Overlapping with other location! Please change availability time");
            return true;
        }
        return false;
    }

    private boolean validateOverlapCondition(Availability availability, AvailabilityEntity L2) {
        int isValid = 0;
        List<AvailabilityEntity> availabilityEntityList = availabilityRepo.findAllByProviderEntityAndLocationEntityNot(availability.getProviderEntity(), availability.getLocationEntity());
        for(AvailabilityEntity L1: availabilityEntityList) {
            LocalDate L1StartDate = L1.getBookingWindowStart();
            while(L1StartDate.compareTo(L1.getBookingWindowEnd())<=0) {
                boolean isL1BlockDay = false;
                for(BlockDayEntity blockDay: L1.getBlockDayEntitySet()) {
                    if((L1StartDate.isBefore(blockDay.getToDate()) &&
                            L1StartDate.isAfter(blockDay.getFromDate())) ||
                            L1StartDate.equals(blockDay.getFromDate()) ||
                            L1StartDate.equals(blockDay.getToDate())) {
                        isL1BlockDay = true;
                        break;
                    }
                }
                if(isL1BlockDay) {
                    L1StartDate = L1StartDate.plusDays(1);
                    continue;
                }

                //TODO: compare with L2 dates
                LocalDate L2StartDate = L2.getBookingWindowStart();
                while(L2StartDate.compareTo(L2.getBookingWindowEnd())<=0) {
                    boolean isL2BlockDay = false;
                    for(BlockDayEntity blockDay: L2.getBlockDayEntitySet()) {
                        if((L2StartDate.isBefore(blockDay.getToDate()) &&
                                L2StartDate.isAfter(blockDay.getFromDate())) ||
                                L2StartDate.equals(blockDay.getFromDate()) ||
                                L2StartDate.equals(blockDay.getToDate())) {
                            isL2BlockDay = true;
                            break;
                        }
                    }
                    if(isL2BlockDay) {
                        L2StartDate = L2StartDate.plusDays(1);
                        continue;
                    }

                    //check start and end time overlapping
                    DayOfWeek L1DayOfWeek = L1StartDate.getDayOfWeek();
                    Optional<DayWiseAvailabilityEntity> L1DayWiseTime = L1.getDayWiseAvailabilityEntitySet().stream().filter(day -> day.getDayOfWeek().equals(L1DayOfWeek)).findFirst();
                    if(L1DayWiseTime.isPresent()) {
                        DayOfWeek L2DayOfWeek = L2StartDate.getDayOfWeek();
                        Optional<DayWiseAvailabilityEntity> L2DayWiseTime = L2.getDayWiseAvailabilityEntitySet().stream().filter(day -> day.getDayOfWeek().equals(L2DayOfWeek)).findFirst();
                        if(L2DayWiseTime.isPresent()) {
                            // Convert the input dates and times to LocalDateTime objects using the specified time zones
                            LocalDateTime L1Start = LocalDateTime.of(L1StartDate, L1DayWiseTime.get().getStartTime());
                            LocalDateTime L1End = LocalDateTime.of(L1StartDate, L1DayWiseTime.get().getEndTime());

                            // Convert the LocalDateTime objects to the same time zone
                            LocalDateTime L1ConvertedStartTime = L1Start.atZone(ZoneId.of(L1.getBookingWindowTimeZone().getValue())).withZoneSameInstant(ZoneId.of(L2.getBookingWindowTimeZone().getValue())).toLocalDateTime();
                            LocalDateTime L1ConvertedEndTime = L1End.atZone(ZoneId.of(L1.getBookingWindowTimeZone().getValue())).withZoneSameInstant(ZoneId.of(L2.getBookingWindowTimeZone().getValue())).toLocalDateTime();
                            LocalDateTime L2ConvertedStartTime = LocalDateTime.of(L2StartDate, L2DayWiseTime.get().getStartTime());
                            LocalDateTime L2ConvertedEndTime = LocalDateTime.of(L2StartDate, L2DayWiseTime.get().getEndTime());

                            if(L1ConvertedStartTime.isBefore(L2ConvertedEndTime) && L1ConvertedEndTime.isAfter(L2ConvertedStartTime)) isValid++;
                        }
                    }
                    L2StartDate = L2StartDate.plusDays(1);
                }
                //TODO: end of parent loop
                L1StartDate = L1StartDate.plusDays(1);
            }
        }
        return isValid == 0;
    }

    private void createViolation(ConstraintValidatorContext context, String violationConstant) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(violationConstant).addConstraintViolation();
    }
}
