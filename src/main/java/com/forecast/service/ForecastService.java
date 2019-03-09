package com.forecast.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.forecast.bean.TempBean;
import com.forecast.config.AppConfig;
import com.forecast.entity.Temperature;
import com.forecast.repo.TemperatureRepository;

@Component
public class ForecastService {

	@Autowired
	private AppConfig myAppConfig;
	@Autowired
	private TemperatureRepository myTempratureRepository;

	private String appId = null;
	private String consumerKey = null;
	private String consumerSecret = null;
	private String url = null;
	private String oauthNonce = null;
	private long timestamp;

	public TempBean getResponse(String city) {
		appId = myAppConfig.getAppid();
		consumerKey = myAppConfig.getKey();
		consumerSecret = myAppConfig.getSecret();
		url = myAppConfig.getUrl();
		getOauthNonce();
		String signature = getSignature(city);
		return getResponse(city, signature);
	}

	private void getOauthNonce() {
		timestamp = new Date().getTime() / 1000;
		byte[] nonce = new byte[32];
		Random rand = new Random();
		rand.nextBytes(nonce);
		oauthNonce = new String(nonce).replaceAll("\\W", "");
	}

	private String getSignature(String city) {

		List<String> parameters = new ArrayList<String>();
		parameters.add("oauth_consumer_key=" + consumerKey);
		parameters.add("oauth_nonce=" + oauthNonce);
		parameters.add("oauth_signature_method=HMAC-SHA1");
		parameters.add("oauth_timestamp=" + timestamp);
		parameters.add("oauth_version=1.0");
		try {
			parameters.add("location=" + URLEncoder.encode(city + ",in", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		parameters.add("u=c");
		parameters.add("format=json");
		Collections.sort(parameters);

		StringBuffer parametersList = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
		}

		String signatureString = "";
		try {
			signatureString = "GET&" + URLEncoder.encode(url, "UTF-8") + "&"
					+ URLEncoder.encode(parametersList.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String signature = null;
		try {
			SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
			Encoder encoder = Base64.getEncoder();
			signature = encoder.encodeToString(rawHMAC);
		} catch (Exception e) {
			System.err.println("Unable to append signature");
		}
		return signature;
	}

	private TempBean getResponse(String city,String signature)
	{
        String authorizationLine = "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\", " +
                "oauth_nonce=\"" + oauthNonce + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_signature_method=\"HMAC-SHA1\", " +
                "oauth_signature=\"" + signature + "\", " +
                "oauth_version=\"1.0\"";
            
		CloseableHttpClient client = null;
		client = HttpClients.custom().build();

		HttpGet httpGet = new HttpGet(url + "?location=" + city + ",in&format=json&u=c");
		httpGet.setHeader("Authorization", authorizationLine);

		httpGet.setHeader("X-Yahoo-App-Id", appId);
		httpGet.setHeader("Content-type", "application/json");
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (IOException e) {

			e.printStackTrace();
		}
		try {
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				String res = EntityUtils.toString(response.getEntity());
				JSONObject jsonObj = new JSONObject(res);
				JSONObject j = (JSONObject) (jsonObj.get("location"));
				if (j.has("city")) {
					city = (String) j.get("city");
					String country = (String) j.get("country");
					JSONArray jsonArray = ((JSONArray) (jsonObj.getJSONArray("forecasts")));
					jsonObj = (JSONObject) jsonArray.get(1);
					String day = (String) jsonObj.get("day");
					Integer low = (Integer) jsonObj.get("low");
					Integer high = (Integer) jsonObj.get("high");
					long d = ((Integer) jsonObj.get("date")).longValue() * 1000;
					Date now = new Date(d);
					Instant current = now.toInstant();
					LocalDateTime ldt = LocalDateTime.ofInstant(current, ZoneId.systemDefault());
					String text = (String) jsonObj.get("text");
					Temperature temp = new Temperature();
					temp.setCity(city);
					temp.setCountry(country);
					temp.setDay(day);
					temp.setText(text);
					temp.setLow(low);
					temp.setHigh(high);
					temp.setDate(ldt);
					myTempratureRepository.save(temp);
					TempBean tempBean = new TempBean();
					tempBean.setCity(city);
					tempBean.setCountry(country);
					tempBean.setDayoftheweek(day);
					tempBean.setLowtemperature(low);
					tempBean.setHightemperature(high);
					tempBean.setWeather(text);
					tempBean.setDate(ldt.toString());
					return tempBean;
				}
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
