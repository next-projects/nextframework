package org.nextframework.report.generator.mvc;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.controller.ClasspathModelAndView;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.Input;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.OnErrors;
import org.nextframework.controller.ResourceModelAndView;
import org.nextframework.controller.resource.Resource;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.BusinessException;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportItemIterator;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.ReportGenerator;
import org.nextframework.report.generator.ReportReader;
import org.nextframework.report.generator.annotation.ReportField;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.report.generator.datasource.DataSourceProvider;
import org.nextframework.report.generator.datasource.extension.GeneratedReportListener;
import org.nextframework.report.generator.generated.ReportSpec;
import org.nextframework.report.generator.layout.DynamicBaseReportDefinition;
import org.nextframework.report.renderer.html.HtmlReportRenderer;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.report.renderer.jasper.JasperUtils;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.view.progress.IProgressMonitor;
import org.nextframework.view.progress.ProgressMonitor;
import org.nextframework.view.progress.ProgressTask;
import org.nextframework.view.progress.ProgressTaskFactory;
import org.nextframework.web.WebUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

public abstract class ReportDesignController<CUSTOM_BEAN extends ReportDesignCustomBean> extends MultiActionController {

	ReportDesignControllerUtil util = new ReportDesignControllerUtil();

	protected Class<CUSTOM_BEAN> customBeanClass;

	@SuppressWarnings("all")
	public ReportDesignController() {
		Class[] classes = GenericTypeResolver.resolveTypeArguments(this.getClass(), ReportDesignController.class);
		this.customBeanClass = classes[0];
	}

	/////////////////////////////////////////////// EDIT ///////////////////////////////////////////////

	@DefaultAction
	@SuppressWarnings("rawtypes")
	public ModelAndView index(WebRequestContext request) {
		Class<?>[] entities = util.getAvaiableEntities();
		Map<Class, String> displayNames = util.getDisplayNameForEntities(entities, request.getLocale());
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("entities", entities);
		setAttribute("displayNames", displayNames);
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design1");
	}

	public ModelAndView selectProperties(ReportDesignModel model) throws SAXException, IOException {

		ReportElement reportElement = null;
		if (model.getReportXml() != null) {
			reportElement = new ReportReader(model.getReportXml()).getReportElement();
		}
		Class<?> selectedGeneratedType = getReportType(model);
		Set<String> avaiableProperties = util.getAvailablePropertiesForClass(selectedGeneratedType, null, 3);

		if (!model.getProperties().contains("id")) {
			model.getProperties().add("id");
		}

		Map<String, Map<String, Object>> propertiesMetadata = util.getPropertiesMetadata(reportElement, getLocale(), selectedGeneratedType, avaiableProperties);

		setAttribute("model", model);
		setAttribute("avaiableProperties", avaiableProperties);
		setAttribute("propertyMetadata", propertiesMetadata);
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("reportTypeDisplayName", BeanDescriptorFactory.forClass(ClassUtils.getUserClass(model.getSelectedType())).getDisplayName());

		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design2");
	}

	private Class<?> getReportType(ReportDesignModel model) {
		//TODO update this code if there is more than one datasourceprovider
		if (model.getSelectedGeneratedType() != null) {
			return model.getSelectedGeneratedType();
		}
		try {
			DataSourceProvider defaultDataSourceProvider = ServiceFactory.getService(DataSourceProvider.class);
			Class<?> selectedGeneratedType = model.getSelectedType();
			return defaultDataSourceProvider.getMainType(selectedGeneratedType.getName());
		} catch (Exception e) {
			throw new RuntimeException("Verify datasourceprovider code", e);
		}
	}

