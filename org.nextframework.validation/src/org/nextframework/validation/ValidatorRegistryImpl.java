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
package org.nextframework.validation;

import java.lang.annotation.Annotation;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.SimpleTime;
import org.nextframework.types.InscricaoEstadual;
import org.nextframework.types.Phone;
import org.nextframework.validation.annotation.Email;
import org.nextframework.validation.annotation.MaxLength;
import org.nextframework.validation.annotation.MaxValue;
import org.nextframework.validation.annotation.MinLength;
import org.nextframework.validation.annotation.MinValue;
import org.nextframework.validation.annotation.Required;
import org.nextframework.validation.annotation.Year;
import org.nextframework.validation.validators.ByteValidator;
import org.nextframework.validation.validators.CepValidator;
import org.nextframework.validation.validators.CnpjValidator;
import org.nextframework.validation.validators.CpfValidator;
import org.nextframework.validation.validators.DateValidator;
import org.nextframework.validation.validators.EmailValidator;
import org.nextframework.validation.validators.FloatValidator;
import org.nextframework.validation.validators.InscricaoEstadualValidator;
import org.nextframework.validation.validators.IntegerValidator;
import org.nextframework.validation.validators.LongValidator;
import org.nextframework.validation.validators.MaxLengthValidator;
import org.nextframework.validation.validators.MaxValueValidator;
import org.nextframework.validation.validators.MinLengthValidator;
import org.nextframework.validation.validators.MinValueValidator;
import org.nextframework.validation.validators.RequiredValidator;
import org.nextframework.validation.validators.ShortValidator;
import org.nextframework.validation.validators.PhoneValidator;
import org.nextframework.validation.validators.TimeValidator;
import org.nextframework.validation.validators.YearValidator;


/**
 * Onde são registrados todos os validadores
 * @author rogelgarcia
 *
 */
public class ValidatorRegistryImpl implements ValidatorRegistry {

	protected Map<Class<? extends Annotation>, PropertyValidator> validators = new HashMap<Class<? extends Annotation>, PropertyValidator>();
	protected Map<Class<?>, PropertyValidator> typeValidators = new HashMap<Class<?>, PropertyValidator>();
	//TODO REGISTRAR OS TIPOS STRING
	protected Map<String, PropertyValidator> typeValidatorsString = new HashMap<String, PropertyValidator>();

	//public static final String validatorUtilities = "org/nextframework/validation/validators/javascript/validateUtilities.js";
	
	protected ValidatorAnnotationExtractor annotationExtractor = new ValidatorAnnotationExtractorImpl(this);
	
	public ValidatorRegistryImpl(){
		init();
	}


	protected void init() {
		register(Required.class, new RequiredValidator());
		register(Email.class, new EmailValidator());
		register(MaxLength.class, new MaxLengthValidator());
		register(MinLength.class, new MinLengthValidator());
		
		register(MaxValue.class, new MaxValueValidator());
		register(MinValue.class, new MinValueValidator());
		
		register(Year.class, new YearValidator());
		
		registerType(Float.class, new FloatValidator());
		registerType(Double.class, new FloatValidator());
		
		registerType(Integer.class, new IntegerValidator());
		registerType(Long.class, new LongValidator());
		registerType(Byte.class, new ByteValidator());
		registerType(Short.class, new ShortValidator());
		
		registerType(Date.class, new DateValidator());
		registerType(java.util.Date.class, new DateValidator());
		registerType(Time.class, new TimeValidator());
		registerType(SimpleTime.class, new TimeValidator());
		
		registerType(Cpf.class, new CpfValidator());
		registerType(Cnpj.class, new CnpjValidator());
		registerType(InscricaoEstadual.class, new InscricaoEstadualValidator());
		registerType(Cep.class, new CepValidator());
		registerType(Phone.class, new PhoneValidator());
		
		registerType("integer", new IntegerValidator());
		registerType("long", new LongValidator());
		registerType("float", new FloatValidator());
		registerType("date", new DateValidator());
	}


	public PropertyValidator getPropertyValidator(Class<? extends Annotation> key) {
		return validators.get(key);
	}
	
	public PropertyValidator getTypeValidator(Class<?> key) {
		return typeValidators.get(key);
	}


	public PropertyValidator register(Class<? extends Annotation> key, PropertyValidator value) {
		return validators.put(key, value);
	}
	
	public PropertyValidator registerType(Class<?> key, PropertyValidator value) {
		return typeValidators.put(key, value);
	}
	
	public PropertyValidator registerType(String key, PropertyValidator value){
		return typeValidatorsString.put(key, value);
	}
	
	public void clear() {
		validators.clear();
		typeValidators.clear();
	}


	public ValidatorAnnotationExtractor getExtractor() {
		return annotationExtractor;
	}


	public PropertyValidator getTypeValidator(String key) {
		return typeValidatorsString.get(key);
	}	
}
