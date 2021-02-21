package project.lab_management_system;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import project.lab_management_syst.RestServiceApplication;
import project.lab_management_syst.persistence.model.Student;
import project.lab_management_syst.persistence.repo.StudentRepository;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestServiceApplication.class)
public class SpringBootH2IntegrationTest {
    @Autowired
    private StudentRepository studentRepository;

    @Test
    @Transactional
    public void givenStudentPersist() {
        Student student = new Student("testUsername");
        studentRepository.save(student);
        assertEquals(studentRepository.findByUserName("testUsername"), student);
    }
}
