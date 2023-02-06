package com.github.vivyteam.url.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.vivyteam.url.entity.ShortenedUrl;
import com.github.vivyteam.url.exception.InvalidShortenedUrlException;
import com.github.vivyteam.url.repository.ShortenedUrlRepository;
import com.github.vivyteam.url.util.Base62Util;

class UrlShortenerImplTest {

	@Test
	void shouldReturnShortenedUrl_whenShortenUrl_givenNewFullUrl() {
		// mock repository
		ShortenedUrlRepository mockedRepository = Mockito.mock(ShortenedUrlRepository.class);

		// mock entity
		int id = 123_456;
		String fullUrl = "http://example.com";
		ShortenedUrl mockedEntity = new ShortenedUrl();
		mockedEntity.setId(id);
		mockedEntity.setFullUrl(fullUrl);
		mockedEntity.setHashedUrl(DigestUtils.md5Hex(fullUrl));

		// service under the test
		UrlShortenerImpl service = new UrlShortenerImpl(mockedRepository);

		// given
		when(mockedRepository.findByHashedUrl(anyString())).thenReturn(Collections.emptyList());
		when(mockedRepository.save(any(ShortenedUrl.class))).thenReturn(mockedEntity);

		// when
		String shortenedUrl = service.shortenUrl(fullUrl);

		// then
		assertEquals(Base62Util.fromBase10(id), shortenedUrl);
	}

	@Test
	void shouldReturnShortenedUrl_whenShortenUrl_givenExistingFullUrl() {
		// mock repository
		ShortenedUrlRepository mockedRepository = Mockito.mock(ShortenedUrlRepository.class);

		// mock entity
		int id = 123_456;
		String fullUrl = "http://example.com";
		ShortenedUrl mockedEntity = new ShortenedUrl();
		mockedEntity.setId(id);
		mockedEntity.setFullUrl(fullUrl);
		mockedEntity.setHashedUrl(DigestUtils.md5Hex(fullUrl));

		// service under the test
		UrlShortenerImpl service = new UrlShortenerImpl(mockedRepository);

		// given
		when(mockedRepository.findByHashedUrl(anyString())).thenReturn(Arrays.asList(mockedEntity));

		// when
		String actual = service.shortenUrl(fullUrl);

		// then
		assertEquals(Base62Util.fromBase10(id), actual);
	}

	@Test
	void shouldReturnTrue_whenGetFullUrl_givenExistingShortenedUrl() {
		// mock repository
		ShortenedUrlRepository mockedRepository = Mockito.mock(ShortenedUrlRepository.class);

		// mock entity
		int id = 123_456;
		String fullUrl = "http://example.com";
		ShortenedUrl mockedEntity = new ShortenedUrl();
		mockedEntity.setId(id);
		mockedEntity.setFullUrl(fullUrl);
		mockedEntity.setHashedUrl(DigestUtils.md5Hex(fullUrl));

		// service under the test
		UrlShortenerImpl service = new UrlShortenerImpl(mockedRepository);

		// given
		String shortenedurl = Base62Util.fromBase10(123_456);
		when(mockedRepository.findById(anyInt())).thenReturn(Optional.ofNullable(mockedEntity));
		
		// when
		Optional<String> actual = service.getFullUrl(shortenedurl);
		
		// then
		assertTrue(actual.isPresent());
		assertEquals(fullUrl, actual.get());
	}
	
	@Test
	void shouldReturnFalse_whenGetFullUrl_givenNonExistingShortenedUrl() {
		// mock repository
		ShortenedUrlRepository mockedRepository = Mockito.mock(ShortenedUrlRepository.class);

		// service under the test
		UrlShortenerImpl service = new UrlShortenerImpl(mockedRepository);

		// given
		String shortenedurl = Base62Util.fromBase10(123_456);
		when(mockedRepository.findById(anyInt())).thenReturn(Optional.empty());
		
		// when
		Optional<String> actual = service.getFullUrl(shortenedurl);
		
		// then
		assertFalse(actual.isPresent());
	}
	
	@Test
	void shouldThrowException_whenGetFullUrl_givenInvalidShortenedUrl() {
		// mock repository
		ShortenedUrlRepository mockedRepository = Mockito.mock(ShortenedUrlRepository.class);

		// service under the test
		UrlShortenerImpl service = new UrlShortenerImpl(mockedRepository);

		// given
		String shortenedurl = "123=+/";
		
		// then
		assertThrows(InvalidShortenedUrlException.class, () -> {
			// when
			service.getFullUrl(shortenedurl);	
		});
	}
}
