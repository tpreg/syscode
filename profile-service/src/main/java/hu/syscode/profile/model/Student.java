package hu.syscode.profile.model;

import static hu.syscode.profile.util.Constants.OWASP_EMAIL_REGEX;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.Assert.notNull;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "student")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;

	@NotBlank
	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Email(regexp = OWASP_EMAIL_REGEX)
	@NotBlank
	@Column(name = "email_address", nullable = false, unique = true)
	private String emailAddress;

	public void update(final String fullName, final String emailAddress) {
		notNull(fullName, "fullName is null");
		notNull(emailAddress, "emailAddress is null");
		final var owaspEmailPattern = Pattern.compile(OWASP_EMAIL_REGEX);
		if (!owaspEmailPattern.matcher(emailAddress).matches()) {
			throw new IllegalArgumentException("Invalid email address");
		}
		if (Objects.equals(this.fullName, fullName) && Objects.equals(this.emailAddress, emailAddress)) {
			return;
		}
		this.fullName = fullName;
		this.emailAddress = emailAddress;
	}

}

