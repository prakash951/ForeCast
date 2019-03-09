package com.forecast.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.forecast.bean.TempBean;
import com.forecast.service.ForecastService;

@RestController
public class ForecastController {

	@Autowired
	ForecastService myForecastService;

	@GetMapping("/forecast/{city}")
	public Response getForecast(@PathVariable String city) {
		String[] citys = city.split(",");
		List<TempBean> list = new ArrayList<>();
		for (String c : citys) {
			TempBean tempBean = myForecastService.getResponse(c);
			if (tempBean != null)
				list.add(tempBean);
		}
		return Response.status(200).entity(list).build();
	}

}
