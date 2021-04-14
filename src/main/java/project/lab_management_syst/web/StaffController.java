package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.LabFormat;
import project.lab_management_syst.persistence.repo.CourseUnitRepository;
import project.lab_management_syst.persistence.repo.LabFormatRepository;
import project.lab_management_syst.web.model.LabQueueSnapshot;
import project.lab_management_syst.web.model.NewLabFormatData;
import project.lab_management_syst.web.model.NewUnitData;
import project.lab_management_syst.web.queue.QueueManager;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    @DeleteMapping("/lab-format/{formatName}")
    public String deleteLabFormat(@PathVariable String formatName) {
        labFormatRepository.deleteById(formatName);

        return "Done!";
    }

    @DeleteMapping("/course-unit/{unitCode}")
    public String deleteCourseUnit(@PathVariable String unitCode) {
        courseUnitRepository.deleteById(unitCode);

        return "Done!";
    }

    @GetMapping("/all-codes")
    public List<String> getAllCodes() {

        List<String> allCourseUnits = new ArrayList<>();
        for (CourseUnit courseUnit : this.courseUnitRepository.findAll()) {
            allCourseUnits.add(courseUnit.getUnitCode());
        }

        return allCourseUnits;
    }

    @GetMapping("/all-formats")
    public List<String> getAllFormatNames() {
        List<String> allFormatNames = new ArrayList<>();

        for (LabFormat labFormat : this.labFormatRepository.findAll()) {
            allFormatNames.add(labFormat.getRepoNamingSchema());
        }

        return allFormatNames;
    }

    @PostMapping("/new-unit")
    public CourseUnit createNewCourseUnit(@RequestBody NewUnitData newUnitData) {
        if (this.courseUnitRepository.findById(newUnitData.unitCode).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Course Unit already exists!");
        }

        CourseUnit newCourseUnit = new CourseUnit();
        newCourseUnit.setUnitCode(newUnitData.unitCode);
        newCourseUnit.setStaffMembers(newUnitData.staffMembers);

        List<CourseUnit.LabTimes> sessionLabTimes = new ArrayList<>();
        for (NewUnitData.LabTimes labTimes : newUnitData.labDates) {
            CourseUnit.LabTimes newLabTimes = new CourseUnit.LabTimes();
            newLabTimes.setStart(labTimes.start);
            newLabTimes.setEnd(labTimes.end);

            sessionLabTimes.add(newLabTimes);
        }
        newCourseUnit.setLabTimes(sessionLabTimes);

        courseUnitRepository.save(newCourseUnit);
        return courseUnitRepository.findById(newCourseUnit.getUnitCode()).get();
    }

    @PostMapping("/new-lab-format")
    public LabFormat createNewLabFormat(@RequestBody NewLabFormatData newLabFormatData) {
        CourseUnit courseUnit = this.courseUnitRepository.findById(newLabFormatData.forUnitCode).get();

        if (courseUnit == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Unit does not exist!");
        }

        if (labFormatRepository.findById(newLabFormatData.repoNameFormat).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "LabFormat with this name format already exists!");
        }

        LabFormat labFormat = new LabFormat();
        labFormat.setCourseUnit(courseUnit);
        courseUnit.getLabFormats().add(labFormat);

        labFormat.setRepoNamingSchema(newLabFormatData.repoNameFormat);
        labFormat.setLabExercises(newLabFormatData.labExercises.stream()
                .map(newLabExerciseData -> {
                    LabExercise labExercise = new LabExercise();
                    labExercise.setLabFormat(labFormat);
                    labExercise.setExerciseName(newLabExerciseData.exerciseName);
                    labExercise.setDeadline(newLabExerciseData.deadline);

                    return labExercise;
                })
                .collect(Collectors.toList())
        );

        courseUnitRepository.save(courseUnit);
        return labFormatRepository.findById(labFormat.getRepoNamingSchema()).get();
    }

    @GetMapping("stream/lab-queue/{exId}")
    public Flux<ServerSentEvent<LabQueueSnapshot>> getLabQueueStream(@PathVariable Long exId) {
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

    @GetMapping("pending-student/{exId}/{userName}")
    public LabQueueSnapshot.StudentRequest getPendingStudent(@PathVariable Long exId, @PathVariable String userName) {
        try {
            return this.queueManager.getPendingStudent(exId, userName);
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
    public List<CourseUnit> getCourseUnits(@PathVariable String id) {
        logger.info("Getting staff course units for " + id);

        List<CourseUnit> labFormats = this.courseUnitRepository.findByStaffMembers(id);

        return labFormats;
    }
}
