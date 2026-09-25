package com.example.online.banking.service;

import com.example.online.banking.ENum.LoginStatus;
import com.example.online.banking.model.LoginHistory;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public void recordLogin(
            User user,
            String ipAddress,
            String deviceInfo) {

        LoginHistory history = new LoginHistory();

        history.setUser(user);
        history.setLoginTime(LocalDateTime.now());
        history.setLoginStatus(LoginStatus.SUCCESS);
        history.setIpAddress(ipAddress);
        history.setDeviceInfo(deviceInfo);
        history.setCreatedAt(LocalDateTime.now());

        loginHistoryRepository.save(history);

        log.info(
                "Login history recorded for user: {}",
                user.getUsername()
        );
    }

    public List<LoginHistory> getLoginHistory(Long userId) {

        return loginHistoryRepository
                .findByUserUserIdOrderByLoginTimeDesc(userId);
    }
}