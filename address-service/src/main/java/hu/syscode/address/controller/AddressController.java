package hu.syscode.address.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.syscode.address.dto.AddressDTO;
import hu.syscode.address.service.AddressService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/addresses/")
@AllArgsConstructor
public class AddressController {

	private AddressService addressService;

	@GetMapping("{id}")
	public ResponseEntity<AddressDTO> getAddressById(@PathVariable final UUID id) {
		return ResponseEntity.ok(this.addressService.getAddressById(id));
	}

}
