function submitNextForm(form, options){

	form = next.dom.toElement(form);
	options = options || {};

	try{
		clearMessages();
	}catch(e){}

	var validateFunction = resolveFormSubmitFunction(options.validateFunction);
	if(form.validate == 'true' && validateFunction){
		var valid = validateFunction();
		if(!valid){
			return false;
		}
	}

	if(options.action && options.actionParameter && form[options.actionParameter]){
		var actionField = form[options.actionParameter];
		actionField.value = options.action;
	}

	var beforeSubmit = options.beforeSubmit || function(){};
	var afterSubmit = options.afterSubmit || function(){};
	var onError = options.onError || function(message){
		if(message){
			alert(message);
		}
	};

	checkFilesAjaxUpload(form, function(){
		checkFormMultipart(form);
		if(beforeSubmit(form) === false){
			return;
		}
		form.submit();
		afterSubmit(form);
	}, onError);

	return false;
}

function resolveFormSubmitFunction(functionName){
	if(!functionName){
		return null;
	}
	if(typeof functionName == 'function'){
		return functionName;
	}
	if(typeof functionName == 'string'){
		return typeof window[functionName] == 'function' ? window[functionName] : null;
	}
	return null;
}

function checkFilesAjaxUpload(form, onComplete, onError){

	form = next.dom.toElement(form);
	onComplete = onComplete || function(){};
	onError = onError || function(message){if(message){alert(message);}};

	var ajaxUploadInput = form.querySelector('input[type="file"][data-ajax-upload="true"]');
	if(!ajaxUploadInput){
		onComplete();
		return;
	}

	if(form._nextAjaxUploading){
		onError('Já existe um upload em andamento para este formulário.');
		return;
	}

	var tempFileTokenSuffix = '_tempFileToken';
	var fileClassNameSuffix = '_fileClassName';
	var inputs = form.querySelectorAll('input[type="hidden"][name$="' + tempFileTokenSuffix + '"]');
	var pendingUploads = [];

	for(var i = 0; i < inputs.length; i++){

		var tokenField = inputs[i];
		var fieldName = tokenField.name.substring(0, tokenField.name.length - tempFileTokenSuffix.length);
		var fileInput = findFormInputByName(form, fieldName, 'file');

		if(!fileInput || fileInput.getAttribute('data-ajax-upload') != 'true'){
			continue;
		}

		var fileClassName = null;
		var fileClassNameField = findFormInputByName(form, fieldName + fileClassNameSuffix, 'hidden');
		if(fileClassNameField){
			fileClassName = fileClassNameField.value;
		}

		if(fileInput.files && fileInput.files.length > 0){
			pendingUploads.push({fieldName: fieldName, fileInput: fileInput, tokenField: tokenField, fileClassName: fileClassName});
		}

	}

	if(pendingUploads.length == 0){
		onComplete();
		return;
	}

	createAjaxUploadProgressPopup(form, pendingUploads);
	form._nextAjaxUploading = true;
	var index = 0;

	var nextUpload = function(){

		if(index >= pendingUploads.length){
			form._nextAjaxUploading = false;
			closeAjaxUploadProgressPopup(form);
			onComplete();
			return;
		}

		var item = pendingUploads[index++];

		uploadAjaxFile(form, item, index, pendingUploads.length, nextUpload, function(message){
			form._nextAjaxUploading = false;
			closeAjaxUploadProgressPopup(form);
			onError(message);
		});

	};

	nextUpload();

}

function uploadAjaxFile(form, item, currentIndex, totalUploads, onComplete, onError){

	var uploadCompletionDelayMs = 500;

	var formData = new FormData();
	formData.append('file', item.fileInput.files[0]);
	if(!item.fileClassName){
		onError('Classe concreta do arquivo não informada para o campo ' + item.fieldName + '.');
		return;
	}
	formData.append('fileClassName', item.fileClassName);

	updateAjaxUploadProgress(form, item, currentIndex, totalUploads, 0, 'Enviando arquivo...', false);

	next.ajax.send({
		url: next.http.getApplicationContext() + '/uploadfile',
		appendContext: false,
		params: formData,
		onProgress: function(event){
			if(event.lengthComputable){
				var percent = Math.round((event.loaded / event.total) * 100);
				updateAjaxUploadProgress(form, item, currentIndex, totalUploads, percent, 'Enviando arquivo...', false);
			}
		},
		onComplete: function(data){
			item.tokenField.value = data;
			item.fileInput.disabled = true;
			var fileObjectField = findFormInputByName(form, item.fieldName + '_fileObject', 'hidden');
			if(fileObjectField){
				fileObjectField.value = '';
			}
			updateAjaxUploadProgress(form, item, currentIndex, totalUploads, 100, 'Upload concluído.', false);
			window.setTimeout(onComplete, uploadCompletionDelayMs);
		},
		onError: function(data, status, cp, request){
			updateAjaxUploadProgress(form, item, currentIndex, totalUploads, 0, 'Falha no upload.', true);
			onError(getAjaxUploadErrorMessage(data, status, request));
		},
		on404: function(data, status, cp, request){
			updateAjaxUploadProgress(form, item, currentIndex, totalUploads, 0, 'Falha no upload.', true);
			onError(getAjaxUploadErrorMessage(data, status, request));
		}
	});

}

function updateAjaxUploadProgress(form, item, currentIndex, totalUploads, percent, message, hasError){

	var progressState = form._nextAjaxUploadProgress || null;
	if(!progressState){
		progressState = createAjaxUploadProgressPopup(form);
	}

	progressState.progressBar.className = next.globalMap.get(hasError ? 'Progress.barError' : 'Progress.barInside', hasError ? 'progressBarError' : 'progressbarInside');
	progressState.progressBar.style.width = percent + '%';
	if(progressState.progressBar.innerHTML != null){
		progressState.progressBar.innerHTML = percent + '%';
	}
	progressState.infoDiv.textContent = message || '';
	renderAjaxUploadProgressDetails(progressState.tasksDiv, item, currentIndex, totalUploads);

}

