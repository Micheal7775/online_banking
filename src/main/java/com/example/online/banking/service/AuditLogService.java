package com.example.online.banking.service;

import com.example.online.banking.ENum.AuditStatus;
import com.example.online.banking.model.AuditLog;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog logAction(
            User user,
            String action,
            String entityType,
            Long entityId,
            String description,
            String ipAddress,
            AuditStatus status) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setIpAddress(ipAddress);
        auditLog.setStatus(status);
        auditLog.setCreatedAt(LocalDateTime.now());

        return auditLogRepository.save(auditLog);
    }




    public List<AuditLog> getUserAuditLogs(Long userId) {

        return auditLogRepository
                .findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    public List<AuditLog> getAuditLogsByAction(String action) {

        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(action);
    }
}