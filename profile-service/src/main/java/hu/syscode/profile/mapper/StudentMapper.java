package hu.syscode.profile.mapper;

import org.mapstruct.Mapper;

import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.model.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

	Student convertToEntityFromDto(final StudentDTO studentDTO);

	StudentDTO convertToDtoFromEntity(final Student student);

}