	public ModelAndView designReport(ReportDesignModel model) throws SAXException, IOException {

		ReportElement reportElement = null;
		if (model.getReportXml() != null) {
			reportElement = new ReportReader(model.getReportXml()).getReportElement();
		}

		Set<String> properties = new HashSet<String>();
		properties.addAll(model.getProperties());
		Class<?> reportType = getReportType(model);
		Set<String> avaiablePropertiesForClass = util.getAvailablePropertiesForClass(reportType, null, 3);
		properties.addAll(avaiablePropertiesForClass);

		List<CalculatedFieldElement> calculatedFields = new ArrayList<CalculatedFieldElement>();
		if (model.getReportElement() != null) {
			calculatedFields = model.getReportElement().getData().getCalculatedFields();
			for (CalculatedFieldElement calculatedFieldElement : calculatedFields) {
				properties.remove(calculatedFieldElement.getName());
			}
		}

		Map<String, Map<String, Object>> propertiesMetadata = util.getPropertiesMetadata(reportElement, getLocale(), reportType, properties);
		//complete metadata with calculated fields
		for (CalculatedFieldElement calculatedFieldElement : calculatedFields) {
			Map<String, Object> map = util.getPropertiesMapForCalculatedField(calculatedFieldElement);
			propertiesMetadata.put(calculatedFieldElement.getName(), map);
			model.getProperties().add(calculatedFieldElement.getName());
		}

		try {
			if (model != null && model.getId() != null) {
				CUSTOM_BEAN customBean = loadPersistedReportById(model.getId()); //FIXME Está carregando 2 vezes
				setAttribute("customBean", customBean);
			}
			setAttribute("customBeanClass", customBeanClass);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível carregar os dados customizados", e);
		}

		setAttribute("emptyList", new ArrayList<Object>());
		setAttribute("model", model);
		setAttribute("reportTypeDisplayName", BeanDescriptorFactory.forClass(ClassUtils.getUserClass(reportType)).getDisplayName());
		setAttribute("propertyMetadata", propertiesMetadata);
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("avaiableProperties", avaiablePropertiesForClass);
		setAttribute("controllerPath", "/" + NextWeb.getApplicationContext().getApplicationName() + NextWeb.getRequestContext().getFirstRequestUrl());

		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design3");
	}

	public ModelAndView editXMLOnError() {
		ReportDesignModel model = loadDesignModel();
		setAttribute("model", model);
		setAttribute("crudPath", getPathForReportCrud());
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.editXML");
	}

	@Input("editXMLOnError")
	public ModelAndView editDesignForId() throws SAXException, IOException {

		ReportDesignModel model = loadDesignModel();

		ReportReader reader = new ReportReader(model.getReportXml());
		ReportElement reportElement = reader.getReportElement();
		model.setSelectedType(ClassUtils.getUserClass(reportElement.getData().getMainType()));
		model.setSelectedGeneratedType(reportElement.getData().getMainType());
		model.setReportElement(reportElement);

		setAttribute("groups", reportElement.getData().getGroups());
		setAttribute("filters", reportElement.getData().getFilters());
		setAttribute("items", reportElement.getLayout().getItems());
		setAttribute("charts", reportElement.getCharts() != null ? reportElement.getCharts().getItems() : null);

		model.getProperties().addAll(reportElement.getProperties());
		model.setReportTitle(reportElement.getReportTitle());

		return designReport(model);
	}

	@SuppressWarnings("all")
	@OnErrors("editDesignForId")
	public ModelAndView saveReport(WebRequestContext request, ReportDesignModel model) throws Exception {

		CUSTOM_BEAN customBean = (CUSTOM_BEAN) customBeanClass.newInstance();

		//Faz o bind no bean customizado
		ServletRequestDataBinder binder = bind(request, customBean, false);
		BindException errors = new BindException(binder.getBindingResult());
		if (errors.hasErrors()) {
			throw new NextException("Não foi possível fazer o bind dos dados customizados", errors);
		}

		//Faz leitura da composição do relatório
		ReportElement reportElement = new ReportReader(model.getReportXml()).getReportElement();

		validateRequiredFields(model, reportElement);

		//Define atributos básicos do custombean
		customBean.setId(model.getId());
		customBean.setXml(model.getReportXml());
		customBean.setReportPublic(model.getReportPublic());

		//salva!
		persistReport(model, reportElement, customBean);

		return new ModelAndView("redirect:" + getPathForReportCrud());
	}

