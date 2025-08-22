package com.thonmay.jwt_mfa_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.thonmay.jwt_mfa_api.model.Role;
import com.thonmay.jwt_mfa_api.model.User;
import com.thonmay.jwt_mfa_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JwtMfaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtMfaApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				System.out.println("Creating ADMIN user...");
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("adminpass"));
				admin.setRole(Role.ADMIN);
				admin.setMfaEnabled(false);
				userRepository.save(admin);
				System.out.println("ADMIN user created.");
			}
		};
	}
}
