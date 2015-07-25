package org.nextframework.report.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportConstants;
import org.nextframework.report.definition.elements.ReportImage;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportItemIterator;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.ReportDefinitionStyle;


public class ReportDefinition implements ReportParent {

	String reportName;
	String title = "";
	String subtitle = "";
	
	List<ReportColumn> columns = new ArrayList<ReportColumn>();
	List<ReportGroup> groups = new ArrayList<ReportGroup>();
	
	ReportSection sectionTitle        = new ReportSection(this, ReportSectionType.TITLE);
	ReportSection sectionPageHeader   = new ReportSection(this, ReportSectionType.PAGE_HEADER);
	ReportSection sectionPageFooter   = new ReportSection(this, ReportSectionType.PAGE_FOOTER);
	ReportSection sectionColumnHeader = new ReportSection(this, ReportSectionType.COLUMN_HEADER);
	ReportSection sectionColumnFooter = new ReportSection(this, ReportSectionType.COLUMN_FOOTER);
	ReportSection sectionDetailHeader = new ReportSection(this, ReportSectionType.DETAIL_HEADER);
	ReportSection sectionDetail       = new ReportSection(this, ReportSectionType.DETAIL);
	ReportSection sectionSummaryDataHeader = new ReportSection(this, ReportSectionType.SUMARY_DATA_HEADER);
	ReportSection sectionSummaryDataDetail = new ReportSection(this, ReportSectionType.SUMARY_DATA_DETAIL);
	ReportSection sectionFirstPageHeader  = new ReportSection(this, ReportSectionType.FIRST_PAGE_HEADER);
	ReportSection sectionLastPageFooter = new ReportSection(this, ReportSectionType.LAST_PAGE_FOOTER);
	ReportSection sectionSummary  = new ReportSection(this, ReportSectionType.SUMARY);
	Map<ReportGroup, ReportGroupSection> sectionsForGroups = new LinkedHashMap<ReportGroup, ReportGroupSection>();
	
	List<ReportItem> reportTitleItems = new ArrayList<ReportItem>();
	List<ReportItem> reportItens = new ArrayList<ReportItem>();
	
	Map<String, Object> renderParameters = new HashMap<String, Object>();
	
	ReportDefinitionStyle style = new ReportDefinitionStyle();
	
	Map<String, Object> reportParameters = new HashMap<String, Object>();
	
	List<?> data = new ArrayList<Object>();
	
	public ReportDefinition(){
	}
	
	public ReportDefinition(String reportName){
		this.reportName = reportName;
	}
	
	public List<?> getData() {
		return data;
	}
	public void setData(List<?> data) {
		this.data = data;
	}


	public ReportDefinitionStyle getStyle() {
		return style;
	}
	
	public void setStyle(ReportDefinitionStyle style) {
		this.style = style;
	}

