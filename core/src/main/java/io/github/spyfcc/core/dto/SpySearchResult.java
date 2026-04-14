package io.github.spyfcc.core.dto;

import java.util.List;

import io.github.spyfcc.core.event.TrafficEvent;

public class SpySearchResult {
	
	private List<TrafficEvent> content;
	private long totalElements;
	private int page =0;
	private int size =20;
	private boolean hasNext;
	public List<TrafficEvent> getContent() {
		return content;
	}
	public void setContent(List<TrafficEvent> content) {
		this.content = content;
	}
	public long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	
	

}
