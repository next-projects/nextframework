package org.nextframework.persistence;

/**
 * Listener utilizado em conjunto com o SaveOrUpdateStrategy para verificar exclus�o de itens.
 * @author rogelgarcia
 *
 */
public interface CollectionItemDeleteListener<E> {

	/**
	 * Invocado para cada objeto que ser� excluido.<BR>
	 * 
	 * � responsabilidade da implementa��o chamar o m�todo execute do parametro chain para efetuar a exclus�o do objeto.
	 * @param chain
	 */
	public void onDelete(E object, SaveOrUpdateStrategyChain chain);
}
