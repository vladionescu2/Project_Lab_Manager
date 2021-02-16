package project.lab_management_syst.persistence.model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Student {

    public Student() {
        super();
        this.currentLabs = new HashMap<String, StudentLabs>();
    }

    public Student(String userName) {
        this();
        this.userName = userName;
    }

    @Id
    public String userName;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="courseCode")
    public Map<String, StudentLabs> currentLabs;
}
