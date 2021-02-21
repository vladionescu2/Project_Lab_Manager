package project.lab_management_syst.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode
public class Student {

    public Student() {
        super();
        this.currentLabs = new HashMap<String, StudentRepo>();
    }

    public Student(String userName) {
        this();
        this.userName = userName;
    }

    @Id
    String userName;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="repoName")
    @JoinColumn
    Map<String, StudentRepo> currentLabs;

    public StudentRepo generateStudentRepo(LabFormat labFormat) {
        return new StudentRepo(labFormat, userName);
    }
}
