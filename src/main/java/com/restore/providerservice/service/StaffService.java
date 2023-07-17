package com.restore.providerservice.service;

import com.restore.core.dto.app.Staff;
import com.restore.core.dto.app.User;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface StaffService {
    void createStaffUser(User user,UUID providerGroupId) throws RestoreSkillsException, IOException;
    Staff get(UUID staffId) throws RestoreSkillsException;
    Staff getByUserId(UUID userId) throws RestoreSkillsException;
    Object getAll(Pageable pageable) throws RestoreSkillsException;
    void updateStatus(UUID userId, boolean status)throws RestoreSkillsException;
    void remove(UUID userId) throws RestoreSkillsException;
    void update(Staff staff, String schemaName) throws RestoreSkillsException;
}
