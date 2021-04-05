package project.lab_management_syst.web.queue;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import project.lab_management_syst.persistence.model.CourseUnit;
import project.lab_management_syst.persistence.model.LabExercise;
import project.lab_management_syst.persistence.model.Submission;
import project.lab_management_syst.persistence.repo.LabExerciseRepository;
import project.lab_management_syst.persistence.repo.SubmissionRepository;
import project.lab_management_syst.web.model.GetMarkingRequest;
import project.lab_management_syst.web.model.LabQueueSnapshot;
import project.lab_management_syst.web.model.QueuePositions;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class QueueManager {
    @Getter
    private final Map<Long, LabQueue> labQueueMap;
    private final LabExerciseRepository labExerciseRepository;
    private final SubmissionRepository submissionRepository;
    private final Map<String, QueuePositionListener> queuePositionsListeners;
    private final Map<Long, LabQueueListener> labQueueListeners;

    Logger logger = LogManager.getLogger();


    public QueueManager(LabExerciseRepository labExerciseRepository,
                        SubmissionRepository submissionRepository) {
        this.labExerciseRepository = labExerciseRepository;
        this.submissionRepository = submissionRepository;
        this.labQueueMap = new HashMap<>();

        this.queuePositionsListeners = new HashMap<>();
        this.labQueueListeners = new HashMap<>();
    }

    public Flux<QueuePositions> getStudentQueuePositionsStream(String userName) {
        logger.info("Creating new stream for " + userName);
            return Flux.create(sink -> {
                queuePositionsListeners.put(userName, new QueuePositionListener() {
                    @Override
                    public void onQueueChange(QueuePositions newPosition) {
                        sink.next(newPosition);
                    }

                    @Override
                    public void complete() {
                        sink.complete();
                    }
                });
            });
    }

    public Flux<LabQueueSnapshot> getLabQueueStream(Long exerciseId) {
        if (!labQueueListeners.containsKey(exerciseId)) {
            logger.info("Creating new stream for exercise id " + exerciseId);
            Flux<LabQueueSnapshot> labQueueFlux = Flux.create(sink -> {
                labQueueListeners.put(exerciseId, new LabQueueListener(sink));
            });
            labQueueListeners.get(exerciseId).flux = labQueueFlux;
        }

        return labQueueListeners.get(exerciseId).flux;
    }

    public QueuePositions handleGetPendingRequests(List<Long> exerciseIds, String userName) {
        QueuePositions queuePositions = new QueuePositions();
        queuePositions.seatNr = null;
        for (Long exerciseId: exerciseIds) {
            if (this.hasLabQueue(exerciseId)) {
                LabQueue labQueue = this.getLabQueue(exerciseId);
                Integer queuePos = labQueue.getMarkingPosition(userName);

                if (queuePos != null) {
                    queuePositions.positions.put(exerciseId, queuePos);
                    queuePositions.seatNr = labQueue.getSeatNr(userName);
                }
            }
        }

        return queuePositions;
    }

    public QueuePositions handleNewMarkingRequests(GetMarkingRequest request, String userName) {
        for (Long exerciseId: request.toCancel) {
            if (!this.hasLabQueue(exerciseId)) {
                continue;
            }

            LabQueue labQueue = this.getLabQueue(exerciseId);
            labQueue.removeMarkingRequest(userName);
        }

        QueuePositions queuePositions = new QueuePositions();
        for (Long exerciseId: request.toMark) {
            LabQueue labQueue = this.hasLabQueue(exerciseId) ?
                    this.getLabQueue(exerciseId) : createNewLabQueue(exerciseId);

            int queuePos;
            if (labQueue.hasMarkingRequest(userName)) {
                queuePos = labQueue.getMarkingPosition(userName);
            }
            else {
                Submission submission = this.submissionRepository.findByLabExerciseExerciseIdAndStudentUserName
                        (exerciseId, userName);
                if (submission == null) {
                    throw new IllegalArgumentException("Student has no submission for the given exercise");
                }
                queuePos = labQueue.addMarkingRequest(submission, request.seatNr);
            }

            queuePositions.positions.put(exerciseId, queuePos);
        }

        Set<Long> modifiedQueues = new HashSet<>();
        modifiedQueues.addAll(request.toMark);
        modifiedQueues.addAll(request.toCancel);

        logger.info("Modified queues are " + modifiedQueues);
        for (Long exercise : modifiedQueues) {
            streamNewPositions(exercise);
        }

        return queuePositions;
    }

    public List<LabQueueSnapshot.StudentRequest> getAllStudentRequests(Long exerciseId) {
        LabQueue labQueue = this.hasLabQueue(exerciseId) ?
                this.getLabQueue(exerciseId) : createNewLabQueue(exerciseId);

        List<LabQueueSnapshot.StudentRequest> studentRequests = labQueue.getAllMarkingRequests().stream()
                .map(markingRequest -> new LabQueueSnapshot.StudentRequest(
                        markingRequest.getSubmission().getStudent().getUserName(),
                        markingRequest.getSeatNr()))
                .collect(Collectors.toList());

        return studentRequests;
    }

    private void streamNewPositions(Long exerciseId) {
        LabQueue labQueue = this.getLabQueue(exerciseId);

        logger.info("Sending new queue positions for ex id " + exerciseId);
//        if (labQueueListeners.containsKey(exerciseId)) {
//            List<LabQueueSnapshot.StudentRequest> studentRequests = this.getAllStudentRequests(exerciseId);
//            labQueueListeners.get(exerciseId).onQueueChange(new LabQueueSnapshot(studentRequests));
//        }

        for (String student : queuePositionsListeners.keySet()) {
            if (!labQueue.hasMarkingRequest(student)) { continue; }
            logger.info("Sending new queue position for " + student);
            QueuePositions newPosition = new QueuePositions();
            newPosition.positions.put(exerciseId, labQueue.getMarkingPosition(student));
            queuePositionsListeners.get(student).onQueueChange(newPosition);
        }
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
                logger.info("Found upcoming lab session on " + labTime.getEnd());
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

    public void removeLabQueue(Long labExerciseId) { this.labQueueMap.remove(labExerciseId); }
}
