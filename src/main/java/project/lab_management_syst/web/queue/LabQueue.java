package project.lab_management_syst.web.queue;

import lombok.Getter;
import lombok.Setter;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.Submission;

import java.time.Duration;
import java.time.LocalDateTime;

class LabQueue {
    @Getter
    private final LabExercise labExercise;
    @Getter
    private final CourseUnit.LabTimes labTimes;
    private final CustomPriorityQueue labQueueComponent;

    public LabQueue(CourseUnit.LabTimes labTimes, LabExercise labExercise) {
        this.labQueueComponent = new CustomPriorityQueue(300);
        this.labTimes = labTimes;
        this.labExercise = labExercise;
    }

    public int addMarkingRequest(Submission submission) {
        return this.labQueueComponent.insert(new MarkingRequest(submission));
    }

    public Integer getMarkingPosition(String userName) {

        return this.labQueueComponent.getPosition(userName);
    }

    public boolean hasMarkingRequest(String userName) {
        return this.labQueueComponent.hasMarkingRequest(userName);
    }

    public MarkingRequest getMarkingRequest(String userName) {
        return this.labQueueComponent.getMarkingRequest(userName);
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
    }
}
