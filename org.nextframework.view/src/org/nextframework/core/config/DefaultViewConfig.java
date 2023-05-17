package org.nextframework.core.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nextframework.view.BaseTag;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.GroupTag;
import org.nextframework.view.InputTag;
import org.nextframework.view.InputTagType;
import org.nextframework.view.LinkTag;
import org.nextframework.view.MessagesTag;
import org.nextframework.view.ModalTag;
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
import org.nextframework.view.template.PropertyLayoutTag;
import org.nextframework.view.template.PropertyTag;
import org.nextframework.view.template.ReportPanelTag;
import org.nextframework.view.template.ReportTableTag;
import org.nextframework.view.template.SimplePanelTag;
import org.nextframework.view.template.ViewTag;

public class DefaultViewConfig implements ViewConfig {

	protected Map<Class<?>, InputTagType> customInputTypesMap = new HashMap<Class<?>, InputTagType>();

	protected Map<Class<? extends BaseTag>, Map<String, String>> styleClassesMap = new LinkedHashMap<Class<? extends BaseTag>, Map<String, String>>();

	@Override
	public String getJSPDefaultCharset() {
		return "iso-8859-1";
	}

	@Override
	public Map<Class<?>, InputTagType> getCustomInputTypes() {
		return customInputTypesMap;
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
	public boolean isDefaultFlatMode() {
		return true;
	}

	@Override
	public Integer getDefaultColumns() {
		return isUseBootstrap() ? 12 : 2;
	}

	@Override
	public Integer getDefaultColspan(String renderAs) {
		return isUseBootstrap() && !PropertyTag.COLUMN.equals(renderAs) ? 2 : 1;
	}

	@Override
	public String getDefaultPropertyRenderAs() {
		return isDefaultFlatMode() ? PropertyTag.STACKED : PropertyTag.DOUBLE;
	}

	@Override
	public RequiredMarkMode getRequiredMarkMode() {
		return isUseBootstrap() ? RequiredMarkMode.STYLECLASS : RequiredMarkMode.AFTER;
	}

	@Override
	public String getRequiredMarkString() {
		return isUseBootstrap() ? "" : "&#0149;";
	}

	@Override
	public Set<String> getStyleClassFields(Class<? extends BaseTag> tagClass) {
		Map<String, String> tagMap = getStyleClasses().get(tagClass);
		return tagMap != null ? tagMap.keySet() : null;
	}

	@Override
	public String getDefaultStyleClass(Class<? extends BaseTag> tagClass, String field) {
		Map<String, String> tagMap = getStyleClasses().get(tagClass);
		return tagMap != null ? tagMap.get(field) : null;
	}

	protected Map<Class<? extends BaseTag>, Map<String, String>> getStyleClasses() {
		//styleClassesMap.clear(); //TODO REMOVER ISSO EM PRODUCAO!!
		if (styleClassesMap.isEmpty()) {
			initStyleClasses();
		}
		return styleClassesMap;
	}

	protected void initStyleClasses() {

		// Componentes

		if (isUseBootstrap()) {

			regDefaultStyleClasses(DataGridTag.class, "containerStyleClass", "table-responsive");
			regDefaultStyleClasses(DataGridTag.class, "styleClass", "table table-sm table-striped table-bordered table-hover");
			regDefaultStyleClasses(DataGridTag.class, "headerStyleClass", "table-light");
			regDefaultStyleClasses(DataGridTag.class, "groupStyleClasses", "group1, group2, group3");
			regDefaultStyleClasses(DataGridTag.class, "footerStyleClass", "table-light");
			regDefaultStyleClasses(DataGridTag.class, "baseReportStyleClass", "w-100");

			regDefaultStyleClasses(GroupTag.class, "fieldsetStyleClass", "border border-primary border-opacity-50 rounded p-2");
			regDefaultStyleClasses(GroupTag.class, "legendStyleClass", "float-none w-auto m-0 p-2 h6 text-primary text-opacity-75");

			regDefaultStyleClasses(InputTag.class, "CHECKBOX-class", "form-check-input");
			regDefaultStyleClasses(InputTag.class, "CHECKLIST-class", "form-check-input");
			regDefaultStyleClasses(InputTag.class, "DATE-group", "input-group flex-nowrap");
			regDefaultStyleClasses(InputTag.class, "DATE-button", "bi bi-calendar3 pe-auto input-group-text");
			regDefaultStyleClasses(InputTag.class, "HIDDEN-class", " ");
			regDefaultStyleClasses(InputTag.class, "RADIO-class", "form-check-input");
			regDefaultStyleClasses(InputTag.class, "SELECT_ONE-class", "form-select");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_BOX-group", "input-group flex-nowrap");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_BOX-buttons", "input-group-text d-flex flex-column justify-content-center gap-2");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_BOX-buttonAdd", "btn btn-primary bi bi-arrow-right");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_BOX-buttonRemove", "btn btn-primary bi bi-arrow-left");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_POPUP-group", "input-group flex-nowrap");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_POPUP-button", "bi bi-three-dots input-group-text");
			regDefaultStyleClasses(InputTag.class, "class", "form-control");
			regDefaultStyleClasses(InputTag.class, "requiredStyleClass", "requiredMark");

