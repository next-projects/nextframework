package org.nextframework.view;

public class TabPanelBlock {

	private PanelRenderedBlock block;

	private String idTabPanel;

	private Integer tabNumber;

	public TabPanelBlock(PanelRenderedBlock block, String idTabPanel, Integer tabNumber) {
		this.block = block;
		this.idTabPanel = idTabPanel;
		this.tabNumber = tabNumber;
	}

	public String getId() {
		return idTabPanel + "_" + tabNumber;
	}
	
	public String getOnSelectTab(){
		String string = (String) block.getProperties().get("onselecttab");
		if(string == null){
			string = "";
		}
		return TagUtils.escape(string);
	}
	
	public String getTitle(){
		String title = (String) block.getProperties().get("title");
		if(title == null){
			return "No title attribute ("+tabNumber+")";
		} else {
			return title;
		}
	}
	
	public String getBody(){
		return block.getBody();
	}
}
