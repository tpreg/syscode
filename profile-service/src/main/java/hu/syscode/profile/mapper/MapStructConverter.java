package hu.syscode.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.model.Student;

@Mapper
public interface MapStructConverter {

	MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

	Student toEntity(final StudentDTO studentDTO);

	StudentDTO toDto(final Student student);

}
