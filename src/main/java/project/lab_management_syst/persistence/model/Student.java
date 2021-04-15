package project.lab_management_syst.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "student")
    @MapKey(name="repoName")
    @JsonManagedReference
    Map<String, StudentRepo> currentLabs;

    public StudentRepo generateStudentRepo(LabFormat labFormat) {
        StudentRepo studentRepo = new StudentRepo(labFormat, this);
        this.currentLabs.put(studentRepo.repoName, studentRepo);

        return studentRepo;
    }
}
