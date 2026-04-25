package io.github.spyfcc.core.store;

import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.event.TrafficEvent;

public interface SpyStore {


    void save(TrafficEvent event);

    SpySearchResult search(SpySearchRequest request);
}
