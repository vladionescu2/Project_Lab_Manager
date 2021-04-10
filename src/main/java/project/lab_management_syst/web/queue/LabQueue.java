package project.lab_management_syst.web.queue;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.Submission;
import project.lab_management_syst.persistence.repo.SubmissionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LabQueue {
    Logger logger = LogManager.getLogger();

    @Getter
    private final LabExercise labExercise;
    @Getter
    private final CourseUnit.LabTimes labTimes;
    private final CustomPriorityQueue labQueueComponent;
    private final Map<String, MarkingRequest> unresolvedRequests;
    private final Map<String, MarkingRequest> staffCurrentMarkingRequest;
    private final SubmissionRepository submissionRepository;

    public LabQueue(CourseUnit.LabTimes labTimes, LabExercise labExercise, SubmissionRepository submissionRepository) {
        this.labQueueComponent = new CustomPriorityQueue();
        this.labTimes = labTimes;
        this.labExercise = labExercise;
        this.unresolvedRequests = new HashMap<>();
        this.staffCurrentMarkingRequest = new HashMap<>();

        this.submissionRepository = submissionRepository;
    }

    public int addMarkingRequest(Submission submission, int seatNr) {
        return this.labQueueComponent.insert(new MarkingRequest(submission, seatNr));
    }

    public Integer getMarkingPosition(String userName) {
        if (this.labQueueComponent.hasMarkingRequest(userName)) {
            return this.labQueueComponent.getPosition(userName);
        }

        if (this.unresolvedRequests.containsKey(userName)) {
            return 0;
        }

        return null;
    }

    public Integer getSeatNr(String userName) {
        if (this.labQueueComponent.hasMarkingRequest(userName)) {
            return this.labQueueComponent.getMarkingRequest(userName).getSeatNr();
        }

        return this.unresolvedRequests.get(userName).getSeatNr();
    }

    public boolean hasUnresolvedRequest(String userName) {
        return this.unresolvedRequests.containsKey(userName);
    }

    public boolean hasMarkingRequest(String userName) {
        return this.labQueueComponent.hasMarkingRequest(userName) || this.unresolvedRequests.containsKey(userName);
    }

    public MarkingRequest getMarkingRequest(String userName) {
        return this.labQueueComponent.getMarkingRequest(userName);
    }

    public void removeMarkingRequest(String userName) {
        this.labQueueComponent.deleteRequest(userName);
        this.unresolvedRequests.remove(userName);
    }

    public List<MarkingRequest> getAllMarkingRequests() {
        List<MarkingRequest> markingRequests = new ArrayList<>();
        for (MarkingRequest markingRequest : this.labQueueComponent) {
            markingRequests.add(markingRequest);
        }
        logger.info("All marking requests: " + markingRequests);

        return markingRequests;
    }

    public MarkingRequest getNextMarkingRequest(String staffUserName) {
        MarkingRequest markingRequest = labQueueComponent.delMax();
        this.unresolvedRequests.put(markingRequest.getSubmission().getStudent().getUserName(), markingRequest);
        this.staffCurrentMarkingRequest.put(staffUserName, markingRequest);

        return markingRequest;
    }

    public String studentMarked(String staffUserName) {
        MarkingRequest markingRequest = this.staffCurrentMarkingRequest.get(staffUserName);
        if (markingRequest == null) {
            return null;
        }
        markingRequest.getSubmission().setMarked(true);
        this.submissionRepository.save(markingRequest.getSubmission());

        String studentUserName = markingRequest.getSubmission().getStudent().getUserName();

        this.staffCurrentMarkingRequest.remove(staffUserName);
        this.unresolvedRequests.remove(studentUserName);

        return studentUserName;
    }

    protected class MarkingRequest implements Comparable<MarkingRequest> {
        @Getter
        private final Submission submission;
        @Getter
        @Setter
        private int seatNr;
        public final int score;

        public MarkingRequest(Submission submission) {
            this.submission = submission;
            this.score = this.determineRequestScore();
        }

        public MarkingRequest(Submission submission, int seatNr) {
            this(submission);
            this.seatNr = seatNr;
        }

        private int determineRequestScore() {
            LocalDateTime deadline = submission.getLabExercise().getDeadline();
            LocalDateTime timeStamp = submission.getTimeStamp();
            int timeBeforeDeadline = (int) Duration.between(timeStamp, deadline).getSeconds();

            LocalDateTime labEnd = labTimes.getEnd();
            LocalDateTime labStart = labTimes.getStart();
            LocalDateTime now = LocalDateTime.now();

            Duration sinceLabStart = Duration.between(labStart, now);
            int timeBeforeLabEnd = (int) Duration.between(now, labEnd).getSeconds();

            double earlySubmissionCoefficient = 1;
            if (sinceLabStart.compareTo(Duration.ofMinutes(15)) > 0) {
                earlySubmissionCoefficient = 0.3;
            }

            return (int) (timeBeforeDeadline * earlySubmissionCoefficient) + timeBeforeLabEnd;
        }

        @Override
        public int compareTo(MarkingRequest otherRequest) {
            if (this.score != otherRequest.score) {
                return this.score - otherRequest.score;
            }
            if (!this.submission.equals(otherRequest.submission)) {
                return this.submission.getStudent().getUserName().compareTo(
                        otherRequest.submission.getStudent().getUserName());
            }

            return 0;
        }

        @Override
        public String toString() {
            return "Submission: { labExercise: " + submission.getSubmissionTag() +
                    ", student: " + submission.getStudent().getUserName() + " }\n" +
                    "seatNr: " + this.seatNr + ",\nscore: " + this.score;
        }
    }
}
