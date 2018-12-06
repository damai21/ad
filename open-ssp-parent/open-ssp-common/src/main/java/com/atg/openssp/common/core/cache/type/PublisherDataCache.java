package com.atg.openssp.common.core.cache.type;

import com.atg.openssp.common.cache.MapCache;
import openrtb.bidrequest.model.Publisher;

/**
 * @author André Schmer
 *
 */
public final class PublisherDataCache extends MapCache<String, Publisher> {

	public static final PublisherDataCache instance = new PublisherDataCache();

	private PublisherDataCache() {
		super();
	}

}
