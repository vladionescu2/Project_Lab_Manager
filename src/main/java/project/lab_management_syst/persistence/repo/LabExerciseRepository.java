package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.LabFormat;

public interface LabExerciseRepository extends CrudRepository<LabExercise, Long> {
    LabExercise findLabExerciseByExerciseId(Long exerciseId);

    LabExercise findByLabFormatAndExerciseName(LabFormat labFormat, String exerciseName);
}
