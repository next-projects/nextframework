package org.nextframework.message;

import org.nextframework.util.Util;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class NextMessageSourceResolvable extends DefaultMessageSourceResolvable implements Comparable<MessageSourceResolvable> {

	private static final long serialVersionUID = 1L;

	public NextMessageSourceResolvable(String code) {
		this(new String[] { code }, null, null);
	}

	public NextMessageSourceResolvable(String[] codes) {
		this(codes, null, null);
	}

	public NextMessageSourceResolvable(String code, Object[] arguments) {
		this(new String[] { code }, arguments, null);
	}

	public NextMessageSourceResolvable(String[] codes, Object[] arguments) {
		this(codes, arguments, null);
	}

	public NextMessageSourceResolvable(String code, Object[] arguments, String defaultMessage) {
		this(new String[] { code }, arguments, defaultMessage);
	}

	public NextMessageSourceResolvable(String[] codes, Object[] arguments, String defaultMessage) {
		super(codes, arguments, defaultMessage);
	}

	public NextMessageSourceResolvable(MessageSourceResolvable resolvable) {
		this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
	}

	@Override
	public String toString() {
		return resolvableToString();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compareTo(MessageSourceResolvable msr) {

		String code1 = this.getCodes() != null && this.getCodes().length > 0 ? this.getCodes()[0] : null;
		String code2 = msr.getCodes() != null && msr.getCodes().length > 0 ? msr.getCodes()[0] : null;
		if (code1 == null ^ code2 == null) {
			return code1 != null ? 1 : -1;
		}

		int result = code1 != null && code2 != null ? code1.compareTo(code2) : 0;
		if (result != 0) {
			return result;
		}

		String d1 = this.getDefaultMessage();
		String d2 = msr.getDefaultMessage();
		if (d1 == null ^ d2 == null) {
			return d1 != null ? 1 : -1;
		}

		result = d1 != null && d2 != null ? d1.compareTo(d2) : 0;
		if (result != 0) {
			return result;
		}

		Integer totArgs1 = this.getArguments() != null ? this.getArguments().length : 0;
		Integer totArgs2 = msr.getArguments() != null ? msr.getArguments().length : 0;
		result = totArgs1.compareTo(totArgs2);
		if (result != 0) {
			return result;
		}

		if (totArgs1 > 0 && totArgs2 > 0) {
			for (int i = 0; i < totArgs1 && i < totArgs2; i++) {

				Object o1 = this.getArguments()[i];
				Object o2 = msr.getArguments()[i];

				if (o1 == null && o2 == null) {
					continue;
				}

				if (o1 != null && o2 == null) {
					return 1;
				}

				if (o1 == null && o2 != null) {
					return -1;
				}

				if (o1.getClass() != o2.getClass()) {
					return o1.getClass().getName().compareTo(o2.getClass().getName());
				}

				if (!(o1 instanceof Comparable) || !(o2 instanceof Comparable)) {
					o1 = Util.strings.toStringDescription(o1);
					o2 = Util.strings.toStringDescription(o2);
				}

				result = ((Comparable) o1).compareTo(((Comparable) o2));
				if (result != 0) {
					return result;
				}

			}
		}

		return 0;
	}

}