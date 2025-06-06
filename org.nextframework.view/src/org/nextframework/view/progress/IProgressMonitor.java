package org.nextframework.view.progress;

/**
 * Representa um monitor de progresso do sistema.
 * 
 * @author rogel
 * @since 3.5.3
 */
public interface IProgressMonitor {

	/**
	 * Notifies that the main task is beginning.
	 * @param name
	 * @param totalWork
	 */
	void beginTask(Object name, int totalWork);

	/**
	 * Notifies that the work is done; that is, either the main task is completed or the user canceled it. 
	 */
	void done(boolean success);

	/**
	 * Returns whether cancelation of current operation has been requested.
	 * @return
	 */
	boolean isCanceled();

	/**
	 * Sets the cancel state to the given value.
	 * @param value
	 */
	void setCanceled(boolean value);

	/**
	 * Sets the task name to the given value.
	 * @param name
	 */
	void setTaskName(Object name);

	/**
	 * Notifies that a subtask of the main task is beginning.
	 * @param name
	 */
	void subTask(Object name);

	/**
	 * Notifies that a given number of work unit of the main task has been completed.
	 * @param work
	 */
	void worked(int work);

	void setWorkDone(int workDone);

	public void setError(Throwable error);

	public Throwable getError();

	public void setReturn(Object r);

	public Object getReturn();

}
