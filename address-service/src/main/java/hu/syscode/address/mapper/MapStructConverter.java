package hu.syscode.address.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import hu.syscode.address.dto.AddressDTO;
import hu.syscode.address.model.Address;

@Mapper
public interface MapStructConverter {

	MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

	Address convertToEntityFromDto(final AddressDTO addressDTO);

	AddressDTO convertToDtoFromEntity(final Address address);

}
