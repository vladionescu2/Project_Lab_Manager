package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabFormat;
import project.lab_management_syst.persistence.repo.CourseUnitRepository;
import project.lab_management_syst.persistence.repo.LabFormatRepository;
import project.lab_management_syst.web.model.LabQueueSnapshot;
import project.lab_management_syst.web.model.QueuePositions;
import project.lab_management_syst.web.queue.QueueManager;
import reactor.core.publisher.Flux;

import java.util.List;

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
