package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import project.lab_management_syst.persistence.model.Submission;

public interface SubmissionRepository extends CrudRepository<Submission, String> {
    Submission findByLabExerciseExerciseIdAndStudentUserName(Long exerciseId, String userName);
}
