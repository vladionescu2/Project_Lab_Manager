package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode
public class StudentRepo {
    @Id
    String repoName;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    @ToString.Exclude
    Student student;

    @ManyToOne
    @JoinColumn
    LabFormat lab;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentRepo")
    @MapKey(name="submissionTag")
    @JsonManagedReference
    Map<String, Submission> submissions;

    public StudentRepo() {
        super();
        this.submissions = new HashMap<String, Submission>();
    }

    public StudentRepo(LabFormat labFormat, String userName) {
        this();
        this.repoName = labFormat.repoNamingSchema + "_" + userName;
        this.lab = labFormat;
    }
}
