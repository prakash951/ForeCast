package com.forecast.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

	@Autowired
	private Environment env;

	public String getKey() {
		return env.getProperty("oauth_consumer_key");
	}

	public String getSecret() {
		return env.getProperty("oauth_consumer_secret");
	}

	public String getAppid() {
		return env.getProperty("oauth_consumer_appid");
	}
	
	public String getUrl()
	{
		return env.getProperty("oauth_consumer_url");
	}

}
