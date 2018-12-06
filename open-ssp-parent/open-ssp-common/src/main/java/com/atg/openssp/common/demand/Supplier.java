package com.atg.openssp.common.demand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import openrtb.tables.BooleanInt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author André Schmer
 *
 */
public class Supplier implements Serializable, Cloneable {

    private static final long serialVersionUID = 4638985536819833964L;

    private String shortName;

    private String cookieSyncName;

    private String endPoint;

    private boolean connectionKeepAlive;

    private String openRtbVersion;

    private String contentType;

    private String acceptEncoding;// gzip

    private String contentEncoding;// gzip

    private Long supplierId;

    private String currency;

    private Integer tmax;

    private List<String> allowedAdFormats = new ArrayList();

    private List<String> allowedAdPlatforms = new ArrayList();

    private String demandBrokerFilterClassName;

    private String cookieSync;

    private boolean cookieSyncOnly;

    private int underTest;

    private int active;

    private List<String> allowedCountries = new ArrayList<>();

    private List<String> allowedSizes = new ArrayList<>();

    public Supplier() {
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public String getCookieSyncName() {
        return cookieSyncName;
    }

    public void setCookieSyncName(final String cookieSyncName) {
        this.cookieSyncName = cookieSyncName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(final String endPoint) {
        this.endPoint = endPoint;
    }

    public boolean isConnectionKeepAlive() {
        return connectionKeepAlive;
    }

    public void setConnectionKeepAlive(boolean connectionKeepAlive) {
        this.connectionKeepAlive = connectionKeepAlive;
    }

    public String getOpenRtbVersion() {
        return openRtbVersion;
    }

    public void setOpenRtbVersion(final String openRtbVersion) {
        this.openRtbVersion = openRtbVersion;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(final String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(final String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCookieSync() {
        return cookieSync;
    }

    public void setCookieSync(final String cookieSync) {
        this.cookieSync = cookieSync;
    }

    public boolean isCookieSyncOnly() {
        return cookieSyncOnly;
    }

    public void setCookieSyncOnly(final boolean cookieSyncOnly) {
        this.cookieSyncOnly = cookieSyncOnly;
    }

    public BooleanInt getUnderTest() {
        return BooleanInt.convertValue(underTest);
    }

    public void setUnderTest(final BooleanInt underTest) {
        this.underTest = underTest.getValue();
    }

    public BooleanInt getActive() {
        return BooleanInt.convertValue(active);
    }

    public void setActive(final BooleanInt active) {
        this.active = active.getValue();
    }

    public void setTmax(Integer tmax) {
        this.tmax = tmax;
    }

    public Integer getTmax() {
        return tmax;
    }

    public List<SupplierAdFormat> getAllowedAdFormats() {
        ArrayList<SupplierAdFormat> list = new ArrayList();
        for (String s : allowedAdFormats) {
            list.add(SupplierAdFormat.valueOf(s));
        }
        return list;
    }

    public void setAllowedAdFormats(final List<SupplierAdFormat> allowedAdFormats) {
        this.allowedAdFormats.clear();
        if (allowedAdFormats != null) {
            for (SupplierAdFormat f : allowedAdFormats) {
                this.allowedAdFormats.add(f.toString());
            }
        }
    }

    public void addAllowedAdFormats(final SupplierAdFormat allowedFormat) {
        if (allowedFormat != null) {
            this.allowedAdFormats.add(allowedFormat.toString());
        }
    }

    public List<SupplierAdPlatform> getAllowedAdPlatforms() {
        ArrayList<SupplierAdPlatform> list = new ArrayList();
        for (String s : allowedAdPlatforms) {
            list.add(SupplierAdPlatform.valueOf(s));
        }
        return list;
    }

    public void setAllowedAdPlatforms(final List<SupplierAdPlatform> allowedAdPlatforms) {
        this.allowedAdPlatforms.clear();
        if (allowedAdPlatforms != null) {
            for (SupplierAdPlatform p : allowedAdPlatforms) {
                this.allowedAdPlatforms.add(p.toString());
            }
        }
    }

    public void addAllowedAdPlatforms(final SupplierAdPlatform allowedPlatform) {
        if (allowedPlatform != null) {
            this.allowedAdPlatforms.add(allowedPlatform.toString());
        }
    }

    public List<String> getAllowedCountries() {
        ArrayList<String> list = new ArrayList();
        for (String s : allowedCountries) {
            list.add(s);
        }
        return list;
    }

    public void setAllowedCountries(final List<String> allowedCountries) {
        this.allowedCountries.clear();
        if (allowedCountries != null) {
            for (String s : allowedCountries) {
                this.allowedCountries.add(s);
            }
        }
    }

    public void addAllowedCountries(final String allowedCountry) {
        if (allowedCountry != null) {
            this.allowedCountries.add(allowedCountry);
        }
    }

    public List<String> getAllowedSizes() {
        ArrayList<String> list = new ArrayList();
        for (String s : allowedSizes) {
            list.add(s);
        }
        return list;
    }

    public void setAllowedSizes(final List<String> allowedSizes) {
        this.allowedSizes.clear();
        if (allowedSizes != null) {
            for (String s : allowedSizes) {
                this.allowedSizes.add(s);
            }
        }
    }

    public void addAllowedSizes(final String allowedSize) {
        if (allowedSize != null) {
            this.allowedSizes.add(allowedSize);
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (connectionKeepAlive ? 1231 : 1237);
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
        result = prime * result + ((openRtbVersion == null) ? 0 : openRtbVersion.hashCode());
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
        result = prime * result + ((cookieSyncName == null) ? 0 : cookieSyncName.hashCode());
        result = prime * result + ((supplierId == null) ? 0 : supplierId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Supplier other = (Supplier) obj;
        if (connectionKeepAlive != other.connectionKeepAlive) {
            return false;
        }
        if (contentType == null) {
            if (other.contentType != null) {
                return false;
            }
        } else if (!contentType.equals(other.contentType)) {
            return false;
        }
        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (!currency.equals(other.currency)) {
            return false;
        }
        if (endPoint == null) {
            if (other.endPoint != null) {
                return false;
            }
        } else if (!endPoint.equals(other.endPoint)) {
            return false;
        }
        if (!openRtbVersion.equals(other.openRtbVersion)) {
            return false;
        }
        if (shortName == null) {
            if (other.shortName != null) {
                return false;
            }
        } else if (!shortName.equals(other.shortName)) {
            return false;
        }
        if (cookieSyncName == null) {
            if (other.cookieSyncName != null) {
                return false;
            }
        } else if (!cookieSyncName.equals(other.cookieSyncName)) {
            return false;
        }
        if (supplierId == null) {
            if (other.supplierId != null) {
                return false;
            }
        } else if (!supplierId.equals(other.supplierId)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return  new Gson().toJson(this);
//        return String.format("Supplier [shortName=%s, cookieSyncName=%s, endPoint=%s, openRtbVersion=%s]", shortName, cookieSyncName, endPoint, openRtbVersion);
    }

    public String getDemandBrokerFilterClassName() {
        return demandBrokerFilterClassName;
    }

    public void setDemandBrokerFilterClassName(String demandBrokerFilterClassName) {
        this.demandBrokerFilterClassName = demandBrokerFilterClassName;
    }

    public Supplier clone() {
        Supplier s = new Supplier();
        s.shortName = shortName;
        s.cookieSyncName = cookieSyncName;
        s.endPoint = endPoint;
        s.connectionKeepAlive = connectionKeepAlive;
        s.openRtbVersion = openRtbVersion;
        s.contentType = contentType;
        s.acceptEncoding = acceptEncoding;// gzip
        s.contentEncoding = contentEncoding;// gzip
        s.supplierId = supplierId;
        s.currency = currency;
        s.tmax = tmax;
        s.allowedAdFormats.addAll(allowedAdFormats);
        s.allowedAdPlatforms.addAll(allowedAdPlatforms);
        s.demandBrokerFilterClassName = demandBrokerFilterClassName;
        s.cookieSync = cookieSync;
        s.cookieSyncOnly = cookieSyncOnly;
        s.allowedCountries.addAll(allowedCountries);
        s.allowedSizes.addAll(allowedSizes);
        s.underTest = underTest;
        s.active = active;
        return s;
    }

    public static void populateTypeAdapters(GsonBuilder builder) {
//         went back to Strings temporarily
//        builder.registerTypeAdapter(SupplierAdFormat.class, (JsonDeserializer<SupplierAdFormat>) (json, typeOfT, context) -> SupplierAdFormat.valueOf(json.getAsString()));
//        builder.registerTypeAdapter(SupplierAdPlatform.class, (JsonDeserializer<SupplierAdPlatform>) (json, typeOfT, context) -> SupplierAdPlatform.valueOf(json.getAsString()));
//    }
    }
}
