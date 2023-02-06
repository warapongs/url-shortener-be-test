package com.github.vivyteam.url.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UrlUtilTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"https://www.sample.edu/?writing=health",
			"https://sample.net/?desk=noise&rice=rake",
			"https://www.sample.org/flowers",
			"http://aftermath.sample.net/driving/volleyball.html",
			"https://sample.edu/cub#river",
			"https://www.sample.net/furniture#sand"
	})
	void shouldReturnTrue_givenValidUrl(String url) {
		Assertions.assertTrue(UrlUtil.isValidUrl(url));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"httpx://www.sample.edu/?writing=health",
			"https//sample.net/?desk=noise&rice=rake",
			"example.com"
	})
	void shouldReturnFalse_givenInvalidUrl(String url) {
		Assertions.assertFalse(UrlUtil.isValidUrl(url));
	}

}
