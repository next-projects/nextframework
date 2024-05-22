package org.nextframework.report.renderer.jasper.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;

public class JasperReportsRendererUtils {

	public static interface ElementFinder<E extends JRDesignElement> {

		boolean collect(E e);

	}

	private static final String DEFAULT = "default";
	private static final String SUMARY_ELEMENT = "summary";
	private static final String ELEMENT_PROPERTY = "element";
	private static final String DESIGN_PROPERTY = "design";
	protected static final String TYPE_PROPERTY = "type";

	public static <E extends JRDesignElement> E findElement(JRDesignElement jrDesignElement, ElementFinder<E> finder) {
		return findElement(Arrays.asList(jrDesignElement), finder);
	}

	@SuppressWarnings("unchecked")
	public static <E extends JRDesignElement> E findTopElement(List<? extends JRDesignElement> elements, ElementFinder<E> finder) {
		for (JRDesignElement jrDesignElement : elements) {
			try {
				if (finder.collect((E) jrDesignElement)) {
					return (E) jrDesignElement;
				}
			} catch (ClassCastException e) {
				//se der exceçao é porque o finder nao aceita o tipo
			}
			if (jrDesignElement instanceof JRElementGroup) {
				E result = findTopElement(convertToChildList((JRElementGroup) jrDesignElement), finder);
				if (result != null) {
					return (E) jrDesignElement;
				}
			}
		}
		return null;
	}

	private static List<? extends JRDesignElement> convertToChildList(JRElementGroup jrDesignElement) {
		List<JRDesignElement> list = new ArrayList<JRDesignElement>();
		for (JRChild child : jrDesignElement.getChildren()) {
			list.add((JRDesignElement) child);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <E extends JRDesignElement> E findElement(List<? extends JRDesignElement> elements, ElementFinder<E> finder) {
		for (JRDesignElement jrDesignElement : elements) {
			try {
				if (finder.collect((E) jrDesignElement)) {
					return (E) jrDesignElement;
				}
			} catch (ClassCastException e) {
				//se der exceçao é porque o finder nao aceita o tipo
			}
			if (jrDesignElement instanceof JRElementGroup) {
				E result = findElement((JRElementGroup) jrDesignElement, finder);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <E extends JRDesignElement> E findElement(JRElementGroup group, ElementFinder<E> finder) {
		List<JRChild> children = group.getChildren();
		for (JRChild jrChild : children) {
			try {
				if (finder.collect((E) jrChild)) {
					return (E) jrChild;
				}
			} catch (ClassCastException e) {
				//se der exceçao é porque o finder nao aceita o tipo
			}
			if (jrChild instanceof JRElementGroup) {
				E result = findElement((JRElementGroup) jrChild, finder);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public static Map<String, List<JRDesignElement>> findStyleElements(JRDesignElementGroup group) {
		Map<String, List<JRDesignElement>> result = new HashMap<String, List<JRDesignElement>>();
		List<JRChild> children = group.getChildren();
		for (JRChild jrChild : children) {
			JRPropertiesMap propertiesMap = ((JRBaseElement) jrChild).getPropertiesMap();
			String style = propertiesMap.getProperty(DESIGN_PROPERTY);
			if (style == null) {
				style = DEFAULT;
			}
			List<JRDesignElement> list = result.get(style);
			if (list == null) {
				list = new ArrayList<JRDesignElement>();
			}
			list.add((JRDesignElement) jrChild);
			result.put(style, list);
		}
		return result;
	}

	public static ElementFinder<JRDesignTextField> finderDynamicText(final String text) {
		return new JasperReportsRendererUtils.ElementFinder<JRDesignTextField>() {

			public boolean collect(JRDesignTextField e) {
				return e.getExpression().getText() != null && e.getExpression().getText().toLowerCase().contains(text.toLowerCase());
			}

		};
	}

	public static ElementFinder<JRDesignStaticText> finderStaticText(final String text) {
		return new JasperReportsRendererUtils.ElementFinder<JRDesignStaticText>() {

			public boolean collect(JRDesignStaticText e) {
				return e.getText() != null && e.getText().equalsIgnoreCase(text);
			}

		};
	}

	@SuppressWarnings("rawtypes")
	public static ElementFinder getLabelFinder() {
		return new ElementFinder() {

			public boolean collect(JRDesignElement e) {
				if (e instanceof JRDesignStaticText) {
					String text = ((JRDesignStaticText) e).getText();
					if (text == null) {
						return false;
					}
					return text.equalsIgnoreCase("label");
				}
				if (e instanceof JRDesignFrame) {
					return "label".equalsIgnoreCase(e.getPropertiesMap().getProperty("element"));
				}
				return false;
			}

		};
	}

	@SuppressWarnings("rawtypes")
	public static ElementFinder getElementFinder(final String element) {
		return new ElementFinder() {

			public boolean collect(JRDesignElement e) {
				if (e instanceof JRDesignStaticText) {
					String text = ((JRDesignStaticText) e).getText();
					if (text == null) {
						return false;
					}
					return element.equalsIgnoreCase(text);
				}
				String propertyElement = e.getPropertiesMap().getProperty("element");
				if (propertyElement != null) {
					return element.equalsIgnoreCase(propertyElement);
				}
				return false;
			}

		};
	}

	@SuppressWarnings("rawtypes")
	public static ElementFinder getFieldFinder(final String fieldText) {
		return new ElementFinder() {

			public boolean collect(JRDesignElement e) {
				if (e instanceof JRDesignTextField) {
					String text = ((JRDesignTextField) e).getExpression().getText();
					if (text == null) {
						return false;
					}
					return text.toLowerCase().contains(fieldText.toLowerCase());
				}
				if (e instanceof JRDesignFrame) {
					return fieldText.equalsIgnoreCase(e.getPropertiesMap().getProperty("element"));
				}
				return false;
			}

		};
	}

	public static <E extends JRDesignElement> void extractElementByDesign(Map<String, E> mappedElements, Map<String, List<? extends JRDesignElement>> templateElements, ElementFinder<E> finder, boolean top) {
		Set<String> keySet = templateElements.keySet();
		for (String design : keySet) {
			List<? extends JRDesignElement> list = templateElements.get(design);
			E element;
			if (top) {
				element = findTopElement(list, finder);
			} else {
				element = findElement(list, finder);
			}
			mappedElements.put(design, element);
		}
	}

	public static ElementFinder<JRDesignFrame> getBlockFinder(final String type) {
		return new ElementFinder<JRDesignFrame>() {

			public boolean collect(JRDesignFrame e) {
				String rightype = type == null ? DEFAULT : type;
				JRPropertiesMap propertiesMap = e.getPropertiesMap();
				String element = propertiesMap.getProperty(ELEMENT_PROPERTY);
				String typeproperty = propertiesMap.getProperty(TYPE_PROPERTY);
				if (typeproperty == null) {
					typeproperty = DEFAULT;
				}
				if (element != null && element.equalsIgnoreCase(SUMARY_ELEMENT)
						&& typeproperty != null && typeproperty.equalsIgnoreCase(rightype)) {
					return true;
				}
				return false;
			}

		};
	}

}
