package ei.green_theater.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import ei.green_theater.domain.User;
import ei.green_theater.repository.UserRepository;
import ei.green_theater.service.LoginLogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("uiUserId");
        String ip = request.getRemoteAddr();
        loginLogService.logFailedLogin(username, ip); 
        User user = userRepository.findOneByUiUserId(username); // 사용자를 찾습니다.
        
        if (user != null) {
            user.setUiLoginFailCount(user.getUiLoginFailCount() + 1);
            userRepository.save(user);
        }

        // 에러 메시지 인코딩
        response.sendRedirect("/login?error");
    }

}
