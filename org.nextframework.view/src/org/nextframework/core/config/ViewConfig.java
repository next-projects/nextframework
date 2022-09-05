package org.nextframework.core.config;

import java.util.Map;
import java.util.Set;

import org.nextframework.view.BaseTag;
import org.nextframework.view.InputTagType;
import org.nextframework.view.template.PropertyTag;

public interface ViewConfig {

	String getJSPDefaultCharset();

	Map<Class<?>, InputTagType> getCustomInputTypes();

	boolean isDefaultShowCalendar();

	boolean isDefaultResizeDatagridColumns();

	boolean isDefaultHideDatagridWhileLoading();

	boolean isAutoLoadOnView();

	boolean isPersistTemporaryFiles();

	boolean isUseBootstrap();

	boolean isDefaultFlatMode();

	Integer getDefaultColumns();

	Integer getDefaultColspan(String renderAs);

	String getDefaultPropertyRenderAs();

	RequiredMarkMode getRequiredMarkMode();

	public String getRequiredMarkString();

	Set<String> getStyleClassFields(Class<? extends BaseTag> tagClass);

	String getDefaultStyleClass(Class<? extends BaseTag> tagClass, String field);

	public static enum RequiredMarkMode {
		STYLECLASS, BEFORE, AFTER;
	}

}