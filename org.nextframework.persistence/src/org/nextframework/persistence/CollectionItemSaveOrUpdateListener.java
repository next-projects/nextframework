package org.nextframework.persistence;

public interface CollectionItemSaveOrUpdateListener<E> {

	/**
	 * Invocado para cada objeto que ser� salvo (insert ou update).<BR>
	 * 
	 * � responsabilidade da implementa��o chamar o m�todo execute do parametro chain para efetuar a persistencia do objeto.
	 * @param chain
	 */
	public void onSaveOrUpdate(E object, SaveOrUpdateStrategyChain chain);
}
