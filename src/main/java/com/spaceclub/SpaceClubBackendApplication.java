package com.spaceclub;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class SpaceClubBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpaceClubBackendApplication.class, args);
	}

}
