/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.view.template;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.servlet.jsp.JspException;

import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.controller.crud.CrudContext;
import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.NextException;
import org.nextframework.types.Money;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;
import org.nextframework.view.BeanTag;
import org.nextframework.view.ColumnTag;
import org.nextframework.view.ComboReloadGroupTag;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.PanelGridTag;
import org.nextframework.view.PanelTag;
import org.nextframework.web.WebUtils;

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class PropertyTag extends TemplateTag {

	private static Class<? extends PropertyTagFastRenderer> fastRendererClass = null;
	private static ThreadLocal<PropertyTagFastRenderer> fastRenderer = new ThreadLocal<PropertyTagFastRenderer>();

	public static final String INPUT = "input";

	public static final String OUTPUT = "output";

	public static final String COLUMN = "column";

	public static final String SINGLE = "single";

	public static final String DOUBLE = "double";

	public static final String DOUBLELINE = "doubleline";

	public static final String STACKED = "stacked";

	public static final String INVERT = "invert";

	private static final List<String> RENDERAS_OPTIONS = Arrays.asList(COLUMN, SINGLE, DOUBLE, DOUBLELINE, STACKED, INVERT);

	protected String name;
	protected String renderAs = null;
	protected String mode = null;

	//Column or panel
	protected String order = null;
	protected Integer colspan = null;

	// tag input e output
	protected Boolean showLabel;
	protected String label;
	protected Object type;
	protected String pattern = null;
	protected Boolean reloadOnChange = false;
	protected boolean replaceMessagesCodes = false;
	protected String trueFalseNullLabels;

	// select-one-button
	protected String selectOnePath;
	protected String selectOnePathParameters;
	protected String selectOneWindowSize;

	//select-one-insert
	protected String insertPath;

	// select-one ou select-many
	protected Object itens;
	protected Boolean useAjax;
	protected Boolean autoSugestUniqueItem;
	protected String optionalParams = "";
	protected Boolean holdValue;

	//ajax - somente utilizado se userAjax = true; 
	//executado quando termina-se de atualizar os itens do combo
	protected String onLoadItens = "";

	//select-one
	protected String selectLabelProperty;
	protected Boolean includeBlank = true;
	protected String blankLabel;

	// text-area
	protected Integer cols;
	protected Integer rows;

	//hidden
	protected Boolean write;

	//arquivo
	protected Boolean transientFile;
	protected boolean showDeleteButton = true;

	//estilos
	private String headerStyleClass;
	private String headerStyle;
	private String bodyStyleClass;
	private String bodyStyle;
	private String panelStyleClass;
	private String panelStyle;
	private String labelPanelStyleClass;
	private String labelPanelStyle;
	private String labelStyleClass;
	private String labelStyle;

	@Override
	protected void applyDefaultStyleClasses() throws JspException {
		//Não aplica no fluxo natural.
	}

	@Override
	protected String getSubComponentName() {
		return renderAs;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doComponent() throws Exception {

		PropertyConfigTag configTag = findParent(PropertyConfigTag.class);
		BaseTag findFirst = findFirst(PropertyConfigTag.class, PanelTag.class, ColumnTag.class, DataGridTag.class, PanelGridTag.class);

		if (getId() == null || "".equals(getId())) {
			id = generateUniqueId() + "_" + getName();
		}

		verifyRenderAs(configTag, findFirst);

		//Aplica estilos padrão apenas após resolver o 'renderAs'
		super.applyDefaultStyleClasses();

		verifyMode(configTag);

		verifyShowLabel(configTag);

		verifyOrder();

		verifyColspan();

		verifyUseAjax();

		if (DOUBLELINE.equals(renderAs)) {
			pushAttribute("labelseparator", "<BR>");
		}
		pushAttribute("compId", id);
		pushAttribute("Tproperty", this); //Legacy
		checkFastRenderer();
		if (!fastRenderer.get().render(this)) {//tentar renderização rápida
			includeJspTemplate(); //se nao for possível, utilizar renderização normal
		}
		popAttribute("Tproperty");
		popAttribute("compId");
		if (DOUBLELINE.equals(renderAs)) {
			popAttribute("labelseparator");
		}

	}

	private void verifyRenderAs(PropertyConfigTag configTag, BaseTag findFirst) {
		if (Util.strings.isNotEmpty(renderAs)) {
			renderAs = renderAs.toLowerCase();
		}
		if (Util.strings.isEmpty(renderAs)) {
			do {
				if (findFirst instanceof PropertyConfigTag && Util.strings.isNotEmpty(configTag.getRenderAs())) {
					renderAs = configTag.getRenderAs();
				} else if (findFirst instanceof PanelTag) {
					PanelTag panel = (PanelTag) findFirst;
					renderAs = panel.getPropertyRenderAs();
					if (renderAs == null) {
						// procurar opcoes de renderAs nas tags mais acima do panel, já que esse panel não está forçando a renderização double
						if (configTag != null && Util.strings.isNotEmpty(configTag.getRenderAs())) {
							if (configTag.getRenderAs().toLowerCase().equals(DOUBLELINE)) {
								renderAs = DOUBLELINE;
							} else {
								renderAs = SINGLE;
							}
						} else {
							renderAs = SINGLE;
						}
					}
				} else if (findFirst instanceof PanelGridTag) {
					PanelGridTag panelGrid = (PanelGridTag) findFirst;
					renderAs = panelGrid.getPropertyRenderAs();
					if (renderAs == null) {
						//procurar opcoes de renderAs nas tags mais acima do panel, já que esse panel não está forçando a renderização double
						if (configTag != null && Util.strings.isNotEmpty(configTag.getRenderAs())) {
							if (configTag.getRenderAs().toLowerCase().equals(DOUBLELINE)) {
								renderAs = DOUBLELINE;
							} else {
								renderAs = SINGLE;
							}
						} else {
							renderAs = SINGLE;
						}
					}
				} else if (findFirst instanceof DataGridTag) {
					renderAs = COLUMN;
				} else {
					renderAs = SINGLE;
				}
				break;
			} while (true);
		}
		if (DOUBLELINE.equals(renderAs)) {
			renderAs = SINGLE;
		}
		validateRenderAs(renderAs);
	}

	public static void validateRenderAs(String renderAs) {
		if (!RENDERAS_OPTIONS.contains(renderAs)) {
			throw new NextException("Property 'renderAs' must be one of: " + RENDERAS_OPTIONS + ". Value found: " + renderAs);
		}
	}

	private void verifyMode(PropertyConfigTag configTag) {
		if (Util.strings.isNotEmpty(mode)) {
			mode = mode.toLowerCase();
		}
		if (Util.strings.isEmpty(mode)) {
			if (configTag != null && Util.strings.isNotEmpty(configTag.getMode())) {
				mode = configTag.getMode();
			} else {
				if (Util.objects.isNotEmpty(type)) {
					mode = INPUT;
				} else {
					mode = OUTPUT;
				}
			}
		}
		validateMode(mode);
	}

	public static void validateMode(String mode) {
		if (!INPUT.equals(mode) && !OUTPUT.equals(mode)) {
			throw new NextException("Property 'mode' must be one of: input, output. Value found: " + mode);
		}
	}

	private void verifyShowLabel(PropertyConfigTag configTag) {
		if (showLabel == null && configTag != null && configTag.getShowLabel() != null) {
			showLabel = configTag.getShowLabel();
		}
		if (showLabel == null) {
			if (SINGLE.equals(renderAs)) {
				showLabel = false; // nao faz muito sentido escreve sozinho o label, é melhor mandar escrever quando quiser
			}
		}
		if (DOUBLE.equals(renderAs) || INVERT.equals(renderAs)) {
			showLabel = false;//se for modo double não imprimir o label porque já vai estar sendo escrito um
		}
		if (DOUBLELINE.equals(renderAs)) {
			showLabel = true;
		}
	}

	private void verifyOrder() {
		boolean noParentDetail = findParent2(DetailTag.class, false) == null;
		boolean crudList = CrudContext.getCurrentInstance() != null && CrudContext.getCurrentInstance().getListModel().getList() != null;
		if (order == null && noParentDetail && crudList) {
			BeanTag beanTag = findParent(BeanTag.class);
			order = "";
			if (beanTag != null) {
				order = Util.strings.uncaptalize(beanTag.getBeanDescriptor().getTargetClass().getSimpleName()) + ".";
				try {
					for (Annotation annotation : beanTag.getBeanDescriptor().getPropertyDescriptor(name).getAnnotations()) {
						if (annotation.annotationType().isAssignableFrom(Transient.class)) {
							order = null;//se for transient.. nao ordenar
						}
					}
				} catch (Exception e) {
					//se tentar checar que é transient.. e ocorrer algum problema, nao fazer nada
				}
			}
			if (order != null) {
				order += name;
			}
		}
	}

	private void verifyColspan() {
		if (colspan != null && (DOUBLE.equals(renderAs) || INVERT.equals(renderAs))) {
			colspan = colspan - 1;
		}
		if (colspan == null || colspan == 0) {
			colspan = 1;
		}
	}

	private void verifyUseAjax() {
		if (useAjax == null) {
			ComboReloadGroupTag comboReloadGroupTag = findParent(ComboReloadGroupTag.class);
			if (comboReloadGroupTag != null) {
				useAjax = comboReloadGroupTag.getUseAjax();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void checkFastRenderer() throws InstantiationException, IllegalAccessException {
		if (fastRenderer.get() == null) {
			if (fastRendererClass == null) {
				Class<?>[] allClassesOfType;
				allClassesOfType = ClassManagerFactory.getClassManager().getAllClassesOfType(PropertyTagFastRenderer.class);
				if (allClassesOfType.length > 0) {
					fastRendererClass = (Class<PropertyTagFastRenderer>) allClassesOfType[0];
					if (log.isDebugEnabled()) {
						log.debug("Using custom fast renderer for tag property of class: " + fastRendererClass.getName());
					}
				} else {
					fastRendererClass = NextPropertyTagFastRenderer.class;
					if (log.isDebugEnabled()) {
						log.debug("Using fast renderer for default tag property. ");
					}
				}
			}
			fastRenderer.set(fastRendererClass.newInstance());
		}
	}

	//funcionalidade chamada do template.. nao deve ser invocada
	//configura a proprieade caso ela seja id
	public Object getIdConfig() {
		if (type == null && isEntityId()) {
			if (write == null) {
				write = true;
			}
			type = "hidden";
		}
		return null;
	}

	public boolean isEntityId() {
		boolean res = false;
		Object attribute = getRequest().getAttribute("annotations");
		if (attribute != null) {
			Annotation[] annotations = (Annotation[]) attribute;
			for (Annotation annotation : annotations) {
				if (annotation instanceof Id) {
					res = true;
				}
			}
		}
		return res;
	}

	public String getHeader() {
		return getHeaderForLabel(label);
	}

	public String getHeaderForLabel(String label) {
		if (Util.strings.isNotEmpty(order)) {
			return getOrderedHeader(label, order);
		} else {
			return label;
		}
	}

	public String getOrderedHeader(String label, String order) {
		String link = getRequest().getContextPath() + NextWeb.getRequestContext().getRequestQuery() + "?orderBy=" + order;
		//Verifica URL Sufix
		link = WebUtils.rewriteUrl(link);
		return "<a class=\"order\" href=\"" + link + "\">" + label + "</a>";
	}

	/**
	 * Auto alinhamento dos valores de uma determinada coluna
	 */
	public String getColumnAlign() {
		//em modo input nao alinhar a direita pois o proprio input terá alinhamento
		Object type = getRequest().getAttribute("type");
		return getColumnAlignForType(type);
	}

	public String getColumnAlignForType(Object type) {
		if ("input".equalsIgnoreCase(getMode())) {
			return "";
		}
		if (Util.objects.isNotEmpty(getDynamicAttributesMap().get("align"))) {
			return getDynamicAttributesMap().get("align").toString();
		}
		if (type != null && (type.equals(Money.class) || (type instanceof Class<?> && Number.class.isAssignableFrom((Class<?>) type)))) {
			return "right";
		} else {
			return "";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRenderAs() {
		return renderAs;
	}

	public void setRenderAs(String renderAs) {
		this.renderAs = renderAs;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public Boolean getShowLabel() {
		return showLabel;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Boolean getReloadOnChange() {
		return reloadOnChange;
	}

	public void setReloadOnChange(Boolean reloadOnChange) {
		this.reloadOnChange = reloadOnChange;
	}

	public boolean isReplaceMessagesCodes() {
		return replaceMessagesCodes;
	}

	public void setReplaceMessagesCodes(boolean replaceMessagesCodes) {
		this.replaceMessagesCodes = replaceMessagesCodes;
	}

	public String getTrueFalseNullLabels() {
		return trueFalseNullLabels;
	}

	public void setTrueFalseNullLabels(String trueFalseNullValues) {
		this.trueFalseNullLabels = trueFalseNullValues;
	}

	public String getSelectOnePath() {
		return selectOnePath;
	}

	public void setSelectOnePath(String selectOnePath) {
		this.selectOnePath = selectOnePath;
	}

	public String getSelectOnePathParameters() {
		return selectOnePathParameters;
	}

	public void setSelectOnePathParameters(String selectOnePathParameters) {
		this.selectOnePathParameters = selectOnePathParameters;
	}

	public String getSelectOneWindowSize() {
		return selectOneWindowSize;
	}

	public void setSelectOneWindowSize(String selectOnePathWindowSize) {
		this.selectOneWindowSize = selectOnePathWindowSize;
	}

	public String getInsertPath() {
		return insertPath;
	}

	public void setInsertPath(String insertPath) {
		this.insertPath = insertPath;
	}

	public Object getItens() {
		return itens;
	}

	public void setItens(Object itens) {
		this.itens = itens;
	}

	public Boolean getUseAjax() {
		return useAjax;
	}

	public String getUseAjaxString() {
		return useAjax == null ? "" : useAjax.toString();
	}

	public void setUseAjax(Boolean useAjax) {
		this.useAjax = useAjax;
	}

	public Boolean getAutoSugestUniqueItem() {
		return autoSugestUniqueItem;
	}

	public void setAutoSugestUniqueItem(Boolean autoSugestUniqueItem) {
		this.autoSugestUniqueItem = autoSugestUniqueItem;
	}

	public String getOptionalParams() {
		return optionalParams;
	}

	public void setOptionalParams(String optionalParams) {
		this.optionalParams = optionalParams;
	}

	public Boolean getHoldValue() {
		return holdValue;
	}

	public void setHoldValue(Boolean holdValue) {
		this.holdValue = holdValue;
	}

	public String getOnLoadItens() {
		return onLoadItens;
	}

	public void setOnLoadItens(String onLoadItens) {
		this.onLoadItens = onLoadItens;
	}

	public String getSelectLabelProperty() {
		return selectLabelProperty;
	}

	public void setSelectLabelProperty(String selectLabelProperty) {
		this.selectLabelProperty = selectLabelProperty;
	}

	public Boolean getIncludeBlank() {
		return includeBlank;
	}

	public void setIncludeBlank(Boolean includeBlank) {
		this.includeBlank = includeBlank;
	}

	public String getBlankLabel() {
		return blankLabel;
	}

	public void setBlankLabel(String blankLabel) {
		this.blankLabel = blankLabel;
	}

	public Integer getCols() {
		return cols;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Boolean getWrite() {
		return write;
	}

	public void setWrite(Boolean write) {
		this.write = write;
	}

	public Boolean getTransientFile() {
		return transientFile;
	}

	public void setTransientFile(Boolean transientFile) {
		this.transientFile = transientFile;
	}

	public boolean isShowDeleteButton() {
		return showDeleteButton;
	}

	public void setShowDeleteButton(boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}

	@Deprecated
	public boolean isShowRemoverButton() {
		return showDeleteButton;
	}

	@Deprecated
	public void setShowRemoverButton(boolean showRemoverButton) {
		this.showDeleteButton = showRemoverButton;
	}

	public String getHeaderStyleClass() {
		return headerStyleClass;
	}

	public void setHeaderStyleClass(String headerStyleClass) {
		this.headerStyleClass = headerStyleClass;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public String getBodyStyleClass() {
		return bodyStyleClass;
	}

	public void setBodyStyleClass(String bodyStyleClass) {
		this.bodyStyleClass = bodyStyleClass;
	}

	public String getBodyStyle() {
		return bodyStyle;
	}

	public void setBodyStyle(String bodyStyle) {
		this.bodyStyle = bodyStyle;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getPanelStyle() {
		return panelStyle;
	}

	public void setPanelStyle(String panelStyle) {
		this.panelStyle = panelStyle;
	}

	public String getLabelPanelStyle() {
		return labelPanelStyle;
	}

	public void setLabelPanelStyle(String labelPanelStyle) {
		this.labelPanelStyle = labelPanelStyle;
	}

	public String getLabelPanelStyleClass() {
		return labelPanelStyleClass;
	}

	public void setLabelPanelStyleClass(String labelPanelStyleClass) {
		this.labelPanelStyleClass = labelPanelStyleClass;
	}

	public String getLabelStyleClass() {
		return labelStyleClass;
	}

	public void setLabelStyleClass(String labelStyleClass) {
		this.labelStyleClass = labelStyleClass;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

}