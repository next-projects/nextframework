package org.nextframework.report.renderer.html.builder;

import static java.util.Arrays.asList;
import static org.nextframework.report.definition.ReportSectionType.DETAIL;
import static org.nextframework.report.definition.ReportSectionType.DETAIL_HEADER;
import static org.nextframework.report.definition.ReportSectionType.GROUP_DETAIL;
import static org.nextframework.report.definition.ReportSectionType.GROUP_HEADER;
import static org.nextframework.report.definition.ReportSectionType.SUMARY_DATA_DETAIL;
import static org.nextframework.report.definition.ReportSectionType.SUMARY_DATA_HEADER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;

public class UnpagedJasperPrint2 {

	List<PrintElement> printElements = new ArrayList<PrintElement>();
	
	private MappedJasperPrint mappedJasperPrint;

	public UnpagedJasperPrint2(MappedJasperPrint mappedJasperPrint){
		this.mappedJasperPrint = mappedJasperPrint;
		build();
	}
	
	public List<PrintElement> getPrintElements() {
		return printElements;
	}

	private void build() {
		List<PrintElement> printElements = new ArrayList<PrintElement>();
		PrintElementsFactory printElementsFactory = new PrintElementsFactory(mappedJasperPrint);
		JasperPrint jasperPrint = mappedJasperPrint.getJasperPrint();
		List<JRPrintPage> pages = jasperPrint.getPages();
		int pageIndex = 0;
		for (JRPrintPage page : pages) {
			List<JRPrintElement> elements = page.getElements();
			for (JRPrintElement jrPrintElement : elements) {
				PrintElement pe = printElementsFactory.createPrintElement(jrPrintElement, pageIndex);
				if(pe != null){
					printElements.add(pe);
				}
			}
			pageIndex++;
		}
		this.printElements = printElements;
		
		GroupPrintElements groupBase = new GroupPrintElements();
		ReportPointer pointer = new ReportPointer(mappedJasperPrint.getMappedJasperReport().getReportDefinition());
//		System.out.println(pointer);
		createGroupsForSubReports(pointer, groupBase, printElements);
		
		this.printElements = groupBase.getPrintElements();
		
		reorganizeRows(this.printElements);
		
//		System.out.println(this.printElements);
	}

	private void createGroupsForSubReports(ReportPointer pointer, GroupPrintElements group, List<PrintElement> printElements) {
		while(printElements.size() > 0){
			PrintElement printElement = printElements.get(0);
//			System.out.println("Found "+printElement);
			if(isSameReport(printElement, pointer)){
//				System.out.println("Adding to -> "+pointer.definition.getReportName());
				printElements.remove(0);
				if(group.getGroupDefinition() != null){
					if(!group.getGroupDefinition().equals(printElement.getReportItem().getRow().getSection().getDefinition())){
						throw new IllegalStateException();
					}
				}
				group.getPrintElements().add(printElement);
			} else {
				ReportPointer next = pointer.next();
				if(next == pointer.parent){
//					System.out.println("<- Returning to parent");
					break;
				} else {
//					System.out.println(">> Entering in "+next.definition.getReportName() + "  "+next.subreport);
					String key = searchKeyForSubreport(next.subreport);
//					System.out.println(key);
					GroupPrintElements groupPrintElements = new GroupPrintElements();
					groupPrintElements.setGroupDefinition(next.definition);
					groupPrintElements.setKeyInfo(new KeyInfo(key));
					groupPrintElements.setReportItem(next.subreport);
					group.getPrintElements().add(groupPrintElements);
					createGroupsForSubReports(next, groupPrintElements, printElements);
				}
			}
		}
	}

	private String searchKeyForSubreport(Subreport subreport) {
		//TODO REFACTOR.. DUPLICATE CODE
		String subreportIndex = mappedJasperPrint.getMappedJasperReport().getReportDefinition().getSubreportIndex(subreport);
		if(!subreportIndex.equals("-1")){
			MappedJasperReport mappedJasperReport = mappedJasperPrint.getMappedJasperReport();
			return readKeyFromMappedJasperReport(subreport, mappedJasperReport);
		} else {
			
			//this is a sub subreport.. search deeper
			List<MappedJasperReport> subreports = mappedJasperPrint.getSubreports();
			for (MappedJasperReport mappedJasperReport : subreports) {
				ReportDefinition parent = mappedJasperReport.getReportDefinition();
				String subreportIndex2 = parent.getSubreportIndex(subreport);
				if(!subreportIndex2.equals("-1")){
					return readKeyFromMappedJasperReport(subreport, mappedJasperReport);
				}
			}
		}
		
		throw new IllegalStateException("subreport key not found");
	}

	public String readKeyFromMappedJasperReport(Subreport subreport, MappedJasperReport mappedJasperReport) {
		Map<String, ReportItem> mappedKeys = mappedJasperReport.getMappedKeys();
		Set<Entry<String, ReportItem>> entrySet = mappedKeys.entrySet();
		for (Entry<String, ReportItem> entry : entrySet) {
			if(entry.getValue().equals(subreport)){
				return entry.getKey();
			}
		}
		
		throw new IllegalStateException("subreport key not found");
	}

	private boolean isSameReport(PrintElement printElement, ReportPointer pointer) {
		return pointer.definition.equals(printElement.getReportItem().getRow().getSection().getDefinition());
//		String originReportName = printElement.getJrPrintElement().getOrigin().getReportName();
//		return (pointer.parent == null && originReportName == null)
//				||
//				(pointer.definition.getReportName().equals(originReportName));
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
		
		PrintElement lastPrintElement = null; 
		for (Iterator<PrintElement> iterator = printElements.iterator(); iterator.hasNext();) {
			PrintElement printElement = iterator.next();
			if(printElement instanceof GroupPrintElements){
				if(((GroupPrintElements) printElement).getGroupDefinition() != null){
					//subreport.. must continue;
					continue;
				}
			}
			if(lastPrintElement != null){
				lastY = lastPrintElement.getJrPrintElement().getY();
				int currentY = printElement.getJrPrintElement().getY();
				if(currentY < lastY){ //page flip
					ReportSection lastSection = lastPrintElement.getReportItem().getRow().getSection();
					ReportSection currentSection = printElement.getReportItem().getRow().getSection();
					ReportSectionType lastSectionType = lastSection.getType();
					ReportSectionType currentSessionType = currentSection.getType();
					if((asList(GROUP_HEADER, GROUP_DETAIL).contains(lastSectionType) &&
						asList(SUMARY_DATA_HEADER, SUMARY_DATA_DETAIL).contains(currentSessionType))
						||
						(asList(DETAIL).contains(lastSectionType) &&
						asList(DETAIL_HEADER, SUMARY_DATA_HEADER, SUMARY_DATA_DETAIL).contains(currentSessionType))
						){
							if(lastPrintElement.getSource().equals(printElement.getSource())){
								iterator.remove();
								continue;
							}
					}
				}
				
			}
			lastPrintElement = printElement;
		}
	}

}
