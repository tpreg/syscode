package hu.syscode.address.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.syscode.address.dto.AddressDTO;
import hu.syscode.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/addresses")
@Tag(name = "Address", description = "The Address API")
public class AddressController {

	private AddressService addressService;

	@GetMapping("/{id}")
	@Operation(summary = "Get an address by its id", description = "Provide an id to lookup specific address", operationId = "getAddressById", tags = {
			"Address" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the address", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = AddressDTO.class))),
			@ApiResponse(responseCode = "401", description = "You are not authorized"),

			@ApiResponse(responseCode = "404", description = "Address not found", content = @Content) })
	public ResponseEntity<AddressDTO> getAddressById(@PathVariable final UUID id) {
		final var addressById = this.addressService.getAddressById(id);
		return ResponseEntity.ok(addressById);
	}

}
