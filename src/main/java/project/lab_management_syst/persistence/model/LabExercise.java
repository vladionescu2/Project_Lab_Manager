package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
public class LabExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    @ToString.Exclude
    private LabFormat labFormat;

    private String exerciseName;
    private LocalDateTime deadline;
}
