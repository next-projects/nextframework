/* Progress bar */
ProgressBar = function(_progressId, _serverId, _ajaxurl, _onComplete, _onError){

	this.progressId = _progressId;
	this.ajaxUrl = _ajaxurl;
	this.serverId = _serverId;
	this.infoDiv = null;
	this.button = null;	
	this.progressDivBar = null; 
	this.progressBar = null;
	this.tasksDiv = null;
	this.onComplete = _onComplete;
	this.onError = _onError;

	this.getMasterDiv = function(){
		return document.getElementById(this.progressId);
	};

	this.alertInformation = function(){
		alert("DIV ID: "+this.progressId);
	};

	this.syncInterval = null;
	this.synchorizationDelay = 1000;

	this.onRecieveData = function(data){
		alert(data);
	};

	this.downloadData = function(){
		var __ajaxUrl = this.ajaxUrl;
		var __progressId = this.progressId;
		var __serverId = this.serverId;
		var randomData = "&rd"+(Math.floor(Math.random()*1000000000))+"="+(Math.floor(Math.random()*1000000000));
		sendRequest(__ajaxUrl, "progressbarid="+__progressId+"&serverId="+__serverId+randomData, "POST", function(data){eval(data)});
	};

	this.setInformation = function(percentDone, subtask, done, tasks){
		if(done){
			percentDone = 100;
		}
		this.progressBar.style.width = percentDone+"%";
		this.infoDiv.innerHTML = subtask;
		var text = "";
		if(tasks.length > 0){
			text = "<ul>";
			for(var i = 0; i < tasks.length; i++){
				var task = tasks[i];
				text += "<li>"+task+"</li>"; 
			}
			text += "</ul>";
		}
		this.tasksDiv.innerHTML = text;
		if(done){
			var element = {};
			element.progressBar = this;
			element.percentDone = percentDone;
			element.subtask = subtask;
			element.done = done;
			element.tasks = tasks;
			this.onComplete(element);
		}
	};

	this.setError = function(percentDone, subtask, done, tasks){
		this.progressBar.style.width = percentDone+"%";
		this.progressBar.className = next.globalMap.get("Progress.barError", "progressBarError");
		this.infoDiv.innerHTML = subtask;
		var text = "";
		if(tasks.length > 0){
			text = "<ul>";
			for(var i = 0; i < tasks.length; i++){
				var task = tasks[i];
				text += "<li>"+task+"</li>"; 
			}
			text += "</ul>";
		}
		this.tasksDiv.innerHTML = text;
		var element = {};
		element.progressBar = this;
		element.percentDone = percentDone;
		element.subtask = subtask;
		element.done = done;
		element.tasks = tasks;
		this.onError(element);
	};

	this.startSynchronization = function(){
		if(this.syncInterval){
			window.clearInterval(this.syncInterval);
		}
		var bigThis = this;
		this.syncInterval = window.setTimeout(
				function(){
					bigThis.downloadData();
				}, 
				this.synchorizationDelay);
	};

	this.stopSynchronization = function(){
		if(this.syncInterval){
			window.clearInterval(this.syncInterval);
			this.syncInterval = null;
		}
	};

	this.isInitialized = function(){
		return this.getMasterDiv().getAttribute("data-initialized");
	};

};

ProgressBar.instances = {};
ProgressBar.getById = function(id){
	return ProgressBar.instances[id];
}

ProgressBar.clear = function(id){
	if(ProgressBar.getById(id)){
		ProgressBar.getById(id).getMasterDiv().innerHTML = '';
		ProgressBar.instances[id] = false;
	}
}

ProgressBar.setup = function(params){

	var progressId = params.id;
	var ajaxUrl = params.ajaxUrl;
	var serverId = params.serverId;
	var onError = params.onError;
	var onComplete = params.onComplete;

	function param_default(pname, def) {
		if (typeof params[pname] == "undefined") {
			params[pname] = def;
		}
	};

	function getWidthForStyling(el) {
		var width = el.offsetWidth;
		var paddingHorizontal = parseInt(getStyleProperty(el, "paddingLeft", "padding-left")) + parseInt(getStyleProperty(el, "paddingRight", "padding-right"));
		return width - paddingHorizontal;
	}

	function stopProcess(){
		var pb = ProgressBar.getById(progressId);
		pb.stopSynchronization();
	}

	function startProcess(){
		var pb = ProgressBar.getById(progressId);
		pb.startSynchronization();
		
		if(pb.button.removeEventListener){
			pb.button.removeEventListener('click', startProcess);
		} else {
			pb.button.detachEvent('onclick', startProcess);
		}
		pb.button.innerHTML = "Cancelar";
		if (pb.button.addEventListener){
			pb.button.addEventListener('click', stopProcess, false);
		} else if (pb.button.attachEvent){
			pb.button.attachEvent('onclick', stopProcess);
		}
	}

	var progressBar = null;

	if(!ProgressBar.getById(progressId)){
		progressBar = new ProgressBar(progressId, serverId, ajaxUrl, onComplete, onError);
		var masterDiv = progressBar.getMasterDiv();

		masterDiv.className = next.globalMap.get("Progress.container", "progressbarContainer");

		masterDiv.setAttribute("data-initialized", true);

		var startButton = document.createElement("button");
		startButton.appendChild(document.createTextNode("start "+progressId));

		if (startButton.addEventListener){
			startButton.addEventListener('click', startProcess, false);
		} else if (startButton.attachEvent){
			startButton.attachEvent('onclick', startProcess);
		}

		var barDiv = document.createElement("div");
		barDiv.className = next.globalMap.get("Progress.bar", "progressbar");

		var progressBarInternalDiv = document.createElement("div");
		progressBarInternalDiv.className = next.globalMap.get("Progress.barInside", "progressbarInside");

		var infoDiv = document.createElement("div");
		infoDiv.className = next.globalMap.get("Progress.status", "progressStatus");

		var tasksDiv = document.createElement("div");
		tasksDiv.className = next.globalMap.get("Progress.tasks", "progressTasks");

		masterDiv.appendChild(barDiv);
		barDiv.appendChild(progressBarInternalDiv);
		masterDiv.appendChild(infoDiv);
		masterDiv.appendChild(tasksDiv);

		progressBar.progressDivBar = barDiv;
		progressBar.progressBar = progressBarInternalDiv;
		progressBar.infoDiv = infoDiv;
		progressBar.tasksDiv = tasksDiv;

	}

	ProgressBar.instances[progressId] = progressBar;

	return progressBar;
};
