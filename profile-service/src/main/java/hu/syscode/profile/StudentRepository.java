package hu.syscode.profile;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.syscode.profile.model.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {

}