	private void validateRequiredFields(ReportDesignModel model, ReportElement reportElement) {

		if (model.getSelectedType() == null) { //Se tiver no modo edição de XML
			return;
		}

		Locale locale = getLocale();
		Class<?> selectedGeneratedType = getReportType(model);
		Set<String> avaiableProperties = util.getAvailablePropertiesForClass(selectedGeneratedType, null, 1);
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(selectedGeneratedType);

		String camposRequired = "";
		boolean possuiFiltro = false;
		for (String property : avaiableProperties) {
			PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(property);
			ReportField reportField = propertyDescriptor.getAnnotation(ReportField.class);
			boolean filterable = util.isFilterable(beanDescriptor, property, reportField);
			if (filterable) {
				if (reportField != null && reportField.requiredFilter()) {
					String completeDisplayName = util.getCompleteDisplayName(beanDescriptor, propertyDescriptor, property, locale);
					camposRequired += (camposRequired.length() == 0 ? "" : ", ") + completeDisplayName;
					FilterElement filter = reportElement.getData().getFilterByName(property);
					if (filter != null && filter.isFilterRequired()) {
						possuiFiltro = true;
						break;
					}
				}
			}
		}

		if (!possuiFiltro && camposRequired.length() > 0) {
			throw new NextException("É necessário que pelo menos um dos filtros obrigatórios seja definido: " + camposRequired);
		}

	}

	public ModelAndView showFilterViewForId(WebRequestContext request) {
		ReportDesignModel model = loadDesignModel();
		try {
			return showFilterView(request, model);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível executar o relatório", e);
		}
	}

	protected ReportDesignModel loadDesignModel() {
		CUSTOM_BEAN customBean = loadPersistedReportById(new Integer(getParameter("id")));
		ReportDesignModel model = new ReportDesignModel();
		model.setId(customBean.getId());
		model.setReportXml(customBean.getXml());
		model.setReportPublic(customBean.getReportPublic());
		return model;
	}

	/////////////////////////////////////////////// FILTER ///////////////////////////////////////////////

	public ModelAndView showFilterView(WebRequestContext request, ReportDesignModel model) throws Exception {

		ReportElement reportElement = new ReportReader(model.getReportXml()).getReportElement();

		Map<String, Map<String, Object>> filterProperties = new LinkedHashMap<String, Map<String, Object>>();
		List<FilterElement> filtersElements = reportElement.getData().getFilters();
		for (FilterElement filterElement : filtersElements) {
			Map<String, Object> properties = new HashMap<String, Object>();
			if (Util.strings.isNotEmpty(filterElement.getFilterDisplayName())) {
				properties.put("displayName", filterElement.getFilterDisplayName());
			}
			properties.put("filterSelectMultiple", filterElement.isFilterSelectMultiple());
			filterProperties.put(filterElement.getName(), properties);
		}

		model.setReportTitle(reportElement.getReportTitle());

		Class<?> mainType = reportElement.getData().getMainType();
		Map<String, Map<String, Object>> filtersMetadataMap = util.getPropertiesMetadata(reportElement, getLocale(), mainType, filterProperties);
		List<String> filters = util.reorganizeFilters(mainType, filterProperties.keySet());

		checkFiltersMap(model, reportElement, filters, filtersMetadataMap);

		setAttribute("filters", filters);
		setAttribute("filtersMetadataMap", filtersMetadataMap);
		setAttribute("model", model);
		setAttribute("reportElement", reportElement);
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("reportPath", WebUtils.getFirstFullUrl());

		Map<String, Object> filterMap = getFilterMap(model, reportElement, false);
		setAttribute("filterValuesMap", filterMap);

		Map<String, String> filterActions = getFilterActions(model);
		setAttribute("filterActions", filterActions);

		ModelAndView mv = new ClasspathModelAndView("org.nextframework.report.generator.mvc.filter");

		//Obtem o objeto de controle do monitoramento
		Map<Integer, ProgressMonitor> monitorMap = getMonitorMap(request);
		synchronized (monitorMap) {
			ProgressMonitor monitor = monitorMap.get(model.getId());
			if (monitor != null) {
				//Verifica se a tarefa concluiu
				if (monitor.getDone() != null) {
					//Se houver erro, apresenta
					if (monitor.getError() != null) {
						request.addError(monitor.getError());
					}
					//Se houver resultado válido, apresenta. É possível que o resultado seja um outro ModelAndView
					if (monitor.getReturn() != null) {
						ReportDesignTask task = getTaskMap(request).get(model.getId());
						ModelAndView customResult = task.showResults(request, model, monitor.getReturn());
						if (customResult != null) {
							mv = customResult;
						}
					} else {
						request.addMessage("Nenhum resultado encontrado. Verifique o filtro.", MessageType.WARN);
					}
					monitorMap.remove(model.getId());
					getTaskMap(request).remove(model.getId());
				} else {
					//Caso não existe, simplesmente envia o monitor para a barra de rolagem ser apresentada
					request.setAttribute("progressMonitor", monitor);
				}
			}
		}

		return mv;
	}

