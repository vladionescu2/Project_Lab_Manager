package project.lab_management_syst.web.model;

import java.util.HashMap;
import java.util.Map;

public class QueuePositions {
    public Map<Long, Integer> positions;
    public Integer seatNr;

    public QueuePositions() {
        this.positions = new HashMap<>();
    }
}
