package org.nextframework.view.progress;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.nextframework.exception.NextException;

public class ProgressMonitor implements IProgressMonitor {

	public static String DONE_SUCCESS = "OK";
	public static String DONE_ERROR = "ERROR";

	private List<String> tasks = new Vector<String>();
	private String subtask = "";
	private int totalWork = 1;
	private int workDone = 0;
	private boolean canceled = false;

	private String done = null;
	Throwable error = null;
	Object retorno = null;

	@Override
	public void beginTask(String name, int totalWork) {
		tasks.add(name);
		this.totalWork = totalWork;
	}

	@Override
	public void done(boolean success) {
		done = success ? DONE_SUCCESS : DONE_ERROR;
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean value) {
		canceled = value;
	}

	@Override
	public void setTaskName(String name) {
		tasks.add(name);
	}

	@Override
	public void subTask(String name) {
		this.subtask = name;
	}

	@Override
	public void worked(int work) {
		if (tasks.isEmpty()) {
			throw new NextException("O IProgressMonitor não foi inicializado. Chame o método beginTask");
		}
		workDone += work;
	}

	@Override
	public void setWorkDone(int workDone) {
		this.workDone = workDone;
	}

	@Override
	public void setError(Throwable error) {
		this.error = error;
	}

	@Override
	public Throwable getError() {
		return error;
	}

	@Override
	public void setReturn(Object retorno) {
		this.retorno = retorno;
	}

	@Override
	public Object getReturn() {
		return retorno;
	}

	/* Métodos da implementação */
	public List<String> getTasks() {
		return tasks;
	}

	public int getPercentDone() {
		return (int) (workDone * 100.0 / totalWork);
	}

	public String getSubtask() {
		return subtask;
	}

	public String getDone() {
		return done;
	}

	private double collectionIncremento = 0;
	private double collectionIncrementado = 0;

	public void workWithCollection(Collection<?> itens, int totalOfWork) {
		collectionIncremento = 1.0 / (itens.size() / (double) totalOfWork);
		collectionIncrementado = 0;
	}

	public void incrementCollectionWork() {
		collectionIncrementado += collectionIncremento;
		if (collectionIncrementado > 1) {
			int incrementoInt = (int) Math.floor(collectionIncrementado);
			worked(incrementoInt);
			collectionIncrementado = collectionIncrementado - incrementoInt;
		}
	}

}
