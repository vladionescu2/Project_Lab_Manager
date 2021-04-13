package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.LabFormat;

import java.util.List;

public interface LabFormatRepository extends CrudRepository<LabFormat, String> {
    LabFormat findByRepoNamingSchema(String repoName);

    List<LabFormat> findByCourseUnitStaffMembers(String id);
}
