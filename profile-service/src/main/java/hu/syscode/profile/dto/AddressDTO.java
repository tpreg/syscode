package hu.syscode.profile.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(UUID id, @NotBlank String address) {

}
