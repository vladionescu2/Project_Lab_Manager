package project.lab_management_syst.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import project.lab_management_syst.persistence.model.Student;
import project.lab_management_syst.persistence.model.StudentRepo;
import project.lab_management_syst.persistence.repo.StudentRepository;
import project.lab_management_syst.web.model.GetMarkingRequest;
import project.lab_management_syst.web.queue.QueueManager;
import project.lab_management_syst.web.model.QueuePositions;
import reactor.core.publisher.Flux;

import java.time.Duration;
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

    @GetMapping(value = "/queue-pos/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<QueuePositions>> streamQueuePosition(@PathVariable String username) {
        return this.queueManager.getStudentQueuePositionsStream(username).map(queuePositions -> ServerSentEvent.<QueuePositions>builder()
                .event("new-pos")
                .data(queuePositions)
                .build());
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
