package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.lab_management_syst.persistence.model.LabFormat;
import project.lab_management_syst.persistence.model.StudentRepo;
import project.lab_management_syst.persistence.model.Student;
import project.lab_management_syst.persistence.model.Submission;
import project.lab_management_syst.persistence.repo.LabFormatRepository;
import project.lab_management_syst.persistence.repo.StudentRepository;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class WebhookReceiver {
    Logger logger = LogManager.getLogger();

    private StudentRepository studentRepository;
    private LabFormatRepository labFormatRepository;

    public WebhookReceiver(StudentRepository studentRepository, LabFormatRepository labFormatRepository) {
        this.studentRepository = studentRepository;
        this.labFormatRepository = labFormatRepository;
    }

    @PostMapping(path = "/receiver", consumes = "application/json")
    public String receiveEvent(@RequestBody Map<String, Object> eventInfo) {
        String userName = (String) eventInfo.get("user_username");
        logger.info("New request from username:" + userName);

        Student currentStudent = studentRepository.findByUserName(userName);
        if (currentStudent == null) {
            logger.info("Creating new Student called " + userName);
            currentStudent = new Student(userName);
        }

        String repoName = (String) ((Map)eventInfo.get("project")).get("name");
        logger.info("Potential submission to the " + repoName + " repository");
        String formatToSearchFor = repoName.replaceAll("^(.+)_.+$", "$1");
        logger.info("Searching for " + formatToSearchFor + " in lab formats");
        LabFormat labFormat = labFormatRepository.findByRepoNamingSchema(formatToSearchFor);
        if (labFormat == null) {
            logger.info("The given repository is not a lab repository");
            return "The given repository is not a lab repository";
        }

        String tag = ((String) eventInfo.get("ref")).replaceAll(".*/(.+)$", "$1");
        logger.info("Tag used: " + tag);
        LocalDateTime submissionDeadline = labFormat.labExercises.get(tag);
        if (submissionDeadline == null) {
            logger.info("The given tag is not used for a lab exercise");
            return "The given tag is not used for a lab exercise";
        }

        boolean isLate = false;
        LocalDateTime timeStamp = LocalDateTime.now();
        if (timeStamp.isAfter(submissionDeadline)) {
            logger.info("Submission is late!");
            isLate = true;
        }

        String commitId = (String) eventInfo.get("checkout_sha");
        logger.info("Commit Id: " + commitId);

        Submission newSubmission = new Submission(commitId, tag, timeStamp);
        newSubmission.setLate(isLate);

        StudentRepo newLab = currentStudent.getCurrentLabs().get(repoName);
        if (newLab == null) {
            logger.info("Creating new StudentRepo called " + repoName);
            newLab = currentStudent.generateStudentRepo(labFormat);
        }
        newLab.setStudent(currentStudent);
        newSubmission.setStudentRepo(newLab);
        newLab.getSubmissions().put(newSubmission.getSubmissionTag(), newSubmission);
        currentStudent.getCurrentLabs().put(labFormat.repoNamingSchema, newLab);
        studentRepository.save(currentStudent);

        logger.info("Saved new submission");

        return "Received the request\n";
    }
}
