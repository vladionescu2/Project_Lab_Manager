package project.lab_management_syst.web.model;

import java.time.LocalDateTime;
import java.util.List;

public class NewUnitRequest {
    public String unitCode;
    public List<LabTimes> labDates;
    public List<String> staffMembers;

    public static class LabTimes {
        public LocalDateTime start;
        public LocalDateTime end;
    }
}
