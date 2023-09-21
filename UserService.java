package ei.green_theater.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ei.green_theater.repository.UserRepository;
import ei.green_theater.repository.WithdrawalLogRepository;
import ei.green_theater.domain.User;
import ei.green_theater.domain.WithdrawalLog;
import ei.green_theater.dto.UserDto;
import ei.green_theater.dto.WithdrawalLogDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WithdrawalLogRepository withdrawalLogRepository;  // 추가
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void insert(UserDto userDto) {
    	 // 이메일(아이디) 유효성 검사
        if (!isValidEmail(userDto.getUiUserId())) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다.");
        }

        // 비밀번호 유효성 검사
        if (!isValidPassword(userDto.getUiUserPw())) {
            throw new IllegalArgumentException("비밀번호는 특수문자, 숫자, 영문 조합 8자리 이상이어야 합니다.");
        }
    	
    	
    	userDto.setUiUserPw(bCryptPasswordEncoder.encode(userDto.getUiUserPw()));

        userRepository.save(userDto.toEntity());
    }
    
 // 이메일 유효성 검사 메서드
    private boolean isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        return email.matches(regex) && !email.matches("[\\uAC00-\\uD7A3]+") && email.length() <= 100;
    }

    // 비밀번호 유효성 검사 메서드
    private boolean isValidPassword(String password) {
        // 8자리 이상, 특수문자, 숫자, 영문 조합
        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,}$";
        return password.matches(regex);
    }
    
    public boolean isIdDuplicate(String id) {
        User user = userRepository.findOneByUiUserId(id);
        return user != null;
    }

    public boolean changePassword(String id, String currentPassword, String newPassword) {
        User user = userRepository.findOneByUiUserId(id);
        if (user == null) {
            return false;
        }

        if (!bCryptPasswordEncoder.matches(currentPassword, user.getUiUserPw())) {
            return false;
        }

        user.setUiUserPw(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public boolean isCurrentPasswordValid(String id, String currentPassword) {
        User user = userRepository.findOneByUiUserId(id);
        if (user == null) {
            return false;                                            
        }
        return bCryptPasswordEncoder.matches(currentPassword, user.getUiUserPw());
    }
    
    
    @Transactional
    public boolean withdrawUser(String email, String password) {
        User user = userRepository.findOneByUiUserId(email);
        
        if (user != null && bCryptPasswordEncoder.matches(password, user.getUiUserPw())) {
            // 사용자 정보를 탈퇴 상태로 업데이트
            user.setUiWithdrawalDate(LocalDate.now());
            user.setUiInactivityYn('Y');
            userRepository.save(user);
            
            // 탈퇴 로그 저장
            WithdrawalLog log = new WithdrawalLog();
            log.setWlUserId(email);
            log.setWlStatusCode(2);  // 직접 탈퇴 코드
            log.setWlLoginDatetime(LocalDateTime.now());
            withdrawalLogRepository.save(log);
            
            // 로그아웃 처리
            logout();
            
            return true;
        }
        return false;
    }
    public void saveWithdrawalLog(WithdrawalLogDto dto) {
        WithdrawalLog log = dto.toEntity();
        withdrawalLogRepository.save(log);
    }
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }
}
