package org.nextframework.persistence;


public class DAODelegateSaveOrUpdateStrategyListener<E> implements DAODelegateListener<E> {

	@SuppressWarnings("unchecked")
	public void onSaveOrUpdate(E object, SaveOrUpdateStrategyChain chain) {
		((DAO<E>)DAOUtils.getDAOForClass(object.getClass())).saveOrUpdate(object);
	}

	@SuppressWarnings("unchecked")
	public void onDelete(E object, SaveOrUpdateStrategyChain chain) {
		((DAO<E>)DAOUtils.getDAOForClass(object.getClass())).delete(object);
	}
}
