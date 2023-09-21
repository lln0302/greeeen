package ei.green_theater.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ei.green_theater.service.CommonCodeService;
import ei.green_theater.service.UserService;
import ei.green_theater.config.ConfigProperties;
import ei.green_theater.domain.AgreementHistory;
import ei.green_theater.domain.CommonCode;
import ei.green_theater.dto.UserDto;
import ei.green_theater.repository.AgreementHistoryRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class LoginController {
	private final UserService userService;
	private final AgreementHistoryRepository agreementHistoryRepository;
	private ConfigProperties configProperties;
	
	@Autowired
	private CommonCodeService commonCodeService; // CommonCodeService 주입

	@GetMapping("/login")
	public String login(HttpServletRequest request) {
		return "login";
	}


	@GetMapping("/signUp")
	public String signUp(Model model) {
		
	    model.addAttribute("userDto", new UserDto());
	    model.addAttribute("apiBaseUrl", configProperties.getApiBaseUrl());
	    return "signUp";
	}


	@PostMapping("/signUp")
	public String signUp(@ModelAttribute("userDto") UserDto userDto, @RequestParam("emailDomain") String emailDomain,
			@RequestParam("agreement1") boolean agreement1, @RequestParam("agreement2") boolean agreement2,
			HttpServletRequest request, Model model) {

		String id = userDto.getUiUserId().trim();

		// 선택된 이메일 도메인과 아이디를 합칩니다.
		if (emailDomain.equals("custom")) {
			emailDomain = request.getParameter("customEmailDomain");
		}
		String fullId = id + "@" + emailDomain;
		if (fullId.isEmpty()) {
			// 여기에 잘못된 입력값에 대한 처리를 추가
			return "signUp";
		}

		// 전화번호 접두사와 주요 부분을 가져와 합칩니다.
		String phonePrefix = request.getParameter("uiMobileNoPrefix");
		String phoneMain = userDto.getUiMobileNo().trim();
		String fullPhoneNumber = phonePrefix + phoneMain;

		userDto.setUiUserId(fullId); // id 필드에 전체 이메일 주소 저장
		userDto.setUiMobileNo(fullPhoneNumber); // phone 필드에 전체 전화번호 저장
		// 전체 전화번호 설정 부분 아래에 분류 설정 코드 추가
//	    userDto.setUiUserClassification("1101");

		CommonCode userClassification = commonCodeService.getUserClassificationCode();
		if (userClassification != null) {
			userDto.setUiUserClassification(
					userClassification.getId().getCcCode1() + userClassification.getId().getCcCode2());
		} else {
			// 에러 처리: 원하는 CommonCode가 찾아지지 않았을 때의 처리
		}
		userService.insert(userDto);
		// 체크한 약관에 대한 AgreementHistory 기록
		if (agreement1) {
			AgreementHistory agreementHistory1 = new AgreementHistory();
			agreementHistory1.setAhUserId(fullId);
			agreementHistory1.setAhAiSeq(1); // ai_seq 1이 서비스 이용 약관
			agreementHistory1.setAhAgreeDatetime(LocalDateTime.now());
			agreementHistoryRepository.save(agreementHistory1);
		}

		if (agreement2) {
			AgreementHistory agreementHistory2 = new AgreementHistory();
			agreementHistory2.setAhUserId(fullId);
			agreementHistory2.setAhAiSeq(2); // ai_seq 2가 개인정보 처리 방침
			agreementHistory2.setAhAgreeDatetime(LocalDateTime.now());
			agreementHistoryRepository.save(agreementHistory2);
		}
		model.addAttribute("registeredId", fullId); // 모델에 아이디 추가

		return "signUpComplete";
	}

	@GetMapping("/checkId")
	@ResponseBody
	public String checkId(@RequestParam("id") String id) {
		// 아이디가 null, 공백, 혹은 완전히 비어있는지 확인
		if (id == null || id.trim().isEmpty()) {
			return "아이디는(은) 공백으로 시작하거나 비어있을 수 없습니다.";
		}

		// 아이디가 공백으로 시작하는지 확인
		if (id.startsWith(" ")) {
			return "아이디는(은) 공백으로 시작할 수 없습니다.";
		}

		// 이메일에 한국어를 포함하지 않는지 확인
		if (id.matches("[\\uAC00-\\uD7A3]+")) {
			return "이메일 아이디는 한글을 포함할 수 없습니다.";
		}

		if (userService.isIdDuplicate(id)) {
			return "아이디가 중복됩니다";
		} else {
			return "사용할 수 있는 아이디입니다";
		}
	}

}
