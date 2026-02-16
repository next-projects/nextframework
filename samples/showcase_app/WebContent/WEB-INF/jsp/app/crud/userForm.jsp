<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

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
