package org.nextframework.core.config;

import java.util.Map;
import java.util.Set;

import org.nextframework.view.BaseTag;
import org.nextframework.view.InputTagType;

public interface ViewConfig {

	String getJSPDefaultCharset();

	Map<Class<?>, InputTagType> getCustomInputTypes();

	String isDefaultPropertyRenderAs();

	String getRequiredMarkString();

	boolean isDefaultShowCalendar();

	boolean isDefaultResizeDatagridColumns();

	boolean isDefaultHideDatagridWhileLoading();

	boolean isAutoLoadOnView();

	boolean isPersistTemporaryFiles();

	boolean isUseBootstrap();

	Set<String> getStyleClassFields(Class<? extends BaseTag> tagClass);

	String getDefaultStyleClass(Class<? extends BaseTag> tagClass, String field);

}