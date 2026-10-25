package org.nextframework.authorization;

/**
 * Encodes passwords for storage and verifies supplied passwords against their
 * encoded representation.
 */
public interface PasswordEncoder {

	String encode(CharSequence rawPassword);

	boolean matches(CharSequence rawPassword, String encodedPassword);
}
