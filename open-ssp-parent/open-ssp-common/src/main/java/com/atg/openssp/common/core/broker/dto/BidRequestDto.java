package com.atg.openssp.common.core.broker.dto;

import openrtb.bidrequest.model.BidRequest;

import java.io.Serializable;
import java.util.List;

public class BidRequestDto implements Serializable {
    private static final long serialVersionUID = 6743606462533687452L;

    private BidRequest bidRequest;

    public BidRequestDto() {}

    public BidRequest getBidRequest() {
        return bidRequest;
    }

    public void setBidRequest(final BidRequest bidRequest) {
        this.bidRequest= bidRequest;
    }

    @Override
    public String toString() {
        return String.format("BidRequest [bidRequest=%s]", bidRequest);
    }
}
