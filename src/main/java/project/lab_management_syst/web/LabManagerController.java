package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import project.lab_management_syst.persistence.model.StudentRepo;
import project.lab_management_syst.persistence.repo.StudentRepository;

import java.util.*;

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

    @GetMapping("/marking/{username}")
    public QueuePositions getMarkingRequests(@PathVariable String username) {
        logger.info("Request to retrieve marking requests for " + username);

        QueuePositions queuePositions = new QueuePositions();
        queuePositions.positions = new HashMap<String, Integer>();

        queuePositions.positions.put("commit1", 1);

        return queuePositions;
    }

    @PostMapping(value = "/marking/{username}", consumes = "application/json")
    public QueuePositions requestMarking(@PathVariable String username, @RequestBody CommitsForMarking receivedCommits) {
        logger.info("Request for marking from " + username);

        QueuePositions queuePositions = new QueuePositions();
        queuePositions.positions = new HashMap<String, Integer>();

        for (String commitId : receivedCommits.commitIds) {
            queuePositions.positions.put(commitId, 1);
        }

        return queuePositions;
    }

    @DeleteMapping("/marking/{username}")
    public String deleteMarking(@PathVariable String username) {
        logger.info("Request to delete marking from " + username);
        return "OK";
    }

    public static class CommitsForMarking {
        public List<String> commitIds;
    }

    public static class QueuePositions {
        public Map<String, Integer> positions;
    }
}
