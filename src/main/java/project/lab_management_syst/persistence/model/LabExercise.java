package project.lab_management_syst.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
public class LabExercise {
    @Id
    @GeneratedValue
    private long exerciseId;

    private String exerciseName;
    private LocalDateTime deadline;
}
