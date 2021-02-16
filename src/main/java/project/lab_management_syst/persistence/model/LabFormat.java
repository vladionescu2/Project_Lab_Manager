package project.lab_management_syst.persistence.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
public class LabFormat {
    @Id
    public String gitLabRepoName;

    public String moduleCode;

    @ElementCollection
    public Map<String, LocalDateTime> labExercises;

    public String toString() {
        return String.format("Lab[gitLabRepoName=%s, moduleCode=%s, labExercises=%s]", gitLabRepoName, moduleCode, labExercises);
    }
}
