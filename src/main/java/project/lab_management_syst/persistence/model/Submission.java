package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
public class Submission {
    @Id
    String commitId;
    String submissionTag;
    LocalDateTime timeStamp;
    boolean isLate = false;
    boolean isMarked = false;

    @OneToOne
    LabExercise labExercise;

    @OneToOne
    @JsonBackReference
    Student student;

    @ManyToOne
    @JoinColumn()
    @JsonBackReference
    StudentRepo studentRepo;

    public Submission() {
        super();
    }

    public Submission(String commitId, String submissionTag, LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.commitId = commitId;
        this.submissionTag = submissionTag;
    }
}
