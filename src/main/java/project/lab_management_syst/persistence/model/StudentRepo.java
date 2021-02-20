package project.lab_management_syst.persistence.model;

import org.dom4j.tree.AbstractEntity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class StudentRepo {
    @Id
    public String repoName;

    @ManyToOne
    @JoinColumn
    public LabFormat lab;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="submissionTag")
    @JoinColumn
    public Map<String, StudentRepoSubmission> submissions;

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
