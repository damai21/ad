package restful.client;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * @author André Schmer
 *
 */
public class HttpComponentsClientHttpRequestFactoryBasicAuth extends HttpComponentsClientHttpRequestFactory {

	private final HttpHost host;

	public HttpComponentsClientHttpRequestFactoryBasicAuth(final HttpHost host) {
		super();
		this.host = host;
	}

	@Override
	protected HttpContext createHttpContext(final HttpMethod httpMethod, final URI uri) {
		return createHttpContext();
	}

	private HttpContext createHttpContext() {
		// Create AuthCache instance
		final AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		final BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		// Add AuthCache to the execution context
		final BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
		return localcontext;
	}

}