	@SuppressWarnings("rawtypes")
	protected void checkFiltersMap(ReportDesignModel model, ReportElement reportElement, List<String> filters, Map<String, Map<String, Object>> filtersMetadataMap) {
		GeneratedReportListener[] grListeners = ServiceFactory.loadServices(GeneratedReportListener.class);
		Class mainType = Util.objects.getRealClass(reportElement.getData().getMainType());
		for (String filter : filters) {
			for (GeneratedReportListener filterListener : grListeners) {
				if (filterListener.getFromClass() == null || filterListener.getFromClass() == mainType) {
					filterListener.checkFilters(model, reportElement, filter, filtersMetadataMap);
				}
			}
		}
	}

	@SuppressWarnings("all")
	protected Map<String, Object> getFilterMap(ReportDesignModel model, ReportElement reportElement, boolean bind) {
		Map<String, Object> filterMap = null;
		if (!bind) {
			filterMap = (Map<String, Object>) getUserAttribute(ReportDesignController.class.getSimpleName() + "_" + model.getId());
		}
		if (filterMap == null) {
			filterMap = util.getFilterMap(reportElement);
			if (bind) {
				String msg = validate(model, filterMap);
				if (msg != null) {
					throw new NextException(msg);
				}
			}
			setUserAttribute(ReportDesignController.class.getSimpleName() + "_" + model.getId(), filterMap);
		}
		return filterMap;
	}

	protected String validate(ReportDesignModel model, Map<String, Object> filterMap) {
		return null;
	}

	protected Map<String, String> getFilterActions(ReportDesignModel model) {
		Map<String, String> actions = new LinkedHashMap<String, String>();
		actions.put("downloadPdf", "Gerar PDF");
		return actions;
	}

	/////////////////////////////////////////////// TASKS ///////////////////////////////////////////////

	@OnErrors("showFilterView")
	@Input("showFilterView")
	public ModelAndView downloadPdf(WebRequestContext request, ReportDesignModel model) throws Exception {

		ReportDesignTask task = new ReportDesignTask() {

			@Override
			public Object convertResults(ReportDefinition definition, IProgressMonitor progressMonitor) throws Exception {
				progressMonitor.setTaskName(Util.objects.newMessage("org.nextframework.report.generator.mvc.ReportDesignController.generatingPDF", null, "Gerando PDF"));
				DynamicBaseReportDefinition definition2 = (DynamicBaseReportDefinition) definition;
				if (Util.collections.isNotEmpty(definition2.getSummarizedData().getItems())) {
					definition.getParameters().put("renderPDF", Boolean.TRUE);
					byte[] pdfBytes = JasperReportsRenderer.renderAsPDF(definition);
					return new Resource("application/pdf", definition.getReportName() + ".pdf", pdfBytes);
				}
				return null;
			}

			@Override
			public ModelAndView showResults(WebRequestContext request, ReportDesignModel model, Object data) throws Exception {
				return new ResourceModelAndView((Resource) data);
			}

		};

		return executeTask(request, model, task);
	}

	@OnErrors("showFilterView")
	@Input("showFilterView")
	public ModelAndView showResults(final WebRequestContext request, final ReportDesignModel model) throws Exception {

		ReportDesignTask task = new ReportDesignTask() {

			@Override
			public Object convertResults(ReportDefinition definition, IProgressMonitor progressMonitor) throws Exception {
				progressMonitor.setTaskName(Util.objects.newMessage("org.nextframework.report.generator.mvc.ReportDesignController.generatingHTML", null, "Gerando HTML"));
				DynamicBaseReportDefinition definition2 = (DynamicBaseReportDefinition) definition;
				if (Util.collections.isNotEmpty(definition2.getSummarizedData().getItems())) {
					return HtmlReportRenderer.renderAsHtml(definition);
				}
				return null;
			}

			@Override
			public ModelAndView showResults(WebRequestContext request, ReportDesignModel model, Object data) throws Exception {
				request.setAttribute("html", data);
				return null;
			}

		};

		return executeTask(request, model, task);
	}

