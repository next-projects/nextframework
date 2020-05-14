package org.nextframework.view.util;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.util.Util;
import org.nextframework.view.OutputTag;

/**
 * @author marcus
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGroupPropertyAnaliser {

	protected Object _last;
	protected String[] groupProperties;
	protected List[] groupItens = null;
	protected Object[] lastProperties = null;
	protected Object[] currentProperties = null;
	protected String groupid;
	protected int changedIndex;
	protected int uid;

	public AbstractGroupPropertyAnaliser(String[] groupProperties) {
		init(groupProperties);
	}

	protected void init(String[] groupProperties) {
		this.groupProperties = groupProperties;
		if (groupProperties != null) {
			this.lastProperties = new Object[groupProperties.length];
			this.currentProperties = new Object[groupProperties.length];
			//inicia o grupo
			this.groupItens = new List[groupProperties.length];
			for (int i = 0; i < this.groupItens.length; i++) {
				this.groupItens[i] = new ArrayList<Object>();
			}
			uid = 0;
		}
	}

	protected void mapProperties(String[] groupProperties, Object[] array, Object objeto) {
		if (objeto != null) {
			BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(objeto);
			for (int i = 0; i < groupProperties.length; i++) {
				String property = groupProperties[i];
				Object value = beanDescriptor.getPropertyDescriptor(property).getValue();
				String v = getPropertyValue(value);
				array[i] = Util.strings.isEmpty(v) ? "-" : v;
			}
		}
	}

	protected String getPropertyValue(Object value) {

		OutputTag outputTag = new OutputTag();
		outputTag.setValue(value);
		String objectDescriptionToString = outputTag.getStringBody();

		char[] toCharArray = objectDescriptionToString.toCharArray();
		//aspas nao devem ser enviadas
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < toCharArray.length; j++) {
			char c = toCharArray[j];
			if (c != '\'' && c != '\"' && c > 20) {
				builder.append(c);
			}
		}
		String b = builder.toString();
		return b;
	}

	public void defineActualProperties(Object _current) {
		if (this.groupProperties != null) {
			mapProperties(this.groupProperties, this.lastProperties, _last);
			mapProperties(this.groupProperties, this.currentProperties, _current);
			_last = _current;
		}
	}

	public void defineActualPropertiesAndTotalize(Object _current) {
		defineActualProperties(_current);
		if (detectChangedIndex() >= 0) {
			renderTotalizersAndGroups();
		}
		adicionaItens(_current);
	}

	public void adicionaItens(Object _current) {
		if (this.groupProperties != null) {
			for (int i = 0; i < this.groupItens.length; i++) {
				this.groupItens[i].add(_current);
			}
		}
	}

	public int detectChangedIndex() {
		if (this.currentProperties == null) {
			return -2;
		}
		for (int i = 0; i < this.currentProperties.length; i++) {
			Object current = this.currentProperties[i];
			Object last = this.lastProperties[i];
			if (current != null && !current.equals(last)) {
				this.groupid = "";
				for (int j = 0; j < i; j++) {
					Object property = currentProperties[j];
					this.groupid += property == null ? "-" : property.toString() + "~";
				}
				this.changedIndex = i;
				return i;
			}
		}
		return -1;
	}

	public String getGroupId() {
		return this.groupid;
	}

	private void cleanGroupItens(int i) {
		this.groupItens[i] = new ArrayList<Object>();
	}

	public void renderTotalizersAndGroups() {
		changeStatus();
		for (int i = this.currentProperties.length - 1; i >= this.changedIndex; i--) {
			resolveTotalizer(i);
		}
		for (int i = this.changedIndex; i < this.currentProperties.length; i++) {
			resolveGroup(i);
		}
	}

	protected void resolveGroup(int i) {
		Object property = this.currentProperties[i];
		this.groupid += property == null ? "-" : property.toString() + "~";
		renderGroup(uid++, i, property);
	}

	protected void resolveTotalizer(int i) {
		Object oldProperty = this.lastProperties[i];
		if (oldProperty != null) {
			renderTotalizer(i, oldProperty);
			cleanGroupItens(i);
		}
	}

	public void closeGroups() {
		if (this.groupProperties != null) {
			defineActualProperties(null);
			changeStatus();
			for (int i = this.currentProperties.length - 1; i >= 0; i--) {
				resolveTotalizer(i);
			}
			this.lastProperties = new Object[groupProperties.length];
			this.currentProperties = new Object[groupProperties.length];
		}
	}

	protected abstract void renderTotalizer(int level, Object oldProperty);

	protected abstract void renderGroup(int uid, int level, Object property);

	protected abstract void changeStatus();
}