package org.nextframework.report.renderer.html.builder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.type.BandTypeEnum;

import org.nextframework.exception.NextException;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.renderer.jasper.builder.JasperDesignBuilder;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;

public class UnpagedJasperPrint {

	private MappedJasperPrint mappedJasperPrint;
	
	List<PrintElement> printElements = new ArrayList<PrintElement>();

	public UnpagedJasperPrint(MappedJasperPrint mappedJasperPrint){
		this.mappedJasperPrint = mappedJasperPrint;
		build();
	}
	
	public List<PrintElement> getPrintElements() {
		return printElements;
	}

	private void build() {
		List<PrintElement> printElements = new ArrayList<PrintElement>();
		JasperPrint jasperPrint = mappedJasperPrint.getJasperPrint();
		
		List<JRPrintPage> pages = jasperPrint.getPages();
		int yPadding = 0;
		int pageSize = jasperPrint.getPageHeight() - jasperPrint.getTopMargin() - jasperPrint.getBottomMargin();
		int pageNumber = 0;
		
		int uniqueId = 0;
		
		Map<String, MappedJasperReport> mappings = new HashMap<String, MappedJasperReport>();
		for (MappedJasperReport mappedJasperReport : mappedJasperPrint.getSubreports()) {
			String reportName = mappedJasperReport.getReportDefinition().getReportName();
			if(mappings.containsKey(reportName)){
				//not necessarily it is wrong TODO test
				//throw new NextException("More than one subreport element was defined with the same name: "+reportName);
			}
			mappings.put(reportName, mappedJasperReport);
		}
		mappings.put(mappedJasperPrint.getMappedJasperReport().getReportDefinition().getReportName(), mappedJasperPrint.getMappedJasperReport());
		PrintElement lastPagePrintElement = null;
		for (JRPrintPage page : pages) {
			boolean hideHeadersForPage = true;
			List<JRPrintElement> elements = page.getElements();
			PrintElement lastPrintElement = null;
			for (JRPrintElement element : elements) {
				PrintElement printElement = createPrintElement(element, mappings, yPadding, "i"+(uniqueId++));
				if(printElement != null){
					if(printElement.getReportItem() == null){
						throw new NextException("No report item found in definition for print element "+printElement+". It is possible that there are subreports with the same name. Use setReportName on the ReportDefinition to change the report name.");
					}
					if(printElement.getReportItem().getRow().getSection().getType() == ReportSectionType.GROUP_HEADER){
						hideHeadersForPage = false;
					}
					lastPrintElement = printElement;
					if(lastPagePrintElement != null && hideHeadersForPage){
						if(Arrays.asList(
									ReportSectionType.PAGE_HEADER,
									ReportSectionType.SUMARY_DATA_HEADER,
									ReportSectionType.SUMARY_DATA_DETAIL
								).contains(printElement.getReportItem().getRow().getSection().getType())){
							boolean diferentPrintsAndPageElementFromMaster = !lastPagePrintElement.getSource().equals(printElement.getSource())
											&& printElement.getSource().equals(mappedJasperPrint.getMappedJasperReport().getReportDefinition());
							if(lastPagePrintElement.getSource().equals(printElement.getSource()) ||	diferentPrintsAndPageElementFromMaster){
								continue;
							}
						} else if((lastPagePrintElement.getReportItem().getRow().getSection().getType() == ReportSectionType.DETAIL
											&& printElement.getReportItem().getRow().getSection().getType() == ReportSectionType.DETAIL_HEADER)
											&& (lastPagePrintElement.getSource().getReportName().equals(printElement.getSource().getReportName()))){
							continue;
						}
					}
					printElements.add(printElement);
				}
			}
			lastPagePrintElement = lastPrintElement;
			yPadding += pageSize;
			pageNumber++;
		}
		
		reorganizeFrames(printElements, mappings);
		
		//search for subreports
		this.printElements = searchReportItens(mappings, mappedJasperPrint.getMappedJasperReport().getReportDefinition(), printElements);
		
		reorganizeRows(this.printElements);
	}

