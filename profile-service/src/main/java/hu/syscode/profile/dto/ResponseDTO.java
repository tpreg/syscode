package hu.syscode.profile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ResponseDTO(@Valid @NotNull StudentDTO studentDTO, @Valid @NotNull AddressDTO addressDTO) {

}
