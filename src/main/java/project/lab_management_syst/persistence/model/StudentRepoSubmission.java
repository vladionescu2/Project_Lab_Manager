package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
public class StudentRepoSubmission {
    @Id
    String commitId;
    String submissionTag;
    LocalDateTime timeStamp;
    boolean isLate = false;
    boolean isMarked = false;

    @ManyToOne
    @JoinColumn()
    @JsonBackReference
    StudentRepo studentRepo;

    public StudentRepoSubmission() {
        super();
    }

    public StudentRepoSubmission(String commitId, String submissionTag, LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.commitId = commitId;
        this.submissionTag = submissionTag;
    }
}
