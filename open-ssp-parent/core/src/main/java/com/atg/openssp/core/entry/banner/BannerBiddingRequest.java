package com.atg.openssp.core.entry.banner;

import com.atg.openssp.core.entry.header.AdUnit;

import java.util.ArrayList;
import java.util.List;

public class BannerBiddingRequest {
    private ArrayList<AdUnit> adUnitsToBidUpon = new ArrayList<AdUnit>();
    private String id;
    private String site;
    private String impression;
    private String user;
    private String Device;
    private String app;
    private String page;
    private String _fshash;
    private String _fsloc;
    private String _fsuid;
    private String _fssid;


    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSite() {
        return site;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getApp() {
        return app;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public void setFsHash(String fsHash) {
        this._fshash = fsHash;
    }

    public String getFsHash() {
        return _fshash;
    }

    public void setFsLoc(String fsLoc) {
        this._fsloc = fsLoc;
    }

    public String getFsLoc() {
        return _fsloc;
    }

    public void setFsUid(String fsUid) {
        this._fsuid = fsUid;
    }

    public String getFsUid() {
        return _fsuid;
    }

    public void setFsSid(String fsSid) {
        this._fssid = fsSid;
    }

    public String getFsSid() {
        return _fssid;
    }

    public void setAdUnitsToBidUpon(List<AdUnit> adUnitsToBidUpon) {
        this.adUnitsToBidUpon.clear();
        this.adUnitsToBidUpon.addAll(adUnitsToBidUpon);
    }

    public List<AdUnit> getAdUnitsToBidUpon() {
        return adUnitsToBidUpon;
    }
}
