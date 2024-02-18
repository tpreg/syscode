package hu.syscode.address.service;

import static hu.syscode.address.mapper.MapStructConverter.MAPPER;

import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.syscode.address.dto.AddressDTO;
import hu.syscode.address.model.Address;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

	@Override
	public AddressDTO getAddressById(final UUID addressId) {
		final var addressRecord = new Address(addressId, "Kossuth utca 12.");
		return MAPPER.convertToDtoFromEntity(addressRecord);
	}

}
