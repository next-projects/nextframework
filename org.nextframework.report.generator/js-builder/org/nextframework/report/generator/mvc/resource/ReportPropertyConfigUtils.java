package org.nextframework.report.generator.mvc.resource;

import org.nextframework.js.NextGlobalJs;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.JSStringAdapter;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.Select;
import org.stjs.javascript.functions.Function1;

public class ReportPropertyConfigUtils {

	public static String getDisplayName(Map<String, Object> properties) {
		return (String) properties.$get("displayName");
	}

	public static Boolean isAggregatable(Map<String, Object> properties) {
		return (Boolean) properties.$get("numberType");
	}

	public static boolean isTransient(Map<String, Object> properties) {
		return (Boolean) properties.$get("transient");
	}

	public static boolean isExtended(Map<String, Object> properties) {
		return (Boolean) properties.$get("extended");
	}

	public static boolean isFilterable(Map<String, Object> properties) {
		return (Boolean) properties.$get("filterable");
	}

	public static boolean isEntity(Map<String, Object> properties) {
		return (Boolean) properties.$get("entity");
	}

	public static boolean isEnum(Map<String, Object> properties) {
		return (Boolean) properties.$get("enumType");
	}

	public static boolean isFilterSelectMultiple(Map<String, Object> properties) {
		return (Boolean) properties.$get("filterSelectMultiple");
	}

	public static void setFilterSelectMultiple(Map<String, Object> properties, boolean checked) {
		properties.$put("filterSelectMultiple", checked);
	}

	public static boolean isFilterRequired(Map<String, Object> properties) {
		return (Boolean) properties.$get("requiredFilter");
	}

	public static void setFilterRequired(Map<String, Object> properties, boolean checked) {
		properties.$put("requiredFilter", checked);
	}

	public static String getFilterPreSelectDate(Map<String, Object> properties) {
		return (String) properties.$get("preSelectDate");
	}

	public static void setFilterPreSelectDate(Map<String, Object> properties, String filterPreSelectDate) {
		if ("<null>".equals(filterPreSelectDate)) {
			filterPreSelectDate = null;
		}
		properties.$put("preSelectDate", filterPreSelectDate);
	}

	public static String getFilterPreSelectEntity(Map<String, Object> properties) {
		return (String) properties.$get("preSelectEntity");
	}

	public static void setFilterPreSelectEntity(Map<String, Object> properties, String filterPreSelectEntity) {
		if ("<null>".equals(filterPreSelectEntity)) {
			filterPreSelectEntity = null;
		}
		properties.$put("preSelectEntity", filterPreSelectEntity);
	}

	public static String getFilterFixedCriteria(Map<String, Object> properties) {
		return (String) properties.$get("fixedCriteria");
	}

	public static void setFilterFixedCriteria(Map<String, Object> properties, String filterFixedCriteria) {
		if ("<null>".equals(filterFixedCriteria)) {
			filterFixedCriteria = null;
		}
		properties.$put("fixedCriteria", filterFixedCriteria);
	}

	public static String getFilterDisplayName(Map<String, Object> properties) {
		String fdn = (String) properties.$get("filterDisplayName");
		if (fdn == null) {
			fdn = getDisplayName(properties);
		}
		return fdn;
	}

	public static void setFilterDisplayName(Map<String, Object> properties, String value) {
		properties.$put("filterDisplayName", value);
	}

	public static Array<String> getProcessors(Map<String, Object> properties) {
		String processors = (String) properties.$get("processors");
		if (processors == null) {
			return JSCollections.$array();
		}
		return JSStringAdapter.split(processors, ",");
	}

	public static void setProcessors(Map<String, Object> properties, Array<String> processors) {
		String sProcessors = NextGlobalJs.next.util.join(processors, ",");
		properties.$put("processors", sProcessors);
	}

	public static boolean isDate(Map<String, Object> options) {
		Object type = getType(options);
		return type.equals("java.util.Calendar") || type.equals("java.util.Date") || type.equals("java.sql.Date");
	}

