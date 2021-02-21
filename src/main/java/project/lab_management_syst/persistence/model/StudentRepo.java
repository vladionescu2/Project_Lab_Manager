package project.lab_management_syst.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dom4j.tree.AbstractEntity;

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
    LabFormat lab;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="submissionTag")
    @JoinColumn
    Map<String, StudentRepoSubmission> submissions;

    public StudentRepo() {
        super();
        this.submissions = new HashMap<String, StudentRepoSubmission>();
    }

    public StudentRepo(LabFormat labFormat, String userName) {
        this();
        this.repoName = labFormat.repoNamingSchema + "_" + userName;
        this.lab = labFormat;
    }
}
