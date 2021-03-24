package project.lab_management_syst.web;

import org.springframework.stereotype.Component;
import project.lab_management_syst.persistence.model.StudentRepoSubmission;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.PriorityQueue;

@Component
public class QueueManager {
    public Map<String, LabQueueGroup> labQueueGroups;

    public static class LabQueueGroup {
        public Map<String, LabQueue> labQueues;
    }

    public static class LabQueue extends PriorityQueue<MarkingRequest> {

    }

    public static class MarkingRequest implements Comparable<MarkingRequest> {
        public StudentRepoSubmission submission;
        public int score;

        public MarkingRequest(StudentRepoSubmission submission) {
            this.score = this.getScore();
        }

        private int getScore() {
            LocalDateTime deadline = submission.getStudentRepo().getLab().getLabExercises().get(submission.getSubmissionTag());
            LocalDateTime timeStamp = submission.getTimeStamp();

            return 10;
        }

        public int compareTo(MarkingRequest request) {
            if (request == null) {
                throw new NullPointerException("Compared request should not be null!");
            }

            return score - request.score;
        }
    }

}
