package project.lab_management_syst.web.queue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

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
    private int n;           // number of elements on PQ
    private final LabQueue.MarkingRequest[] pq;        // binary heap using 1-based indexing
    private final Map<LabQueue.MarkingRequest, Integer> qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private final Map<String, LabQueue.MarkingRequest> students;

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     *
     * @param maxN the keys on this priority queue are index from {@code 0}
     *             {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public CustomPriorityQueue(int maxN) {
        if (maxN < 0) throw new IllegalArgumentException();
        n = 0;
        pq = new LabQueue.MarkingRequest[maxN + 1];
        qp = new HashMap<>();                   // make this of length maxN??
        students = new HashMap<>();
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     * {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    public Integer getPosition(String userName) {
        LabQueue.MarkingRequest request = students.get(userName);

        return request == null ? null : qp.get(request);
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
        return n;
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
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        n++;
        students.put(i.getSubmission().getStudent().getUserName(), i);
        qp.put(i, n);
        pq[n] = i;
        swim(n);

        return qp.get(i);
    }

    /**
     * Returns an index associated with a minimum key.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public LabQueue.MarkingRequest minIndex() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    /**
     * Removes a minimum key and returns its associated index.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public LabQueue.MarkingRequest delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        LabQueue.MarkingRequest min = pq[1];
        exch(1, n--);
        sink(1);
        assert min == pq[n + 1];
        qp.remove(min);
        pq[n + 1] = null;        // not needed
        return min;
    }

    /**
     * Remove the key associated with index {@code i}.
     *
     * @param i the index of the key to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException   no key is associated with index {@code i}
     */
    public void delete(LabQueue.MarkingRequest i) {
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp.get(i);
        exch(index, n--);
        swim(index);
        sink(index);
        qp.remove(i);
    }

    /***************************************************************************
     * General helper functions.
     ***************************************************************************/
    private boolean greater(int i, int j) {
        return pq[i].compareTo(pq[j]) > 0;
    }

    private void exch(int i, int j) {
        LabQueue.MarkingRequest swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp.put(pq[i], i);
        qp.put(pq[j], j);
    }


    /***************************************************************************
     * Heap helper functions.
     ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && greater(j, j + 1)) j++;
            if (!greater(k, j)) break;
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
            copy = new CustomPriorityQueue(pq.length - 1);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public LabQueue.MarkingRequest next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }
}