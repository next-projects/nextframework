package org.nextframework.test.controller;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.controller.Action;
import org.nextframework.controller.Command;
import org.nextframework.controller.CommandEventListener;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.Input;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.OnErrors;
import org.nextframework.controller.json.JacksonJsonTranslator;
import org.nextframework.controller.json.JsonModelAndView;
import org.nextframework.controller.json.JsonTranslator;
import org.nextframework.controller.mvt.ModelAndViewTranslator;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.service.ServiceFactory;
import org.nextframework.service.ServiceProvider;
import org.nextframework.types.Cpf;
import org.nextframework.types.Money;
import org.nextframework.validation.ValidatorRegistry;
import org.nextframework.validation.ValidatorRegistryImpl;
import org.nextframework.validation.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * End-to-end tests for {@link MultiActionController}.
 *
 * Exercises the full controller pipeline: action resolution -> command instantiation
 * -> data binding -> validation -> action execution -> response generation.
 * Uses Spring MockHttpServletRequest/Response to simulate real HTTP requests
 * without a servlet container.
 */
public class MultiActionControllerEndToEndTest {

	// ============================================================
	// Inner Classes: Command Objects
	// ============================================================

	public static class Address {

		private String street;
		private String city;
		private List<String> phones = new java.util.ArrayList<String>();

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public List<String> getPhones() {
			return phones;
		}

		public void setPhones(List<String> phones) {
			this.phones = phones;
		}

	}

	public static class SampleCommand {

		@Required
		private String name;
		private Integer age;
		private Date birthDate;
		private Money salary;
		private Cpf cpf;
		private Address address;
		private List<String> tags = new java.util.ArrayList<String>();
		private List<Address> addresses = new java.util.ArrayList<Address>();
		private Map<String, String> properties = new java.util.LinkedHashMap<String, String>();
		private Map<String, Address> addressMap = new java.util.LinkedHashMap<String, Address>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public Date getBirthDate() {
			return birthDate;
		}

		public void setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
		}

		public Money getSalary() {
			return salary;
		}

		public void setSalary(Money salary) {
			this.salary = salary;
		}

		public Cpf getCpf() {
			return cpf;
		}

		public void setCpf(Cpf cpf) {
			this.cpf = cpf;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}

		public List<Address> getAddresses() {
			return addresses;
		}

		public void setAddresses(List<Address> addresses) {
			this.addresses = addresses;
		}

