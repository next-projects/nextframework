package org.nextframework.persistence;

import java.util.List;

/**
 * Interface usada pelo DAO para configurar um resultado de uma query para a listagem de dados
 * @author rogel
 *
 * @param <E>
 */
public interface ResultList<E> {
	
	public List<E> list();

}
