/**
 * 
 */
package org.nextframework.report.renderer.html.design;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HtmlTag {

	private HtmlStyle style = new HtmlStyle();
	private HtmlClass styleClass = new HtmlClass();

	private boolean breakLine = false;
	private String tagName;
	private String innerHTML;
	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	private List<HtmlTag> children = new ArrayList<HtmlTag>();

	public HtmlClass getStyleClass() {
		return styleClass;
	}

	public HtmlStyle getStyle() {
		return style;
	}

	public boolean isBreakLine() {
		return breakLine;
	}

	public String getTagName() {
		return tagName;
	}

	public String getInnerHTML() {
		return innerHTML;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public List<HtmlTag> getChildren() {
		return children;
	}

	public void setStyle(HtmlStyle style) {
		this.style = style;
	}

	public void setBreakLine(boolean breakLine) {
		this.breakLine = breakLine;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setChildren(List<HtmlTag> children) {
		this.children = children;
	}

	public void setInnerHTML(String innerHTML) {
		this.innerHTML = innerHTML;
	}

	public HtmlTag(String tagName) {
		super();
		this.tagName = tagName;
	}

	@Override
	public String toString() {
		return getTagString("");
	}

	private String getTagString(String padding) {

		if (tagName == null) {
			return "";
		}

		StringBuilder body = new StringBuilder();
		for (HtmlTag child : children) {
			body.append(child.getTagString(padding));
		}

		if (tagName.equals("span") || tagName.equals("div")) {
			if (children.isEmpty() && attributes.isEmpty() && style.isEmpty()) {
				if (innerHTML == null) {
					innerHTML = "";
				}
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append(padding).append("<").append(tagName);
		Set<String> keySet = attributes.keySet();
		for (String string : keySet) {
			builder.append(" ").append(string).append("=").append("\"").append(attributes.get(string)).append("\"");
		}
		builder.append(style);
		builder.append(styleClass);
		builder.append(">");

		if (breakLine) {
			builder.append("\n").append(padding).append("    ");
		} else {
			padding = "";
		}

		builder.append(body.toString());
		if (innerHTML != null) {
			builder.append(innerHTML);
		}
		builder.append("</").append(tagName).append(">");

		return builder.toString();
	}

}
