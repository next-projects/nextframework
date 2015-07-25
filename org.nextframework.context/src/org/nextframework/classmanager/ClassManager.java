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
package org.nextframework.classmanager;

import java.lang.annotation.Annotation;


/**
 * O ClassManager funciona como um repositório de classes.<BR>
 * É como um filtro para classes, voce determina que tipo de classe deseja e o ClassManger
 * retorna todas as classes com aquelas características.<BR>
 * Ex.: Você registra uma classe A que extende a classe B. Mais tarde você pode pedir todas as 
 * classes que são do tipo B, o classManager irá retornar a classe A.<BR> 
 * É possivel também criar familias de classes. Uma familia é um grupo de classes ou 
 * anotações. Se determinada classe X for uma subclasse de uma das classes da familia Y ou 
 * tiver uma anotaçao que fizer parte da familia Y, 
 * podemos dizer que a classe X faz parte da família Y. <BR>
 * 
 * O classManager não procura as classes no classPath. As classes retornadas nas pesquisas devem ter 
 * sido previamente registradas!
 * 
 * @author rogelgarcia
 */
public interface ClassManager {
	

	/**
	 * Procura todas as classes
	 * @return todas as classes encontradas
	 */
	Class<?>[] getAllClasses();
	
//	/**
//	 * Procura todas as classes em determinado pacote
//	 * @param pacote pacote onde devem ser procuradas as classes (utilizar . (ponto) como separador)
//	 * @return classes encontradas
//	 */
//	Class<?>[] getAllClassesFromPackage(String pacote);

	/**
	 * Procura as classes que são subtipo da classe fornecida
	 * @param <T> Tipo da classe
	 * @param type Classe que deve ser procurada
	 * @return Classes do mesmo tipo da classe fornecida
	 */
	<E> Class<E>[] getAllClassesOfType(Class<E> type);
	
	/**
	 * Retorna todas as classes que possuem determinada anotação
	 * @param annotationType Anotação a ser procurada
	 * @return Classes com a anotação fornecida
	 */
	Class<?>[] getClassesWithAnnotation(Class<? extends Annotation> annotationType);
//
//	/**
//	 * Registra uma classe nesse classManager
//	 * @param clazz
//	 */
//	void registerClass(Class<?> clazz);
	
}
