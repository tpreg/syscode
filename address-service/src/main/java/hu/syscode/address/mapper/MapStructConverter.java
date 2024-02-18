package hu.syscode.address.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import hu.syscode.address.dto.AddressDTO;
import hu.syscode.address.model.Address;

@Mapper
public interface MapStructConverter {

	MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

	Address toEntity(final AddressDTO addressDTO);

	AddressDTO toDto(final Address student);

}
