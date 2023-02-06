package com.github.vivyteam.url.util;

import java.net.URL;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtil {

	public static boolean isValidUrl(String url) {
		try {
	        new URL(url).toURI();
	        return true;
	    } catch (Exception ex) {
	        return false;
	    }
	}
}
