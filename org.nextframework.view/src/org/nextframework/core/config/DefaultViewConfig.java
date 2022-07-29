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
import org.nextframework.view.SubmitTag;
import org.nextframework.view.TabPanelTag;
import org.nextframework.view.menu.MenuTag;
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

	@Override
	public Map<Class<?>, InputTagType> getCustomInputTypes() {
		return customInputTypesMap;
	}

	@Override
	public String isDefaultPropertyRenderAs() {
		return PropertyTag.DOUBLE;
	}

	@Override
	public String getRequiredMarkString() {
		return "&#0149;";
	}

	@Override
	public boolean isDefaultShowCalendar() {
		return true;
	}

	@Override
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

		styleClassesMap.clear();
		if (!styleClassesMap.isEmpty()) {
			return;
		}

		//Componentes

		if (isUseBootstrap()) {

			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "containerStyleClass", "table-responsive");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "styleClass", "table table-sm table-striped table-bordered table-hover");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "headerStyleClass", "table-light");

			regDefaultStyleClasses(styleClassesMap, InputTag.class, "requiredStyleClass", "position-absolute top-0 end-0 requiredMark");
			
			regDefaultStyleClasses(styleClassesMap, MenuTag.class, "panelStyleClass", "mb-2 mb-md-0");

			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "panelClass", "pagination pagination-sm d-inline-flex m-0");
			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "itemClass", "page-item");
			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "selectedClass", "page-link active");
			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "unselectedClass", "page-link");
			
			regDefaultStyleClasses(styleClassesMap, SubmitTag.class, "BUTTON-class", "btn btn-sm btn-primary");
			regDefaultStyleClasses(styleClassesMap, SubmitTag.class, "SUBMIT-class", "btn btn-sm btn-primary");

			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "navClass", "nav nav-tabs");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "contentClass", "tab-content");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "selectedClass", "tab-pane active");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "unselectedClass", "tab-pane");

		} else {

			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "containerStyleClass", "datagridcontainer");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "styleClass", "dataGrid");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "headerStyleClass", "dataGridHeader");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "bodyStyleClasses", "dataGridBody1, dataGridBody2");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "footerStyleClass", "dataGridFooter");
			regDefaultStyleClasses(styleClassesMap, DataGridTag.class, "groupStyleClasses", "dataGridGroup1, dataGridGroup2, dataGridGroup3");

			regDefaultStyleClasses(styleClassesMap, InputTag.class, "requiredStyleClass", "requiredMark");
			regDefaultStyleClasses(styleClassesMap, InputTag.class, "CHECKBOX-class", "checkboxClass");

			regDefaultStyleClasses(styleClassesMap, MenuTag.class, "panelStyleClass", "menuClass");

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

			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "panelClass", "pagePanel");
			regDefaultStyleClasses(styleClassesMap, PaggingTag.class, "selectedClass", "pageSelected");

			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "navClass", "tabPanel");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "selectedClass", "tabSelected");
			regDefaultStyleClasses(styleClassesMap, TabPanelTag.class, "unselectedClass", "tabUnselected");

		}

		//Templates

		if (isUseBootstrap()) {

			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "pageStyleClass", "container");
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "titleStyleClass", "h3 mb-4");
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "bodyStyleClass", null);

			regDefaultStyleClasses(styleClassesMap, ListViewTag.class, "linkBarStyleClass", "btn-toolbar d-flex justify-content-end mb-4");
			regDefaultStyleClasses(styleClassesMap, ListViewTag.class, "linkStyleClass", "btn btn-sm btn-outline-primary ms-2");

			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "panelStyleClass", "card mb-4");
			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "sectionTitleStyleClass", "card-header");
			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "actionBarStyleClass", "card-footer text-end");
			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(styleClassesMap, FilterTableTag.class, "styleClass", "m-2");
			regDefaultStyleClasses(styleClassesMap, FilterTableTag.class, "columnStyleClasses", "text-end p-2,position-relative p-2");

			regDefaultStyleClasses(styleClassesMap, ListPanelTag.class, "panelStyleClass", "card");

			regDefaultStyleClasses(styleClassesMap, ListTableTag.class, "pagePanelStyleClass", "card-footer text-end");

			regDefaultStyleClasses(styleClassesMap, FormViewTag.class, "linkBarStyleClass", "btn-toolbar d-flex justify-content-end mb-4");
			regDefaultStyleClasses(styleClassesMap, FormViewTag.class, "linkStyleClass", "btn btn-sm btn-outline-primary ms-2");

			regDefaultStyleClasses(styleClassesMap, FormPanelTag.class, "panelStyleClass", "card");
			regDefaultStyleClasses(styleClassesMap, FormPanelTag.class, "actionBarStyleClass", "card-footer text-end");
			regDefaultStyleClasses(styleClassesMap, FormPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(styleClassesMap, FormTableTag.class, "styleClass", "m-2");
			regDefaultStyleClasses(styleClassesMap, FormTableTag.class, "columnStyleClasses", "text-end p-2,position-relative p-2");

			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "panelStyleClass", "card");
			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "sectionTitleStyleClass", "card-header");
			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "actionBarStyleClass", "card-footer text-end");

			regDefaultStyleClasses(styleClassesMap, ReportTableTag.class, "styleClass", "m-2");
			regDefaultStyleClasses(styleClassesMap, ReportTableTag.class, "columnStyleClasses", "text-end p-2,position-relative p-2");

			regDefaultStyleClasses(styleClassesMap, DetailTag.class, "buttonStyleClass", "btn btn-sm");

			regDefaultStyleClasses(styleClassesMap, PropertyTag.class, "DOUBLE-labelStyleClass", "col-form-label");
			regDefaultStyleClasses(styleClassesMap, PropertyTag.class, "DOUBLE-class", "form-control");

		} else {

			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "pageStyleClass", null);
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "titleStyleClass", "pageTitle");
			regDefaultStyleClasses(styleClassesMap, ViewTag.class, "bodyStyleClass", "pageBody");

			regDefaultStyleClasses(styleClassesMap, ListViewTag.class, "linkBarStyleClass", "linkBar");

			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "panelStyleClass", "filterWindow");
			regDefaultStyleClasses(styleClassesMap, FilterPanelTag.class, "sectionTitleStyleClass", "sectionTitle");
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

			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "panelStyleClass", "filterWindow");
			regDefaultStyleClasses(styleClassesMap, ReportPanelTag.class, "sectionTitleStyleClass", "sectionTitle");
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