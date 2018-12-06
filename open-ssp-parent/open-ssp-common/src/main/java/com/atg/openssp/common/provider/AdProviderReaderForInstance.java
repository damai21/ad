package com.atg.openssp.common.provider;

import com.atg.openssp.common.core.entry.SessionAgent;

/**
 * 
 * @author Brian Sorensen
 *
 */
public interface AdProviderReaderForInstance {

	float getPrice();

	float getExchangedCurrencyPrice();

	String getCurrrency();

	String buildResponse();

	String getVendorId();

	String getAdid();

}
