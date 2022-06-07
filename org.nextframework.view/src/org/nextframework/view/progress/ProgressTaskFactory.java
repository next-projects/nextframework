package org.nextframework.view.progress;

import org.apache.commons.logging.Log;
import org.nextframework.core.standard.Next;

/**
 * Classe para criar trabalhos com ProgressMonitors
 * 
 * @author rogel
 * @since 3.5.3
 */
public class ProgressTaskFactory {

	/**
	 * Executa a tarefa definida por ProgressTask em uma outra thread e retorna o monitor de progresso relacionado (será criado um monitor padrão)
	 * @param task
	 * @return
	 */
	public static ProgressMonitor startTask(ProgressTask task, String name, Log logger) {
		ProgressMonitor monitor = new ProgressMonitor();
		startTask(monitor, task, name, logger);
		return monitor;
	}

	/**
	 * Executa a tarefa definida por ProgressTask em uma outra thread.
	 * O monitor usado deve ser passado como parâmetro. 
	 * @param task
	 * @return
	 */
	public static void startTask(final IProgressMonitor monitor, final ProgressTask task, String name, final Log logger) {
		Thread t = new Thread(Next.getApplicationName().toUpperCase() + " - " + ProgressTask.class.getSimpleName() + " - " + name) {
			public void run() {
				monitor.subTask("Inicializando tarefa...");
				try {
					Object r = task.run(monitor);
					monitor.setReturn(r);
					monitor.done(monitor.getError() == null);
				} catch (Exception e) {
					monitor.setError(e);
					monitor.done(false);
					if (logger != null) {
						logger.error("Erro ao executar tarefa!", e);
					}
				}
			};
		};
		t.start();
	}

}
