<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:listView title="Users">
	<t:listPanel>
		<t:listTable>
			<t:property name="id"/>
			<t:property name="username"/>
			<t:property name="name"/>
			<t:property name="createdAt"/>
		</t:listTable>
	</t:listPanel>
</t:listView>