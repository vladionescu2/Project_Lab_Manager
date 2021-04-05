package project.lab_management_syst.persistence.model;

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
    public CourseUnit() {
        this.staffMembers = new ArrayList<>();
    }

    @Entity
    @Data
    @EqualsAndHashCode
    public static class LabTimes {
        @Id
        @GeneratedValue
        private Long id;
        LocalDateTime start;
        LocalDateTime end;
    }

    @ElementCollection
    List<String> staffMembers;

    @Id
    String unitCode;

    @OneToMany
    List<LabTimes> labTimes;
}
