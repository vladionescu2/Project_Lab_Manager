package project.lab_management_syst.web;

import org.springframework.stereotype.Component;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.Submission;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


@Component
public class QueueManager {
    public Map<String, LabQueueGroup> labQueueGroups;

    public static class LabQueueGroup {
        private final CourseUnit.LabTimes labTimes;
        public Map<String, LabQueue> labQueues;

        public LabQueueGroup(CourseUnit.LabTimes labTimes) {
            this.labTimes = labTimes;
            this.labQueues = new HashMap<String, LabQueue>();
        }

        public void addLabQueue(String repoSchema, LabQueue labQueue) {
            labQueue.owningGroup = this;
            this.labQueues.put(repoSchema, labQueue);
        }
    }

    public static class LabQueue extends PriorityQueue<MarkingRequest> {
        private LabQueueGroup owningGroup;
    }

    public static class MarkingRequest implements Comparable<MarkingRequest> {
        private final Submission submission;
        private final LabQueue labQueue;
        public final int score;

        public MarkingRequest(Submission submission, LabQueue labQueue) {
            this.submission = submission;
            this.labQueue = labQueue;
            this.score = this.determineRequestScore();
        }

        private int determineRequestScore() {
            LocalDateTime deadline = submission.getStudentRepo().getLab().getLabExercises().get(submission.getSubmissionTag());
            LocalDateTime timeStamp = submission.getTimeStamp();
            int timeBeforeDeadline = (int)Duration.between(timeStamp, deadline).getSeconds();

            LocalDateTime labEnd = labQueue.owningGroup.labTimes.getEnd();
            LocalDateTime labStart = labQueue.owningGroup.labTimes.getStart();
            LocalDateTime now = LocalDateTime.now();

            Duration sinceLabStart = Duration.between(labStart, now);

            int timeBeforeLabEnd = (int)Duration.between(now, labEnd).getSeconds();
            double earlySubmissionCoefficient = 1;
            if (sinceLabStart.compareTo(Duration.ofMinutes(15)) > 0) {
                earlySubmissionCoefficient = 0.3;
            }

//            int timeAfterLabStart = (int)Duration.between(labStart, now).getSeconds();
//            if (timeAfterLabStart < 0) {
//                timeAfterLabStart = 0;
//            }
//
//
//            int timeBetweenStartAndEnd = (int)Duration.between(labStart, labEnd).getSeconds();
//            earlySubmissionCoeff = timeBeforeLabEnd / (double)timeBetweenStartAndEnd;
//
//            if (earlySubmissionCoeff > 1) {
//                earlySubmissionCoeff = 1;
//            }
//            else if (earlySubmissionCoeff < 0) {
//                earlySubmissionCoeff = 0;
//            }

            return (int)(timeBeforeDeadline * earlySubmissionCoefficient) + timeBeforeLabEnd;
        }

        public int compareTo(MarkingRequest request) {
            if (request == null) {
                throw new NullPointerException("Compared request should not be null!");
            }

            return score - request.score;
        }
    }

}
