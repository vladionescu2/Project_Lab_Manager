package project.lab_management_syst.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode
public class LabFormat {
    @Id
    public String repoNamingSchema;

    @ManyToOne
    public CourseUnit courseUnit;

    @ElementCollection
    public Map<String, LocalDateTime> labExercises;
}
