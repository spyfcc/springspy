package io.github.spyfcc.core.manager;


import java.util.concurrent.ExecutorService;

import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.store.FileStore;
import io.github.spyfcc.core.store.MemoryStore;

public class StorageManager {
	private final ExecutorService worker;
	private final MemoryStore memoryStore;
	private final FileStore fileStore;
	
	public StorageManager(ExecutorService worker , int memorySize ,String filepath) {
		this.worker = worker;
		this.memoryStore = new MemoryStore(memorySize);
		this.fileStore = new FileStore(filepath);
		
	}
	public void storeAsync(TrafficEvent event) {
		try {
			worker.submit(() -> {
				memoryStore.save(event);
				fileStore.save(event);
			});
		} catch (Exception e) {
		
		}
	}
	
	public MemoryStore memory() {
		return memoryStore;
	}

}
