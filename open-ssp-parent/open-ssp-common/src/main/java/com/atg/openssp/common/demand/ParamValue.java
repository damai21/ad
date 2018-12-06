package com.atg.openssp.common.demand;

import com.atg.openssp.common.cache.dto.VideoAd;
import openrtb.bidrequest.model.*;

import java.util.List;

/**
 * Optimized for handling VideoAd impressions with the behaviour of very individual requirements of the tag handler which binds to the SSP.
 *
 * Use this class as data holder for the request params. Change the fields as you require.
 *
 * @author André Schmer
 *
 */
public class ParamValue {


	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	// private Zone zone;
	private BidRequest bidRequest;
	private Impression imp;
	private App app;
	private User user;
	private Device device;
	private Site site;
	 private VideoAd videoAd;
	 private String w;
	 private String h;
	 private List<String> mimes;
	 private String domain;
	 private String page;
	// private List<Integer> protocols;
	 private int startdelay;
	private String ipAddress;
	private String browserUserAgentString;
	private String isTest;
	private Publisher publisher;






	public String getIpAddress() {
		return ipAddress;
	}

	public VideoAd getVideoAd() {
		return videoAd;
	}

	public void setVideoAd(VideoAd videoAd) {
		this.videoAd = videoAd;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getBrowserUserAgentString() {
		return browserUserAgentString;
	}

	public void setBrowserUserAgentString(String browserUserAgentString) {
		this.browserUserAgentString = browserUserAgentString;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(final Site site) {
		this.site = site;
	}

	 public String getW() {
	 return w;
	 }

	 public void setW(final String w) {
	 this.w = w;
	 }

	 public String getH() {
	 return h;
	 }

	 public void setH(final String h) {
	 this.h = h;
	 }

	 public List<String> getMimes() {
	 return mimes;
	 }

	 public void setMimes(final List<String> mimes) {
	 this.mimes = mimes;
	 }

	 public String getDomain() {
	 return domain;
	 }

	 public void setDomain(final String domain) {
	 this.domain = domain;
	 }

	 public String getPage() {
	 return page;
	 }

	 public void setPage(final String page) {
	 this.page = page;
	 }

	public Impression getImp() {
		return imp;
	}

	public BidRequest getBidRequest() {
		return bidRequest;
	}

	public void setBidRequest(BidRequest bidRequest) {
		this.bidRequest = bidRequest;
	}

	public void setImp(Impression imp) {
		this.imp = imp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
//	 public void setProtocols(final List<Integer> list) {
//	 protocols = list;
//
//	 }
//
//	 public List<Integer> getProtocols() {
//	 return protocols;
//	 }

	 public void setStartdelay(final int startdelay) {
	 this.startdelay = startdelay;
	 }

	 public int getStartdelay() {
	 return startdelay;
	 }

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(final Publisher publisher) {
		this.publisher = publisher;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(final String isTest) {
		this.isTest = isTest;
	}

	@Override
	public String toString() {
		return String.format("ParamValue [site=%s ,publisher=%s, device=%s,imp=%s,user=%s ] ", site,publisher,device,imp,user);
	}

}
