package com.github.vivyteam.url.api;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.vivyteam.url.api.contract.FullUrl;
import com.github.vivyteam.url.api.contract.ShortenedUrl;
import com.github.vivyteam.url.exception.InvalidShortenedUrlException;
import com.github.vivyteam.url.exception.InvalidUrlException;
import com.github.vivyteam.url.service.UrlShortener;
import com.github.vivyteam.url.util.UrlUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UrlApi {

	private UrlShortener urlShortener;
	private String baseUrl;

	public UrlApi(UrlShortener urlShortener, @Value("${url-shortener.baseUrl}") String baseUrl) {
		this.urlShortener = urlShortener;
		this.baseUrl = baseUrl;
	}

	@GetMapping("/{url}/short")
	public Mono<ShortenedUrl> shortUrl(@PathVariable final String url) {
		if (!UrlUtil.isValidUrl(url)) {
			throw new InvalidUrlException(String.format("'%s' is not a valid URL", url));
		}
		String shortenedUrl = urlShortener.shortenUrl(url);
		return Mono.just(new ShortenedUrl(String.format("%s/%s", this.baseUrl, shortenedUrl)));
	}

	@GetMapping("/{shortenedUrl}/full")
	public Mono<FullUrl> getFullUrl(@PathVariable final String shortenedUrl) {
		Optional<String> fullUrl = urlShortener.getFullUrl(shortenedUrl);
		if (!fullUrl.isPresent()) {
			throw new InvalidShortenedUrlException(String.format("Shortened URL '%s' is not found", shortenedUrl));
		}
		return Mono.just(new FullUrl(fullUrl.get()));
	}

	@GetMapping("/{shortenedUrl}")
	public Mono<Void> redirectFullUrl(@PathVariable final String shortenedUrl, ServerHttpResponse response) {
		Optional<String> fullUrl = urlShortener.getFullUrl(shortenedUrl);
		if (!fullUrl.isPresent()) {
			throw new InvalidShortenedUrlException(String.format("Shortened URL '%s' is not found", shortenedUrl));
		}
		response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create(fullUrl.get()));
        return response.setComplete();
	}
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Invalid shortened URL")
	@ExceptionHandler(InvalidShortenedUrlException.class)
	public void invalidShortenedUrlHandler(Exception ex) {
		log.error(ex.getMessage());
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid URL")
	@ExceptionHandler(InvalidUrlException.class)
	public void invalidUrlHandler(Exception ex) {
		log.error(ex.getMessage());
	}

}