	private void reorganizeFrames(List<PrintElement> printElements, Map<String, MappedJasperReport> mappings) {
		for (int i = 0; i < printElements.size(); i++) {
			PrintElement printElement = printElements.get(i);
			if(printElement.getJrPrintElement() instanceof JRPrintFrame){
				printElements.remove(i);
				GroupPrintElements group = copyPrintElementToGroup(printElement);
				group.setSubreportChecked(false);
				JRPrintFrame frame = (JRPrintFrame) printElement.getJrPrintElement();
				int sequence = 0;
				for (JRPrintElement subJrPrintElement : frame.getElements()) {
					PrintElement subPrintElement = createPrintElement(subJrPrintElement, mappings, 0, printElement.getUniqueId()+"_"+sequence);
					if(subPrintElement != null){
						group.getPrintElements().add(subPrintElement);
					}
				}
				reorganizeFrames(group.getPrintElements(), mappings);
				printElements.add(i, group);
			}
		}
	}

	private PrintElement createPrintElement(JRPrintElement element, Map<String, MappedJasperReport> mappings, int yPadding, String id) {
		if(element.getKey() == null){
			return null;
		}
		if(//(pageNumber > 0 && element.getOrigin().getBandTypeValue() == BandTypeEnum.PAGE_HEADER) || 
				element.getOrigin().getBandTypeValue() == BandTypeEnum.TITLE){
			return null;
		}
		if(element instanceof JRPrintLine){
			Color color = ((JRPrintLine)element).getForecolor();
			if(JasperDesignBuilder.LINE_BREAK.equals(color)){
				return null;
			}
		}
		if(element.getKey().startsWith(JasperDesignBuilder.BACKGROUND_FRAME_KEY)){
			return null;
		}
		
		PrintElement printElement = new PrintElement();
		printElement.setUniqueId(id);
//		printElement.setSource(mappedJasperPrint.getMappedJasperReport().getReportDefinition());
		printElement.setMappedJasperReport(mappedJasperPrint.getMappedJasperReport());
		if(element.getOrigin().getReportName() != null){
			MappedJasperReport mappedJasperReport = mappings.get(element.getOrigin().getReportName());
//			printElement.setSource(mappedJasperReport.getReportDefinition());
			printElement.setMappedJasperReport(mappedJasperReport);
		}
		printElement.setJrPrintElement(element);
		printElement.setY(yPadding + element.getY());
		return printElement;
	}

	private void reorganizeRows(List<PrintElement> printElements) {
		int row = -1;
		int lastY = -1;
		KeyInfo lastRowKey = null; 
		for (PrintElement printElement : printElements) {
			KeyInfo currentKeyInfo = printElement.getKeyInfo();
			if(printElement.getY() != lastY){
				row++;
				lastY = printElement.getY();
			} else if(lastRowKey != null && currentKeyInfo != null && lastRowKey.getRowIndex() != currentKeyInfo.getRowIndex()){
				row++;
			}
			printElement.setRow(row);
			lastRowKey = currentKeyInfo;
			if(printElement instanceof GroupPrintElements){
				reorganizeRows(((GroupPrintElements) printElement).getPrintElements());
			}
		}
	}

