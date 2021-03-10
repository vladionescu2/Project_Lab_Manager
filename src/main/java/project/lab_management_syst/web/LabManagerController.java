package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import project.lab_management_syst.persistence.model.Student;
import project.lab_management_syst.persistence.model.StudentRepo;
import project.lab_management_syst.persistence.model.StudentRepoSubmission;
import project.lab_management_syst.persistence.repo.StudentRepository;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LabManagerController {
    Logger logger = LogManager.getLogger();

    private StudentRepository studentRepository;

    public LabManagerController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/submission/{id}")
    public Map<String, StudentRepo> getStudentSubmissions(@PathVariable String id) {
        logger.info("Request for student with username " + id);

        try {
            return studentRepository.findByUserName(id).getCurrentLabs();
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student doesn't exist in the database");
        }
    }
}
