package project.lab_management_syst.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class CourseUnit {

    @Entity
    @Data
    @EqualsAndHashCode
    static
    class LabTimes {
        @Id
        @GeneratedValue
        private Long id;
        LocalDateTime start;
        LocalDateTime end;
    }

    @Id
    String unitCode;

    @OneToMany
    List<LabTimes> labTimes;
}
