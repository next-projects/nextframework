/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.controller;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.NextException;
import org.nextframework.types.File;
import org.nextframework.web.NextScheduleService;
import org.nextframework.web.NextScheduledTaskRegistrar;

public class TempFileTokenSupport implements NextScheduledTaskRegistrar {

	private static final String TEMP_UPLOADS_FOLDER = "tempFiles";
	private static final String TEMP_UPLOAD_FILE_SUFFIX = ".next";

	private static final Pattern TOKEN_PATTERN = Pattern.compile("^[0-9a-fA-F\\-]{36}_\\d{13}$");
	private static final int TEMP_FILES_CLEANUP_INTERVAL_MINUTES = 60;
	private static final long TEMP_FILES_TTL_MILLIS = 360L * 60L * 1000L;

	public static File newFile(String fileClassName) {

		if (fileClassName == null || fileClassName.trim().isEmpty()) {
			throw new NextException("Classe concreta do arquivo não informada.");
		}

		Class<?> requiredType;
		try {
			requiredType = Class.forName(fileClassName);
		} catch (ClassNotFoundException e) {
			throw new NextException("Classe de arquivo [" + fileClassName + "] não encontrada.", e);
		}

		if (!File.class.isAssignableFrom(requiredType)) {
			throw new NextException("A classe [" + requiredType.getName() + "] não implementa org.nextframework.types.File.");
		}

		if (requiredType.isInterface() || Modifier.isAbstract(requiredType.getModifiers())) {
			throw new NextException("A classe de arquivo [" + requiredType.getName() + "] deve ser concreta para suportar upload temporário.");
		}

		try {
			return (File) requiredType.getConstructor().newInstance();
		} catch (Exception e) {
			throw new NextException("Não foi possível instanciar a classe de arquivo [" + requiredType.getName() + "]", e);
		}

	}

	public static String createToken() {
		return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
	}

	public static void persist(File value, String token) throws IOException {
		validateToken(token);
		java.io.File tempFile = getTempUploadFile(token);
		TempFileUtils.writeObject(tempFile, value);
	}

	public static File load(String token) {
		validateToken(token);
		java.io.File tempFile = getTempUploadFile(token);
		return TempFileUtils.readObject(tempFile, File.class);
	}

	public static boolean delete(String token) {
		validateToken(token);
		java.io.File file = getTempUploadFile(token);
		return !file.exists() || file.delete();
	}

	private static java.io.File getTempUploadsFolder() {
		return TempFileUtils.getApplicationTempSubFolder(TEMP_UPLOADS_FOLDER);
	}

	private static java.io.File getTempUploadFile(String token) {
		validateToken(token);
		return TempFileUtils.getApplicationTempFile(TEMP_UPLOADS_FOLDER, token + TEMP_UPLOAD_FILE_SUFFIX);
	}

	private static void validateToken(String token) {
		if (!isValidToken(token)) {
			throw new NextException("Token de arquivo temporário inválido.");
		}
	}

	public static boolean isValidToken(String token) {
		return token != null && TOKEN_PATTERN.matcher(token).matches();
	}

	public static void deleteTemporaryUploadFiles(WebRequestContext request) {
		Set<String> tempFileTokens = getRequestTempFilesTokens(request);
		for (String tempFileToken : tempFileTokens) {
			delete(tempFileToken);
		}
	}

	private static Set<String> getRequestTempFilesTokens(WebRequestContext request) {
		Set<String> tempFileTokens = new HashSet<>();
		Map<String, String[]> parameterMap = request.getServletRequest().getParameterMap();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			if (!entry.getKey().endsWith(ServletRequestDataBinderNext.TEMP_FILE_TOKEN_SUFFIX)) {
				continue;
			}
			if (entry.getValue() != null) {
				for (String value : entry.getValue()) {
					if (TempFileTokenSupport.isValidToken(value)) {
						tempFileTokens.add(value);
					}
				}
			}
		}
		return tempFileTokens;
	}

	@Override
	public void registerScheduledTasks(NextScheduleService nextScheduleService) {
		nextScheduleService.registerPeriodicRunnable("TempFileTokenSupport.deleteExpired", new Runnable() {

			@Override
			public void run() {
				deleteExpired(TEMP_FILES_TTL_MILLIS);
			}

		}, TEMP_FILES_CLEANUP_INTERVAL_MINUTES);
	}

	public int deleteExpired(long ttlMillis) {

		if (ttlMillis < 0) {
			throw new IllegalArgumentException("ttlMillis deve ser maior ou igual a zero.");
		}

		java.io.File folder = getTempUploadsFolder();
		if (!folder.exists()) {
			return 0;
		}

		long limite = System.currentTimeMillis() - ttlMillis;
		int removidos = 0;
		java.io.File[] files = folder.listFiles();
		if (files == null) {
			return 0;
		}

		for (java.io.File file : files) {
			String token = getTokenFromFile(file);
			if (token == null) {
				continue;
			}
			long timestamp = getTimestamp(token);
			if (timestamp <= limite && file.delete()) {
				removidos++;
			}
		}

		return removidos;
	}

	private String getTokenFromFile(java.io.File file) {
		if (file == null) {
			return null;
		}
		String name = file.getName();
		if (!name.endsWith(TEMP_UPLOAD_FILE_SUFFIX)) {
			return null;
		}
		String token = name.substring(0, name.length() - TEMP_UPLOAD_FILE_SUFFIX.length());
		return isValidToken(token) ? token : null;
	}

	private long getTimestamp(String token) {
		validateToken(token);
		return Long.parseLong(token.substring(token.lastIndexOf('_') + 1));
	}

}
