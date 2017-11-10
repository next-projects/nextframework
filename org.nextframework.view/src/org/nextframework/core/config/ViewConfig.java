package org.nextframework.core.config;

import java.util.Map;

import org.nextframework.view.InputTagType;

public interface ViewConfig {

	//REFACTOR - This interface should be in the view module - IT WAS InputTagType instead of Object
	Map<Class<?>, InputTagType> getCustomInputTypes();

	String getRequiredMarkString();

	boolean isDefaultShowCalendar();

	boolean isDefaultResizeDatagridColumns();

	boolean isDefaultHideDatagridWhileLoading();

	boolean isAutoLoadOnView();

	boolean isPersistTemporaryFiles();

	String getJSPDefaultCharset();

	boolean isUseBootstrap();

	String getPaggingDefaultSelectedClass();

	String getPaggingExtraStyleClass();

}