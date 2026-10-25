package org.nextframework.test.context;

import static org.junit.Assert.assertEquals;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.nextframework.util.NextFormater;
import org.nextframework.util.Util;

public class DateFormattingConcurrencyTest {

	@Test
	public void preservesLegacyDateFormatting() {

		Date date = Util.dates.dmyhm("29/02/2024 13:47");

		assertEquals("29/02/2024", Util.dates.toDmy(date));
		assertEquals("29/02/2024 13:47", Util.dates.toDmyhm(date));
		assertEquals("29/02/2024 13:47:00", Util.dates.toDmyhms(date));
		assertEquals("1999-12-31 23:59:58",
				Util.dates.toString(Util.dates.ymd_hms("99/12/31,23:59:58"), "yyyy-MM-dd HH:mm:ss"));
		assertEquals("2024-02-29 13:47:26",
				Util.dates.toString(Util.dates.yyyyMMdd_HHmmss("2024-02-29 13:47:26"), "yyyy-MM-dd HH:mm:ss"));
		assertEquals("29/02/2024", NextFormater.getInstance().format(date));
		assertEquals("29/02/2024 13:47", NextFormater.getInstance().format(new Timestamp(date.getTime())));
		assertEquals("13:47", NextFormater.getInstance().format(Time.valueOf("13:47:26")));

	}

	@Test
	public void singletonUtilitiesAreSafeUnderConcurrentUse() throws Exception {

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2024, Calendar.FEBRUARY, 29, 13, 47, 26);
		Date date = calendar.getTime();
		Timestamp timestamp = new Timestamp(date.getTime());
		ExecutorService executor = Executors.newFixedThreadPool(12);
		try {
			List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
			for (int task = 0; task < 24; task++) {
				tasks.add(() -> {
					for (int iteration = 0; iteration < 500; iteration++) {
						assertEquals("29/02/2024 13:47:26", Util.dates.toDmyhms(date));
						assertEquals("29/02/2024 13:47:26",
								Util.dates.toDmyhms(Util.dates.toDate("29/02/2024 13:47:26", "dd/MM/yyyy HH:mm:ss")));
						assertEquals("29/02/2024 13:47", NextFormater.getInstance().format(timestamp));
					}
					return null;
				});
			}
			for (Future<Void> result : executor.invokeAll(tasks)) {
				result.get();
			}
		} finally {
			executor.shutdownNow();
		}

	}
}
