package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PinceletasUserAuthServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(PinceletasUserAuthServiceApplication.class, args);
	}

}