	/////////////////////////////////////////////// EXECUTE TASKS ///////////////////////////////////////////////

	public interface ReportDesignTask {

		Object convertResults(ReportDefinition definition, IProgressMonitor progressMonitor) throws Exception;

		ModelAndView showResults(WebRequestContext request, ReportDesignModel model, Object data) throws Exception;

	}

	protected ModelAndView executeTask(WebRequestContext request, ReportDesignModel model, final ReportDesignTask task) throws Exception {

		//Faz a bindagem dos parâmetros
		final ReportElement reportElement = new ReportReader(model.getReportXml()).getReportElement();
		final Map<String, Object> filterMap = getFilterMap(model, reportElement, true);

		//Valida campos permitidos
		validadeAllowedProperties(reportElement);

		//Obtem o objeto de controle do monitoramento
		Map<Integer, ProgressMonitor> monitorMap = getMonitorMap(request);
		synchronized (monitorMap) {

			//Se já existir, dá bomba
			ProgressMonitor monitor = monitorMap.get(model.getId());
			if (monitor != null) {
				request.addError(Util.objects.newMessage("org.nextframework.report.generator.mvc.ReportDesignController.alreadyRunning", null, "Não é possível executar o relatório, pois uma requisição ainda está em andamento!"));
				return showFilterView(request, model);
			}

			//Se não existir, inicia a thread e obtém o monitor
			ProgressTask pTask = new ProgressTask() {

				@Override
				public Object run(IProgressMonitor progressMonitor) throws Exception {
					progressMonitor.beginTask(Util.objects.newMessage("org.nextframework.report.generator.mvc.ReportDesignController.initializing", null, "Inicializando"), 120);
					ReportDefinition definition = getReportDefinition(reportElement, filterMap, getLocale(), getMaxResults(), progressMonitor);
					progressMonitor.setTaskName(Util.objects.newMessage("org.nextframework.report.generator.mvc.ReportDesignController.formatting", null, "Formatando"));
					Object converted = task.convertResults(definition, progressMonitor);
					progressMonitor.worked(20);
					return converted;
				}

			};

			monitor = ProgressTaskFactory.startTask(pTask, ReportDesignTask.class.getSimpleName() + " " + model.getId(), logger);
			monitorMap.put(model.getId(), monitor);
			getTaskMap(request).put(model.getId(), task);
		}

		return continueOnAction("showFilterView", model);
	}

	private void validadeAllowedProperties(final ReportElement reportElement) {
		BeanDescriptor bd = BeanDescriptorFactory.forClass(reportElement.getData().getMainType());
		for (String property : reportElement.getProperties()) {
			if (!reportElement.getData().isCalculated(property)) {
				PropertyDescriptor propertyDescriptor = bd.getPropertyDescriptor(property);
				ReportField reportField = propertyDescriptor.getAnnotation(ReportField.class);
				Id idField = propertyDescriptor.getAnnotation(Id.class);
				if (reportField == null && idField == null) {
					throw new BusinessException("org.nextframework.report.generator.mvc.ReportDesignController.invalidProperty", new Object[] { property, reportElement.getData().getMainType().getSimpleName() }, "Não é possível executar o relatório, pois a propriedade {0} de {1} não é permitida!");
				}
			}
		}
	}

