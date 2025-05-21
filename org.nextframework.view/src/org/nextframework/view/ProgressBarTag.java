package org.nextframework.view;

import org.nextframework.exception.NextException;
import org.nextframework.view.ajax.ProgressBarCallback;
import org.nextframework.view.progress.ProgressMonitor;

public class ProgressBarTag extends BaseTag {

	private ProgressMonitor progressMonitor;
	private String onError;
	private String onComplete;
	private String serverId;

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

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public String getOnError() {
		return onError;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public String getOnComplete() {
		return onComplete;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

	public String getServerId() {
		return serverId;
	}

}
