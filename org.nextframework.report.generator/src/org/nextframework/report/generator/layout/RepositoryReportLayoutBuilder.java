package org.nextframework.report.generator.layout;

import java.lang.reflect.Type;

import javax.persistence.Entity;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.report.definition.builder.BaseReportBuilder;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.style.ReportAlignment;
import org.nextframework.types.Money;
import org.nextframework.util.Util;

public abstract class RepositoryReportLayoutBuilder extends LayoutReportBuilder {

	@Override
	protected void configureDefinition() {
		super.configureDefinition();
		getDefinition().setParameter(CONVERTER, new ReportBuilderValueConverter(locale));
	}

	@Override
	protected Object checkFilterValue(PropertyDescriptor propertyDescriptor, Object propertyValue) {

		propertyValue = super.checkFilterValue(propertyDescriptor, propertyValue);

		if (propertyValue != null) {
			if (propertyValue.getClass().isArray()) {
				for (Object pv : (Object[]) propertyValue) {
					verifyDescriptionProperty(pv);
				}
			} else {
				verifyDescriptionProperty(propertyValue);
			}
		} else {
			Type type = propertyDescriptor.getType();
			if (type instanceof Class<?>) {
				Class<?> clazz = (Class<?>) type;
				if (clazz.getPackage() != null && !clazz.getPackage().getName().startsWith("java")) {
					propertyValue = getEmptyFilterValue();
				}
			}
		}

		return propertyValue;
	}

	@SuppressWarnings("unchecked")
	private <BEAN> void verifyDescriptionProperty(BEAN obj) {
		if (obj != null && obj.getClass().isAnnotationPresent(Entity.class)) {
			BeanDescriptor entityBeanDescriptor = BeanDescriptorFactory.forBean(obj);
			String descriptionPropertyName = entityBeanDescriptor.getDescriptionPropertyName();
			Object value = entityBeanDescriptor.getPropertyDescriptor(descriptionPropertyName).getValue();
			if (value == null) {
				GenericDAO<BEAN> dao = (GenericDAO<BEAN>) DAOUtils.getDAOForClass(obj.getClass());
				dao.loadDescriptionProperty(obj);
			}
		}
	}

	protected String getEmptyFilterValue() {
		return "[TODOS]";
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected FieldConfig createFieldConfig(BaseReportBuilder builder, BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor,
			String label, String fieldName, String fieldPreffix, String fieldSuffix, String reportExpression,
			String pattern, ReportAlignment alignment) {

		FieldConfig fieldConfig = super.createFieldConfig(builder, beanDescriptor, propertyDescriptor,
				label, fieldName, fieldPreffix, fieldSuffix, reportExpression,
				pattern, alignment);

		fieldConfig.label = Util.beans.getDisplayName(propertyDescriptor, getLocale());

		Type type = propertyDescriptor.getType();
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (Money.class.isAssignableFrom(clazz)) {
				fieldConfig.alignment = ReportAlignment.RIGHT;
			}
		}

		return fieldConfig;
	}

}