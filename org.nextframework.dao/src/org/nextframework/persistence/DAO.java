package org.nextframework.persistence;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;

import org.nextframework.controller.crud.ListViewFilter;
import org.springframework.beans.factory.annotation.Qualifier;

public interface DAO<BEAN> {
	
	public abstract void setPersistenceContext(String persistenceContext);

	/**
	 * Salva determinado bean.
	 * Se quiser alterar o funcionamento do SaveOrUpdateStrategy sobrescreva o método updateSaveOrUpdate.
	 * Se o bean já possuir um id, será feito um UPDATE. Se não será feito um INSERT.
	 * Caso tenha sido feito um INSERT, após esse método retornar o bean passado como parâmetro,
	 * possuirá um id. 
	 * @param bean O objeto a ser salvo.
	 */
	public abstract void saveOrUpdate(BEAN bean);

	public abstract void bulkSaveOrUpdate(Collection<BEAN> list);

	/**
	 * Carrega determinado bean. Esse bean deverá ter algum valor no ID. 
	 * O retorno será um bean com as propriedades carregadas.
	 * @param bean
	 * @return
	 */
	public abstract BEAN load(BEAN bean);

	public abstract BEAN loadById(Serializable id);
	
	@SuppressWarnings("all")
	public Collection loadCollection(Object owner, String attribute);
	
	public BEAN loadWithIdAndDescription(BEAN bean);
	
	public BEAN loadWithIdAndDescriptionById(Serializable id);
	
	public BEAN load(BEAN bean, String[] attributesToLoad);
	
	public void loadAttributes(BEAN bean, String[] attributesToLoad);
	
	public void loadDescriptionProperty(BEAN bean, String... extraFields);

	/**
	 * Carrega o bean para a tela de entrada de dados, no caso de um CRUD.
	 * Se quiser atualizar o QueryBuilder para essa query, sobrescreva o método updateEntradaQuery
	 * @param bean
	 * @return
	 */
	public abstract BEAN loadFormModel(BEAN bean);


	public boolean isEmpty();

	/**
	 * Retorna uma lista com todos os beans encontrados no banco, ordenados pela anotação @OrderBy da classe
	 * @return
	 */
	public abstract List<BEAN> findAll();

	/**
	 * Retorna uma lista com todos os beans encontrados no banco 
	 * ordenados por determinada propriedade
	 * @param orderBy propriedade que deve ser utilizada na ordenação
	 * @return
	 */
	public abstract List<BEAN> findAll(String orderBy);
	
	public List<BEAN> findByProperty(String propertyName, Object o);
	
	public BEAN findByPropertyUnique(String propertyName, Object o);
	
	/**
	 * Executa um find utilizando como filtro o objeto passado. <BR>
	 * Por exemplo, supondo que estamos em um DAO de municipio, e passamos como parâmetro um objeto do tipo estado.
	 * Será procurado no bean municipio qual campo da classe faz referencia ao estado, e esse campo será utilizado como filtro.
	 * O parametro do filtro será o objeto passado para esse método.
	 * @param o Objeto que deve ser utilizado como filtro.
	 * @param extraFields Campos extras que devem ser carregados
	 * @return
	 */
	public abstract List<BEAN> findBy(Object o, String... extraFields);
	
	/**
	 * Retorna todos os objetos encontrados no banco de dados.
	 * Pode ser informado quais campos devem ser carregados.
	 * Esse método é utilizado pelos combo boxes nos JSPs.
	 * @param extraFields
	 * @return
	 */
	public abstract List<BEAN> findForCombo(String... extraFields);



	/**
	 * Executa um find para a tela de listagem de dados, utilizando determinado filtro.
	 * Para atualizar a query sobrescreva o método updateListagemQuery.
	 * @param filtro
	 * @return
	 */
	public abstract ResultList<BEAN> loadListModel(ListViewFilter filtro);

	/**
	 * Exclui determinado bean do banco de dados.
	 * Utilizará caso exista a transação atual.
	 * @param bean
	 */
	public abstract void delete(final BEAN bean);

	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface EntityType {

		Class<?> value();

	}

	/**
	 * Tells that this annotated DAO must not be used by GenericService autowire
	 * @author rogelgarcia
	 *
	 */
	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NoGenericServiceInjection {
	}

}