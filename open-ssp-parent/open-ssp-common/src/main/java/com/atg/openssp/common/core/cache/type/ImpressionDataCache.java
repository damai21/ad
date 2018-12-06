package com.atg.openssp.common.core.cache.type;

import com.atg.openssp.common.cache.MapCache;
import openrtb.bidrequest.model.Impression;
import openrtb.bidrequest.model.Publisher;

/**
 * @author André Schmer
 *
 */
public final class ImpressionDataCache extends MapCache<String, Impression> {

	public static final ImpressionDataCache instance = new ImpressionDataCache();

	private ImpressionDataCache() {
		super();
	}

}
