package com.getbux.api;

import com.getbux.common.TradingRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import java.io.IOException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

import static com.getbux.configuration.AppConfiguration.API_URL;
import static com.getbux.configuration.AppConfiguration.BUY_PATH;
import static com.getbux.configuration.AppConfiguration.SELL_PATH;
import static com.getbux.constants.Headers.*;
import static com.getbux.utils.JSONUtils.mapper;

public class TradingAPIClient {


    public static String buy(TradingRequest tradingRequest) throws IOException {
        String url = API_URL + BUY_PATH;
        BuyRequest buyRequest = new DefaultBuyRequest(tradingRequest.getProductId());
        String body = mapper.writeValueAsString(buyRequest);

        HttpResponse response = executeRequest(Request.Post(url), body);
        logRequest(url, "POST", response);

        String responseBody = EntityUtils.toString(response.getEntity());
        return mapper.readValue(responseBody, BuyResponse.class).getPositionId();
    }

    public static void sell(TradingRequest tradingRequest) throws IOException {
        String url = API_URL + SELL_PATH + tradingRequest.getProductId();

        HttpResponse response = executeRequest(Request.Delete(url), null);

        logRequest(url, "DELETE", response);
    }

    private static HttpResponse executeRequest(Request request, String body) throws IOException {
        request.addHeader(AUTH_HEADER_NAME, AUTH_HEADER_VALUE)
                .addHeader(LANGUAGE_HEADER_NAME, LANGUAGE_HEADER_VALUE)
                .addHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE);

        if(!StringUtils.isEmpty(body)) {
            request.bodyByteArray(body.getBytes());
        }

        return request.execute().returnResponse();
    }

    private static void logRequest(String url, String method, HttpResponse response) {
        System.out.println("Sending " + method + " request to URL : " + url);
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());
    }

}
