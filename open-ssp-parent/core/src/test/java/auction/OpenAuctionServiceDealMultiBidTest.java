package auction;

import java.util.Random;

import com.atg.openssp.common.core.entry.BiddingServiceInfo;
import com.atg.openssp.common.core.exchange.Auction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atg.openssp.common.cache.CurrencyCache;
import com.atg.openssp.common.demand.BidExchange;
import com.atg.openssp.common.demand.Supplier;
import com.atg.openssp.common.exception.InvalidBidException;

import openrtb.bidrequest.model.BidRequest;
import openrtb.bidresponse.model.BidResponse;
import util.math.FloatComparator;

/**
 * @author André Schmer
 *
 */
public class OpenAuctionServiceDealMultiBidTest {

	private static Supplier supplier1;
	private static Supplier supplier2;
	private static Supplier supplier3;
	private static Supplier supplier4;

	public OpenAuctionServiceDealMultiBidTest() {}

	@BeforeClass
	public static void setUp() throws Exception {
		CurrencyCache.instance.put("EUR", 1.0f);
		CurrencyCache.instance.put("USD", 1.079f);
		CurrencyCache.instance.switchCache();

		supplier1 = new Supplier();
		supplier1.setShortName("dsp1");
		supplier1.setCookieSync("T00A");
		supplier1.setSupplierId(1l);
		supplier1.setTmax(300);

		supplier2 = new Supplier();
		supplier2.setShortName("dsp2");
		supplier2.setCookieSync("T00B");
		supplier2.setSupplierId(2l);
		supplier2.setTmax(300);

		supplier3 = new Supplier();
		supplier3.setShortName("dsp3");
		supplier3.setCookieSync("T00C");
		supplier3.setSupplierId(3l);
		supplier3.setTmax(300);

		supplier4 = new Supplier();
		supplier4.setShortName("dsp4");
		supplier4.setCookieSync("T00D");
		supplier4.setSupplierId(4l);
		supplier4.setTmax(300);
	}

