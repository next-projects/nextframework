package org.nextframework.persistence;

/**
 * Listener utilizado em conjunto com o SaveOrUpdateStrategy para verificar exclusão de itens.
 * @author rogelgarcia
 *
 */
public interface CollectionItemDeleteListener<E> {

	/**
	 * Invocado para cada objeto que será excluido.<BR>
	 * 
	 * É responsabilidade da implementação chamar o método execute do parametro chain para efetuar a exclusão do objeto.
	 * @param chain
	 */
	public void onDelete(E object, SaveOrUpdateStrategyChain chain);
}
