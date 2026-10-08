package com.example.UrlShortnerUsingRedis;

import com.example.UrlShortnerUsingRedis.Services.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UrlShortnerUsingRedisApplicationTests {

	@Autowired
	private UrlService service;

	@Test
	void testGenerateShortCode() {
		String test = service.generateShortCode();
		System.out.println(test);
	}
}
