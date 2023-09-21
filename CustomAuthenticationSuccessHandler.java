package ei.green_theater.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import ei.green_theater.domain.User;
import ei.green_theater.repository.UserRepository;  // UserRepository import 추가
import ei.green_theater.service.LoginLogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private LoginLogService loginLogService; 
    
    @Autowired
    private UserRepository userRepository;  // UserRepository 추가

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        
        // 로그인 로그 서비스에 성공한 로그 저장
        String ip = request.getRemoteAddr();
        loginLogService.logSuccessfulLogin(username, ip); 
        
        // 사용자 정보를 DB에서 조회
        User user = userRepository.findOneByUiUserId(username);
        if (user != null) {
            // 로그인 실패 횟수를 0으로 초기화
            user.setUiLoginFailCount(0);
            userRepository.save(user);
        }

        // 메인 페이지로 리다이렉트
        response.sendRedirect("/");
    }
}
