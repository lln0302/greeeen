package ei.green_theater.security;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.AllArgsConstructor;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; // Add this line

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().ignoringAntMatchers("/handlePayment") //결제 정보 csrf 토큰 무시
            .and().authorizeRequests()
            .antMatchers("/css/**", "/js/**", "/img/**", "/font/**", "/handlePayment").permitAll() 
            .antMatchers("/changePassword","/changePassword/change","withdrawal", "/api/withdrawal", "/myPage", "/watching", "/reservation").authenticated()
            .antMatchers("/", "/main", "/signUp", "/login", "/emailConfirm", "/checkId", "/mail/mailConfirm",
            	    "/verifyCode", "/findId/find", "/findId", "/findPassword", "/sendTemporaryPassword", "/sendTemporaryPassword/send", "/lookAround",
            	    "/service", "/youthPolicy", "/privacy").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login").loginProcessingUrl("/doLogin")
            .usernameParameter("fullUserId")
            .passwordParameter("uiUserPw")
            .failureUrl("/login?error=true")  
            .failureHandler(customAuthenticationFailureHandler)  
            .successHandler(customAuthenticationSuccessHandler)
            .permitAll()
            .and().logout().permitAll().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/main").deleteCookies("JSESSIONID").invalidateHttpSession(true)
            .clearAuthentication(true);

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
