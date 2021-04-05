package project.lab_management_syst.web.queue;

import project.lab_management_syst.web.model.LabQueueSnapshot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class LabQueueListener {
    private FluxSink<LabQueueSnapshot> sink;
    public Flux<LabQueueSnapshot> flux;

    public LabQueueListener(FluxSink<LabQueueSnapshot> sink) {
        this.sink = sink;
    }

    public void onQueueChange(LabQueueSnapshot labQueueSnapshot) {
        this.sink.next(labQueueSnapshot);
    }

    public void complete() {
        this.sink.complete();
    }
}
