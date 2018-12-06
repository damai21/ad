package com.atg.openssp.common.core.broker.remote;

import com.atg.openssp.common.cache.broker.AbstractDataBroker;
import com.atg.openssp.common.core.cache.type.BidRequestDataCache;
import com.atg.openssp.common.exception.EmptyHostException;
import openrtb.bidrequest.model.BidRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.context.Path;
import restful.context.PathBuilder;
import restful.exception.RestException;

/**
 * Act as broker between connector which loads the data from the webservice into a data transfer object and the cache.
 * <p>
 * This special data-broker loads the currency rate, see {@see CurrencyDto}, informations from the central webservice into a cache. It uses a {@see PathBuilder}
 * object to store information about the endpoint which is used by the generic {@see AbstractDataBroker} to connect to the remote.
 *
 * @author André Schmer
 */
public final class RemoteSiteDataBroker extends AbstractDataBroker<BidRequest> {

    private static final Logger log = LoggerFactory.getLogger(RemoteSiteDataBroker.class);

    public RemoteSiteDataBroker() {
    }

    @Override
    public boolean doCaching() {
        long startTS = System.currentTimeMillis();
        try {

//            final BidRequestDto bidRequestDto = super.connect(BidRequestDto.class);
			final BidRequest bidRequest = super.connect(BidRequest.class);
            if (bidRequest != null ) {
                log.warn("this is content" + bidRequest);
                long endTS = System.currentTimeMillis();
//                DataBrokerLogProcessor.instance.setLogData("BidRequestData", bidRequestDto.getBidRequest().getTmax() startTS, endTS, endTS - startTS);
                log.debug("sizeof Site data=" + bidRequest.getId());

                BidRequestDataCache.instance.put(bidRequest.getId(), bidRequest);
                return true;
            }


            log.error("no BidRequest data");
        } catch (final Exception e) {
            log.error(getClass() + ", " + e.getMessage());
        }
        return false;
    }


    @Override
    public PathBuilder getRestfulContext() {
        return getDefaulPathBuilder()
                .addPath(Path.CORE)
				.addPath(Path.SITE)
                ;
    }

    @Override
    protected void finalWork() {
        // need to switch the intermediate cache to make the data available
        BidRequestDataCache.instance.switchCache();
    }

}
