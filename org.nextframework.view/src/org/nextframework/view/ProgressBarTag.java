package org.nextframework.view;

import org.nextframework.exception.NextException;
import org.nextframework.view.ajax.ProgressBarCallback;
import org.nextframework.view.progress.ProgressMonitor;

public class ProgressBarTag extends BaseTag {

	String onProgressBarCreation = "___defaultProgressBarEvent(element)";
	ProgressMonitor progressMonitor;
	String serverId;

	String onError;
	String onComplete;

	public String getOnError() {
		return onError;
	}

	public String getOnComplete() {
		return onComplete;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

	public String getServerId() {
		return serverId;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public String getOnProgressBarCreation() {
		return onProgressBarCreation;
	}

	@Override
	protected void doComponent() throws Exception {
		if (progressMonitor == null) {
			throw new NextException("O progressMonitor de um progressBar não pode ser nulo");
		}
		if (getId() == null) {
			setId(generateUniqueId());
		}
		this.serverId = ProgressBarCallback.registerProgressMonitor(progressMonitor);
		includeJspTemplate();
	}

}
