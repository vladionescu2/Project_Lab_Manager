package project.lab_management_syst.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class StudentSubmission {
    @Id
    public String commitId;
    public String submissionTag;
    public LocalDateTime timeStamp;

    public StudentSubmission() {
        super();
    }

    public StudentSubmission(String commitId, String submissionTag, LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.commitId = commitId;
        this.submissionTag = submissionTag;
    }
}
