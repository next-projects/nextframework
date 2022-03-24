package org.nextframework.message;

import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.nextframework.core.standard.Next;
import org.springframework.context.MessageSourceResolvable;

/**
 * Create a file "log4j2.component.properties" in the classpath.
 * Write inside:
 * log4j2.messageFactory=org.nextframework.message.NextLocalizedMessageFactory
 */
public class NextLocalizedMessageFactory extends AbstractMessageFactory {

	private static final long serialVersionUID = 1L;

	private ParameterizedMessageFactory delegateFactory = new ParameterizedMessageFactory();

	@Override
	public Message newMessage(final Object message) {
		if (message instanceof MessageSourceResolvable) {
			return new NextLocalizedMessage((MessageSourceResolvable) message);
		}
		return super.newMessage(message);
	}

	@Override
	public Message newMessage(final String message, final Object... params) {
		return delegateFactory.newMessage(message, params);
	}

	@Override
	public Message newMessage(final String message, final Object p0) {
		return delegateFactory.newMessage(message, p0);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1) {
		return delegateFactory.newMessage(message, p0, p1);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
		return delegateFactory.newMessage(message, p0, p1, p2);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3, p4);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3, p4, p5);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
	}

	@Override
	public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
		return delegateFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	}

	public static class NextLocalizedMessage extends ObjectMessage {

		private static final long serialVersionUID = 1L;

		private String objectString;

		public NextLocalizedMessage(MessageSourceResolvable msr) {
			super(msr);
		}

		@Override
		public String getFormattedMessage() {
			if (objectString == null) {
				MessageSourceResolvable msr = (MessageSourceResolvable) getParameter();
				objectString = Next.getMessageSource().getMessage(msr, null) + " [" + msr.getCodes()[0] + "]";
			}
			return objectString;
		}

		@Override
		public void formatTo(final StringBuilder buffer) {
			buffer.append(getFormattedMessage());
		}

	}

}