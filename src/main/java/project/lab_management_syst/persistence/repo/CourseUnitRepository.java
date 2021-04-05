package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.CourseUnit;

public interface CourseUnitRepository extends CrudRepository<CourseUnit, String> {
    CourseUnit[] findByStaffMembersContains(String staffId);
}
