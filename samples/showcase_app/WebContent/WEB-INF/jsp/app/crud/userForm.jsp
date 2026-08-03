<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:formView title="User" showNewLink="false">
	<t:formPanel>
		<t:formTable>
			<t:property name="id"/>
			<t:property name="username"/>
			<t:property name="password"/>
			<t:property name="name"/>
			<t:property name="createdAt" mode="output" write="true"/>
		</t:formTable>
	</t:formPanel>
</t:formView>