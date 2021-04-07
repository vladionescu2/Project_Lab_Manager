package project.lab_management_syst.web.queue;

import project.lab_management_syst.web.model.LabQueueSnapshot;
import reactor.core.publisher.Flux;

public interface LabQueueListener {
    public void onQueueChange(LabQueueSnapshot labQueueSnapshot);

    public void complete();

    public Flux<LabQueueListener> flux = null;
}
