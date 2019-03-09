package com.forecast.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forecast.entity.Temperature;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {

}