	@Before
	public void before() {

	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public final void testMultiBidDealWins() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final float dealFloor1 = 3.f;
		final float dealFloor2 = 2.8f;

		final String deal_id_1 = "998877";
		final String deal_id_2 = "998866";

		final String currency = "USD";
		final String requestId1 = "r-id-1";
		final String requestId2 = "r-id-2";

		// bidrequest1
		final BidRequest bidRequest1 = RequestResponseHelper.createRequest(requestId1, impFloor, dealFloor1, currency, deal_id_1, 0).build();
		// bidrequest1
		final BidRequest bidRequest2 = RequestResponseHelper.createRequest(requestId2, impFloor, dealFloor2, currency, deal_id_2, 0).build();

		// bidresponse
		final float bidPrice1 = 3.5f;
		final BidResponse response = RequestResponseHelper.createResponse(requestId1, bidPrice1, currency, deal_id_1, "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier1, bidRequest1);
		bidExchange.setBidResponse(supplier1, response);

		// bidresponse2
		final float bidPrice2 = 4.10f;
		final BidResponse response2 = RequestResponseHelper.createResponse(requestId2, bidPrice2, currency, deal_id_2, "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier2, bidRequest2);
		bidExchange.setBidResponse(supplier2, response2);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			// TODO: eval to extract AuctionService to more general
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(3.51079f, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(3.510789776f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier2.getShortName(), winner.getSupplier().getShortName());
			Assert.assertEquals(null, winner.getDealId());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public final void test100Bids() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final String currency = "USD";
		final String requestId1 = "r-id-1";
        final String requestId2 = "r-id-2";
        final String requestId3 = "r-id-3";

		// bidrequest1
		final BidRequest bidRequest = RequestResponseHelper.createRequest(requestId1, impFloor, 0, currency, null, 0).build();
		for (int i = 0; i < 100; i++) {
			final Supplier supplier = new Supplier();
			supplier.setSupplierId(Long.valueOf(i));
			supplier.setShortName("looser" + i);
			supplier.setCookieSync("T"+i);
			bidExchange.setBidRequest(supplier, bidRequest);
			final float bidPrice = new Random().nextFloat();
			final BidResponse response = RequestResponseHelper.createResponse(requestId1, bidPrice, currency, "deal-id", "ad-id", "c-id", "cr-id");
			bidExchange.setBidResponse(supplier, response);
		}

		final float bidPrice2 = 2.5f;
		final BidResponse response2 = RequestResponseHelper.createResponse(requestId2, bidPrice2, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier2, bidRequest);
		bidExchange.setBidResponse(supplier2, response2);

		final float bidPrice1 = 2.8f;
		final BidResponse response1 = RequestResponseHelper.createResponse(requestId3, bidPrice1, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier1, bidRequest);
		bidExchange.setBidResponse(supplier1, response1);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(2.51079f, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(2.51078984f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier1.getShortName(), winner.getSupplier().getShortName());
			Assert.assertEquals(null, winner.getDealId());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public final void testMultiBidNonDealWins() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final float dealFloor1 = 3.f;
		final float dealFloor2 = 3.15f;
		final String currency = "USD";

		final String deal_id_1 = "998877";
		final String deal_id_2 = "998866";
		final String requestId1 = "r-id-1";
		final String requestId2a = "r-id-2a";
		final String requestId2b = "r-id-2b";

		// bidrequest1
		final BidRequest bidRequest1 = RequestResponseHelper.createRequest(requestId1, impFloor, dealFloor1, currency, deal_id_1, 0).build();
		// bidrequest2
		final BidRequest bidRequest2 = RequestResponseHelper.createRequest(requestId2a, impFloor, dealFloor2, currency, deal_id_2, 0).build();

		// bidresponse1 Deal
		final float bidPrice1 = 2.8f;
		final BidResponse response1 = RequestResponseHelper.createResponse(requestId1, bidPrice1, currency, deal_id_1, "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier1, bidRequest1);
		bidExchange.setBidResponse(supplier1, response1);

		// bidresponse2 Deal
		final float bidPrice2 = 2.9f;
		final BidResponse response2 = RequestResponseHelper.createResponse(requestId2a, bidPrice2, currency, deal_id_2,"ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier3, bidRequest2);
		bidExchange.setBidResponse(supplier3, response2);

		// bidresponse3 NON Deal
		final float bidPrice3 = 1.10f;
		final BidResponse response3 = RequestResponseHelper.createResponse(requestId2b, bidPrice3, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier2, bidRequest1);
		bidExchange.setBidResponse(supplier2, response3);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(2.81079f, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(2.810789605f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier3.getShortName(), winner.getSupplier().getShortName());
			Assert.assertEquals(null, winner.getDealId());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public final void testMultiBidHigherDealWins() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final float dealFloor1 = 3.0f;
		final float dealFloor2 = 3.15f;
		final String currency = "USD";

		final String deal_id_1 = "998877";
		final String deal_id_2 = "998866";
		final String requestId1 = "r-id-1";
		final String requestId2 = "r-id-2";
        final String requestId3 = "r-id-3";
        final String requestId4 = "r-id-4";

		// bidrequest
		final BidRequest bidRequest1 = RequestResponseHelper.createRequest(requestId1, impFloor, dealFloor1, currency, deal_id_1, 0).build();
		// bidresponse1 Deal
		final float bidPrice1 = 2.8f;
		final BidResponse response1 = RequestResponseHelper.createResponse(requestId1, bidPrice1, currency, deal_id_1, "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier1, bidRequest1);
		bidExchange.setBidResponse(supplier1, response1);

		// bidrequest
		final BidRequest bidRequest2 = RequestResponseHelper.createRequest(requestId2, impFloor, dealFloor2, currency, deal_id_2, 0).build();
		// bidresponse2 Deal
		final float bidPrice2 = 3.20f;
		final BidResponse response2 = RequestResponseHelper.createResponse(requestId2, bidPrice2, currency, deal_id_2, "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier3, bidRequest2);
		bidExchange.setBidResponse(supplier3, response2);

		// bidresponse3 NON Deal
		final float bidPrice3 = 1.10f;
		final BidResponse response3 = RequestResponseHelper.createResponse(requestId3, bidPrice3, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier2, bidRequest1);
		bidExchange.setBidResponse(supplier2, response3);

		// bidresponse4 NON Deal
		final float bidPrice4 = 1.50f;
		final BidResponse response4 = RequestResponseHelper.createResponse(requestId4, bidPrice4, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier4, bidRequest1);
		bidExchange.setBidResponse(supplier4, response4);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(2.81079f, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(2.810789605f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier3.getShortName(), winner.getSupplier().getShortName());
			Assert.assertEquals(null, winner.getDealId());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public final void testMultiBidDealsLowerThanFloorNonDealWinsForDealPrice() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final float dealFloor1 = 3.f;
		final float dealFloor2 = 3.15f;
		final String currency = "USD";

		final String deal_id_1 = "998877";
		final String deal_id_2 = "998866";
		final String requestId1 = "r-id-1";
		final String requestId2 = "r-id-2";
        final String requestId3 = "r-id-3";
        final String requestId4 = "r-id-4";

		// bidrequest
		final BidRequest bidRequest1 = RequestResponseHelper.createRequest(requestId1, impFloor, dealFloor1, currency, deal_id_1, 0).build();
		// bidresponse1 Deal
		final float bidPrice1 = 2.8f;
		final BidResponse response1 = RequestResponseHelper.createResponse(requestId1, bidPrice1, currency, deal_id_1, "ad-id", "c-id", "cr-id");
		// response1.setSupplier(supplier1);
		bidExchange.setBidRequest(supplier1, bidRequest1);
		bidExchange.setBidResponse(supplier1, response1);

		// bidrequest
		final BidRequest bidRequest2 = RequestResponseHelper.createRequest(requestId2, impFloor, dealFloor2, currency, deal_id_2, 0).build();
		// bidresponse2 Deal
		final float bidPrice2 = 2.20f;
		final BidResponse response2 = RequestResponseHelper.createResponse(requestId2, bidPrice2, currency, deal_id_2, "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier3, bidRequest2);
		bidExchange.setBidResponse(supplier3, response2);

		// bidresponse3 NON Deal
		final float bidPrice3 = 1.10f;
		final BidResponse response3 = RequestResponseHelper.createResponse(requestId3, bidPrice3, currency, "deal-id", "ad-id", "c-id", "cr-id");
		// response3.setSupplier(supplier2);
		bidExchange.setBidRequest(supplier2, bidRequest1);
		bidExchange.setBidResponse(supplier2, response3);

		// bidresponse4 NON Deal
		final float bidPrice4 = 1.50f;
		final BidResponse response4 = RequestResponseHelper.createResponse(requestId4, bidPrice4, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidRequest(supplier4, bidRequest1);
		bidExchange.setBidResponse(supplier4, response4);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(2.21079f, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(2.210790075f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier1.getShortName(), winner.getSupplier().getShortName());
			Assert.assertEquals(null, winner.getDealId());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testMultiBid() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final String currency = "USD";
		final String requestId1 = "r-id-1";

		final BidRequest bidRequest = RequestResponseHelper.createRequest(requestId1, impFloor, 0, currency, "deal-id", 0).build();
		bidExchange.setBidRequest(supplier1, bidRequest);

		final float[] prices = { 2.8f, 2.5f, 2.3f, 2.6f, 1.3f, 1.77f };
		final BidResponse response = RequestResponseHelper.createResponseMultiBid(prices, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidResponse(supplier1, response);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(2.6107900142669678, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(2.610789402f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier1.getShortName(), winner.getSupplier().getShortName());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}

	}

	@Test
	public void testMultiBidMultiSeat() {
		final BidExchange bidExchange = new BidExchange();
		final float impFloor = 0.88f;
		final String currency = "USD";
		final String requestId1 = "r-id-1";

		final BidRequest bidRequest = RequestResponseHelper.createRequest(requestId1, impFloor, 0, currency, "deal-id", 0).build();
		bidExchange.setBidRequest(supplier1, bidRequest);

		final float[][] prices = { { 2.8f, 2.5f }, { 2.3f, 2.6f }, { 1.3f, 1.77f } };
		final BidResponse response = RequestResponseHelper.createResponseMultiBidMultiSeat(prices, currency, "deal-id", "ad-id", "c-id", "cr-id");
		bidExchange.setBidResponse(supplier1, response);

		BiddingServiceInfo info = new BiddingServiceInfo();
		try {
			final Auction.SingularAuctionResult winner = (Auction.SingularAuctionResult) Auction.auctioneer(info, bidExchange);
			Assert.assertTrue(winner.isValid());
			Assert.assertEquals(2.61079f, winner.getPrice(), 0);
			final float currencyRateEUR = CurrencyCache.instance.get(currency);
			Assert.assertEquals(FloatComparator.rr(2.610789402f / currencyRateEUR), winner.getExchangedCurrencyPrice(), 0);
			Assert.assertEquals(supplier1.getShortName(), winner.getSupplier().getShortName());
		} catch (final InvalidBidException e) {
			Assert.fail("Exception thrown: " + e.getMessage());
		}

	}

}
