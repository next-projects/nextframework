package org.nextframework.authorization;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password encoder using PBKDF2-HMAC-SHA-256 with a random salt.
 */
public final class Pbkdf2PasswordEncoder implements PasswordEncoder {

	private static final String PREFIX = "pbkdf2-sha256";
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int DEFAULT_ITERATIONS = 600_000;
	private static final int MIN_ACCEPTED_ITERATIONS = 600_000;
	private static final int MAX_ACCEPTED_ITERATIONS = 2_000_000;
	private static final int SALT_LENGTH = 16;
	private static final int MIN_SALT_LENGTH = 16;
	private static final int MAX_SALT_LENGTH = 64;
	private static final int HASH_LENGTH = 32;
	private static final int MIN_HASH_LENGTH = 16;
	private static final int MAX_HASH_LENGTH = 64;

	private final SecureRandom secureRandom = new SecureRandom();
	private final int iterations;

	public Pbkdf2PasswordEncoder() {
		this(DEFAULT_ITERATIONS);
	}

	public Pbkdf2PasswordEncoder(int iterations) {
		if (iterations < MIN_ACCEPTED_ITERATIONS || iterations > MAX_ACCEPTED_ITERATIONS) {
			throw new IllegalArgumentException("PBKDF2 iterations must be between " + MIN_ACCEPTED_ITERATIONS + " and " + MAX_ACCEPTED_ITERATIONS);
		}
		this.iterations = iterations;
	}

	@Override
	public String encode(CharSequence rawPassword) {
		if (rawPassword == null) {
			throw new IllegalArgumentException("Raw password must not be null");
		}
		byte[] salt = new byte[SALT_LENGTH];
		secureRandom.nextBytes(salt);
		byte[] hash = derive(rawPassword, salt, iterations, HASH_LENGTH);
		return PREFIX + "$" + iterations + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (rawPassword == null || encodedPassword == null) {
			return false;
		}
		String[] parts = encodedPassword.split("\\$", -1);
		if (parts.length != 4 || !PREFIX.equals(parts[0])) {
			return false;
		}
		try {
			int iterations = Integer.parseInt(parts[1]);
			byte[] salt = Base64.getDecoder().decode(parts[2]);
			byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
			if (iterations < MIN_ACCEPTED_ITERATIONS || iterations > MAX_ACCEPTED_ITERATIONS
					|| salt.length < MIN_SALT_LENGTH || salt.length > MAX_SALT_LENGTH
					|| expectedHash.length < MIN_HASH_LENGTH || expectedHash.length > MAX_HASH_LENGTH) {
				return false;
			}
			byte[] actualHash = derive(rawPassword, salt, iterations, expectedHash.length);
			return MessageDigest.isEqual(expectedHash, actualHash);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private byte[] derive(CharSequence rawPassword, byte[] salt, int iterations, int hashLength) {
		char[] password = rawPassword.toString().toCharArray();
		PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, hashLength * Byte.SIZE);
		Arrays.fill(password, '\0');
		try {
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(ALGORITHM + " is not available", e);
		} finally {
			keySpec.clearPassword();
		}
	}
}
