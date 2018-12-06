package com.atg.openssp.common.provider;

import com.atg.openssp.common.core.entry.SessionAgent;

/**
 * 
 * @author Brian Sorensen
 *
 */
public interface AdProviderReaderForMultipleInstance {

    int getItemCount();

	float getPrice(int index);

	float getExchangedCurrencyPrice(int index);

	String getCurrrency(int index);

	String buildResponse(int index);

	String getVendorId(int index);

	String getAdid(int index);

}
