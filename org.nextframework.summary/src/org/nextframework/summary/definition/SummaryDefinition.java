package org.nextframework.summary.definition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nextframework.bean.BeanDescriptorUtils;
import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.annotations.Group;
import org.nextframework.summary.annotations.Variable;

public class SummaryDefinition<E extends Summary<?>> {

	//criar o BeansUtil apenas para diminuir a dependencia das classes do next

	private Class<E> summaryClass;

	private SortedSet<SummaryGroupDefinition> groups = new TreeSet<SummaryGroupDefinition>(new Comparator<SummaryGroupDefinition>() {

		@Override
		public int compare(SummaryGroupDefinition o1, SummaryGroupDefinition o2) {
			return o1.getGroup().value() - o2.getGroup().value();
		}

	});

	private List<SummaryVariableDefinition> variables = new ArrayList<SummaryVariableDefinition>();

	public SummaryDefinition(Class<E> summaryClass) {
		this.summaryClass = summaryClass;
		init();
	}

	public Class<E> getSummaryClass() {
		return summaryClass;
	}

	public List<SummaryItemDefinition> getItens() {
		ArrayList<SummaryItemDefinition> itens = new ArrayList<SummaryItemDefinition>();
		itens.addAll(groups);
		itens.addAll(variables);
		return itens;
	}

	public SortedSet<SummaryGroupDefinition> getGroups() {
		return groups;
	}

	public List<SummaryVariableDefinition> getVariables() {
		return variables;
	}

	protected void init() {

		Method[] methods = summaryClass.getMethods();
		//TODO FAZER COM QUE OS MÉTODOS DEFINIDOS EM SUBCLASSES TENHAM PRIORIDADE MENOR
		//TODO VERIFICAR SE OS GRUPOS DEFINIDOS EM ESCOPOS REALMENTE EXISTEM
		Set<String> groups = new HashSet<String>();
		for (Method method : methods) {
			Group group = method.getAnnotation(Group.class);
			Variable variable = method.getAnnotation(Variable.class);
			if (group != null || variable != null) {
				if (!BeanDescriptorUtils.isGetter(method)) {
					throw new IllegalArgumentException("Cannot build summary for " + summaryClass.getName() + ". Only getter methods can accept @Group or @Variable annotations! Invalid method: " + method);
				}
				if (group != null && variable != null) {
					throw new IllegalArgumentException("Cannot build summary for " + summaryClass.getName() + ". Only one of @Group or @Variable annotations can be used! Invalid method: " + method);
				}
				if (group != null) {
					SummaryGroupDefinition def = new SummaryGroupDefinition(group, BeanDescriptorUtils.getPropertyFromGetter(method.getName()), method.getReturnType(), method);
					this.groups.add(def);
					if (!groups.add(def.group.name())) {
						throw new RuntimeException("Cannot build summary for " + summaryClass.getName() + ". Group " + group.value() + " has more than one declaration. " + method);
					}
				}
				if (variable != null) {
					this.variables.add(new SummaryVariableDefinition(variable, BeanDescriptorUtils.getPropertyFromGetter(method.getName()), method.getReturnType(), method));
				}
			}
		}

		for (SummaryVariableDefinition svd : getVariables()) {
			String scopeGroup = svd.variable.scopeGroup();
			if (org.springframework.util.StringUtils.hasText(svd.variable.scopeGroup()) && !groups.contains(scopeGroup)) {
				throw new RuntimeException("Cannot build summary for " + summaryClass.getName() + ". Group '" + scopeGroup + "' was not defined. Variable '" + svd.name + "'.");
			}
			if (org.springframework.util.StringUtils.hasText(svd.variable.incrementOnGroupChange()) && !groups.contains(svd.variable.incrementOnGroupChange())) {
				throw new RuntimeException("Cannot build summary for " + summaryClass.getName() + ". Group '" + scopeGroup + "' was not defined. Variable '" + svd.name + "'.");
			}
		}

		Collections.sort(this.variables, new Comparator<SummaryVariableDefinition>() {

			@Override
			public int compare(SummaryVariableDefinition o1, SummaryVariableDefinition o2) {
				return o1.getMethod().getName().compareTo(o2.getMethod().getName());
			}

		});

		Collections.sort(this.variables, new Comparator<SummaryVariableDefinition>() {

			@Override
			public int compare(SummaryVariableDefinition o1, SummaryVariableDefinition o2) {
				if (o1.getVariable().calculation() == CalculationType.NONE) {
					if (o2.getVariable().calculation() == CalculationType.NONE) {
						return o1.getName().compareTo(o2.getName());
					}
					return 1;
				} else if (o2.getVariable().calculation() != CalculationType.NONE) {
					return o1.getName().compareTo(o2.getName());
				}

				return -1;
			}

		});

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SummaryDefinition [summaryClass=").append(summaryClass.getName()).append(",\n itens=").append(getItens()).append("]");
		return builder.toString();
	}

}
