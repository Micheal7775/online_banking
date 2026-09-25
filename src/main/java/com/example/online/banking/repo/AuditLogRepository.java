package com.example.online.banking.repo;

import com.example.online.banking.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<AuditLog> findByActionOrderByCreatedAtDesc(
            String action
    );
}