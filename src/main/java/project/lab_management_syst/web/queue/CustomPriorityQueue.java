package project.lab_management_syst.web.queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.lab_management_syst.web.model.QueuePositions;

import java.util.*;

/**
 *  The {@code IndexMinPQ} class represents an indexed priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with <em>delete</em> and <em>change-the-key</em>
 *  methods. In order to let the client refer to keys on the priority queue,
 *  an integer between {@code 0} and {@code maxN - 1}
 *  is associated with each key—the client uses this integer to specify
 *  which key to delete or change.
 *  It also supports methods for peeking at the minimum key,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  This implementation uses a binary heap along with an array to associate
 *  keys with integers in the given range.
 *  The <em>insert</em>, <em>delete-the-minimum</em>, <em>delete</em>,
 *  <em>change-key</em>, <em>decrease-key</em>, and <em>increase-key</em>
 *  operations take &Theta;(log <em>n</em>) time in the worst case,
 *  where <em>n</em> is the number of elements in the priority queue.
 *  Construction takes time proportional to the specified capacity.
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 */
public class CustomPriorityQueue implements Iterable<LabQueue.MarkingRequest> {
    private final ArrayList<LabQueue.MarkingRequest> pq;        // binary heap using 1-based indexing
    private final Map<LabQueue.MarkingRequest, Integer> qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private final Map<String, LabQueue.MarkingRequest> students;

    Logger logger = LogManager.getLogger();

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code initCapacity - 1}.
     *
     * @param initCapacity the keys on this priority queue are index from {@code 0}
     *             {@code initCapacity - 1}
     * @throws IllegalArgumentException if {@code initCapacity < 0}
     */
    public CustomPriorityQueue(int initCapacity) {
        if (initCapacity < 0) throw new IllegalArgumentException();
//        pq = new LabQueue.MarkingRequest[initCapacity + 1];
        pq = new ArrayList<>(initCapacity + 1);
        qp = new HashMap<>(initCapacity + 1);                   // make this of length initCapacity??
        students = new HashMap<>(initCapacity + 1);
        pq.add(null);
    }

    public CustomPriorityQueue() {
        this(50);
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     * {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

//    public QueuePositions[] getAllPositionsLessThan(int position) {
//        QueuePositions[] queuePositions = new QueuePositions[this.size() - position];
//        for (int i = position; i <= this.size(); i++) {
//            QueuePositions studentPos = new QueuePositions();
//
//        }
//    }

    public Integer getPosition(String userName) {
//        logger.info("Current heap: " + pq);
//        logger.info("Current qp: " + qp);

        LabQueue.MarkingRequest request = students.get(userName);

        return request == null ? null : qp.get(request);
    }

    public LabQueue.MarkingRequest getMarkingRequest(String userName) {
//        logger.info("Current heap: " + pq);
//        logger.info("Current qp: " + qp);
        return students.get(userName);
    }

    public boolean hasMarkingRequest(String userName) {
        return students.containsKey(userName);
    }

    public void deleteRequest(String userName) {
        LabQueue.MarkingRequest request = students.get(userName);

        if (request != null) {
            this.delete(request);
            this.students.remove(userName);
        }
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(LabQueue.MarkingRequest i) {
        return qp.containsKey(i);
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return pq.size() - 1;
    }

    /**
     * Associates key with index {@code i}.
     *
     * @param i   an index
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *                                  with index {@code i}
     */
    public int insert(LabQueue.MarkingRequest i) {
//        logger.info("Inserting request " + i);
//        logger.info("Heap before insertion: " + pq);
//        logger.info("qp before insertion " + qp);

        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        pq.add(i);
        qp.put(i, this.size());
        students.put(i.getSubmission().getStudent().getUserName(), i);
        swim(this.size());

        //FOR TESTING
        assert isMaxHeap();

//        logger.info("qp after insesrtion " + qp);
//        logger.info("Heap after insertion: " + pq);
        return qp.get(i);
    }

    /**
     * Returns an index associated with a minimum key.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public LabQueue.MarkingRequest maxIndex() {
        if (this.size() == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq.get(1);
    }

    /**
     * Removes a maximum key and returns its associated index.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
//    public LabQueue.MarkingRequest delMax() {
//        if (this.size() == 0) throw new NoSuchElementException("Priority queue underflow");
//        LabQueue.MarkingRequest max = pq.get(1);
//        exch(1, this.size());
//        sink(1);
//        assert max == pq.get(this.size());
//        qp.remove(max);
//        pq.remove(this.size());
//
//        //FOR TESTING
//        assert (isMaxHeap());
//        return max;
//    }

    public LabQueue.MarkingRequest delMax() {
        if (this.size() == 0) throw new NoSuchElementException("Priority queue underflow");
        LabQueue.MarkingRequest max = pq.get(1);
        delete(pq.get(1));

        return max;
    }

    /**
     * Remove the key associated with index {@code i}.
     *
     * @param i the index of the key to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException   no key is associated with index {@code i}
     */
    public void delete(LabQueue.MarkingRequest i) {
//        logger.info("Deleting request " + i);
//        logger.info("Heap before deletion: " + pq);
//        logger.info("qp before deletion: " + qp);

        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp.get(i);
        if (index == this.size()) {
            qp.remove(i);
            pq.remove(index);
            return;
        }
        exch(index, this.size());
        pq.remove(this.size());
        swim(index);
        sink(index);
        qp.remove(i);

//        logger.info("qp after deletion: " + qp);
//        logger.info("Heap after deletion: " + pq);
        //FOR TESTING
        assert isMaxHeap();
    }

    /***************************************************************************
     * General helper functions.
     ***************************************************************************/
    private boolean less(int i, int j) {
        return pq.get(i).compareTo(pq.get(j)) < 0;
    }

    private void exch(int i, int j) {
        LabQueue.MarkingRequest swap = pq.get(i);
        pq.set(i, pq.get(j));
        pq.set(j, swap);
        qp.put(pq.get(i), i);
        qp.put(pq.get(j), j);
    }

    private boolean isMaxHeap() {
        return isMaxHeapOrdered(1);
    }

    private boolean isMaxHeapOrdered(int k) {
        if (k > this.size())
            return true;
        int left = 2 * k;
        int right = 2 * k + 1;
        if (left <= this.size() && less(k, left)) return false;
        if (right <= this.size() && less(k, right)) return false;
        return isMaxHeapOrdered(left) && isMaxHeapOrdered(right);
    }


    /***************************************************************************
     * Heap helper functions.
     ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && less(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= this.size()) {
            int j = 2 * k;
            if (j < this.size() && less(j, j + 1)) j++;
            if (!less(k, j)) break;
            exch(k, j);
            k = j;
        }
    }


    /***************************************************************************
     * Iterators.
     ***************************************************************************/

    /**
     * Returns an iterator that iterates over the keys on the
     * priority queue in ascending order.
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<LabQueue.MarkingRequest> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<LabQueue.MarkingRequest> {
        // create a new pq
        private final CustomPriorityQueue copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            logger.info("Original pq:" + pq);
            copy = new CustomPriorityQueue(pq.size());
            for (int i = 1; i <= size(); i++)
                copy.insert(pq.get(i));

            logger.info("Iterator pq:" + this.copy.pq);
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public LabQueue.MarkingRequest next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMax();
        }
    }
}