package org.nextframework.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.exception.NextException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.types.File;
import org.nextframework.types.Password;
import org.nextframework.util.Util;
import org.nextframework.validation.annotation.Required;

public class InputTagHelper {

	protected void autowireAttributes(final InputTag inputTag) {
		if (inputTag.autowire) {
			inputTag.setAutowiredType(inputTag.getPageContext().findAttribute("type"));
			if (inputTag.type == null) {
				inputTag.type = inputTag.getPageContext().findAttribute("type");
			}
			if (inputTag.name == null) {
				try {
					inputTag.name = (String) inputTag.getPageContext().findAttribute("name");
				} catch (ClassCastException e) {
					// ignorar se nao for string
				}
			}
			if (inputTag.value == null) {
				inputTag.value = inputTag.getPageContext().findAttribute("value");
			}
			if (inputTag.getPropertySetter() == null) {
				inputTag.setPropertySetter((PropertySetter) inputTag.getPageContext().findAttribute("propertySetter"));
				if (inputTag.getPropertySetter() == null) {
					inputTag.setPropertySetter(new PropertySetter() {

						public void set(Object value) {
							BaseTag.log.warn(inputTag.name + " tentou setar o valor da propriedade, mas não existe propertySetter para essa propriedade");
						}

					});
				}
			}
			if (inputTag.required == null) {
				try {
					inputTag.required = (Boolean) inputTag.getPageContext().findAttribute("required");
				} catch (ClassCastException e) {
					// ignorar se nao for boolean
				}
			}
			if (Util.strings.isEmpty(inputTag.label)) {
				try {
					Object labelF = inputTag.getPageContext().findAttribute("label");
					inputTag.label = labelF != null ? labelF.toString() : null;
				} catch (NullPointerException e) {
					// ignorar se nao existir o atributo
				}
			}
			if (inputTag.getAnnotations() == null) {
				try {
					inputTag.setAnnotations((Annotation[]) inputTag.getPageContext().findAttribute("annotations"));
				} catch (Exception e) {
					inputTag.setAnnotations(new Annotation[0]);
				}
				if (inputTag.getAnnotations() == null) {
					inputTag.setAnnotations(new Annotation[0]);
				}
			}
			// verificar se é required
			if (inputTag.required == null) {
				for (Annotation ann : inputTag.getAnnotations()) {
					if (ann instanceof Required) {
						inputTag.required = true;
						break;
					}
				}
			}
		}
	}

	protected InputTagType chooseType(InputTag inputTag) {
		InputTagType selectedType;
		if (Util.objects.isEmpty(inputTag.type)) {
			if (inputTag.value == null) {
				throw new NullPointerException("Em uma tag input o atributo value e o type não podem ser ambos nulos");
			}
			Class<?> c = inputTag.value.getClass();
			selectedType = inputTag.inputTagHelper.chooseTypeByClass(inputTag, c);
		} else {
			if (inputTag.type instanceof String) {
				String typeString = ((String) inputTag.type).toUpperCase().replaceAll("-", "_");
				try {
					InputTagType valueOf = InputTagType.valueOf(typeString);
					selectedType = valueOf;
				} catch (IllegalArgumentException e) {
					throw new NextException("Type não suportado por input: " + inputTag.type);
				}
			} else if (inputTag.type instanceof Class<?>) {
				selectedType = inputTag.inputTagHelper.chooseTypeByClass(inputTag, (Class<?>) inputTag.type);
			} else if (inputTag.type instanceof ParameterizedType) {
				selectedType = inputTag.inputTagHelper.chooseTypeByClass(inputTag, (Class<?>) ((ParameterizedType) inputTag.type).getRawType());
			} else if (inputTag.type instanceof Collection<?>) {
				selectedType = InputTagType.SELECT_MANY;
			} else {
				throw new IllegalArgumentException("Input não suporta valor '" + inputTag.type + "' no atributo type");
			}
		}
		if (selectedType == InputTagType.TEXT && ((inputTag.rows != null && inputTag.rows != 0) || (inputTag.cols != null && inputTag.cols != 0)) && !"text".equals(inputTag.type)) {
			selectedType = InputTagType.TEXT_AREA;
		}
		if (selectedType == InputTagType.TEXT) {
			for (Annotation ann : inputTag.getAnnotations()) {
				if (ann instanceof Password) {
					selectedType = InputTagType.PASSWORD;
				}
			}
		}
		if (selectedType == InputTagType.CHECKBOX && inputTag.itens != null && !"".equals(inputTag.itens) && inputTag.value != null) {
			selectedType = InputTagType.CHECKLIST;
		}
		if (inputTag.type == null && inputTag.itens != null && !"".equals(inputTag.itens) && selectedType != InputTagType.SELECT_MANY) {
			selectedType = InputTagType.SELECT_ONE;
		}
		if (selectedType == InputTagType.SELECT_ONE && Util.strings.isNotEmpty(inputTag.selectOnePath)) {
			selectedType = InputTagType.SELECT_ONE_BUTTON;
		}
		if (selectedType == InputTagType.SELECT_ONE && Util.strings.isNotEmpty(inputTag.insertPath)) {
			selectedType = InputTagType.SELECT_ONE_INSERT;
		}
		return selectedType;
	}

	@SuppressWarnings("rawtypes")
	InputTagType chooseTypeByClass(InputTag inputTag, Class c) {
		if (Map.class.isAssignableFrom(c)) {
			throw new IllegalArgumentException("O input não suporta valores do tipo Map");
		}
		if (Enum.class.isAssignableFrom(c)) {
			return InputTagType.SELECT_ONE;
		}
		if (File.class.isAssignableFrom(c)) {
			return InputTagType.FILE;
		}
		if (inputTag.isEntity(c)) {
			return InputTagType.SELECT_ONE;
		}
		if (Collection.class.isAssignableFrom(c)) {
			return InputTagType.SELECT_MANY;
		}
		if (inputTag.itens != null) {
			return InputTagType.SELECT_ONE;
		}
		InputTagType type2 = InputTagTypeManager.getInstance().getTypeForClass(c);
		if (type2 == null) {
			//verificar tipos personalizados
			type2 = (InputTagType) ServiceFactory.getService(ViewConfig.class).getCustomInputTypes().get(c);
			if (type2 == null) {
				type2 = InputTagType.TEXT;
			}
		}
		return type2;
	}

	protected <A extends Annotation> InputListener<A> getInputListener(InputTag inputTag, A annotation) {
		if (inputTag.baseTagPropertyEditorsManagerCache == null) {
			inputTag.baseTagPropertyEditorsManagerCache = TagUtils.getPropertyEditorsManager();
		}
		return inputTag.baseTagPropertyEditorsManagerCache.getInputListener(annotation);
	}

	@SuppressWarnings("rawtypes")
	public boolean isDateOrTime(Object value) {
		return value instanceof Calendar ||
				value instanceof Date ||
				value instanceof Class && (Date.class.isAssignableFrom((Class) value) || Calendar.class.isAssignableFrom((Class) value));
	}

}
