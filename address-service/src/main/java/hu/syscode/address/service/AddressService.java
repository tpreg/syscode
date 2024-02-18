package hu.syscode.address.service;

import java.util.UUID;

import hu.syscode.address.dto.AddressDTO;

public interface AddressService {

	AddressDTO getAddressById(UUID addressId);

}
