package com.giffing.wicket.spring.boot.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.giffing.wicket.spring.boot.example.web",
		"com.giffing.wicket.spring.boot.example.repository",
		"com.giffing.wicket.spring.boot.example.web.security",
		"com.giffing.wicket.spring.boot.example.web.pages.login"
})
//@EnableJpaRepositories(basePackageClasses={DefaultRepositoryService.class})
public class WicketApplication {

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder()
			.sources(WicketApplication.class)
			.run(args);
		System.out.println("sdd");
	}
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	
}
