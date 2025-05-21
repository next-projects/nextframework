package org.nextframework.persistence;

import java.util.Map;
import java.util.WeakHashMap;

import org.nextframework.core.standard.Next;
import org.nextframework.util.Util;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class DAOUtils {

	private static HibernateTemplate hibernateTemplate;

	private static HibernateTemplate getHibernateTemplate() {
		if (hibernateTemplate == null) {
			hibernateTemplate = Next.getObject(HibernateTemplate.class);
		}
		return hibernateTemplate;
	}

	public static boolean isTransient(Object value) {
		return PersistenceUtils.getId(value, getHibernateTemplate().getSessionFactory()) == null;
	}

	private static Map<Class<?>, GenericDAO<?>> daosMap = new WeakHashMap<Class<?>, GenericDAO<?>>();

	@SuppressWarnings("unchecked")
	public static <BEAN> GenericDAO<BEAN> getDAOForClass(Class<BEAN> classType) {
		Class<? extends Object> class1 = Util.objects.getRealClass(classType);
		GenericDAO<BEAN> dao = (GenericDAO<BEAN>) daosMap.get(class1);
		if (dao == null) {
			dao = (GenericDAO<BEAN>) Next.getObject(Util.strings.uncaptalize(class1.getSimpleName()) + "DAO");
			daosMap.put(class1, dao);
		}
		return dao;
	}

}
