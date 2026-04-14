package io.github.spyfcc.core.store;

import java.util.LinkedList;
import java.util.List;

import io.github.spyfcc.core.event.TrafficEvent;

public class MemoryStore {

    private final int maxSize;
    private final LinkedList<TrafficEvent> cache = new LinkedList<>();

    public MemoryStore(int maxSize) {
        this.maxSize = maxSize;
    }

    public synchronized void save(TrafficEvent event) {
        if (event == null) {
            return;
        }

        cache.addLast(event);

        if (cache.size() > maxSize) {
            cache.removeFirst();
        }
    }

    public synchronized List<TrafficEvent> list() {
        return new LinkedList<>(cache);
    }
}