package com.dynalar.dynalar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class DynalarApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynalarApplication.class, args);
	}
}