package org.nextframework.persistence;

public interface CollectionItemSaveOrUpdateListener<E> {

	/**
	 * Invocado para cada objeto que será salvo (insert ou update).<BR>
	 * 
	 * É responsabilidade da implementação chamar o método execute do parametro chain para efetuar a persistencia do objeto.
	 * @param chain
	 */
	public void onSaveOrUpdate(E object, SaveOrUpdateStrategyChain chain);
}
