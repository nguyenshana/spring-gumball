package com.example.springgumballv3;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GumballModelRepository extends JpaRepository<GumballModel, Long> {

	// https://springframework.guru/spring-data-jpa-query/
	@Query("SELECT g FROM GumballModel g WHERE g.serialNumber = ?1")
	Optional<GumballModel> findBySerialNumber(String serialNumber);

}