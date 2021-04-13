package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.Student;

public interface StudentRepository extends CrudRepository<Student, String> {
    Student findByUserName(String userName);


}
