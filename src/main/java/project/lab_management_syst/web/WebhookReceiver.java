package project.lab_management_syst.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.lab_management_syst.persistence.model.StudentLabs;
import project.lab_management_syst.persistence.model.Student;
import project.lab_management_syst.persistence.model.StudentSubmission;
import project.lab_management_syst.persistence.repo.StudentRepository;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class WebhookReceiver {
    Logger logger = LogManager.getLogger();

    private StudentRepository studentRepository;

    public WebhookReceiver(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping(path = "/receiver", consumes = "application/json")
    public String receiveEvent(@RequestBody Map<String, Object> eventInfo) {
        String userName = (String) eventInfo.get("user_username");
        logger.info("New request from username:" + userName);

        Student currentStudent;
        if ((currentStudent = studentRepository.findByUserName(userName)) == null) {
            logger.info("Already found student " + userName);
            currentStudent = new Student(userName);
        }

        String commitId = (String) eventInfo.get("checkout_sha");
        logger.info("Commit Id: " + commitId);
        String tag = ((String) eventInfo.get("ref")).replaceAll(".*/(.+)$", "$1");
        logger.info("Tag used: " + tag);

        LocalDateTime timeStamp = LocalDateTime.now();

        StudentSubmission newSubmission = new StudentSubmission(commitId, tag, timeStamp);
        StudentLabs newLab = new StudentLabs("COMP0");
        newLab.submissions.put(newSubmission.submissionTag, newSubmission);
        currentStudent.currentLabs.put(newLab.courseCode, newLab);
        studentRepository.save(currentStudent);

        return "Received the request\n";
    }
}
