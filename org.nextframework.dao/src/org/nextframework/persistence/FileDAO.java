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
package org.nextframework.persistence;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.Transient;

import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.nextframework.types.File;
import org.nextframework.util.Util;
import org.springframework.util.ClassUtils;


public class FileDAO<BEAN extends File> extends GenericDAO<BEAN> {
	
	//default changed to true on 2014-01-14
	protected boolean autoDetectTransient = true;

	public FileDAO() {
		super();
	}
	
	public FileDAO(boolean autoDetectTransient) {
		super();
		this.autoDetectTransient = autoDetectTransient;
	}

	public FileDAO(Class<BEAN> beanClass) {
		super(beanClass);
	}
	
	public FileDAO(Class<BEAN> beanClass, boolean autoDetectTransient) {
		super(beanClass);
		this.autoDetectTransient = autoDetectTransient;
	}

	public <E extends File> E loadWithContents(E bean) {
		E arquivo = new QueryBuilder<E>()
							.from(ClassUtils.getUserClass(bean.getClass()))
							.entity(bean)
							.unique();
		readFile(arquivo);
		return arquivo;
	}

	public void fillWithContents(File file) {
		readFile(file);
	}
	
	protected void readFile(File arquivo) {
		if(autoDetectTransient){
			boolean isTransient = checkTransientContent(arquivo);
			if(!isTransient){
				//se nao for transiente será salvo no banco de dados entao devemos sair do método
				return;
			}
		}
		String nomeArquivo = getNomeArquivo(arquivo);
		log.debug("Lendo arquivo do disco (upload) "+nomeArquivo);
		java.io.File file = new java.io.File(nomeArquivo);
		try {
			InputStream inputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] bytes = new byte[(int) file.length()];
			bufferedInputStream.read(bytes);
			arquivo.setContent(bytes);
			inputStream.close();
		} catch (FileNotFoundException e) {
			throw new NextException("Arquivo não encontrado. Código: "+arquivo.getCdfile(), e);
		} catch (IOException e) {
			throw new NextException("Não foi possível ler o arquivo. ", e);
		}
	}

	protected boolean checkTransientContent(File arquivo) {
		boolean isTransient = false;
		try {
			//TODO cache this
			if(arquivo.getClass().getMethod("getContent").isAnnotationPresent(Transient.class)){
				isTransient = true;
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return isTransient;
	}
	
	protected String getNomeArquivo(File file) {
		if(file == null) {
			throw new NullPointerException("Arquivo inválido (nulo)");
		}
		if(file.getCdfile() == null) {
			throw new NullPointerException("Id do arquivo inválido (nulo)");
		}
		if(autoDetectTransient){
			boolean isTransient = checkTransientContent(file);
			if(!isTransient){
				//se nao for transiente será salvo no banco de dados entao devemos sair do método
				return null;
			}
		}
		String saveDir = getSaveDir();
		return saveDir + java.io.File.separator + getFilePrefix(file) + file.getCdfile() + "." + getExtensao(file);
	}

	protected String getFilePrefix(File file) {
		return "arquivo";
	}

	protected String getExtensao(File file) {
		return "next";
	}
	
	protected String getSaveDir() {
		return System.getProperty("user.home")+java.io.File.separator+"dados"+java.io.File.separator+Next.getApplicationName()+java.io.File.separator+"arquivos";
	}
	
	public void saveFile(Object bean, String filePropertyName) {
		File arquivoVelho = null;
		File arquivoAtual = null;
		PropertyDescriptor pd;
		Object beanId = PersistenceUtils.getId(bean, getHibernateTemplate().getSessionFactory());
		try {
			pd = new PropertyDescriptor(filePropertyName, bean.getClass());
			arquivoAtual = (File) pd.getReadMethod().invoke(bean);
		} catch (Exception e) {
			throw new NextException("Não foi possivel adquirir arquivo. Propriedade "+filePropertyName+" da classe "+bean.getClass()+" (id="+beanId+")", e);
		}
		if(beanId != null){
			Object beanVelho = new QueryBuilder<Object>()
						.from(bean.getClass())
						.leftOuterJoinFetch(Util.strings.uncaptalize(bean.getClass().getSimpleName())+"."+filePropertyName+" "+filePropertyName)
						.entity(bean)
						.unique();
			if(beanVelho == null){
				throw new NextException("Não foi possivel adquirir arquivo. Propriedade "+filePropertyName+" da classe "+bean.getClass().getName()+" (id="+beanId+"). " +
						"Não foi encontrado no banco de dados um objeto "+bean.getClass().getName()+" com chave "+beanId+". " 
								, new NextException("Verifique se o campo com @Id da classe "+bean.getClass()+" está utilizando um tipo de dados primitivo, e se for o caso substitua por uma classe Wrapper. "));
			}
			try {
				arquivoVelho = (File) pd.getReadMethod().invoke(beanVelho);
			} catch (Exception e) {
				throw new NextException("Não foi possivel adquirir arquivo. Propriedade "+filePropertyName+" da classe "+bean.getClass().getName()+" (id="+beanId+")", e);
			}
		}
		if(arquivoVelho != null && arquivoAtual == null){
			//atualizar o objeto com file = null antes de excluir o arquivo para não ocasionar problema de constraint
			getHibernateTemplate().bulkUpdate("update "+bean.getClass().getName()+" set "+filePropertyName+" = null where id = "+beanId);
		}
		File save = save(arquivoAtual, arquivoVelho);
		try {
			pd.getWriteMethod().invoke(bean, save);
		} catch (Exception e) {
			throw new NextException("Não foi possível configurar o arquivo. Propriedade "+filePropertyName+" da classe "+bean.getClass(), e);
		}
	}
	
	public File save(File arquivoNovo, File arquivoVelho){
		try {
			if (arquivoVelho == null) {
				// criar
				if(arquivoNovo != null && arquivoNovo.getSize() == null){
					throw new NullPointerException("Propriedade size do arquivo é null");
				}
				if (arquivoNovo != null && arquivoNovo.getSize() > 0) {
					getHibernateTemplate().saveOrUpdate(arquivoNovo);
					
					String nomeArquivo = getNomeArquivo(arquivoNovo);
					writeFile(arquivoNovo, nomeArquivo);
				} else {
					return null;
				}
			} else {
				// atualizar
				if(arquivoNovo == null){
					//apagar o arquivo
					getHibernateTemplate().delete(arquivoVelho);
					String nomeArquivo = getNomeArquivo(arquivoVelho);
					deleteFile(nomeArquivo);
				} else if(arquivoNovo.getSize() > 0 && arquivoNovo.getContent() != null){ 
					getHibernateTemplate().evict(arquivoVelho);
					arquivoNovo.setCdfile(arquivoVelho.getCdfile());
					//sobrescrever o arquivo
					getHibernateTemplate().saveOrUpdate(arquivoNovo);
					String nomeArquivo = getNomeArquivo(arquivoNovo);
					writeFile(arquivoNovo, nomeArquivo);
				} else {
					//se o tamanho for zero não mexer no arquivo
					arquivoNovo.setCdfile(arquivoVelho.getCdfile());
				}
			}
			getHibernateTemplate().flush();
			return arquivoNovo;
		} catch (IOException e) {
			String name;
			try {
				name = getNomeArquivo(arquivoNovo);
			} catch (Exception e2) {
				name = "(Não foi possível adquirir o nome do arquivo. Erro: "+e2.getMessage()+")";
			}
			throw new NextException("Não foi possível salvar o conteúdo do arquivo no disco. "+name, e);
		} catch(Exception e){
			String name;
			try {
				name = getNomeArquivo(arquivoNovo);
			} catch (Exception e2) {
				name = "(Não foi possível adquirir o nome do arquivo. Erro: "+e2.getMessage()+")";
			}
			throw new NextException("Não foi possível salvar o registro do arquivo no banco de dados. "+name, e);			
		}
	}
	
	public void delete(BEAN bean) {
		super.delete(bean);
		if(autoDetectTransient){
			boolean isTransient = checkTransientContent(bean);
			if(!isTransient){
				//se nao for transiente será salvo no banco de dados entao devemos sair do método
				return;
			}
		}
		deleteFile(getNomeArquivo(bean));
	};
	
	protected void deleteFile(String nomeArquivo) {
		java.io.File file = new java.io.File(nomeArquivo);
		file.delete();
	}
	
	protected void writeFile(File arquivoNovo, String nomeArquivo) throws IOException {
		if(autoDetectTransient){
			boolean isTransient = checkTransientContent(arquivoNovo);
			if(!isTransient){
				//se nao for transiente será salvo no banco de dados entao devemos sair do método
				return;
			}
		}
		log.info("Gravando arquivo no disco (upload): "+nomeArquivo);
		java.io.File file = new java.io.File(nomeArquivo);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			//file.mkdirs();
			file.createNewFile();
		}
		OutputStream out = new FileOutputStream(file);
		out.write(arquivoNovo.getContent());
		out.close();
	}

}
