package ei.green_theater.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import ei.green_theater.domain.PurchaseHistory;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "TB_USER_INFO")
@DynamicUpdate
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ui_seq")
	private int uiSeq;

	@Column(name = "ui_user_id")
	private String uiUserId;

	@Column(name = "ui_user_pw")
	private String uiUserPw;

	@Column(name = "ui_user_classification", nullable = false)
	private String uiUserClassification;

	@Column(name = "ui_user_name")
	private String uiUserName;

	@Column(name = "ui_mobile_no")
	private String uiMobileNo;

	@Column(name = "ui_birthdate")
	private LocalDate uiBirthdate;

	@Column(name = "ui_gender")
	private String uiGender;

	@Column(name = "ui_join_date")
	private LocalDate uiJoinDate;

	@Column(name = "ui_withdrawal_date")
	private LocalDate uiWithdrawalDate = null;

	@Column(name = "ui_pw_due_date")
	private LocalDate uiPwDueDate;

	@Column(name = "ui_login_fail_count", nullable = false, columnDefinition = "int unsigned default 0")
	private int uiLoginFailCount = 0;

	@Column(name = "ui_inactivity_yn", nullable = false, columnDefinition = "char(1) default 'N'")
	private char uiInactivityYn = 'N';

	@Column(name = "ui_update_datetime")
	private LocalDateTime uiUpdateDatetime;

	@Column(name = "ui_registration_datetime")
	private LocalDateTime uiRegistrationDatetime;

//	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
//	@JoinTable(name = "TB_USER_INFO_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AgreementHistory> agreementHistories = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
	private List<PurchaseHistory> purchaseHistory;

	@Builder
//	public User(int uiSeq, String uiUserId, String uiUserPw, String uiUserName, String uiMobileNo,
//			LocalDate uiBirthdate, String uiGender, Set<Role> roles, String uiUserClassification) {
	public User(int uiSeq, String uiUserId, String uiUserPw, String uiUserName, String uiMobileNo,
			LocalDate uiBirthdate, String uiGender, String uiUserClassification) {
		this.uiSeq = uiSeq;
		this.uiUserId = uiUserId;
		this.uiUserPw = uiUserPw;
		this.uiUserName = uiUserName;
		this.uiMobileNo = uiMobileNo;
		this.uiBirthdate = uiBirthdate;
		this.uiGender = uiGender;
//		this.roles = roles;
		this.uiUserClassification = uiUserClassification;
	}

	@PrePersist
	public void onPrePersist() {
		this.uiJoinDate = LocalDate.now();
		this.uiPwDueDate = this.uiJoinDate.plusMonths(6);
		this.uiRegistrationDatetime = LocalDateTime.now();
		this.uiUpdateDatetime = this.uiRegistrationDatetime;
	}

	@PreUpdate
	public void onPreUpdate() {
		this.uiUpdateDatetime = LocalDateTime.now();
	}
	
	//isEnabled 탈퇴일자가 null이 아닐때로 수정
	public boolean isEnabled() {
	    return this.uiWithdrawalDate == null;
	}
	
	public void incrementLoginFailCount() {
	    this.uiLoginFailCount++;
	}
	
	public void resetLoginFailCount() {
	    this.uiLoginFailCount = 0;
	}
}
