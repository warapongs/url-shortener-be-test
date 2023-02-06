package com.github.vivyteam.url.service;

import java.util.Optional;

public interface UrlShortener {

	String shortenUrl(String fullUrl);

	Optional<String> getFullUrl(String shortenedUrl);

}
