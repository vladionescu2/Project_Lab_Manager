package project.lab_management_syst.web.model;

import java.time.LocalDateTime;
import java.util.List;

public class NewLabFormatData {
    public String forUnitCode;
    public String repoNameFormat;
    public List<NewLabExerciseData> labExercises;

    public static class NewLabExerciseData {
        public String exerciseName;
        public LocalDateTime deadline;
    }
}
