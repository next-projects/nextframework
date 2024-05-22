package com.github.jonpeterson.jackson.module.versioning;

import java.util.Arrays;
import java.util.List;

import org.nextframework.controller.json.JsonTranslator;
import org.nextframework.service.ServiceFactory;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

public class VersionedModelSerializerTest {

	public static void main(String[] args) throws Exception {

		Emp e1 = new Emp(5, "A5");
		Emp e1b = new Emp(5, "A5");
		Emp e2 = new Emp(6, "A6");

		List<Emp> es = Arrays.asList(e1, e1b, e2, e2);
		System.out.println(es);

		String json = ServiceFactory.getService(JsonTranslator.class).toJson(es);
		System.out.println(json);

		List<Emp> es2 = ServiceFactory.getService(JsonTranslator.class).fromJsonAsList(json, Emp.class);
		System.out.println(es2);

	}

	@JsonVersionedModel(propertyName = "_v", currentVersion = "1", defaultDeserializeToVersion = "1")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	public static class Emp {

		private Integer id;
		private String nome;

		public Emp() {

		}

		public Emp(Integer id2, String nome2) {
			id = id2;
			nome = nome2;
		}

		public Integer getId() {
			return id;
		}

		public String getNome() {
			return nome;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		@Override
		public boolean equals(Object outro) {
			return this.id.equals(((Emp) outro).getId());
		}

		@Override
		public int hashCode() {
			return this.getId().hashCode();
		}

		@Override
		public String toString() {
			return id + " - " + nome;
		}

	}

}
