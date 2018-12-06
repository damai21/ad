package restful.context;

/**
 * @author André Schmer
 * 
 */
public enum Path {
	APP("app")/**
	 * app
	 */
	,

	CORE("/ssp")/**
	 * ssp-services
	 */
	,

	EUR_REF("/currency/currency.json")/**
	 * eurref
	 */
	,

	PRICELAYER("/price/pricelayer.json")/**
	 * pricelayer
	 */
	,

	SITE("/bids/banner.json")/**
	 * site
	 */
	,

	SSP_ADAPTER("sspAdapter")/**
	 * sspAdapter
	 */
	,

	SUPPLIER("/supplier/supplier.json")/**
	 * supplier
	 */
	,

	WEBSITE("?website=1")/**
	 * ?website=1
	 */
	;

	private String value;

	private Path(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
