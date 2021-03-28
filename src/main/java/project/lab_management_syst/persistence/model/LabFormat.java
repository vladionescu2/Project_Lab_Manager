package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "labFormat")
    @MapKey(name = "exerciseName")
    @JsonManagedReference
    public Map<String, LabExercise> labExercises;
}