		public Map<String, String> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, String> properties) {
			this.properties = properties;
		}

		public Map<String, Address> getAddressMap() {
			return addressMap;
		}

		public void setAddressMap(Map<String, Address> addressMap) {
			this.addressMap = addressMap;
		}

	}

	public static class UploadCommand {

		private MultipartFile file;
		private String description;

		public MultipartFile getFile() {
			return file;
		}

		public void setFile(MultipartFile file) {
			this.file = file;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	public static class SessionCommand {

		private String filterName;
		private Integer pageNumber;

		public String getFilterName() {
			return filterName;
		}

		public void setFilterName(String filterName) {
			this.filterName = filterName;
		}

		public Integer getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
		}

	}

	// ============================================================
	// Inner Class: Sample Controller
	// ============================================================

	public static class SampleTestController extends MultiActionController {

		/** Expose protected method for testing */
		public ModelAndView handleRequest(HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {
			return handleRequestInternal(request, response);
		}

		@Override
		protected ModelAndView noActionHandler(HttpServletRequest request, javax.servlet.http.HttpServletResponse response, org.nextframework.controller.NoActionHandlerException e) throws org.nextframework.controller.NoActionHandlerException {
			try {
				response.sendError(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND, e.getMessage());
			} catch (java.io.IOException ioe) {
				throw new RuntimeException(ioe);
			}
			return null;
		}

		@DefaultAction
		@Command(validate = false)
		public ModelAndView doDefault(WebRequestContext request, SampleCommand command) {
			request.getServletRequest().setAttribute("command", command);
			return new ModelAndView("defaultView");
		}

		@Action("form")
		@Command(validate = false)
		public ModelAndView doForm(WebRequestContext request, SampleCommand command) {
			request.getServletRequest().setAttribute("command", command);
			return new ModelAndView("formView");
		}

		@Action("save")
		@Command(validate = true)
		@Input("form")
		public ModelAndView doSave(WebRequestContext request, SampleCommand command) {
			request.getServletRequest().setAttribute("command", command);
			return new ModelAndView("successView");
		}

		@Action("json")
		@Command(validate = false)
		public ModelAndView doJson(WebRequestContext request, SampleCommand command) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("status", "ok");
			data.put("name", command.getName());
			return new JsonModelAndView(data);
		}

		@Action("upload")
		@Command(validate = false)
		public ModelAndView doUpload(WebRequestContext request, UploadCommand command) {
			request.getServletRequest().setAttribute("uploadCommand", command);
			return new ModelAndView("uploadSuccess");
		}

		@Action("sessionTest")
		@Command(session = true)
		public ModelAndView doSessionTest(WebRequestContext request, SessionCommand command) {
			request.getServletRequest().setAttribute("sessionCommand", command);
			return new ModelAndView("sessionView");
		}

		@Action("error")
		@OnErrors("form")
		@Command(validate = false)
		public ModelAndView doError(WebRequestContext request, SampleCommand command) {
			throw new RuntimeException("Test error");
		}

		@Action("chain")
		@Command(validate = false)
		public ModelAndView doChain(WebRequestContext request, SampleCommand command) {
			return goToAction("form");
		}

		@Action("setAttribute")
		@Command(validate = false)
		public ModelAndView doSetAttribute(WebRequestContext request, SampleCommand command) {
			request.getServletRequest().setAttribute("customAttr", "customValue");
			request.getServletRequest().setAttribute("commandName", command.getName());
			return new ModelAndView("attrView");
		}

		@Action("redirectAction")
		@Command(validate = false)
		public ModelAndView doRedirect(WebRequestContext request, SampleCommand command) {
			return sendRedirectToAction("form");
		}

		@Action("bindCollections")
		@Command(validate = false)
		public ModelAndView doBindCollections(WebRequestContext request, SampleCommand command) {
			request.getServletRequest().setAttribute("command", command);
			return new ModelAndView("collectionsView");
		}

	}

	// ============================================================
	// Inner Class: Test Service Provider
	// ============================================================

	static class TestServiceProvider implements ServiceProvider {

		private final Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();
		private final Map<Class<?>, Object[]> arrayServices = new HashMap<Class<?>, Object[]>();

		public <E> void registerService(Class<E> type, E service) {
			services.put(type, service);
		}

		@SuppressWarnings("unchecked")
		public <E> void registerArrayService(Class<E> type, E... items) {
			arrayServices.put(type, items);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <E> E getService(Class<E> serviceInterface) {
			return (E) services.get(serviceInterface);
		}

		@Override
		public int priority() {
			return -1;
		}

		@Override
		public void release() {
		}

		@Override
		@SuppressWarnings("unchecked")
		public <E> E[] loadServices(Class<E> serviceInterface) {
			Object[] arr = arrayServices.get(serviceInterface);
			if (arr != null) {
				E[] result = (E[]) Array.newInstance(serviceInterface, arr.length);
				System.arraycopy(arr, 0, result, 0, arr.length);
				return result;
			}
			return (E[]) Array.newInstance(serviceInterface, 0);
		}

	}

	// ============================================================
	// Test Infrastructure
	// ============================================================

	private MockServletContext servletContext;
	private MockHttpSession session;
	private GenericApplicationContext applicationContext;
	private TestServiceProvider testProvider;
	private SampleTestController controller;

	@Before
	public void setUp() {
		servletContext = new MockServletContext();
		session = new MockHttpSession(servletContext);

		applicationContext = new GenericApplicationContext();
		applicationContext.refresh();

		testProvider = new TestServiceProvider();
		testProvider.registerService(org.springframework.context.ApplicationContext.class, applicationContext);
		testProvider.registerService(MessageSource.class, new StaticMessageSource());
		testProvider.registerService(ValidatorRegistry.class, new ValidatorRegistryImpl());
		testProvider.registerService(JsonTranslator.class, new JacksonJsonTranslator());
		testProvider.registerArrayService(CommandEventListener.class);
		testProvider.registerArrayService(ModelAndViewTranslator.class);

		ServiceFactory.registerProvider(testProvider);

		controller = new SampleTestController();
	}

	@After
	public void tearDown() {
		if (applicationContext != null) {
			applicationContext.close();
		}
		ServiceFactory.refresh();
	}

	// ============================================================
	// Helper Methods
	// ============================================================

	private MockHttpServletRequest createRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.setSession(session);
		request.setMethod("GET");
		request.setServletPath("/test");
		return request;
	}

	private MockHttpServletResponse createResponse() {
		return new MockHttpServletResponse();
	}

	private ModelAndView handle(HttpServletRequest request, MockHttpServletResponse response) throws Exception {
		return controller.handleRequest(request, response);
	}

	// ============================================================
	// 1. Action Resolution Tests
	// ============================================================

	@Test
	public void testDefaultAction_noActionParam_invokesDoDefault() throws Exception {
		MockHttpServletRequest request = createRequest();
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull("ModelAndView should not be null", mv);
		assertEquals("defaultView", mv.getViewName());
	}

	@Test
	public void testNamedAction_form_invokesDoForm() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertEquals("formView", mv.getViewName());
	}

	@Test
	public void testUnknownAction_returns404() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "nonExistentAction");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNull("ModelAndView should be null for 404", mv);
		assertEquals(404, response.getStatus());
	}

	// ============================================================
	// 2. Data Binding Tests
	// ============================================================

	@Test
	public void testSimpleTypeBinding_stringAndInteger() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "John");
		request.setParameter("age", "30");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertEquals("John", cmd.getName());
		assertEquals(Integer.valueOf(30), cmd.getAge());
	}

	@Test
	public void testDateBinding_withPattern() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "Test");
		request.setParameter("birthDate", "25/12/1990");
		request.setParameter("_datePattern_birthDate", "dd/MM/yyyy");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Date should have been parsed", cmd.getBirthDate());

		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(cmd.getBirthDate());
		assertEquals(1990, cal.get(java.util.Calendar.YEAR));
		assertEquals(java.util.Calendar.DECEMBER, cal.get(java.util.Calendar.MONTH));
		assertEquals(25, cal.get(java.util.Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testMoneyBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "Test");
		request.setParameter("salary", "1.234,56");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Money should have been parsed", cmd.getSalary());
		assertEquals(1234.56, cmd.getSalary().doubleValue(), 0.001);
	}

	@Test
	public void testCpfBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "Test");
		request.setParameter("cpf", "073.572.796-18");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Cpf should have been parsed", cmd.getCpf());
	}

	@Test
	public void testNestedObjectBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "Test");
		request.setParameter("address.street", "Rua X");
		request.setParameter("address.city", "SP");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Address should have been auto-created", cmd.getAddress());
		assertEquals("Rua X", cmd.getAddress().getStreet());
		assertEquals("SP", cmd.getAddress().getCity());
	}

	@Test
	public void testListOfStringsBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "bindCollections");
		request.setParameter("name", "Test");
		request.setParameter("tags[0]", "alpha");
		request.setParameter("tags[1]", "beta");
		request.setParameter("tags[2]", "gamma");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Tags list should not be null", cmd.getTags());
		assertEquals(3, cmd.getTags().size());
		assertEquals("alpha", cmd.getTags().get(0));
		assertEquals("beta", cmd.getTags().get(1));
		assertEquals("gamma", cmd.getTags().get(2));
	}

	@Test
	public void testListOfObjectsBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "bindCollections");
		request.setParameter("name", "Test");
		request.setParameter("addresses[0].street", "Rua A");
		request.setParameter("addresses[0].city", "SP");
		request.setParameter("addresses[1].street", "Rua B");
		request.setParameter("addresses[1].city", "RJ");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Addresses list should not be null", cmd.getAddresses());
		assertEquals(2, cmd.getAddresses().size());
		assertEquals("Rua A", cmd.getAddresses().get(0).getStreet());
		assertEquals("SP", cmd.getAddresses().get(0).getCity());
		assertEquals("Rua B", cmd.getAddresses().get(1).getStreet());
		assertEquals("RJ", cmd.getAddresses().get(1).getCity());
	}

	@Test
	public void testMultiNestedListBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "bindCollections");
		request.setParameter("name", "Test");
		request.setParameter("addresses[0].phones[0]", "111");
		request.setParameter("addresses[0].phones[1]", "222");
		request.setParameter("addresses[1].phones[0]", "333");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull(cmd.getAddresses());
		assertEquals(2, cmd.getAddresses().size());
		assertNotNull(cmd.getAddresses().get(0).getPhones());
		assertEquals(2, cmd.getAddresses().get(0).getPhones().size());
		assertEquals("111", cmd.getAddresses().get(0).getPhones().get(0));
		assertEquals("222", cmd.getAddresses().get(0).getPhones().get(1));
		assertNotNull(cmd.getAddresses().get(1).getPhones());
		assertEquals(1, cmd.getAddresses().get(1).getPhones().size());
		assertEquals("333", cmd.getAddresses().get(1).getPhones().get(0));
	}

	@Test
	public void testMapOfStringsBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "bindCollections");
		request.setParameter("name", "Test");
		request.setParameter("properties[color]", "red");
		request.setParameter("properties[size]", "large");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Properties map should not be null", cmd.getProperties());
		assertEquals(2, cmd.getProperties().size());
		assertEquals("red", cmd.getProperties().get("color"));
		assertEquals("large", cmd.getProperties().get("size"));
	}

	@Test
	public void testMapOfObjectsBinding() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "bindCollections");
		request.setParameter("name", "Test");
		request.setParameter("addressMap[home].street", "Rua X");
		request.setParameter("addressMap[home].city", "SP");
		request.setParameter("addressMap[work].street", "Av Y");
		request.setParameter("addressMap[work].city", "RJ");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertNotNull("Address map should not be null", cmd.getAddressMap());
		assertEquals(2, cmd.getAddressMap().size());
		assertNotNull(cmd.getAddressMap().get("home"));
		assertEquals("Rua X", cmd.getAddressMap().get("home").getStreet());
		assertEquals("SP", cmd.getAddressMap().get("home").getCity());
		assertNotNull(cmd.getAddressMap().get("work"));
		assertEquals("Av Y", cmd.getAddressMap().get("work").getStreet());
		assertEquals("RJ", cmd.getAddressMap().get("work").getCity());
	}

	// ============================================================
	// 3. Validation Tests
	// ============================================================

	@Test
	public void testValidation_requiredFieldMissing_redirectsToInput() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "save");
		// name is @Required but not set — use _name marker so validator checks it
		request.setParameter("_name", "");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		// When validation fails with @Input("form"), it redirects to doForm
		assertEquals("formView", mv.getViewName());
	}

	@Test
	public void testValidation_requiredFieldProvided_success() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "save");
		request.setParameter("name", "ValidName");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertEquals("successView", mv.getViewName());
	}

	@Test
	public void testValidation_suppressValidation_skipsValidation() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "save");
		request.setParameter("suppressValidation", "true");
		request.setParameter("_name", ""); // marker present but validation suppressed
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertEquals("successView", mv.getViewName());
	}

	// ============================================================
	// 4. Response Type Tests
	// ============================================================

	@Test
	public void testResponseType_modelAndViewWithViewName() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "Test");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertEquals("formView", mv.getViewName());
	}

	@Test
	public void testResponseType_jsonModelAndView() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "json");
		request.setParameter("name", "TestUser");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertTrue("View should be MappingJackson2JsonView",
				mv.getView() instanceof MappingJackson2JsonView);
		assertNotNull("Model should contain jsonObject", mv.getModel().get("jsonObject"));
	}

	@Test
	public void testResponseType_redirect() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "redirectAction");
		request.setParameter("name", "Test");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertTrue("View name should start with redirect:",
				mv.getViewName().startsWith("redirect:"));
		assertTrue("View name should contain ACTION=form",
				mv.getViewName().contains("ACTION=form"));
	}

	@Test
	public void testResponseType_actionChaining() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "chain");
		request.setParameter("name", "Test");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		// goToAction("form") chains to doForm, which returns "formView"
		assertEquals("formView", mv.getViewName());
	}

	// ============================================================
	// 5. Response Contents Tests
	// ============================================================

	@Test
	public void testResponseContents_customRequestAttributes() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "setAttribute");
		request.setParameter("name", "CustomTest");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		assertEquals("customValue", request.getAttribute("customAttr"));
		assertEquals("CustomTest", request.getAttribute("commandName"));
	}

	@Test
	public void testResponseContents_commandInAttribute() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "BoundCommand");
		request.setParameter("age", "25");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SampleCommand cmd = (SampleCommand) request.getAttribute("command");
		assertNotNull(cmd);
		assertEquals("BoundCommand", cmd.getName());
		assertEquals(Integer.valueOf(25), cmd.getAge());
	}

	// ============================================================
	// 6. Session Variables Tests
	// ============================================================

	@Test
	public void testSession_pass1_commandStoredInSession() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "sessionTest");
		request.setParameter("filterName", "first");
		request.setParameter("pageNumber", "1");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		SessionCommand cmd = (SessionCommand) request.getAttribute("sessionCommand");
		assertNotNull(cmd);
		assertEquals("first", cmd.getFilterName());
		assertEquals(Integer.valueOf(1), cmd.getPageNumber());

		// Verify the command is in the session
		String sessionKey = controller.getDefaultSessionCommandName(SessionCommand.class);
		assertNotNull("Command should be in session", session.getAttribute(sessionKey));
	}

	@Test
	public void testSession_pass2_sameObjectRetained() throws Exception {
		// Pass 1: Set initial values
		MockHttpServletRequest request1 = createRequest();
		request1.setParameter("ACTION", "sessionTest");
		request1.setParameter("filterName", "first");
		request1.setParameter("pageNumber", "1");
		MockHttpServletResponse response1 = createResponse();
		handle(request1, response1);
		SessionCommand cmd1 = (SessionCommand) request1.getAttribute("sessionCommand");

		// Pass 2: New request, same session, only update pageNumber
		MockHttpServletRequest request2 = createRequest();
		request2.setParameter("ACTION", "sessionTest");
		request2.setParameter("pageNumber", "2");
		MockHttpServletResponse response2 = createResponse();
		handle(request2, response2);
		SessionCommand cmd2 = (SessionCommand) request2.getAttribute("sessionCommand");

		assertNotNull(cmd2);
		assertSame("Should be same object from session", cmd1, cmd2);
		assertEquals(Integer.valueOf(2), cmd2.getPageNumber());
	}

	@Test
	public void testSession_clearFilter_createsNewCommand() throws Exception {
		// Pass 1: Set initial values
		MockHttpServletRequest request1 = createRequest();
		request1.setParameter("ACTION", "sessionTest");
		request1.setParameter("filterName", "first");
		request1.setParameter("pageNumber", "1");
		MockHttpServletResponse response1 = createResponse();
		handle(request1, response1);
		SessionCommand cmd1 = (SessionCommand) request1.getAttribute("sessionCommand");

		// Pass 2: clearFilter=true should create a new command
		MockHttpServletRequest request2 = createRequest();
		request2.setParameter("ACTION", "sessionTest");
		request2.setParameter("clearFilter", "true");
		request2.setParameter("filterName", "second");
		MockHttpServletResponse response2 = createResponse();
		handle(request2, response2);
		SessionCommand cmd2 = (SessionCommand) request2.getAttribute("sessionCommand");

		assertNotNull(cmd2);
		assertNotSame("Should be a different object after clearFilter", cmd1, cmd2);
		assertEquals("second", cmd2.getFilterName());
	}

	// ============================================================
	// 7. Request Attributes Tests
	// ============================================================

	@Test
	public void testRequestAttributes_firstAndLastAction() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "form");
		request.setParameter("name", "Test");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		// handleRequestInternal sets firstAction and lastAction
		assertNotNull("firstAction should be set", request.getAttribute("firstAction"));
		assertNotNull("lastAction should be set", request.getAttribute("lastAction"));
	}

	@Test
	public void testRequestAttributes_controllerSetsCustomAttributes() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "setAttribute");
		request.setParameter("name", "AttrTest");
		MockHttpServletResponse response = createResponse();

		handle(request, response);

		assertEquals("customValue", request.getAttribute("customAttr"));
		assertEquals("AttrTest", request.getAttribute("commandName"));
	}

	// ============================================================
	// 8. Error Handling Tests
	// ============================================================

	@Test
	public void testErrorHandling_onErrors_redirectsToFormOnException() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "error");
		request.setParameter("name", "Test");
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		// @OnErrors("form") redirects to doForm when RuntimeException is thrown
		assertEquals("formView", mv.getViewName());
	}

	@Test
	public void testErrorHandling_inputRedirect_onValidationFailure() throws Exception {
		MockHttpServletRequest request = createRequest();
		request.setParameter("ACTION", "save");
		request.setParameter("_name", ""); // marker so validator checks @Required
		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		// @Input("form") redirects to doForm when validation fails
		assertEquals("formView", mv.getViewName());
	}

	// ============================================================
	// 9. File Upload Test
	// ============================================================

	@Test
	public void testFileUpload_multipartFile() throws Exception {
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest(servletContext);
		request.setSession(session);
		request.setMethod("POST");
		request.setServletPath("/test");
		request.setParameter("ACTION", "upload");
		request.setParameter("description", "testUpload");

		byte[] fileContent = "Hello, World!".getBytes();
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", fileContent);
		request.addFile(mockFile);

		MockHttpServletResponse response = createResponse();

		ModelAndView mv = handle(request, response);

		assertNotNull(mv);
		assertEquals("uploadSuccess", mv.getViewName());

		UploadCommand cmd = (UploadCommand) request.getAttribute("uploadCommand");
		assertNotNull(cmd);
		assertEquals("testUpload", cmd.getDescription());
		assertNotNull("File should be bound", cmd.getFile());
		assertEquals("test.txt", cmd.getFile().getOriginalFilename());
		assertArrayEquals(fileContent, cmd.getFile().getBytes());
	}

}
