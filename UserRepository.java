package ei.green_theater.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; 
import ei.green_theater.domain.User;
 
public interface UserRepository extends JpaRepository<User, String> {
    User findOneByUiUserId(String uiUserId);
	
    Optional<User> findByUiUserNameAndUiMobileNoAndUiBirthdateAndUiGender(String uiUserName, String uiMobileNo, LocalDate uiBirthdate, String uiGender);

}
