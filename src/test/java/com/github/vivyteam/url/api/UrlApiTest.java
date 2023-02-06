package com.github.vivyteam.url.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.github.vivyteam.url.service.UrlShortener;
import com.github.vivyteam.url.util.Base62Util;

@WebFluxTest(UrlApi.class)
class UrlApiTest {
	
	@Autowired
    private WebTestClient webClient;
	
	@MockBean
	private UrlShortener urlShortener;
	
	@Value("${url-shortener.baseUrl}")
	private String baseUrl;

	@Test
	void shouldReturn200WithShortenedUrl_whenShortenUrl_givenValidFullUrl() {
		String shortenedUrl = Base62Util.fromBase10(123_456);
		
		// given
		String fullUrl = "http://example.com";
		when(urlShortener.shortenUrl(anyString())).thenReturn(shortenedUrl);
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{fullUrl}/short").build(fullUrl))
				.exchange();
		
		//then
		response.expectStatus()
					.isOk()
				.expectBody()
					.jsonPath("url").isEqualTo(String.format("%s/%s", baseUrl, shortenedUrl));
	}
	
	@Test
	void shouldReturn400_whenShortenUrl_givenInvalidFullUrl() {
		// given
		String fullUrl = "httpx://example.com";
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{fullUrl}/short").build(fullUrl))
				.exchange();
		
		//then
		response.expectStatus()
					.isBadRequest();
	}
	
	@Test
	void shouldReturn200WithFullUrl_whenGetFullUrl_givenExistingShortenedUrl() {
		// given
		String shortenedUrl = Base62Util.fromBase10(123_456);
		String fullUrl = "http://example.com";
		when(urlShortener.getFullUrl(anyString())).thenReturn(Optional.ofNullable(fullUrl));
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{shortenedUrl}/full").build(shortenedUrl))
				.exchange();
		
		//then
		response.expectStatus()
					.isOk()
				.expectBody()
					.jsonPath("url").isEqualTo(fullUrl);
	}
	
	@Test
	void shouldReturn404_whenGetFullUrl_givenNonExistingShortenedUrl() {
		// given
		String shortenedUrl = Base62Util.fromBase10(123_456);
		when(urlShortener.getFullUrl(anyString())).thenReturn(Optional.empty());
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{shortenedUrl}/full").build(shortenedUrl))
				.exchange();
		
		//then
		response.expectStatus()
					.isNotFound();
	}
	
	@Test
	void shouldReturn400_whenGetFullUrl_givenInvalidShortenedUrl() {
		// given
		when(urlShortener.getFullUrl(anyString())).thenReturn(Optional.empty());
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{shortenedUrl}/full").build("abc123+/="))
				.exchange();
		
		//then
		response.expectStatus()
					.isNotFound();
	}
	
	@Test
	void shouldReturn302WithLocation_whenRedirect_givenExistingShortenedUrl() {
		// given
		String shortenedUrl = Base62Util.fromBase10(123_456);
		String fullUrl = "http://example.com";
		when(urlShortener.getFullUrl(anyString())).thenReturn(Optional.ofNullable(fullUrl));
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{shortenedUrl}").build(shortenedUrl))
				.exchange();
		
		//then
		response.expectStatus()
					.isTemporaryRedirect()
				.expectHeader()
					.valueEquals("Location", fullUrl);
	}
	
	@Test
	void shouldReturn400_whenRedirect_givenNonExistingShortenedUrl() {
		// given
		String shortenedUrl = Base62Util.fromBase10(123_456);
		when(urlShortener.getFullUrl(anyString())).thenReturn(Optional.empty());
		
		// when
		ResponseSpec response = webClient.get()
				.uri(builder -> builder.path("/{shortenedUrl}").build(shortenedUrl))
				.exchange();
		
		//then
		response.expectStatus()
					.isNotFound();
	}
}
