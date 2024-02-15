package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TourguideApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TourguideApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
