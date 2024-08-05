package com.giffing.wicket.spring.boot.example;

import org.apache.commons.codec.binary.Base32;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

import static com.giffing.wicket.spring.boot.example.totp.TOTPAuthenticator.getTOTPCode;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.giffing.wicket.spring.boot.example.web",
		"com.giffing.wicket.spring.boot.example.repository",
		"com.giffing.wicket.spring.boot.example.web.security",
		"com.giffing.wicket.spring.boot.example.web.pages.login"
})
//@EnableJpaRepositories(basePackageClasses={DefaultRepositoryService.class})
public class WicketApplication {
//	public static String secretKey = "2WIIK56LJ64UMRKO27GQVWYM5XLMQVXU";
	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder()
			.sources(WicketApplication.class)
			.run(args);
		System.out.println("sdd");
//		getLastCode();
	}

//	public static void getLastCode() {
//		String lastCode = null;
//		while (true) {
//			String code = getTOTPCode(secretKey);
//			if(!code.equals(lastCode)) {
//				System.out.println(code + "  last code is " + lastCode);
//			}
//			lastCode = code;
//			try{
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				System.out.println("error my thread code generate");
//			}
//		}
//	}
//	public static void generateSecretKey() {
//		SecureRandom secureRandom = new SecureRandom();
//		byte[] key = new byte[20];
//		secureRandom.nextBytes(key);
//		Base32 base32 = new Base32();
//		String secretKey = base32.encodeToString(key);
//		System.out.println(secretKey + "  this secret key");
//	}
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	
}
