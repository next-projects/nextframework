package org.nextframework.view.progress;

/**
 * Representa uma tarefa que pode atualizar um progressMonitor
 * @author rogel
 * @since 3.5.3
 */
public interface ProgressTask {

	void run(IProgressMonitor progressMonitor) throws Exception;

}
