package project.lab_management_syst.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class StudentRepoSubmission {
    @Id
    public String commitId;
    public String submissionTag;
    public LocalDateTime timeStamp;
    public boolean isLate = false;

    public StudentRepoSubmission() {
        super();
    }

    public StudentRepoSubmission(String commitId, String submissionTag, LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.commitId = commitId;
        this.submissionTag = submissionTag;
    }
}
