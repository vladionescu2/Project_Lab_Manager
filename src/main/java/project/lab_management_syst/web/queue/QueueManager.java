package project.lab_management_syst.web.queue;

import lombok.Getter;
import org.springframework.stereotype.Component;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.Submission;
import project.lab_management_syst.persistence.repo.LabExerciseRepository;
import project.lab_management_syst.persistence.repo.SubmissionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class QueueManager {
    @Getter
    private final Map<Long, LabQueue> labQueueMap;
    private final LabExerciseRepository labExerciseRepository;
    private final SubmissionRepository submissionRepository;

    public QueueManager(LabExerciseRepository labExerciseRepository,
                        SubmissionRepository submissionRepository) {
        this.labExerciseRepository = labExerciseRepository;
        this.submissionRepository = submissionRepository;
        this.labQueueMap = new HashMap<>();
    }

    public QueuePositions handleGetPendingRequests(List<Long> exerciseIds, String userName) {
        QueuePositions queuePositions = new QueuePositions();
        for (Long exerciseId: exerciseIds) {
            if (this.hasLabQueue(exerciseId)) {
                LabQueue labQueue = this.getLabQueue(exerciseId);
                Integer queuePos = labQueue.getMarkingPosition(userName);

                if (queuePos != null) {
                    queuePositions.positions.put(exerciseId, queuePos);
                }
            }
        }

        return queuePositions;
    }

    public QueuePositions handleNewMarkingRequests(List<Long> exerciseIds, String userName) {
        QueuePositions queuePositions = new QueuePositions();
        for (Long exerciseId: exerciseIds) {
            LabQueue labQueue = this.hasLabQueue(exerciseId) ?
                    this.getLabQueue(exerciseId) : createNewLabQueue(exerciseId);

            Submission submission = this.submissionRepository.findByStudentUserName(userName);
            if (submission == null) {
                throw new IllegalArgumentException("Student has no submission for the given exercise");
            }

            int queuePos = labQueue.addMarkingRequest(submission);
            queuePositions.positions.put(exerciseId, queuePos);
        }

        return queuePositions;
    }

    public void addLabQueue(Long labExerciseId, LabQueue labQueue) {
        this.labQueueMap.put(labExerciseId, labQueue);
    }

    public LabQueue createNewLabQueue(long labExerciseId) {
        LabExercise labExercise = this.labExerciseRepository.findLabExerciseByExerciseId(labExerciseId);
        if (labExercise == null) {
            throw new IllegalArgumentException("Could not find exercise with given exercise id");
        }

        List<CourseUnit.LabTimes> labTimes = labExercise.getLabFormat().getCourseUnit().getLabTimes();
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

        throw new IllegalArgumentException("Could not find an upcoming lab session for the given lab exercise");
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
}
