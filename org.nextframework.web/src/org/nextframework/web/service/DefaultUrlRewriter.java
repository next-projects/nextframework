package org.nextframework.web.service;

/**
 * Reescreve URLs geradas
 * @author rogel
 *
 */
public class DefaultUrlRewriter implements UrlRewriter {

	@Override
	public String rewriteUrl(String url) {
		return url;
	}

}
