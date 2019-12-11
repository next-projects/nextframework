package org.nextframework.controller;

import java.util.Locale;

public interface RequestLocaleAware {

	public Locale getLocale();

	public void setLocale(Locale locale);

}