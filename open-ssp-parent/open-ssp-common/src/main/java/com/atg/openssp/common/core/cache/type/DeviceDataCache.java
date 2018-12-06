package com.atg.openssp.common.core.cache.type;

import com.atg.openssp.common.cache.MapCache;
import openrtb.bidrequest.model.Device;
import openrtb.bidrequest.model.Publisher;

/**
 * @author André Schmer
 *
 */
public final class DeviceDataCache extends MapCache<String, Device> {

	public static final DeviceDataCache instance = new DeviceDataCache();

	private DeviceDataCache() {
		super();
	}

}
