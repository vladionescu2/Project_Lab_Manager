package project.lab_management_syst.web.queue;

import project.lab_management_syst.web.model.QueuePositions;

public interface QueuePositionListener {
    void onQueueChange(QueuePositions newPosition);
    void complete();
}
