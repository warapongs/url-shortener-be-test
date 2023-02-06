package com.github.vivyteam.url.util;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Base62UtilTest {

	@Test
	void base32UtilTest() {
		int iterations = 100;
		int min = 0;
		int max = 100_000_000;
		for (int i = 0; i < iterations; i++) {
			int rand = ThreadLocalRandom.current().nextInt(min, max + 1);
			String base62 = Base62Util.fromBase10(rand);
			int base10 = Base62Util.toBase10(base62);
			Assertions.assertEquals(rand, base10);
		}
	}
	
}
