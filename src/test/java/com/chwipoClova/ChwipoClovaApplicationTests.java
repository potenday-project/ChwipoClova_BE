package com.chwipoClova;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class ChwipoClovaApplicationTests {

	@Test
	void contextLoads() {

		Date a = new Date();

		System.out.println(a.getTime());
		Date b = new Date(a.getTime() + (30 * 60 * 1000L));
		System.out.println(b);
	}

}
