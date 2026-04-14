package io.github.spyfcc.core.search;

import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;

public interface SpySearchService {
	SpySearchResult search(SpySearchRequest request);
}
