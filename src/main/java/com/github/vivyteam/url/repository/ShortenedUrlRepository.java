package com.github.vivyteam.url.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.vivyteam.url.entity.ShortenedUrl;

public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Integer> {

	List<ShortenedUrl> findByHashedUrl(String hashedUrl);

}
