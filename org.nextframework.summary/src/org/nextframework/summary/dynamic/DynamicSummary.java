package org.nextframework.summary.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.exception.NextException;
import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.compilation.SummaryResult;

public abstract class DynamicSummary<E> {

	private static int sequence = 0;

	private Class<E> referenceClass;

	public void setReferenceClass(Class<E> referenceClass) {
		this.referenceClass = referenceClass;
	}

	public Class<?> getReferenceClass() {
		return referenceClass;
	}

	List<DynamicGroup> groups = new ArrayList<DynamicGroup>();
	List<DynamicVariable> variables = new ArrayList<DynamicVariable>();

	public DynamicSummary<E> addCount() {
		return addVariable(new DynamicVariable("count", CalculationType.SUM, "1", Integer.class));
	}

	public DynamicSummary<E> addSum(String onProperty) {
		return addVariable(onProperty, CalculationType.SUM);
	}

	public DynamicSummary<E> addVariable(String onProperty, CalculationType calculation) {
		DynamicVariable dynamicVariable = new DynamicVariable(onProperty, calculation);
		return addVariable(dynamicVariable);
	}

	public DynamicSummary<E> addVariable(String onProperty, String displayName, CalculationType calculation) {
		DynamicVariable dynamicVariable = new DynamicVariable(onProperty, displayName, calculation);
		return addVariable(dynamicVariable);
	}

	public DynamicSummary<E> addVariable(DynamicVariable dynamicVariable) {
		checkFreeze();
		variables.add(dynamicVariable);
		return this;
	}

	public DynamicSummary<E> addGroup(String name) {
		return addGroup(name, null);
	}

	public DynamicSummary<E> addGroup(String name, String pattern) {
		checkFreeze();
		groups.add(new DynamicGroup(name, pattern));
		return this;
	}

	public DynamicGroup[] getGroups() {
		return groups.toArray(new DynamicGroup[groups.size()]);
	}

	public DynamicVariable[] getVariables() {
		return variables.toArray(new DynamicVariable[variables.size()]);
	}

	public boolean isFrozen() {
		return freeze;
	}

	private void checkFreeze() {
		if (freeze) {
			throw new NextException("after the DynamicSummary has been frozen it is not possible to alter its state");
		}
	}

	private boolean freeze = false;

	private int serialId;

	protected int getSerialId() {
		return serialId;
	}

	protected void freeze() {
		freeze = true;
	}

	protected DynamicSummary(int serialId) {
		this.serialId = serialId;
	}

	public static <E> DynamicSummary<E> getInstance(Class<E> reference) {
		if (reference == null) {
			throw new NullPointerException("reference cannot be null");
		}
		DynamicSummaryImpl<E> instance = createInstance();
		instance.setReferenceClass(reference);
		return instance;
	}

	private static <E> DynamicSummaryImpl<E> createInstance() {
		synchronized (DynamicSummary.class) {
			return new DynamicSummaryImpl<E>(sequence++);
		}
	}

	public SummaryResult<E, Summary<E>> getSummaryResult(List<E> items) {
		return SummaryResult.createFrom(items, getSummaryClass());
	}

	public Class<Summary<E>> getSummaryClass() {
		if (getReferenceClass() == null) {
			throw new NullPointerException("reference class of DynamicSummary has not been set");
		}
		freeze = true;
		return getClassFor(this);
	}

	protected abstract Class<Summary<E>> getClassFor(DynamicSummary<E> dynamicSummary);

	public abstract String getSourceCode();

	public abstract String getSourceCode(String className);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((referenceClass == null) ? 0 : referenceClass.toString().hashCode());
		result = prime * result + ((variables == null) ? 0 : variables.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DynamicSummary other = (DynamicSummary) obj;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (referenceClass == null) {
			if (other.referenceClass != null)
				return false;
		} else if (!referenceClass.toString().equals(other.referenceClass.toString()))
			return false;
		if (variables == null) {
			if (other.variables != null)
				return false;
		} else if (!variables.equals(other.variables))
			return false;
		return true;
	}

}
