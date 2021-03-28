package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
public class LabExercise {
    @Id
    @GeneratedValue
    private long exerciseId;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private LabFormat labFormat;

    private String exerciseName;
    private LocalDateTime deadline;
}
