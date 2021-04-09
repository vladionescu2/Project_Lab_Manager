package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabFormat;
import project.lab_management_syst.persistence.repo.CourseUnitRepository;
import project.lab_management_syst.persistence.repo.LabFormatRepository;
import project.lab_management_syst.web.model.LabQueueSnapshot;
import project.lab_management_syst.web.model.QueuePositions;
import project.lab_management_syst.web.queue.QueueManager;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("staff")
public class StaffController {
    Logger logger = LogManager.getLogger();
    CourseUnitRepository courseUnitRepository;
    LabFormatRepository labFormatRepository;
    QueueManager queueManager;

    public StaffController(CourseUnitRepository courseUnitRepository,
                           LabFormatRepository labFormatRepository,
                           QueueManager queueManager) {
        this.courseUnitRepository = courseUnitRepository;
        this.labFormatRepository = labFormatRepository;
        this.queueManager = queueManager;
    }

    @GetMapping("stream/lab-queue/{exId}")
    Flux<ServerSentEvent<LabQueueSnapshot>> getLabQueueStream(@PathVariable Long exId) {
        return this.queueManager.getLabQueueStream(exId).map(queueSnapshot -> ServerSentEvent.<LabQueueSnapshot>builder()
                .event("lab-snapshot")
                .data(queueSnapshot)
                .build());
    }

    @GetMapping("next-student/{exId}/{userName}")
    public LabQueueSnapshot.StudentRequest getNextStudent(@PathVariable Long exId, @PathVariable String userName) {
        try {
            return this.queueManager.getNextStudent(exId, userName);
        }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab Queue is empty");
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Lab Queue for the given exercise id");
        }
    }

    @GetMapping("student-marked/{exId}/{userName}")
    public String studentMarked(@PathVariable Long exId, @PathVariable String userName) {
        this.queueManager.studentMarked(exId, userName);

        return "Student marked";
    }

    @GetMapping("lab-queue/{exId}")
    public LabQueueSnapshot getLabQueueSnapshot(@PathVariable Long exId) {
        List<LabQueueSnapshot.StudentRequest> allStudentRequests = this.queueManager.getAllStudentRequests(exId);

        return new LabQueueSnapshot(allStudentRequests);
    }

    @GetMapping("course-units/{id}")
    public List<LabFormat> getCourseUnits(@PathVariable String id) {
        logger.info("Getting staff course units for " + id);

        List<LabFormat> labFormats = this.labFormatRepository.findByCourseUnitStaffMembers(id);

        return labFormats;
    }
}
