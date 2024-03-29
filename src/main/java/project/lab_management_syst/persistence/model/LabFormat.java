package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode
public class LabFormat {
    @Id
    String repoNamingSchema;

    @ManyToOne
    @JsonBackReference
    @JoinColumn
    @ToString.Exclude
    CourseUnit courseUnit;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "labFormat")
    @JsonManagedReference
    List<LabExercise> labExercises;
}
