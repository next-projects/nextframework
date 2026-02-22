package org.nextframework.controller;

import java.lang.reflect.Method;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class NextAnnotationHandlerMapping extends RequestMappingHandlerMapping {

	private String module;

	/**
	 * No Spring moderno, não sobrescrevemos determineUrlsForHandler (que foi removido).
	 * Sobrescrevemos o registro do mapeamento para interceptar como a URL é criada.
	 */
	@Override
	protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {

		// Se houver um módulo definido, verificamos se devemos filtrar ou alterar o prefixo
		if (module != null) {
			RequestMappingInfo newMapping = applyModuleFilter(mapping);
			if (newMapping != null) {
				super.registerHandlerMethod(handler, method, newMapping);
			}
			// Se retornar null, o método não é registrado para este módulo
		} else {
			super.registerHandlerMethod(handler, method, mapping);
		}
	}

	private RequestMappingInfo applyModuleFilter(RequestMappingInfo mapping) {
		// No Spring 6+, o mapping.getPatternsCondition() lida com os caminhos
		// Verificamos se algum dos caminhos começa com o nome do módulo
		boolean matchesModule = mapping.getDirectPaths().stream()
				.anyMatch(path -> path.startsWith("/" + module));

		if (!matchesModule) {
			return null; // Descarta o mapeamento se não pertencer ao módulo
		}

		// Se quiser remover o prefixo do módulo da URL (como o seu código original fazia):
		return mapping.mutate()
				.paths(mapping.getDirectPaths().stream()
						.map(path -> path.startsWith("/" + module) ? path.substring(module.length() + 1) : path)
						.toArray(String[]::new))
				.build();
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModule() {
		return module;
	}

}