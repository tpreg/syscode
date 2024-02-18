package hu.syscode.profile.dto;

import static hu.syscode.profile.util.Constants.OWASP_EMAIL_REGEX;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StudentDTO(UUID id, @NotBlank String fullName, @Email(regexp = OWASP_EMAIL_REGEX) @NotBlank String emailAddress) {

}

