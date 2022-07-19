package org.nextframework.view.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;

import org.nextframework.service.ServiceFactory;
import org.nextframework.validation.JavascriptValidationItem;
import org.nextframework.validation.ValidationItem;
import org.nextframework.validation.ValidatorRegistry;
import org.nextframework.validation.annotation.Required;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.InputTag;
import org.nextframework.view.InputTagHelper;
import org.nextframework.view.InputTagType;
import org.nextframework.view.ValidationTag;
import org.nextframework.view.template.PropertyConfigTag;

public class InputTagComponent {

	protected InputTag inputTag;
	protected InputTagHelper helper;
	protected InputTagType selectedType;

	public void validateTag() {
	}

	public void prepare() {
		boolean disabled = configureDisabled();
		configureValidation(disabled);
		configureReadOnly();
	}

	protected void configureValidation(boolean disabled) {
		if (!disabled) {
			ValidationItem validationItem = null;
			String labelSimples = inputTag.getLabel() != null ? (inputTag.getLabel().replaceAll("&nbsp;", "").replaceAll("<BR>", "")) : "";
			if (inputTag.getType() instanceof Class<?>) {
				validationItem = ServiceFactory.getService(ValidatorRegistry.class).getExtractor().getValidationItem(labelSimples, (Class<?>) inputTag.getType(), inputTag.getAnnotations());
			} else if (inputTag.getType() instanceof String) {
				validationItem = ServiceFactory.getService(ValidatorRegistry.class).getExtractor().getValidationItem(labelSimples, (String) inputTag.getType(), inputTag.getAnnotations());
			} else if (inputTag.getType() instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) inputTag.getType();
				validationItem = ServiceFactory.getService(ValidatorRegistry.class).getExtractor().getValidationItem(labelSimples, (Class<?>) parameterizedType.getRawType(), inputTag.getAnnotations());
			}
			if (inputTag.getRequired() != null) {
				Required requiredValidation = new Required() {
					public Class<? extends Annotation> annotationType() {
						return Required.class;
					}
				};
				if (inputTag.getRequired() && !containsRequired(validationItem.getValidations())) {
					validationItem.getValidations().add(requiredValidation);
				} else if (!inputTag.getRequired() && containsRequired(validationItem.getValidations())) {
					removeRequired(validationItem.getValidations());
				}
			}
			if (validationItem != null && (validationItem.getTypeValidator() != null || validationItem.getValidations().size() > 0)) {
				ValidationTag validationTag = inputTag.findParent(ValidationTag.class);
				if (validationTag != null) {
					JavascriptValidationItem javascriptValidationItem = new JavascriptValidationItem(validationItem);
					javascriptValidationItem.setFieldDisplayName(labelSimples);
					javascriptValidationItem.setFieldName(inputTag.getName());
					validationTag.register(javascriptValidationItem);
				}
			}
		}
	}

	private void removeRequired(List<Annotation> validations) {
		for (Iterator<Annotation> iterator = validations.iterator(); iterator.hasNext();) {
			Annotation annotation = iterator.next();
			if (annotation.annotationType().equals(Required.class)) {
				iterator.remove();
				break;
			}
		}
	}

	private boolean containsRequired(List<Annotation> validations) {
		for (Annotation annotation : validations) {
			if (annotation.annotationType().equals(Required.class)) {
				return true;
			}
		}
		return false;
	}

	protected boolean configureDisabled() {
		PropertyConfigTag propertyConfig = inputTag.findParent(PropertyConfigTag.class);
		boolean disabled = false;
		Object disabledObj = inputTag.getDynamicAttributesMap().get("disabled");
		if (disabledObj == null || "false".equals(disabledObj) || Boolean.FALSE.equals(disabledObj)) {
			inputTag.getDynamicAttributesMap().remove("disabled");
		} else {
			disabled = true;
		}

		DataGridTag dataGridTag = inputTag.findParent(DataGridTag.class);
		if (propertyConfig != null && Boolean.TRUE.equals(propertyConfig.getDisabled()) && (dataGridTag == null || dataGridTag.getCurrentStatus() != DataGridTag.Status.DYNALINE)) {
			if (disabled) {
				inputTag.getDynamicAttributesMap().put("originaldisabled", "disabled");
			}
			inputTag.getDynamicAttributesMap().put("disabled", "disabled");
		}
		return disabled;
	}

	public String getValueAsString() {
		return null;
	}

	public boolean printRequired() {
		return inputTag.isToPrintRequired();
	}

	public void configureReadOnly() {
		Object readonlyObj = inputTag.getDynamicAttributesMap().get("readonly");
		if (readonlyObj == null || "false".equals(readonlyObj) || Boolean.FALSE.equals(readonlyObj)) {
			inputTag.getDynamicAttributesMap().remove("readonly");
		}
	}

	public void afterPrint() {

	}

	public void setTag(InputTag inputTag) {
		this.inputTag = inputTag;
	}

	public void setHelper(InputTagHelper inputTagHelper) {
		this.helper = inputTagHelper;
	}

	public InputTagType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(InputTagType selectedType) {
		this.selectedType = selectedType;
	}

}