			regDefaultStyleClasses(LinkTag.class, "BUTTON-class", "btn btn-primary");

			regDefaultStyleClasses(MenuTag.class, "panelStyleClass", "mb-2 mb-md-0");

			regDefaultStyleClasses(MessagesTag.class, "containerClass", "container messageBlock");
			regDefaultStyleClasses(MessagesTag.class, "bindBlockClass", "alert alert-danger");
			regDefaultStyleClasses(MessagesTag.class, "messageBlockClass", "alert alert-primary");
			regDefaultStyleClasses(MessagesTag.class, "titleClass", "h6");
			regDefaultStyleClasses(MessagesTag.class, "fieldNameClass", "fw-bold");
			regDefaultStyleClasses(MessagesTag.class, "globalErrorClass", "error");
			regDefaultStyleClasses(MessagesTag.class, "bindErrorClass", "error");
			regDefaultStyleClasses(MessagesTag.class, "validationErrorClass", "error");
			regDefaultStyleClasses(MessagesTag.class, "debugClass", "debug");
			regDefaultStyleClasses(MessagesTag.class, "traceClass", "trace");
			regDefaultStyleClasses(MessagesTag.class, "infoClass", "info");
			regDefaultStyleClasses(MessagesTag.class, "warnClass", "warn");
			regDefaultStyleClasses(MessagesTag.class, "errorClass", "error");
			regDefaultStyleClasses(MessagesTag.class, "eventClass", "event");
			regDefaultStyleClasses(MessagesTag.class, "toastClass", "alert alert-primary alert-toast");
			regDefaultStyleClasses(MessagesTag.class, "exceptionClass", "exceptionItem");
			regDefaultStyleClasses(MessagesTag.class, "exceptionCauseClass", "causeItem");

			regDefaultStyleClasses(ModalTag.class, "overlayStyleClass", "modal fade");
			regDefaultStyleClasses(ModalTag.class, "panelStyleClass", "modal-dialog modal-dialog-centered modal-dialog-scrollable");
			regDefaultStyleClasses(ModalTag.class, "contentStyleClass", "modal-content p-4 m-4 shadow");

			regDefaultStyleClasses(PaggingTag.class, "panelClass", "pagination pagination-sm d-inline-flex m-0");
			regDefaultStyleClasses(PaggingTag.class, "itemClass", "page-item");
			regDefaultStyleClasses(PaggingTag.class, "selectedClass", "page-link active");
			regDefaultStyleClasses(PaggingTag.class, "unselectedClass", "page-link");

			regDefaultStyleClasses(SubmitTag.class, "BUTTON-class", "btn btn-primary");
			regDefaultStyleClasses(SubmitTag.class, "SUBMIT-class", "btn btn-primary");

