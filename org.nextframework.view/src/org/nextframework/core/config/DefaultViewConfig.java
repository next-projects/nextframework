package org.nextframework.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nextframework.view.BaseTag;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.InputTag;
import org.nextframework.view.InputTagType;
import org.nextframework.view.MessagesTag;
import org.nextframework.view.PaggingTag;
import org.nextframework.view.TabPanelTag;
import org.nextframework.view.template.DetailTag;
import org.nextframework.view.template.FilterPanelTag;
import org.nextframework.view.template.FilterTableTag;
import org.nextframework.view.template.FormPanelTag;
import org.nextframework.view.template.FormTableTag;
import org.nextframework.view.template.FormViewTag;
import org.nextframework.view.template.ListPanelTag;
import org.nextframework.view.template.ListTableTag;
import org.nextframework.view.template.ListViewTag;
import org.nextframework.view.template.PropertyTag;
import org.nextframework.view.template.ReportPanelTag;
import org.nextframework.view.template.ReportTableTag;
import org.nextframework.view.template.ViewTag;

public class DefaultViewConfig implements ViewConfig {

	protected Map<Class<?>, InputTagType> customInputTypesMap = new HashMap<Class<?>, InputTagType>();

	protected Map<Class<? extends BaseTag>, Map<String, String>> styleClassesMap = new HashMap<Class<? extends BaseTag>, Map<String, String>>();

	@Override
	public String getJSPDefaultCharset() {
		return "iso-8859-1";
	}

	public Map<Class<?>, InputTagType> getCustomInputTypes() {
		return customInputTypesMap;
	}

	@Override
	public String isDefaultPropertyRenderAs() {
		return PropertyTag.DOUBLE;
	}

	public String getRequiredMarkString() {
		return "&#0149;";
	}

	public boolean isDefaultShowCalendar() {
		return true;
	}

	public boolean isDefaultResizeDatagridColumns() {
		return false;
	}

	@Override
	public boolean isDefaultHideDatagridWhileLoading() {
		return false;
	}

	@Override
	public boolean isAutoLoadOnView() {
		return true;
	}

	@Override
	public boolean isPersistTemporaryFiles() {
		return true;
	}

	@Override
	public boolean isUseBootstrap() {
		return true;
	}

	@Override
	public Set<String> getStyleClassFields(Class<? extends BaseTag> tagClass) {
		initStyleClasses();
		Map<String, String> tagMap = this.styleClassesMap.get(tagClass);
		return tagMap != null ? tagMap.keySet() : null;
	}

	@Override
	public String getDefaultStyleClass(Class<? extends BaseTag> tagClass, String field) {
		initStyleClasses();
		Map<String, String> tagMap = this.styleClassesMap.get(tagClass);
		return tagMap != null ? tagMap.get(field) : null;
	}

	private void initStyleClasses() {

		//styleClassesMap.clear();
		if (!styleClassesMap.isEmpty()) {
			return;
		}

		//Componentes

		if (isUseBootstrap()) {

			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "navClass", "nav nav-tabs");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "contentClass", "tab-content");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "selectedClass", "tab-pane active");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "unselectedClass", "tab-pane");

			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "styleClass", "table table-striped table-bordered table-hover");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "headerStyleClass", "thead-dark");

			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "panelClass", "pagination pagination-sm pull-right");
			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "selectedClass", "disabled");

		} else {

			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "containerClass", "messagesContainer");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "bindblockClass", "bindblock");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "messageblockClass", "messageblock");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "titleClass", "messagetitle");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "globalErrorclass", "globalerror");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "fieldNameClass", "fieldname");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "bindErrorClass", "binderror");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "validationErrorClass", "validationerror");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "debugClass", "debug");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "traceClass", "trace");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "infoClass", "info");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "warnClass", "warn");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "errorClass", "error");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "exceptionClass", "exceptionitem");
			regDefaultStyleClasses(styleClassesMap, MessagesTag.class, "exceptionCauseClass", "causeitem");

			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "navClass", "tabPanel");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "selectedClass", "tabSelected");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "unselectedClass", "tabUnselected");

			regDefaultStyleClasses(styleClassesMap, InputTag.class, "CHECKBOX-class", "checkboxClass");

			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "styleClass", "dataGrid");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "headerStyleClass", "dataGridHeader");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "bodyStyleClasses", "dataGridBody1, dataGridBody2");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "footerStyleClass", "dataGridFooter");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "groupStyleClasses", "dataGridGroup1, dataGridGroup2, dataGridGroup3");

			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "panelClass", "pagePanel");
			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "selectedClass", "pageSelected");

		}

		//Templates

		if (isUseBootstrap()) {

			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "pageStyleClass", "panel panel-default");
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "titleStyleClass", "panel-heading");
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "bodyStyleClass", "panel-body");

			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(styleClassesMap, FormPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(styleClassesMap, FormTableTag.class, "rowStyleClasses", "row g-3");
			regDefaultStyleClasses(styleClassesMap, FormTableTag.class, "columnStyleClasses", "col-form-label, col");

			regDefaultStyleClasses(styleClassesMap, DetailTag.class, "buttonStyleClass", "btn btn-sm");

		} else {

			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "pageStyleClass", null);
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "titleStyleClass", "pageTitle");
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "bodyStyleClass", "pageBody");

			regDefaultStyleClasses(styleClassesMap, ListViewTag.class, "linkBarStyleClass", "linkBar");

			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "sectionTitleStyleClass", "sectionTitle");
			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "panelStyleClass", "filterWindow");
			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "actionBarStyleClass", "actionBar");

			regDefaultStyleClasses(styleClassesMap, FilterTableTag.class, "styleClass", "inputTable");
			regDefaultStyleClasses(styleClassesMap, FilterTableTag.class, "columnStyleClasses", "labelColumn, propertyColumn");

			regDefaultStyleClasses(styleClassesMap, ListPanelTag.class, "panelStyleClass", "resultWindow");

			regDefaultStyleClasses(styleClassesMap, ListTableTag.class, "pagePanelStyleClass", "pagging");

			regDefaultStyleClasses(styleClassesMap, FormViewTag.class, "linkBarStyleClass", "linkBar");

			regDefaultStyleClasses(styleClassesMap, FormPanelTag.class, "panelStyleClass", "inputWindow");
			regDefaultStyleClasses(styleClassesMap, FormPanelTag.class, "actionBarStyleClass", "actionBar");

			regDefaultStyleClasses(styleClassesMap, FormTableTag.class, "styleClass", "inputTable");
			regDefaultStyleClasses(styleClassesMap, FormTableTag.class, "columnStyleClasses", "labelColumn, propertyColumn");

			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "sectionTitleStyleClass", "sectionTitle");
			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "panelStyleClass", "filterWindow");
			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "actionBarStyleClass", "actionBar");

			regDefaultStyleClasses(styleClassesMap, ReportTableTag.class, "styleClass", "inputTable");
			regDefaultStyleClasses(styleClassesMap, ReportTableTag.class, "columnStyleClasses", "labelColumn, propertyColumn");

			regDefaultStyleClasses(styleClassesMap, DetailTag.class, "panelStyleClass", "inputTable");

		}

	}

	private void regDefaultStyleClasses(Map<Class<? extends BaseTag>, Map<String, String>> map, Class<? extends BaseTag> tagClass, String field, String styleClass) {
		Map<String, String> tagMap = map.get(tagClass);
		if (tagMap == null) {
			tagMap = new HashMap<String, String>();
			map.put(tagClass, tagMap);
		}
		tagMap.put(field, styleClass);
	}

}