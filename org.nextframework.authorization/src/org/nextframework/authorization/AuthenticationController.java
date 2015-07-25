package org.nextframework.authorization;

/**
 * When the an URL that requires authorization is requested and no user is logged in,
 * next will delegate to the controller that implements this interface the request.
 * 
 * @author rogelgarcia
 *
 */
public interface AuthenticationController {

	String getPath();
}
