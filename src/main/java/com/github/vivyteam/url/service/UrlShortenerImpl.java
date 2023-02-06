package com.github.vivyteam.url.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.github.vivyteam.url.entity.ShortenedUrl;
import com.github.vivyteam.url.exception.InvalidShortenedUrlException;
import com.github.vivyteam.url.repository.ShortenedUrlRepository;
import com.github.vivyteam.url.util.Base62Exception;
import com.github.vivyteam.url.util.Base62Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UrlShortenerImpl implements UrlShortener {
	
	private ShortenedUrlRepository repository;
	
	public UrlShortenerImpl(ShortenedUrlRepository repository) {
		this.repository = repository;
	}

	@Override
	public String shortenUrl(String fullUrl) {
		Optional<ShortenedUrl> opt = getShortenedUrlFor(fullUrl);
		if (opt.isPresent()) {
			log.debug("ShortenedUrl has already been created for {}", fullUrl);
			return Base62Util.fromBase10(opt.get().getId());
		}
		
		ShortenedUrl shortenedUrl = new ShortenedUrl();
		shortenedUrl.setFullUrl(fullUrl);
		shortenedUrl.setHashedUrl(DigestUtils.md5Hex(fullUrl));
		shortenedUrl = this.repository.save(shortenedUrl);
		return Base62Util.fromBase10(shortenedUrl.getId());
	}

	@Override
	public Optional<String> getFullUrl(String shortenedUrl) {
		int id;
		try {
			id = Base62Util.toBase10(shortenedUrl);
		} catch (Base62Exception ex) {
			throw new InvalidShortenedUrlException(String.format("Cannot parse shortened URL '%s'", shortenedUrl), ex);
		}
		Optional<ShortenedUrl> opt = this.repository.findById(id);
		return opt.map(ShortenedUrl::getFullUrl);
	}
	
	private Optional<ShortenedUrl> getShortenedUrlFor(String fullUrl) {
		String hashedUrl = DigestUtils.md5Hex(fullUrl);
		List<ShortenedUrl> shortenedUrls = this.repository.findByHashedUrl(hashedUrl);
		if (!shortenedUrls.isEmpty()) {
			return shortenedUrls.stream().filter(s -> s.getFullUrl().equals(fullUrl)).findFirst();
		}
		return Optional.empty();
	}

}
