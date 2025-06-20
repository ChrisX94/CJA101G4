package com.shakemate.adm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	//encoded password
	public static String hashPassword(String plainPassword) {
		return encoder.encode(plainPassword);
	}
	
	//verify password
	public static boolean matchPassword(String plainPassword, String hashedPassword) {
		return encoder.matches(plainPassword, hashedPassword);
	}
	
	// Testing for encoded password 
	public static void main(String[] args) {
	    String raw = "admin123";
	    String encoded = new BCryptPasswordEncoder().encode(raw);
	    System.out.println(encoded);
	}
}
