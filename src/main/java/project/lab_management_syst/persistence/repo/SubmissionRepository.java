package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.Submission;

public interface SubmissionRepository extends CrudRepository<Submission, String> {
    Submission findByStudentUserName(String userName);
}