	private List<PrintElement> searchReportItens(Map<String, MappedJasperReport> mappings, ReportDefinition reportDefinition, List<PrintElement> originalList) {
		List<PrintElement> elements = new ArrayList<PrintElement>();
		for (int i = 0; i < originalList.size(); i++) {
			PrintElement element = originalList.get(i);
			if(element.getSource().equals(reportDefinition)){
				elements.add(element);
			}
			if(mappings.get(reportDefinition.getReportName()) != null){
				ReportItem reportItem = mappings.get(reportDefinition.getReportName()).getMappedKeys().get(element.getJrPrintElement().getKey());
				if(reportItem instanceof Subreport){
					Subreport subreport = (Subreport) reportItem;
					int maxHeight = element.getJrPrintElement().getY() + element.getJrPrintElement().getHeight();
					int j = i + 1;
					while(j < originalList.size() && !originalList.get(j).getSource().equals(reportDefinition) && originalList.get(j).getJrPrintElement().getY() < maxHeight){
						j++;
					} 
					GroupPrintElements groupPrintElements = copyPrintElementToGroup(element);
					groupPrintElements.setGroupDefinition(subreport.getReport());
					List<PrintElement> subList = originalList.subList(i, j);
					groupPrintElements.getPrintElements().addAll(searchReportItens(mappings, subreport.getReport(), subList));
					elements.remove(elements.size() -1);
					
					//2014-11-28 check if the subreport is actually being continued in the new page
					boolean inserted = false;
					GroupPrintElements previousGroup = null; 
					if(elements.size() >= 1){
						PrintElement lastElement = elements.get(elements.size() - 1);
						while(lastElement instanceof GroupPrintElements){
							previousGroup = (GroupPrintElements) lastElement;
							
							if(previousGroup != null && 
									previousGroup.getGroupDefinition().getReportName().equals(
											subreport.getReport().getReportName())){
								//the subreport continues from the previous page.. use the same element
								inserted = true;
								previousGroup.getPrintElements().addAll(groupPrintElements.getPrintElements());
								break;
							} else {
								//search deeper
								if(previousGroup.getPrintElements().size() > 0){
									lastElement = previousGroup.getPrintElements().get(previousGroup.getPrintElements().size() -1);
								} else {
									break;
								}
							}//end of 2014-11-28
						}
					}
					
					if(!inserted){
						//this is not a continuation
						elements.add(groupPrintElements);
					} else {
						//check if there is deep continuation
						//only check one level.. but it is possible to have more (not implemented)
						//test if the recursion solves the deepness problem
						for (int k = previousGroup.getPrintElements().size()-1; k >= 1; k--) {
							PrintElement a = previousGroup.getPrintElements().get(k - 1);
							PrintElement b = previousGroup.getPrintElements().get(k);
							if(a instanceof GroupPrintElements && b instanceof GroupPrintElements){
								GroupPrintElements aGroup = (GroupPrintElements) a;
								GroupPrintElements bGroup = (GroupPrintElements) b;
								if(aGroup.getSource().getReportName().equals(bGroup.getSource().getReportName())){
									aGroup.getPrintElements().addAll(bGroup.getPrintElements());
									previousGroup.getPrintElements().remove(k);
								}
							}
						}
//						if(previousGroup.getPrintElements().size() >= 2){
//							PrintElement a = previousGroup.getPrintElements().get(previousGroup.getPrintElements().size() - 2);
//							PrintElement b = previousGroup.getPrintElements().get(previousGroup.getPrintElements().size() - 1);
//							if(a instanceof GroupPrintElements && b instanceof GroupPrintElements){
//								GroupPrintElements aGroup = (GroupPrintElements) a;
//								GroupPrintElements bGroup = (GroupPrintElements) b;
//								if(aGroup.getSource().getReportName().equals(bGroup.getSource().getReportName())){
//									aGroup.getPrintElements().addAll(bGroup.getPrintElements());
//									previousGroup.getPrintElements().remove(previousGroup.getPrintElements().size() - 1);
//								}
//							}
//						}
					}
					
					groupPrintElements.setSubreportChecked(true);
				} else if(element instanceof GroupPrintElements){
					//code removed in 2012-10-01
					//This method reads the elements of a subreport and put them in a GroupPrintElements
					//this is done in the above if
					//Don't know why it is necessary to remove the elements of normal groups and put them in the group again
					//This was removed because a subreport can have groups (build with grids) and this code was removing the elements of the subreport 
					// when checking for master report elements
					// i don't know if it crashes other reports
					// this if was added later, on 2012-10-08
					//TODO TEST 
					if(!((GroupPrintElements) element).isSubreportChecked()){
						GroupPrintElements group = (GroupPrintElements) element;
						List<PrintElement> children = new ArrayList<PrintElement>(group.getPrintElements());
						group.getPrintElements().clear();
						group.getPrintElements().addAll(searchReportItens(mappings, reportDefinition, children));
						group.setSubreportChecked(true);
					}
				}
			}
		}
		return elements;
	}

	private GroupPrintElements copyPrintElementToGroup(PrintElement element) {
		GroupPrintElements groupPrintElements = new GroupPrintElements();
		groupPrintElements.setJrPrintElement(element.getJrPrintElement());
//		groupPrintElements.setSource(element.getSource());
		groupPrintElements.setMappedJasperReport(element.getMappedJasperReport());
		groupPrintElements.setY(element.getY());
		groupPrintElements.setGroupDefinition(element.getSource());
		groupPrintElements.setUniqueId(element.getUniqueId());
		return groupPrintElements;
	}
}
