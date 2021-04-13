package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class CourseUnit {
    public CourseUnit() { }

    public static CourseUnit initCourseUnit() {
        CourseUnit courseUnit = new CourseUnit();
        courseUnit.staffMembers = new ArrayList<>();
        courseUnit.labTimes = new ArrayList<>();

        return courseUnit;
    }

    @Entity
    @Data
    @EqualsAndHashCode
    public static class LabTimes {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        LocalDateTime start;
        LocalDateTime end;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseUnit")
    @JsonManagedReference
    List<LabFormat> labFormats;

    @ElementCollection
    List<String> staffMembers;

    @Id
    String unitCode;

    @OneToMany(cascade = CascadeType.ALL)
    List<LabTimes> labTimes;
}
