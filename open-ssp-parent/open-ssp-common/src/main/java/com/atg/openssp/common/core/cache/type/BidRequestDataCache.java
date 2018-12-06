package com.atg.openssp.common.core.cache.type;

import com.atg.openssp.common.cache.MapCache;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.Device;

/**
 * @author André Schmer
 *
 */
public final class BidRequestDataCache extends MapCache<String, BidRequest> {

	public static final BidRequestDataCache instance = new BidRequestDataCache();

	private BidRequestDataCache() {
		super();
	}

}