			regDefaultStyleClasses(TabPanelTag.class, "navPanelClass", "mb-2");
			regDefaultStyleClasses(TabPanelTag.class, "navClass", "nav nav-tabs");
			regDefaultStyleClasses(TabPanelTag.class, "navItemClass", "nav-item");
			regDefaultStyleClasses(TabPanelTag.class, "navLinkClass", "nav-link");
			regDefaultStyleClasses(TabPanelTag.class, "contentClass", "tab-content");
			regDefaultStyleClasses(TabPanelTag.class, "selectedClass", "tab-pane");
			regDefaultStyleClasses(TabPanelTag.class, "unselectedClass", "tab-pane");

		} else {

			regDefaultStyleClasses(DataGridTag.class, "containerStyleClass", "datagridcontainer");
			regDefaultStyleClasses(DataGridTag.class, "styleClass", "dataGrid");
			regDefaultStyleClasses(DataGridTag.class, "headerStyleClass", "dataGridHeader");
			regDefaultStyleClasses(DataGridTag.class, "bodyStyleClasses", "dataGridBody1, dataGridBody2");
			regDefaultStyleClasses(DataGridTag.class, "footerStyleClass", "dataGridFooter");
			regDefaultStyleClasses(DataGridTag.class, "groupStyleClasses", "dataGridGroup1, dataGridGroup2, dataGridGroup3");

			regDefaultStyleClasses(InputTag.class, "DATE-button", "calendarbutton");
			regDefaultStyleClasses(InputTag.class, "CHECKBOX-class", "checkboxClass");
			regDefaultStyleClasses(InputTag.class, "SELECT_MANY_POPUP-button", "select_many_popup_button");
			regDefaultStyleClasses(InputTag.class, "requiredStyleClass", "requiredMark");

			regDefaultStyleClasses(MenuTag.class, "panelStyleClass", "menuClass");

			regDefaultStyleClasses(MessagesTag.class, "containerClass", "messagesContainer");
			regDefaultStyleClasses(MessagesTag.class, "bindBlockClass", "bindBlock");
			regDefaultStyleClasses(MessagesTag.class, "messageBlockClass", "messageBlock");
			regDefaultStyleClasses(MessagesTag.class, "titleClass", "messageTitle");
			regDefaultStyleClasses(MessagesTag.class, "globalErrorClass", "globalError");
			regDefaultStyleClasses(MessagesTag.class, "fieldNameClass", "fieldName");
			regDefaultStyleClasses(MessagesTag.class, "bindErrorClass", "bindError");
			regDefaultStyleClasses(MessagesTag.class, "validationErrorClass", "validationError");
			regDefaultStyleClasses(MessagesTag.class, "debugClass", "debug");
			regDefaultStyleClasses(MessagesTag.class, "traceClass", "trace");
			regDefaultStyleClasses(MessagesTag.class, "infoClass", "info");
			regDefaultStyleClasses(MessagesTag.class, "warnClass", "warn");
			regDefaultStyleClasses(MessagesTag.class, "errorClass", "error");
			regDefaultStyleClasses(MessagesTag.class, "eventClass", "event");
			regDefaultStyleClasses(MessagesTag.class, "toastClass", "messageToast");
			regDefaultStyleClasses(MessagesTag.class, "exceptionClass", "exceptionItem");
			regDefaultStyleClasses(MessagesTag.class, "exceptionCauseClass", "causeItem");

			regDefaultStyleClasses(ModalTag.class, "overlayStyleClass", "progressOverlay");
			regDefaultStyleClasses(ModalTag.class, "panelStyleClass", "progressbarBox");
			regDefaultStyleClasses(ModalTag.class, "contentStyleClass", "progressbarBody");

			regDefaultStyleClasses(PaggingTag.class, "panelClass", "pagePanel");
			regDefaultStyleClasses(PaggingTag.class, "selectedClass", "pageSelected");

			regDefaultStyleClasses(TabPanelTag.class, "navClass", "tabPanel");
			regDefaultStyleClasses(TabPanelTag.class, "selectedClass", "tabSelected");
			regDefaultStyleClasses(TabPanelTag.class, "unselectedClass", "tabUnselected");

		}

		// Templates

		if (isUseBootstrap()) {

			regDefaultStyleClasses(ViewTag.class, "pageStyleClass", null);
			regDefaultStyleClasses(ViewTag.class, "titleStyleClass", "h3");
			regDefaultStyleClasses(ViewTag.class, "bodyStyleClass", null);

			regDefaultStyleClasses(SimplePanelTag.class, "panelStyleClass", "card mb-4");
			regDefaultStyleClasses(SimplePanelTag.class, "sectionTitleStyleClass", "card-header h5");
			regDefaultStyleClasses(SimplePanelTag.class, "bodyStyleClass", "card-body p-2");
			regDefaultStyleClasses(SimplePanelTag.class, "actionBarStyleClass", "card-footer d-flex flex-wrap justify-content-end align-items-center gap-2");

			regDefaultStyleClasses(ListViewTag.class, "linkBarStyleClass", "btn-toolbar d-flex gap-2 justify-content-end mb-4");
			regDefaultStyleClasses(ListViewTag.class, "linkStyleClass", "btn btn-sm btn-outline-secondary");

			regDefaultStyleClasses(FilterPanelTag.class, "panelStyleClass", "card mb-4");
			regDefaultStyleClasses(FilterPanelTag.class, "sectionTitleStyleClass", "card-header h5");
			regDefaultStyleClasses(FilterPanelTag.class, "bodyStyleClass", "card-body p-2");
			regDefaultStyleClasses(FilterPanelTag.class, "actionBarStyleClass", "card-footer d-flex flex-wrap justify-content-end align-items-center gap-2");
			regDefaultStyleClasses(FilterPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(FilterTableTag.class, "NORMAL-styleClass", "m-2");
			regDefaultStyleClasses(FilterTableTag.class, "NORMAL-columnStyleClasses", "p-2");
			regDefaultStyleClasses(FilterTableTag.class, "FLAT-styleClass", "container-fluid");
			regDefaultStyleClasses(FilterTableTag.class, "FLAT-rowStyleClasses", "row");
			regDefaultStyleClasses(FilterTableTag.class, "FLAT-columnStyleClasses", "col-md-{CS} p-2");

			regDefaultStyleClasses(ListPanelTag.class, "panelStyleClass", "card mb-4");
			regDefaultStyleClasses(ListPanelTag.class, "sectionTitleStyleClass", "card-header h5");
			regDefaultStyleClasses(ListPanelTag.class, "bodyStyleClass", null);
			regDefaultStyleClasses(ListPanelTag.class, "actionBarStyleClass", "card-footer d-flex flex-wrap justify-content-end align-items-center gap-2");

			regDefaultStyleClasses(ListTableTag.class, "pagePanelStyleClass", "card-footer d-flex flex-wrap justify-content-end align-items-center gap-2");

			regDefaultStyleClasses(FormViewTag.class, "linkBarStyleClass", "btn-toolbar d-flex gap-2 justify-content-end mb-4"); //Melhor tirar mb-4 e tentar gap
			regDefaultStyleClasses(FormViewTag.class, "linkStyleClass", "btn btn-sm btn-outline-secondary");

			regDefaultStyleClasses(FormPanelTag.class, "panelStyleClass", "card mb-4");
			regDefaultStyleClasses(FormPanelTag.class, "sectionTitleStyleClass", "card-header h5");
			regDefaultStyleClasses(FormPanelTag.class, "bodyStyleClass", "card-body p-2");
			regDefaultStyleClasses(FormPanelTag.class, "actionBarStyleClass", "card-footer d-flex flex-wrap justify-content-end align-items-center gap-2");
			regDefaultStyleClasses(FormPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(FormTableTag.class, "NORMAL-styleClass", "m-2");
			regDefaultStyleClasses(FormTableTag.class, "NORMAL-columnStyleClasses", "p-2");
			regDefaultStyleClasses(FormTableTag.class, "FLAT-styleClass", "container-fluid");
			regDefaultStyleClasses(FormTableTag.class, "FLAT-rowStyleClasses", "row");
			regDefaultStyleClasses(FormTableTag.class, "FLAT-columnStyleClasses", "col-md-{CS} p-2");

			regDefaultStyleClasses(ReportPanelTag.class, "panelStyleClass", "card mb-4");
			regDefaultStyleClasses(ReportPanelTag.class, "sectionTitleStyleClass", "card-header h5");
			regDefaultStyleClasses(ReportPanelTag.class, "bodyStyleClass", "card-body p-2");
			regDefaultStyleClasses(ReportPanelTag.class, "actionBarStyleClass", "card-footer d-flex flex-wrap justify-content-end align-items-center gap-2");
			regDefaultStyleClasses(ReportPanelTag.class, "buttonStyleClass", "btn btn-primary");

			regDefaultStyleClasses(ReportTableTag.class, "NORMAL-styleClass", "m-2");
			regDefaultStyleClasses(ReportTableTag.class, "NORMAL-columnStyleClasses", "p-2");
			regDefaultStyleClasses(ReportTableTag.class, "FLAT-styleClass", "container-fluid");
			regDefaultStyleClasses(ReportTableTag.class, "FLAT-rowStyleClasses", "row");
			regDefaultStyleClasses(ReportTableTag.class, "FLAT-columnStyleClasses", "col-md-{CS} p-2");

			regDefaultStyleClasses(DetailTag.class, "actionButtonStyleClass", "btn btn-sm btn-primary");
			regDefaultStyleClasses(DetailTag.class, "newLineButtonStyleClass", "btn btn-sm btn-primary mt-1");

			regDefaultStyleClasses(PropertyTag.class, "DOUBLE-labelPanelStyleClass", "text-end"); //"d-flex align-items-center justify-content-end text-end"
			regDefaultStyleClasses(PropertyTag.class, "DOUBLE-labelStyleClass", "col-form-label");
			regDefaultStyleClasses(PropertyTag.class, "DOUBLE-panelStyleClass", "align-middle"); //d-flex align-items-center
			regDefaultStyleClasses(PropertyTag.class, "INVERT-panelStyleClass", "align-middle text-end"); //d-flex align-items-center justify-content-end
			regDefaultStyleClasses(PropertyTag.class, "INVERT-labelPanelStyleClass", "align-middle text-wrap"); //d-flex align-items-center form-check text-wrap text-truncate
			regDefaultStyleClasses(PropertyTag.class, "INVERT-labelStyleClass", "form-check-label");
			regDefaultStyleClasses(PropertyTag.class, "STACKED-labelStyleClass", "w-100 text-nowrap text-truncate");
			regDefaultStyleClasses(PropertyTag.class, "STACKED_INVERT-panelStyleClass", "d-flex align-items-end");
			regDefaultStyleClasses(PropertyTag.class, "STACKED_INVERT-stackedPanelStyleClass", "form-check text-wrap");
			regDefaultStyleClasses(PropertyTag.class, "STACKED_INVERT-labelStyleClass", "form-check-label");

			//TODO Fazer a tag PropertyTag utilizar PropertyLayoutTag
			Map<String, String> propertyTagMap = styleClassesMap.get(PropertyTag.class);
			for (Entry<String, String> entry : propertyTagMap.entrySet()) {
				regDefaultStyleClasses(PropertyLayoutTag.class, entry.getKey(), entry.getValue());
			}

		} else {

			regDefaultStyleClasses(ViewTag.class, "pageStyleClass", null);
			regDefaultStyleClasses(ViewTag.class, "titleStyleClass", "pageTitle");
			regDefaultStyleClasses(ViewTag.class, "bodyStyleClass", "pageBody");

			regDefaultStyleClasses(ListViewTag.class, "linkBarStyleClass", "linkBar");

			regDefaultStyleClasses(FilterPanelTag.class, "panelStyleClass", "filterWindow");
			regDefaultStyleClasses(FilterPanelTag.class, "sectionTitleStyleClass", "sectionTitle");
			regDefaultStyleClasses(FilterPanelTag.class, "actionBarStyleClass", "actionBar");

			regDefaultStyleClasses(FilterTableTag.class, "NORMAL-columnStyleClasses", "labelColumn, propertyColumn");
			regDefaultStyleClasses(FilterTableTag.class, "styleClass", "inputTable");

			regDefaultStyleClasses(ListPanelTag.class, "panelStyleClass", "resultWindow");

			regDefaultStyleClasses(ListTableTag.class, "pagePanelStyleClass", "pagging");

			regDefaultStyleClasses(FormViewTag.class, "linkBarStyleClass", "linkBar");

			regDefaultStyleClasses(FormPanelTag.class, "panelStyleClass", "inputWindow");
			regDefaultStyleClasses(FormPanelTag.class, "actionBarStyleClass", "actionBar");

			regDefaultStyleClasses(FormTableTag.class, "NORMAL-columnStyleClasses", "labelColumn, propertyColumn");
			regDefaultStyleClasses(FormTableTag.class, "styleClass", "inputTable");

			regDefaultStyleClasses(ReportPanelTag.class, "panelStyleClass", "filterWindow");
			regDefaultStyleClasses(ReportPanelTag.class, "sectionTitleStyleClass", "sectionTitle");
			regDefaultStyleClasses(ReportPanelTag.class, "actionBarStyleClass", "actionBar");

			regDefaultStyleClasses(ReportTableTag.class, "NORMAL-columnStyleClasses", "labelColumn, propertyColumn");
			regDefaultStyleClasses(ReportTableTag.class, "styleClass", "inputTable");

			regDefaultStyleClasses(DetailTag.class, "panelStyleClass", "inputTable");

		}

	}

	protected void regDefaultStyleClasses(Class<? extends BaseTag> tagClass, String field, String styleClass) {
		Map<String, String> tagMap = styleClassesMap.get(tagClass);
		if (tagMap == null) {
			tagMap = new LinkedHashMap<String, String>();
			styleClassesMap.put(tagClass, tagMap);
		}
		tagMap.put(field, styleClass);
	}

}