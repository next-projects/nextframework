package org.nextframework.view.progress;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.nextframework.exception.NextException;

public class ProgressMonitor implements IProgressMonitor {
	
	private List<String> tasks = new Vector<String>();
	private int totalWork = 1;
	private int workDone = 0;
	private boolean canceled = false;
	private String subtask = "";
	private boolean done = false;
	
	String error = null;

	public void beginTask(String name, int totalWork) {
		tasks.add(name);
		this.totalWork = totalWork;
	}

	public void done() {
		done = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean value) {
		canceled = value;
	}

	public void setTaskName(String name) {
		tasks.add(name);
	}

	public void subTask(String name) {
		this.subtask = name;
	}

	public void worked(int work) {
		if(tasks.isEmpty()){
			throw new NextException("O IProgressMonitor não foi inicializado. Chame o método beginTask");
		}
		workDone += work;
	}
	
	public void setWorkDone(int workDone) {
		this.workDone = workDone;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	/* Métodos da implementação */
	public List<String> getTasks() {
		return tasks;
	}
	
	public int getPercentDone(){
		return (int)(workDone  *100.0/ totalWork);
	}
	
	public String getSubtask() {
		return subtask;
	}
	
	public boolean isDone() {
		return done;
	}
	
	private double collectionIncremento = 0;
	private double collectionIncrementado = 0;
	
	public void workWithCollection(Collection<?> itens, int totalOfWork){
		collectionIncremento = 1.0 / (itens.size() / (double) totalOfWork );
		collectionIncrementado = 0;
	}
	
	public void incrementCollectionWork(){
		collectionIncrementado += collectionIncremento;
		if (collectionIncrementado > 1) {
			int incrementoInt = (int) Math.floor(collectionIncrementado);
			worked(incrementoInt);
			collectionIncrementado = collectionIncrementado - incrementoInt;
		}
	}

}
