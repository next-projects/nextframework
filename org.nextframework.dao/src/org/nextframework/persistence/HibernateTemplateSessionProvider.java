package org.nextframework.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class HibernateTemplateSessionProvider implements HibernateSessionProvider, InitializingBean {

	private HibernateTemplate hibernateTemplate;

	private boolean configureQueryBuilder = false;

	public boolean isConfigureQueryBuilder() {
		return configureQueryBuilder;
	}

	public void setConfigureQueryBuilder(boolean configureQueryBuilder) {
		this.configureQueryBuilder = configureQueryBuilder;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (isConfigureQueryBuilder()) {
			PersistenceConfiguration.getConfig().setSessionProvider(this);
		}
	}

	@Override
	public Object execute(final HibernateCommand command) {
		return getHibernateTemplate().execute(new HibernateCallback<Object>() {

			public Object doInHibernate(Session session) throws HibernateException {
				return command.doInHibernate(session);
			}

		});
	}

	@Override
	public SessionFactory getSessionFactory() {
		return getHibernateTemplate().getSessionFactory();
	}

}
