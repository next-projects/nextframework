package org.nextframework.test.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextframework.util.ObjectUtils;
import org.springframework.context.MessageSourceResolvable;

public class TestObjectUtils {

	private final ObjectUtils objects = new ObjectUtils();

	@Test
	public void testEqualsMesmaInstanciaNaoDelegaParaEqualsDoObjeto() {

		ObjetoNaoReflexivo objeto = new ObjetoNaoReflexivo();

		assertTrue(objects.equals(objeto, objeto));

	}

	@Test
	public void testCheckResolvableCodeAndArgumentReconheceMesmaInstancia() {

		ObjetoNaoReflexivo argumento = new ObjetoNaoReflexivo();
		MessageSourceResolvable mensagem = mensagem("codigo", argumento);

		assertTrue(objects.checkResolvableCodeAndArgument(mensagem, "codigo", argumento));

	}

	@Test
	public void testCheckResolvableCodeAndArgumentComArgumentoNulo() {

		MessageSourceResolvable mensagem = mensagem("codigo", (Object) null);

		assertTrue(objects.checkResolvableCodeAndArgument(mensagem, "codigo", null));

	}

	private MessageSourceResolvable mensagem(String codigo, Object... argumentos) {
		return new MessageSourceResolvable() {

			@Override
			public String[] getCodes() {
				return new String[] { codigo };
			}

			@Override
			public Object[] getArguments() {
				return argumentos;
			}

			@Override
			public String getDefaultMessage() {
				return null;
			}

		};
	}

	private static class ObjetoNaoReflexivo {

		@Override
		public boolean equals(Object obj) {
			return false;
		}

	}

}
