package org.nextframework.persistence;

import org.nextframework.core.standard.Next;
import org.nextframework.util.Util;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class DAOUtils {

	public static boolean isTransient(Object value) {
		return PersistenceUtils.getId(value, Next.getObject(HibernateTemplate.class).getSessionFactory()) == null;
	}

	@SuppressWarnings("unchecked")
	public static <BEAN> GenericDAO<BEAN> getDAOForClass(Class<BEAN> classType) {
		Class<? extends Object> class1 = classType;
		if(class1.getName().contains("$$")){ // generated class, get original
			class1 = class1.getSuperclass();
		}
		return (GenericDAO<BEAN>) Next.getObject(Util.strings.uncaptalize(class1.getSimpleName())+"DAO");		
	}
}
