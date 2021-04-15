package project.lab_management_syst.web.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueuePositions {
    public Map<Long, Integer> positions;
    public List<Long> marked;
    public List<Long> ready;
    public String seatNr;

    public QueuePositions() {
        this.positions = new HashMap<>();
        this.marked = new ArrayList<>();
        this.ready = new ArrayList<>();
    }
}
