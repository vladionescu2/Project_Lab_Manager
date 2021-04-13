package project.lab_management_syst.web.model;

import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.StudentRepo;

import java.util.ArrayList;
import java.util.List;

public class StudentSubmissions {
    public StudentSubmissions(CourseUnit courseUnit) {
        this.courseUnit = courseUnit;
        this.studentRepos = new ArrayList<>();
    }

    public CourseUnit courseUnit;
    public List<StudentRepo> studentRepos;
}
