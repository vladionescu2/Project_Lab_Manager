package project.lab_management_syst.web.queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CustomPriorityQueue implements Iterable<LabQueue.MarkingRequest>{
    private final LinkedList<LabQueue.MarkingRequest> queue;
    private final Map<String, LabQueue.MarkingRequest> students;

    Logger logger = LogManager.getLogger();

    public CustomPriorityQueue() {
        this.queue = new LinkedList<>();
        this.students = new HashMap<>();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Integer getPosition(String userName) {
        LabQueue.MarkingRequest markingRequest = students.get(userName);

        return markingRequest == null ? null : queue.indexOf(markingRequest) + 1;
    }

    public LabQueue.MarkingRequest getMarkingRequest(String userName) {
        return students.get(userName);
    }

    public boolean hasMarkingRequest(String userName) {
        return students.containsKey(userName);
    }

    public void deleteRequest(String userName) {
        LabQueue.MarkingRequest request = students.get(userName);

        if (request != null) {
            queue.remove(request);
            students.remove(userName);
        }
    }

    public int size() { return queue.size(); }

    public int insert(LabQueue.MarkingRequest markingRequest) {
        if (queue.contains(markingRequest)) throw new IllegalArgumentException("index is already in the priority queue");

        students.put(markingRequest.getSubmission().getStudent().getUserName(), markingRequest);

        int i;
        for (i = 0; i < queue.size(); i ++) {
            if (markingRequest.score > queue.get(i).score) {
                break;
            }
        }
        queue.add(i, markingRequest);

        return i + 1;
    }

    public LabQueue.MarkingRequest delMax() {
        if (queue.size() == 0) throw new NoSuchElementException("Priority queue underflow");
        LabQueue.MarkingRequest markingRequest = queue.peek();
        students.remove(markingRequest.getSubmission().getStudent().getUserName());

        return queue.remove();
    }


    public Iterator<LabQueue.MarkingRequest> iterator() {
        return queue.iterator();
    }
}
