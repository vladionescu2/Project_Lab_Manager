package project.lab_management_syst.web;

import org.springframework.stereotype.Component;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.Student;
import project.lab_management_syst.persistence.model.Submission;
import project.lab_management_syst.persistence.repo.LabExerciseRepository;
import project.lab_management_syst.persistence.repo.StudentRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


@Component
public class QueueManager {
    private Map<Long, LabQueue> labQueueMap;
    private LabExerciseRepository labExerciseRepository;
    private StudentRepository studentRepository;

    public QueueManager(LabExerciseRepository labExerciseRepository, StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.labExerciseRepository = labExerciseRepository;
        this.labQueueMap = new HashMap<>();
    }

    public void addLabQueue(Long labExerciseId, LabQueue labQueue) {
        this.labQueueMap.put(labExerciseId, labQueue);
    }

    public LabQueue createNewLabQueue(long labExerciseId) {
        LabExercise labExercise = this.labExerciseRepository.findLabExerciseByExerciseId(labExerciseId);
        if (labExercise == null) {
            throw new IllegalArgumentException("Could not find exercise with given exercise Id");
        }

        List<CourseUnit.LabTimes> labTimes = labExercise.getLabFormat().getCourseUnit().getLabTimes();
        if (labTimes == null) {
            throw new IllegalArgumentException("Could not find lab times for the given lab exercise");
        }
        CourseUnit.LabTimes upcomingLab = this.findUpcomingLabSession(labTimes);

        LabQueue newLabQueue = new LabQueue(upcomingLab, labExercise);
        this.addLabQueue(labExerciseId, newLabQueue);

        return newLabQueue;
    }

    private CourseUnit.LabTimes findUpcomingLabSession(List<CourseUnit.LabTimes> labTimes) {
        LocalDateTime now = LocalDateTime.now();
        for (CourseUnit.LabTimes labTime: labTimes) {
            if (now.isBefore(labTime.getEnd())) {
                return labTime;
            }
        }

        throw new IllegalArgumentException("No new lab sessions found!");
    }

    public LabQueue getLabQueue(Long labExerciseId) {
        return this.labQueueMap.get(labExerciseId);
    }

    public boolean hasLabQueue(Long labExerciseId) {
        return this.labQueueMap.containsKey(labExerciseId);
    }

    public void removeLabQueue(Long labExerciseId) {
        this.labQueueMap.remove(labExerciseId);
    }

    public Map<Long, LabQueue> getLabQueueMap() {
        return this.labQueueMap;
    }

    private class LabQueue {
        private final LabExercise labExercise;
        private final PriorityQueue<MarkingRequest> labQueueComponent;
        private final CourseUnit.LabTimes labTimes;

        public LabQueue(CourseUnit.LabTimes labTimes, LabExercise labExercise) {
            this.labQueueComponent = new PriorityQueue<MarkingRequest>
                    (11, Comparator.comparingInt(request -> request.score));
            this.labTimes = labTimes;
            this.labExercise = labExercise;
        }

        public CourseUnit.LabTimes getLabTimes() {
            return labTimes;
        }

        public LabExercise getLabExercise() {
            return labExercise;
        }

        public void addMarkingRequest(String userName) {
            Student student = studentRepository.findByUserName(userName);
            if (student == null) {
                throw new IllegalArgumentException("No student with given username found!");
            }

            Submission submission = student.getCurrentLabs().get(this.labExercise.getLabFormat().getRepoNamingSchema())
                    .getSubmissions().get(this.labExercise.getExerciseName());
            if (submission == null) {
                throw new IllegalArgumentException("Student has no submission for this LabQueue");
            }

            this.labQueueComponent.add(new MarkingRequest(submission, student.getUserName()));
        }

        private class MarkingRequest {
            private final String userName;
            private final Submission submission;
            public final int score;

            public MarkingRequest(Submission submission, String userName) {
                this.submission = submission;
                this.userName = userName;
                this.score = this.determineRequestScore();
            }

            private int determineRequestScore() {
                LocalDateTime deadline = submission.getLabExercise().getDeadline();
                LocalDateTime timeStamp = submission.getTimeStamp();
                int timeBeforeDeadline = (int)Duration.between(timeStamp, deadline).getSeconds();

                LocalDateTime labEnd = labTimes.getEnd();
                LocalDateTime labStart = labTimes.getStart();
                LocalDateTime now = LocalDateTime.now();

                Duration sinceLabStart = Duration.between(labStart, now);
                int timeBeforeLabEnd = (int)Duration.between(now, labEnd).getSeconds();

                double earlySubmissionCoefficient = 1;
                if (sinceLabStart.compareTo(Duration.ofMinutes(15)) > 0) {
                    earlySubmissionCoefficient = 0.3;
                }

                return (int)(timeBeforeDeadline * earlySubmissionCoefficient) + timeBeforeLabEnd;
            }
        }
    }
}
