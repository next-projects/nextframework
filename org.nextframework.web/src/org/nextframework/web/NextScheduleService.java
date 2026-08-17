package org.nextframework.web;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.core.standard.ApplicationContext;
import org.nextframework.core.standard.Next;

public class NextScheduleService {

	private static final Log log = LogFactory.getLog(NextScheduleService.class);

	private final ApplicationContext applicationContext;
	private final ScheduledExecutorService executorService;

	public NextScheduleService() {
		this.applicationContext = Next.getApplicationContext();
		this.executorService = Executors.newSingleThreadScheduledExecutor(createThreadFactory());
	}

	private ThreadFactory createThreadFactory() {
		final AtomicInteger sequence = new AtomicInteger(1);
		return new ThreadFactory() {

			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable, NextScheduleService.class.getSimpleName() + "-" + sequence.getAndIncrement());
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}

		};
	}

	public void registerPeriodicRunnable(final String name, final Runnable runnable, int minutes) {
		validateRunnable(name, runnable, minutes);
		executorService.scheduleWithFixedDelay(wrapRunnable(name, runnable), 0, minutes, TimeUnit.MINUTES);
	}

	public void registerTimeoutRunnable(final String name, final Runnable runnable, int minutes) {
		validateRunnable(name, runnable, minutes);
		executorService.schedule(wrapRunnable(name, runnable), minutes, TimeUnit.MINUTES);
	}

	private void validateRunnable(String name, Runnable runnable, int minutes) {
		if (runnable == null) {
			throw new IllegalArgumentException("runnable não pode ser nulo");
		}
		if (minutes <= 0) {
			throw new IllegalArgumentException("minutes deve ser maior que zero para a tarefa [" + name + "]");
		}
	}

	private Runnable wrapRunnable(final String name, final Runnable runnable) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					Next.setApplicationContext(applicationContext);
					runnable.run();
				} catch (Throwable ex) {
					log.error("Erro ao invocar tarefa agendada do framework [" + name + "]", ex);
				}
			}

		};
	}

	public void shutdown() {
		executorService.shutdownNow();
	}

}
