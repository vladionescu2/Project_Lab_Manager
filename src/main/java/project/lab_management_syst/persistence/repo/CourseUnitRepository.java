package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.CourseUnit;

import java.util.List;

public interface CourseUnitRepository extends CrudRepository<CourseUnit, String> {
    List<CourseUnit> findByStaffMembers(String staffId);
}