	@Deprecated
	protected ReportDefinition getReportDefinition(ReportDesignModel model) throws Exception {
		ReportElement reportElement = new ReportReader(model.getReportXml()).getReportElement();
		Map<String, Object> filterMap = getFilterMap(model, reportElement, true);
		ReportDefinition definition = getReportDefinition(reportElement, filterMap, getLocale(), getMaxResults(), null);
		return definition;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, ProgressMonitor> getMonitorMap(WebRequestContext request) {
		String attributeName = ReportDesignController.class.getSimpleName() + "_monitorMap";
		Map<Integer, ProgressMonitor> monitorMap = (Map<Integer, ProgressMonitor>) request.getUserAttribute(attributeName);
		if (monitorMap == null) {
			monitorMap = new HashMap<Integer, ProgressMonitor>();
			request.setUserAttribute(attributeName, monitorMap);
		}
		return monitorMap;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, ReportDesignTask> getTaskMap(WebRequestContext request) {
		String attributeName = ReportDesignController.class.getSimpleName() + "_taskMap";
		Map<Integer, ReportDesignTask> taskMap = (Map<Integer, ReportDesignTask>) request.getUserAttribute(attributeName);
		if (taskMap == null) {
			taskMap = new HashMap<Integer, ReportDesignTask>();
			request.setUserAttribute(attributeName, taskMap);
		}
		return taskMap;
	}

	private ReportDefinition getReportDefinition(ReportElement reportElement, Map<String, Object> filterMap, Locale locale, int maxResults, IProgressMonitor progressMonitor) {

		ReportGenerator rg = new ReportGenerator(reportElement, progressMonitor);
		ReportSpec spec = rg.generateReportSpec(filterMap, locale, maxResults);

		if (debugMode()) {
			debug(reportElement.getName(), rg, spec);
		}

		ReportDefinition definition = spec.getReportBuilder().getDefinition();

		if (debugMode()) {
			debug(reportElement.getName(), definition);
		}

		int total = definition.getData().size();
		if (definition instanceof DynamicBaseReportDefinition) {
			DynamicBaseReportDefinition d2 = (DynamicBaseReportDefinition) definition;
			total = d2.getSummarizedData().getItems().size();
		}

		if (total == maxResults) {
			ReportLabel label = new ReportLabel("Obs: Apenas os " + maxResults + " primeiros registros estão sendo mostrados.");
			label.getStyle().setForegroundColor(Color.RED);
			label.getStyle().setFontSize(6);
			label.getStyle().setItalic(true);
			label.setColspan(definition.getColumns().size());
			ReportSectionRow row = definition.getSectionFirstPageHeader().insertRow(0);
			definition.addItem(label, row, 0);
		}

		return definition;
	}

	protected Locale getLocale() {
		return NextWeb.getRequestContext().getLocale();
	}

	protected abstract int getMaxResults();

	/////////////////////////////////////////////// AJAX ///////////////////////////////////////////////

	@SuppressWarnings("all")
	public List<Object[]> getFilterList() { //Ajax
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try {
			Class type = Class.forName(getParameter("type"));
			GenericDAO dao = DAOUtils.getDAOForClass(type);
			List list = dao.findAll();
			for (Object object : list) {
				String id = Util.strings.toStringIdStyled(object);
				String label = Util.strings.toStringDescription(object);
				Object[] obj = new Object[] {
						id, label
				};
				result.add(obj);
			}
		} catch (Exception e) {
			Object[] msg = (Object[]) new Object[] { "error", "error " + e.getMessage() };
			result.add(msg);
		}
		return result;
	}

	/////////////////////////////////////////////// CUSTOM ///////////////////////////////////////////////

	public abstract String getPathForReportCrud();

	public abstract void persistReport(ReportDesignModel model, ReportElement reportElement, CUSTOM_BEAN customBean);

	protected abstract CUSTOM_BEAN loadPersistedReportById(Integer id);

	/////////////////////////////////////////////// DEBUG ///////////////////////////////////////////////

	protected boolean debugMode() {
		return false;
	}

	private void debug(String title, ReportGenerator rg, ReportSpec spec) {
		title = Util.strings.onlyAlphanumerics(title);
		debugSource(title, rg.getSourceCode(), spec.getSummary().getSourceCode());
	}

	protected void debugSource(String title, String sourceCodeReport, String sourceCodeSummary) {
	}

	private void debug(String title, ReportDefinition definition) {
		title = Util.strings.onlyAlphanumerics(title);
		ReportItemIterator reportItemIterator = new ReportItemIterator(definition);
		int i = 1;
		while (reportItemIterator.hasNext()) {
			ReportItem next = reportItemIterator.next();
			if (next instanceof Subreport) {
				debug(title + (i++), ((Subreport) next).getReport());
			}
		}
		debugSource(title, JasperReportsRenderer.renderAsJRXML(definition));
		debugDataCSV(title, JasperUtils.generateDataCSV(definition));
	}

	protected void debugSource(String title, byte[] jrxml) {
	}

	protected void debugDataCSV(String title, String csv) {
	}

}
