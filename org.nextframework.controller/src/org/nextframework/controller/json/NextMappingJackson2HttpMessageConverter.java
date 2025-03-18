package org.nextframework.controller.json;

import org.nextframework.service.ServiceFactory;

/** XML config:
	<mvc:annotation-driven content-negotiation-manager="contentNegotiationManager">
		<mvc:message-converters>
			<bean class="org.nextframework.controller.json.MappingJackson2HttpMessageConverter" />
		</mvc:message-converters>
	</mvc:annotation-driven>
 */
public class NextMappingJackson2HttpMessageConverter extends org.springframework.http.converter.json.MappingJackson2HttpMessageConverter {

	public NextMappingJackson2HttpMessageConverter() {
		super();
		JacksonJsonTranslator translator = (JacksonJsonTranslator) ServiceFactory.getService(JsonTranslator.class);
		setObjectMapper(translator.createObjectMapper());
	}

}