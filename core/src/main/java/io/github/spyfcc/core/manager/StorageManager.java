package io.github.spyfcc.core.manager;

import java.util.concurrent.ExecutorService;

import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.store.MemoryStore;
import io.github.spyfcc.core.store.SpyStore;

public class StorageManager {

    private final ExecutorService worker;
    private final MemoryStore memoryStore;
    private final SpyStore spyStore;
    

    public StorageManager(ExecutorService worker, int memorySize, SpyStore spyStore) {
        this.worker = worker;
        this.memoryStore = new MemoryStore(memorySize);
        this.spyStore = spyStore;
    
    }

    public void storeAsync(TrafficEvent event) {
        try {
            worker.submit(() -> {
                memoryStore.save(event);

                if (spyStore != null) {
                    spyStore.save(event);
                }
            });
        } catch (Exception e) {
            System.err.println("Traffic Spy StorageManager Error : " + e.getMessage());
        }
    }

    public MemoryStore memory() {
        return memoryStore;
    }

    public SpyStore store() {
        return spyStore;
    }
    
    public void clear() {
    	memoryStore.clear();
    }
}