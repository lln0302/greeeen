package ei.green_theater.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import ei.green_theater.domain.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String uiUserId;
    private String uiUserPw;
    private String uiUserName;
    private String uiMobileNo;
    private LocalDate uiBirthdate; 
    private String uiGender;
    private Set<Long> roles;
    private String uiUserClassification;
    // birthdate 필드에 대한 Setter 메서드를 변경
    public void setUiBirthdate(String birthdateStr) {
        this.uiBirthdate = LocalDate.parse(birthdateStr, DateTimeFormatter.ISO_DATE);
    }

    public UserDto(String uiUserId) {
        this.uiUserId = uiUserId;
    }

    // 데이터베이스 날짜를 원하는 형식으로 표시하는 메서드 추가
    public String getFormattedBirthdate() {
        if (uiBirthdate == null) {
            return "";
        }
        return uiBirthdate.toString();
    }
    
    public String getUiUserClassification() {
        return uiUserClassification;
    }
    public void setUiUserClassification(String uiUserClassification) {
        this.uiUserClassification = uiUserClassification;
    }

    public User toEntity() {
        return User.builder().uiUserId(uiUserId).uiUserPw(uiUserPw).uiUserName(uiUserName).uiMobileNo(uiMobileNo).uiBirthdate(uiBirthdate).uiGender(uiGender).uiUserClassification(uiUserClassification).build();
    }
}
