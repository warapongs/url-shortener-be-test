package com.github.vivyteam.url.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base62Util {
	
	private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public static String fromBase10(int num) {
		 StringBuilder sb = new StringBuilder();
		 while (num > 0) {
			 sb.insert(0, BASE62_CHARS.charAt(num % BASE62_CHARS.length()));
			 num /= BASE62_CHARS.length();
		 }
		 return sb.toString();
	}
	
	public static int toBase10(String base62) {
		int num = 0;
		for (char ch : base62.toCharArray()) {
			num = num * 62 + intValueOf(ch);
		}
		return num;
	}
	
	static int intValueOf(char ch) {
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		} else if ('A' <= ch && ch <= 'Z') {
			return ch - 'A' + 10;
		} else if ('a' <= ch && ch <= 'z') {
			return ch - 'a' + 36;
		}
		throw new Base62Exception(String.format("'%s' is not a valid base62 character", ch));
	}

}
