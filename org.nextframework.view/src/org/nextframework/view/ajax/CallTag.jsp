<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<script language="javascript">
	function ${tag.functionName}{
		<c:if test="${!empty tag.callback}">
			var callback = ${tag.callback};
		</c:if>
		<c:if test="${empty tag.callback}">
			var callback = function (data){
				try{
					eval(data);
				}catch(e){
					alert('Erro ao executar callback!\\n' + e.name + ': ' + e.message);
					document.write('<b>Código enviado pelo servidor</b><br><hr>' + data.replace(/\\n/g, '<BR>'));
				}
			};
		</c:if>
		sendRequest('${tag.url}', '${tag.actionParameter}=${tag.action}&' + ${tag.parameters}, 'POST', callback, ajaxcallerrorcallback, arguments);
	}
</script>