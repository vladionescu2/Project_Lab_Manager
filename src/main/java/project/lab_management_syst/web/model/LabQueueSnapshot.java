package project.lab_management_syst.web.model;

import java.util.List;

public class LabQueueSnapshot {
    public List<StudentRequest> studentRequests;

    public LabQueueSnapshot(List<StudentRequest> studentRequests) {
        this.studentRequests = studentRequests;
    }

    public static class StudentRequest {
        public String userName;
        public int seatNr;

        public StudentRequest(String userName, int seatNr) {
            this.seatNr = seatNr;
            this.userName = userName;
        }
    }
}