	public String getReportName() {
		return reportName;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public Map<String, Object> getRenderParameters() {
		return renderParameters;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public void setRenderParameters(Map<String, Object> renderParameters) {
		this.renderParameters = renderParameters;
	}
	
	public Map<ReportGroup, ReportGroupSection> getSectionsForGroups() {
		return sectionsForGroups;
	}
	
	public Map<String, Object> getParameters() {
		return reportParameters;
	}
	
	public Object setParameter(String key, Object value){
		return reportParameters.put(key, value);
	}

	public List<ReportColumn> getColumns() {
		return columns;
	}

	public List<ReportGroup> getGroups() {
		return groups;
	}

	public ReportSection getSectionTitle() {
		return sectionTitle;
	}
	
	public ReportSection getSectionSummaryDataHeader() {
		return sectionSummaryDataHeader;
	}
	
	public ReportSection getSectionSummaryDataDetail() {
		return sectionSummaryDataDetail;
	}
	
	public ReportSection getSectionFirstPageHeader() {
		return sectionFirstPageHeader;
	}
	public ReportSection getSectionPageHeader() {
		return sectionPageHeader;
	}

	public ReportSection getSectionPageFooter() {
		return sectionPageFooter;
	}
	public ReportSection getSectionColumnFooter() {
		return sectionColumnFooter;
	}
	public ReportSection getSectionColumnHeader() {
		return sectionColumnHeader;
	}

	public ReportSection getSectionDetail() {
		return sectionDetail;
	}

	public ReportSection getSectionDetailHeader() {
		return sectionDetailHeader;
	}
	
	public List<ReportItem> getReportItens() {
		return reportItens;
	}

	public List<ReportItem> getReportTitleItems() {
		return reportTitleItems;
	}
	
	public ReportSection getSectionLastPageFooter() {
		return sectionLastPageFooter;
	}
	
	public ReportSection getSectionSummary() {
		return sectionSummary;
	}

	/*
	 * Métodos auxiliares
	 */
	public void addRenderParameter(String key, Object value){
		renderParameters.put(key, value);
	}
	
	public ReportColumn createColumn() {
		ReportColumn e = new ReportColumn(this, columns.size());
		if(columns.size() > 0){
			ReportColumn previous = columns.get(columns.size() -1);
			e.setPrevious(previous);
			previous.setNext(e);
		}
		columns.add(e);
		return e;
	}
	
	public ReportGroup createGroup(String expression) {
		ReportGroup e = new ReportGroup(this, expression);
		groups.add(e);
		sectionsForGroups.put(e, new ReportGroupSection(this, e));
		return e;
	}
	
	public ReportGroupSection getReportGroupSection(String expression){
		return sectionsForGroups.get(getReportGroupForExpression(expression));
	}
	
	public ReportGroup getReportGroupForExpression(String expression){
		Set<ReportGroup> keySet = sectionsForGroups.keySet();
		for (ReportGroup reportGroup : keySet) {
			if(reportGroup.getExpression().equals(expression)){
				return reportGroup;
			}
		}
		return null;
	}
	
	public <E extends ReportItem> E addItem(E e, ReportSectionType sectionType, String groupName, int column) {
		ReportSection section = getSectionForType(sectionType, groupName);
		int row = section.getRows().size() -1;
		return addItem(e, section.getRow(row), column);
	}
	
	public ReportItem addItem(String item, ReportSectionType sectionType, String groupName, int column) {
		return addItem(item, sectionType, groupName, -1, column);
	}
	public ReportItem addItem(String item, ReportSectionType sectionType, int row, int column) {
		return addItem(item, sectionType, null, row, column);
	}
	
	public ReportItem addItem(String item, ReportSectionType sectionType, int column) {
		return addItem(item, sectionType, null, -1, column);
	}
	
	public ReportItem addItem(String item, ReportSectionType sectionType, String groupName, int row, int column) {
		ReportItem reportItem = convertStringToReportItem(item);
		ReportSection section = getSectionForType(sectionType, groupName);
		if(row == -1){
			row = section.getRows().size() -1;
		}
		return addItem(reportItem, section.getRow(row), column);
	}
	
	
	public static ReportItem convertStringToReportItem(String item) {
		if(item == null){
			throw new NullPointerException("item cannot be null");
		}
		ReportItem reportItem;
		if(item.startsWith("$")){
			reportItem = new ReportTextField(item.substring(1));
		} else {
			reportItem = new ReportLabel(item);
		}
		return reportItem;
	}
	
	public ReportSection getSectionForType(ReportSectionType sectionType, String groupName) {
		switch (sectionType) {
		case COLUMN_FOOTER: return getSectionColumnFooter();
		case COLUMN_HEADER: return getSectionColumnHeader();
		case DETAIL:	    return getSectionDetail();
		case DETAIL_HEADER: return getSectionDetailHeader();
		case PAGE_FOOTER:   return getSectionPageFooter();
		case PAGE_HEADER:   return getSectionPageHeader();
		case TITLE:         return getSectionTitle();
		case SUMARY_DATA_HEADER:   return getSectionSummaryDataHeader();
		case SUMARY_DATA_DETAIL:   return getSectionSummaryDataDetail();
		case FIRST_PAGE_HEADER:    return getSectionFirstPageHeader();
		case GROUP:         
		case GROUP_FOOTER:
		case GROUP_HEADER: 
		case GROUP_DETAIL: 
			if(groupName == null){
				throw new NullPointerException("it is necessary to inform group name for section type of groups");
			}
			List<ReportGroup> groups2 = getGroups();
			for (ReportGroup reportGroup : groups2) {
				if(reportGroup.getExpression().equals(groupName)){
					switch (sectionType) {
						case GROUP:         
						case GROUP_HEADER: return reportGroup.getSectionHeader(); 
						case GROUP_FOOTER: return reportGroup.getSectionFooter();
						case GROUP_DETAIL: return reportGroup.getSectionDetail();
					}
				}
			}
		default:
			throw new IllegalArgumentException("unknown section type "+sectionType);
		}
	}
	
	public void addTitleItem(ReportItem e){
		reportTitleItems.add(e);
	}
	public <E extends ReportItem> E addItem(E e, ReportSectionRow row, int column) {
		return addItem(e, row, getColumn(column));
	}
	public <E extends ReportItem> E addItem(E e, ReportSection section, int column) {
		return addItem(e, section, getColumn(column));
	}
	public <E extends ReportItem> E addItem(E e, ReportSection reportSection, ReportColumn column) {
		if(reportSection.getRows().size() == 0){
			reportSection.getRows().get(0);//força a criaçao da linha 0
		}
		return addItem(e, reportSection.getRows().get(reportSection.getRows().size() - 1), column);
	}
	public <E extends ReportItem> E addItem(E e, ReportSectionRow row, ReportColumn column) {
		if(row.section.getType() == ReportSectionType.TITLE){
			throw new RuntimeException("Use addTitleItem to add items to title section");
		}
		checkOverlappingColumns(e, row, column);
		e.setColumn(column);
		e.setRow(row);
		e.setParent(this);
		reportItens.add(e);
		return e;
	}
	private void checkOverlappingColumns(ReportItem e, ReportSectionRow row, ReportColumn column) {
		ReportColumn checkColumn = column;
		int colspan = 0;
		while(checkColumn != null){
			ReportItem existingElementCheckColumn = getElementFor(row, checkColumn);
			if(existingElementCheckColumn != null && existingElementCheckColumn.getColspan() > colspan){
				try {
					throwExistingElementExeption(e, row, column, existingElementCheckColumn);
				} catch (RuntimeException e1) {
					if(colspan == 0){
						throw e1;
					}
					throw new RuntimeException("Existing element in previous column uses colspan "+existingElementCheckColumn.getColspan(), e1);
				}
			}
			colspan++;
			checkColumn = checkColumn.getPrevious();
		}
		int checkColspan = e.getColspan() -1;
		checkColumn = column.getNext();
		while(checkColspan > 0 && checkColumn != null){
			try {
				throwExistingElementExeption(e, row, checkColumn, getElementFor(row, checkColumn));
			} catch (Exception e1) {
				throw new RuntimeException("The element uses colspan "+e.getColspan()+" and forward columns are being used "+(e.getColspan() - checkColspan));
			}
			checkColumn = checkColumn.getNext();
		}
	}
	private void throwExistingElementExeption(ReportItem e, ReportSectionRow row, ReportColumn column, ReportItem existingElement) {
		if(existingElement != null){
			throw new RuntimeException("More than one item was defined in report for "+row+" and "+column+". Item '"+existingElement+"' was already defined when trying to define '"+e+"'.");
		}
	}

	public ReportItem getElementFor(ReportSectionRow sectionRow, ReportColumn column) {
		ReportItem returnItem = null;
		for (ReportItem item : reportItens) {
			if(item.getRow().equals(sectionRow) && item.getColumn().equals(column)){
				if(returnItem != null){
					throw new RuntimeException("More than one item was defined in report for row "+sectionRow+" and column "+column+". Item \""+returnItem+"\" was already defined.");
				}
				returnItem = item;
			}
		}
		return returnItem;
	}

	public String getSubreportIndex(Subreport subreport) {
		int count = 0;
		for (Subreport reportImage : getSubreports()) {
			if(reportImage == subreport){
				return String.valueOf(++count);
			}
			count++;
		}
		return String.valueOf(-1);
	}
	
	public int getImageIndex(ReportImage image) {
		int count = 0;
		for (ReportImage reportImage : getImages()) {
			if(reportImage == image){
				return ++count;
			}
			count++;
		}
		return -1;
	}
	
	public int getChartIndex(ReportChart chart) {
		int count = 0;
		for (ReportChart reportChart : getCharts()) {
			if(reportChart == chart){
				return ++count;
			}
			count++;
		}
		return -1;
	}
	
	
	public List<Subreport> getSubreports(){
		List<Subreport> images = new ArrayList<Subreport>();
		ReportItemIterator reportItemIterator = new ReportItemIterator(this);
		while(reportItemIterator.hasNext()){
			ReportItem reportItem = reportItemIterator.next();
			if(reportItem instanceof Subreport){
				images.add((Subreport) reportItem);
			}
		}
		return images;
	}
	
	public List<ReportImage> getImages(){
		List<ReportImage> images = new ArrayList<ReportImage>();
		ReportItemIterator reportItemIterator = new ReportItemIterator(this);
		while(reportItemIterator.hasNext()){
			ReportItem reportItem = reportItemIterator.next();
			if(reportItem instanceof ReportImage){
				images.add((ReportImage) reportItem);
			}
		}
		return images;
	}
	
	public List<ReportChart> getCharts(){
		List<ReportChart> charts = new ArrayList<ReportChart>();
		ReportItemIterator reportItemIterator = new ReportItemIterator(this);
		while(reportItemIterator.hasNext()){
			ReportItem reportItem = reportItemIterator.next();
			if(reportItem instanceof ReportChart){
				charts.add((ReportChart) reportItem);
			}
		}
		return charts;
	}
	
	@Override
	public List<ReportItem> getChildren() {
		return getReportItens();
	}

	public ReportColumn getColumn(int i) {
		while(getColumns().size() <= i){
			createColumn();
		}
		return getColumns().get(i);
	}
	public boolean hasRowElements(ReportSectionRow reportSectionRow) {
		for (ReportItem item : reportItens) {
			if(item.getRow().equals(reportSectionRow)){
				return true;
			}
		}
		return false;
	}
	public void setColumnWidths(int... widths) {
		if(widths == null){
			return;
		}
		int i = 0;
		for (int width : widths) {
			if(width == -1){
				width = ReportConstants.AUTO_WIDTH;
			}
			getColumn(i++).setWidth(width);
		}		
	}

	
}
