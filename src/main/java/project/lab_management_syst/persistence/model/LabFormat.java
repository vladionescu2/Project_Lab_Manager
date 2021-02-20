package project.lab_management_syst.persistence.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
public class LabFormat {
    @Id
    public String repoNamingSchema;

    public String moduleCode;

    @ElementCollection
    public Map<String, LocalDateTime> labExercises;

    @Override
    public String toString() {
        return "LabFormat{" +
                "repoNamingSchema='" + repoNamingSchema + '\'' +
                ", moduleCode='" + moduleCode + '\'' +
                ", labExercises=" + labExercises +
                '}';
    }
}
