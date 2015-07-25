package org.nextframework.view;

import org.nextframework.util.Util;

/**
 * Essa tag serve apenas para servir como uma marcacao de tag. Não faz nada.
 * @author rogel
 *
 */
public class ContentTag extends BaseTag implements LogicalTag {

	@Override
	protected void doComponent() throws Exception {
		String body = getBody();
		if(body != null){
			String newBody = body.trim().replace("\t", "").replace("\n", "").replace("\r", "");
			if(Util.strings.isNotEmpty(newBody)){
				getOut().println(body);
			}
		}
	}
}
