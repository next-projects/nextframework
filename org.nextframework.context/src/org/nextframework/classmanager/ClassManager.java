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
 * O ClassManager funciona como um reposit�rio de classes.<BR>
 * � como um filtro para classes, voce determina que tipo de classe deseja e o ClassManger
 * retorna todas as classes com aquelas caracter�sticas.<BR>
 * Ex.: Voc� registra uma classe A que extende a classe B. Mais tarde voc� pode pedir todas as 
 * classes que s�o do tipo B, o classManager ir� retornar a classe A.<BR> 
 * � possivel tamb�m criar familias de classes. Uma familia � um grupo de classes ou 
 * anota��es. Se determinada classe X for uma subclasse de uma das classes da familia Y ou 
 * tiver uma anota�ao que fizer parte da familia Y, 
 * podemos dizer que a classe X faz parte da fam�lia Y. <BR>
 * 
 * O classManager n�o procura as classes no classPath. As classes retornadas nas pesquisas devem ter 
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
	 * Procura as classes que s�o subtipo da classe fornecida
	 * @param <T> Tipo da classe
	 * @param type Classe que deve ser procurada
	 * @return Classes do mesmo tipo da classe fornecida
	 */
	<E> Class<E>[] getAllClassesOfType(Class<E> type);
	
	/**
	 * Retorna todas as classes que possuem determinada anota��o
	 * @param annotationType Anota��o a ser procurada
	 * @return Classes com a anota��o fornecida
	 */
	Class<?>[] getClassesWithAnnotation(Class<? extends Annotation> annotationType);
//
//	/**
//	 * Registra uma classe nesse classManager
//	 * @param clazz
//	 */
//	void registerClass(Class<?> clazz);
	
}