function createAjaxUploadProgressPopup(form, pendingUploads){

	closeAjaxUploadProgressPopup(form);

	var popup = next.dom.getNewPopupDiv();
	popup.setSize('SM');

	var titleDiv = next.dom.newElement('div', {className: next.globalMap.get('NextDialogs.header', 'popup_box_header')});
	titleDiv.textContent = 'Enviando arquivos';
	popup.appendChild(titleDiv);

	var bodyDiv = next.dom.newElement('div', {className: next.globalMap.get('NextDialogs.body', 'popup_box_body')});
	popup.appendChild(bodyDiv);

	var progressContainer = next.dom.newElement('div', {className: next.globalMap.get('Progress.container', 'progressbarContainer')});
	bodyDiv.appendChild(progressContainer);

	var progressDiv = next.dom.newElement('div', {className: next.globalMap.get('Progress.bar', 'progress')});
	progressContainer.appendChild(progressDiv);

	var progressBar = next.dom.newElement('div', {className: next.globalMap.get('Progress.barInside', 'progressbarInside')});
	progressBar.style.width = '0%';
	progressDiv.appendChild(progressBar);

	var infoDiv = next.dom.newElement('div', {className: next.globalMap.get('Progress.status', 'progressStatus')});
	bodyDiv.appendChild(infoDiv);

	var tasksDiv = next.dom.newElement('div', {className: next.globalMap.get('Progress.tasks', 'progressTasks')});
	bodyDiv.appendChild(tasksDiv);

	form._nextAjaxUploadProgress = {
		popup: popup,
		progressBar: progressBar,
		infoDiv: infoDiv,
		tasksDiv: tasksDiv,
		totalUploads: pendingUploads ? pendingUploads.length : 0
	};

	return form._nextAjaxUploadProgress;

}

function closeAjaxUploadProgressPopup(form){

	form = next.dom.toElement(form);
	if(!form || !form._nextAjaxUploadProgress){
		return;
	}

	try{
		form._nextAjaxUploadProgress.popup.close();
	}catch(e){}

	form._nextAjaxUploadProgress = null;

}

function renderAjaxUploadProgressDetails(container, item, currentIndex, totalUploads){

	container.innerHTML = '';

	var positionDiv = next.dom.newElement('div');
	positionDiv.textContent = 'Arquivo ' + currentIndex + ' de ' + totalUploads;
	container.appendChild(positionDiv);

	if(item && item.fileInput && item.fileInput.files && item.fileInput.files.length > 0){
		var fileNameDiv = next.dom.newElement('div');
		fileNameDiv.textContent = item.fileInput.files[0].name;
		container.appendChild(fileNameDiv);
	}

}

function getAjaxUploadErrorMessage(data, status, request){
	var message = data;
	if(request){
		var exmessage = request.getResponseHeader('EX-MESSAGE');
		if(!exmessage){
			exmessage = request.getResponseHeader('EX-ERROR-MESSAGE');
		}
		if(exmessage){
			message = exmessage;
		}
	}
	if(!message || message.length == 0){
		message = 'Erro ' + status + ': falha no upload temporário do arquivo.';
	}
	return message;
}

function checkFormMultipart(form){

	var inputs = form.querySelectorAll('input[type="file"]');
	if(inputs.length == 0){
		return;
	}

	var originalEnctype = form.getAttribute('data-next-original-enctype');
	if(!originalEnctype){
		originalEnctype = form.getAttribute('enctype') || 'application/x-www-form-urlencoded';
		form.setAttribute('data-next-original-enctype', originalEnctype);
	}

	var hasFileInput = false;

	for(var i = 0; i < inputs.length; i++){
		var input = inputs[i];
		if(input.getAttribute('data-ajax-upload') == 'true'){
			continue;
		}
		if(!input.disabled){
			hasFileInput = true;
			break;
		}
	}

	if (hasFileInput && form.method.toUpperCase() === 'POST') {
		console.info('Ajustando enctype para multipart/form-data...');
		form.enctype = 'multipart/form-data';
	} else {
		form.enctype = originalEnctype;
	}

}

function clearAjaxUploadField(fileInput){

	if(!fileInput){
		return null;
	}

	var currentInput = fileInput;

	try{
		currentInput.disabled = false;
		currentInput.value = '';
	}catch(e){}

	if(currentInput.value){
		var replacement = currentInput.cloneNode(true);
		currentInput.parentNode.replaceChild(replacement, currentInput);
		currentInput = replacement;
	}

	currentInput.disabled = false;
	resetAjaxUploadProgress(currentInput);

	return currentInput;
}

function resetAjaxUploadProgress(fileInput){

	if(!fileInput){
		return;
	}

	closeAjaxUploadProgressPopup(fileInput.form);

	if(fileInput.getAttribute('data-temp-file-token-field')){
		var tokenField = findFormInputByName(fileInput.form, fileInput.getAttribute('data-temp-file-token-field'));
		if(tokenField && (!fileInput.files || fileInput.files.length == 0)){
			tokenField.value = '';
		}
	}

}

function findFormInputByName(form, name, type){
	var elements = form.elements;
	for(var i = 0; i < elements.length; i++){
		var element = elements[i];
		if(element.name == name && (!type || !element.type || element.type.toLowerCase() == type.toLowerCase())){
			return element;
		}
	}
	return null;
}
