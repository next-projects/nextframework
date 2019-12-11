package org.nextframework.message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nextframework.core.standard.Next;
import org.nextframework.util.Util;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

public class MessageResolverFactory {

	private static Map<Locale, MessageResolver> cache = new HashMap<Locale, MessageResolver>();

	public static MessageResolver getDefault() {
		return get(Locale.getDefault());
	}

	public static synchronized MessageResolver get(Locale locale) {
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
			Object[] args2 = convertArgs(args);
			return messageSource.getMessage(code, args2, locale);
		}

		@Override
		public String message(String code, Object[] args, String defaultValue) {
			Object[] args2 = convertArgs(args);
			return messageSource.getMessage(code, args2, defaultValue, locale);
		}

		@Override
		public String message(MessageSourceResolvable resolvable) {
			Object[] stringArgs = convertArgs(resolvable.getArguments());
			MessageSourceResolvable resolvable2 = Util.objects.newMessage(resolvable.getCodes()[0], stringArgs, resolvable.getDefaultMessage());
			return messageSource.getMessage(resolvable2, locale);
		}

		private Object[] convertArgs(Object[] args) {
			if (args == null || args.length == 0) {
				return args;
			}
			Object[] stringArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				stringArgs[i] = Util.strings.toStringDescription(args[i], "dd/MM/yyyy HH:mm:ss", null);
			}
			return stringArgs;
		}

	}

}