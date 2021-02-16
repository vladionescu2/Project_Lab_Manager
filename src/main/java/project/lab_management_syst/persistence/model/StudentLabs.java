package project.lab_management_syst.persistence.model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class StudentLabs {

    @Id
    public String courseCode;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="submissionTag")
    public Map<String, StudentSubmission> submissions;

    public StudentLabs() {
        super();
        this.submissions = new HashMap<String, StudentSubmission>();
    }

    public StudentLabs(String courseCode) {
        this();
        this.courseCode = courseCode;
    }
}
