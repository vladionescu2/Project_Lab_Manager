package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import project.lab_management_syst.persistence.model.StudentRepo;
import project.lab_management_syst.persistence.repo.StudentRepository;
import project.lab_management_syst.web.model.GetMarkingRequest;
import project.lab_management_syst.web.queue.QueueManager;
import project.lab_management_syst.web.model.QueuePositions;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LabManagerController {
    Logger logger = LogManager.getLogger();

    private StudentRepository studentRepository;
    private QueueManager queueManager;

    public LabManagerController(StudentRepository studentRepository, QueueManager queueManager) {
        this.queueManager = queueManager;
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

    @GetMapping("/marking/{username}")
    public QueuePositions getMarkingRequests(@PathVariable String username, @RequestParam List<Long> exerciseIds) {
        logger.info("Request to retrieve marking requests for " + username);

        return queueManager.handleGetPendingRequests(exerciseIds, username);
    }

    @PostMapping(value = "/marking/{username}", consumes = "application/json")
    public QueuePositions requestMarking(@PathVariable String username, @RequestBody GetMarkingRequest request) {
        logger.info("Request for marking from " + username);

        return queueManager.handleNewMarkingRequests(request, username);
    }

    @DeleteMapping("/marking/{username}")
    public String deleteMarking(@PathVariable String username) {
        logger.info("Request to delete marking from " + username);
        return "OK";
    }

}
