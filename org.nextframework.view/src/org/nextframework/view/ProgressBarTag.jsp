<div id="${tag.id}"></div>
<script type="text/javascript">
	var element = ProgressBar.setup({
		id: "${tag.id}",
		ajaxUrl: "${application}/ajax/progressbar",
		serverId: "${tag.serverId}",
		onError: function(element){${tag.onError}},
		onComplete: function(element){${tag.onComplete}}
	});
	element.startSynchronization();
</script>