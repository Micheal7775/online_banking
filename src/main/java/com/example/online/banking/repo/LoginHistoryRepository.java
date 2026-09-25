package com.example.online.banking.repo;

import com.example.online.banking.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository
        extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByUserUserIdOrderByLoginTimeDesc(
            Long userId
    );
}