	public static String getType(Map<String, Object> options) {
		return (String) options.$get("type");
	}

	public static boolean isNumber(Map<String, Object> properties) {
		return (Boolean) properties.$get("numberType") == true;
	}

	public static boolean isGroupable(Map<String, Object> properties) {
		if ((Boolean) properties.$get("comparable") != true && (Boolean) properties.$get("entity") != true) {
			return false;
		}
		return !isNumber(properties);
	}

	public static void configureInputToLabel(final LabelReportElement labelElement, final Input labelInput) {
		labelInput.value = labelElement.label;
		labelInput.onkeyup = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent p1) {
				labelElement.label = labelInput.value;
				labelElement.changed = true;
				labelElement.getNode().innerHTML = labelElement.label;
				return true;
			}

		};
		labelInput.onchange = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent p1) {
				ReportDesigner.getInstance().writeXml();
				return true;
			}

		};
	}

	public static void configurePatternInputToField(final FieldReportElement field, final Select patternInput) {
		if (field.pattern != null && field.pattern != "") {
			patternInput.value = field.pattern;
		} else {
			patternInput.selectedIndex = 0;
		}
		patternInput.onchange = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent p1) {
				FieldReportElement fieldReportElement = field;
				fieldReportElement.pattern = patternInput.value;
				ReportDesigner.getInstance().writeXml();
				return true;
			}

		};
	}

	public static void configureFieldToAggregateInputs(final FieldDetail fieldDetail, final Input aggregateInput, final Select aggregateTypeInput) {
		if (fieldDetail.label.column.getIndex() == 0) {
			aggregateInput.disabled = true;
			aggregateTypeInput.disabled = true;
		} else {
			aggregateInput.disabled = false;
			aggregateTypeInput.disabled = false;
		}
		aggregateInput.checked = fieldDetail.isAggregate();
		aggregateInput.onclick = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent p1) {
				if (fieldDetail.label.column.getIndex() == 0) {
					Global.alert("O item na primeira coluna nao pode ser agregado.\n" +
							"O espaco da primeira coluna e reservado para grupos.\n" +
							"Mova o campo para outra coluna para poder agregar.");
					return false;
				}
				fieldDetail.setAggregate(aggregateInput.checked);
				ReportDesigner.getInstance().writeXml();
				return true;
			}

		};
		if (fieldDetail.aggregateType == null || fieldDetail.aggregateType.length() == 0) {
			aggregateTypeInput.selectedIndex = 0;
		} else {
			aggregateTypeInput.value = fieldDetail.aggregateType;
		}
		aggregateTypeInput.onchange = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent p1) {
				fieldDetail.aggregateType = aggregateTypeInput.value;
				ReportDesigner.getInstance().writeXml();
				return true;
			}

		};
	}

	public static final int ANY = 1;
	public static final int IN_VAR = 2;
	public static final int IN_SIGNAL = 3;

	public static final int OPEN_PARENTHESIS = 1;
	public static final int CLOSE_PARENTHESIS = 2;
	public static final int SIGNAL = 3;
	public static final int VAR = 4;

	public static Array<String> parseExpression(String expression) {
		Array<String> parts = JSCollections.$array();
		String token = "";
		int status = ReportPropertyConfigUtils.ANY;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			switch (status) {
				case ReportPropertyConfigUtils.ANY:
					if (isLetter(c) || isDigit(c)) {
						token += c;
						status = ReportPropertyConfigUtils.IN_VAR;
					} else {
						if (isNotEmpty(token)) {
							parts.push(token);
						}
						token = "" + c;
						status = ReportPropertyConfigUtils.IN_SIGNAL;
					}
					break;
				case ReportPropertyConfigUtils.IN_VAR:
					if (isLetter(c) || isDigit(c) || c == '.') {
						token += c;
					} else {
						if (isNotEmpty(token)) {
							parts.push(token);
						}
						token = "" + c;
						status = ReportPropertyConfigUtils.IN_SIGNAL;
					}
					break;
				case ReportPropertyConfigUtils.IN_SIGNAL:
					if (isLetter(c) || isDigit(c)) {
						if (isNotEmpty(token)) {
							parts.push(token);
						}
						token = "" + c;
						status = ReportPropertyConfigUtils.IN_VAR;
					} else {
						if (isNotEmpty(token)) {
							parts.push(token);
						}
						token = "" + c;
					}
					break;
			}
		}
		if (isNotEmpty(token)) {
			parts.push(token);
		}
		return parts;
	}

	public static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	public static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	public static boolean isNotEmpty(String token) {
		return token != null && token.trim().length() > 0;
	}

	public static boolean isEmpty(String token) {
		return token == null || token.trim().length() == 0;
	}

	public static String validateExpression(String expression) {
		Array<String> tokens = parseExpression(expression);
		int status = ReportPropertyConfigUtils.ANY;
		int parentesesStack = 0;
		for (int i = 0; i < tokens.$length(); i++) {
			String token = tokens.$get(i);
			if (token == ")") {
				parentesesStack--;
				if (parentesesStack < 0) {
					return "Existem mais ')' do que '('. Verifique a expressão.";
				}
			}
			if (token == "(") {
				parentesesStack++;
			}
			int tokenType = getTokenType(token);
			switch (status) {
				case ReportPropertyConfigUtils.ANY:
					if (tokenType == ReportPropertyConfigUtils.SIGNAL) {
						status = ReportPropertyConfigUtils.IN_SIGNAL;
					}
					if (tokenType == ReportPropertyConfigUtils.VAR) {
						status = ReportPropertyConfigUtils.IN_VAR;
					}
					if (tokenType == ReportPropertyConfigUtils.CLOSE_PARENTHESIS) {
						return "Fecha parênteses inesperado [item " + (i + 1) + "]";
					}
					break;
				case ReportPropertyConfigUtils.IN_VAR:
					if (tokenType == ReportPropertyConfigUtils.VAR) {
						return "Variável inesperada '" + token + "'";
					}
					if (tokenType == ReportPropertyConfigUtils.OPEN_PARENTHESIS) {
						return "Operador esperado '(' [item " + (i + 1) + "]";
					}
					if (tokenType == ReportPropertyConfigUtils.CLOSE_PARENTHESIS) {
						status = ReportPropertyConfigUtils.IN_VAR;
					}
					if (tokenType == ReportPropertyConfigUtils.SIGNAL) {
						status = ReportPropertyConfigUtils.IN_SIGNAL;
					}
					break;
				case ReportPropertyConfigUtils.IN_SIGNAL:
					if (tokenType == ReportPropertyConfigUtils.SIGNAL) {
						return "Operador inesperado '" + token + "'";
					}
					if (tokenType == ReportPropertyConfigUtils.CLOSE_PARENTHESIS) {
						return "Operador inesperado ')' [item " + (i + 1) + "]";
					}
					if (tokenType == ReportPropertyConfigUtils.OPEN_PARENTHESIS) {
						status = ReportPropertyConfigUtils.ANY;
					}
					if (tokenType == ReportPropertyConfigUtils.VAR) {
						status = ReportPropertyConfigUtils.IN_VAR;
					}
					break;
			}
		}
		if (parentesesStack != 0) {
			return "O número de '(' e ')' são diferentes. Verifique a expressão.";
		}
		return null;
	}

	private static int getTokenType(String token) {
		if (token.length() == 1) {
			if (token == "(") {
				return ReportPropertyConfigUtils.OPEN_PARENTHESIS;
			} else if (token == ")") {
				return ReportPropertyConfigUtils.CLOSE_PARENTHESIS;
			} else {
				if (!isDigit(token.charAt(0))) {
					return ReportPropertyConfigUtils.SIGNAL;
				}
			}
		}
		return ReportPropertyConfigUtils.VAR;
	}

}