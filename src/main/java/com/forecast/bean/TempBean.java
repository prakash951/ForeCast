package com.forecast.bean;

import java.util.Date;

public class TempBean {
	
	
	private String dayoftheweek;

	private Integer lowtemperature;

	private Integer hightemperature;

	private String weather;
	
	private String city;
	
	private String country;
	
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDayoftheweek() {
		return dayoftheweek;
	}

	public void setDayoftheweek(String dayoftheweek) {
		this.dayoftheweek = dayoftheweek;
	}

	public Integer getLowtemperature() {
		return lowtemperature;
	}

	public void setLowtemperature(Integer lowtemperature) {
		this.lowtemperature = lowtemperature;
	}

	public Integer getHightemperature() {
		return hightemperature;
	}

	public void setHightemperature(Integer hightemperature) {
		this.hightemperature = hightemperature;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
