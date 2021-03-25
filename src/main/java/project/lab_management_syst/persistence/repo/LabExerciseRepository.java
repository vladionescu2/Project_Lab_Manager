package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.LabExercise;

public interface LabExerciseRepository extends CrudRepository<LabExercise, Long> {
    LabExercise findLabExerciseByExerciseId(Long exerciseId);
}
