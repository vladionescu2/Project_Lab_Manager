package project.lab_management_syst.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class CourseUnit {
    @Id
    String unitCode;

    @ElementCollection
    List<LocalDateTime> labTimes;
}
