package org.nextframework.message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nextframework.core.standard.Next;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

public class MessageResolverFactory {

	private static Map<Locale, MessageResolver> cache = new HashMap<Locale, MessageResolver>();

	public static MessageResolver getDefault() {
		return get(Locale.getDefault());
	}

	public static MessageResolver get(Locale locale) {
		MessageResolver resolver = cache.get(locale);
		if (resolver == null) {
			resolver = new MessageResolverImpl(Next.getMessageSource(), locale);
			cache.put(locale, resolver);
		}
		return resolver;
	}

	public static class MessageResolverImpl implements MessageResolver {

		private MessageSource messageSource;
		private Locale locale;

		private MessageResolverImpl(MessageSource messageSource, Locale locale) {
			this.messageSource = messageSource;
			this.locale = locale;
		}

		@Override
		public Locale getLocale() {
			return locale;
		}

		@Override
		public String message(String code) {
			return messageSource.getMessage(code, null, locale);
		}

		@Override
		public String message(String code, Object[] args) {
			return messageSource.getMessage(code, args, locale);
		}

		@Override
		public String message(String code, Object[] args, String defaultValue) {
			return messageSource.getMessage(code, args, defaultValue, locale);
		}

		@Override
		public String message(MessageSourceResolvable resolvable) {
			return messageSource.getMessage(resolvable, locale);
		}

	}

}