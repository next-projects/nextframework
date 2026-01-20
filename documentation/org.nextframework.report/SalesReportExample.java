package org.nextframework.report.example;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartType;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.Group;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.annotations.Variable;
import org.nextframework.summary.compilation.SummaryBuilder;
import org.nextframework.summary.compilation.SummaryResult;

/**
 * Complete example demonstrating the Report module with:
 * - Entity class (Sale) - see Sale.java
 * - Summary class with two-level grouping
 * - Report builder with chart and data columns
 * - PDF generation
 */
public class SalesReportExample {

    // =========================================================================
    // SUMMARY CLASS - Defines two-level grouping
    // =========================================================================

    public static class SaleSummary extends Summary<Sale> {

        @Group(1)  // Level 1: Group by region
        public String getRegion() {
            return getCurrent().getRegion();
        }

        @Group(2)  // Level 2: Group by product (within region)
        public String getProduct() {
            return getCurrent().getProduct();
        }

        // Detail field - not a group, shown for each row
        public String getDescription() {
            return getCurrent().getDescription();
        }

        // Base quantity accessor
        public Integer getQuantity() {
            return getCurrent().getQuantity();
        }

        // Scoped variables for quantity - enables subtotals at each level
        @Variable(calculation = CalculationType.SUM, scopeGroup = "report")
        public Integer getQuantityReport() {
            return getQuantity();
        }

        @Variable(calculation = CalculationType.SUM, scopeGroup = "region")
        public Integer getQuantityRegion() {
            return getQuantity();
        }

        @Variable(calculation = CalculationType.SUM, scopeGroup = "product")
        public Integer getQuantityProduct() {
            return getQuantity();
        }

        // Base amount accessor
        public BigDecimal getAmount() {
            return getCurrent().getAmount();
        }

        // Scoped variables for amount - enables subtotals at each level
        @Variable(calculation = CalculationType.SUM, scopeGroup = "report")
        public BigDecimal getAmountReport() {
            return getAmount();
        }

        @Variable(calculation = CalculationType.SUM, scopeGroup = "region")
        public BigDecimal getAmountRegion() {
            return getAmount();
        }

        @Variable(calculation = CalculationType.SUM, scopeGroup = "product")
        public BigDecimal getAmountProduct() {
            return getAmount();
        }
    }

    // =========================================================================
    // REPORT BUILDER - Defines the layout with chart and columns
    // =========================================================================

    public static class SalesReportBuilder extends LayoutReportBuilder {

        @Override
        protected void layoutReport() {
            setTitle("Sales Report");
            setSubtitle("By Region and Product");

            // Add chart to first page header (appears only on first page)
            Chart chart = createSalesChart();
            getDefinition().addItem(new ReportChart(chart, 400, 200),
                getDefinition().getSectionFirstPageHeader(), 0);
            getDefinition().getSectionFirstPageHeader().breakLine();

            // Columns - region and product are groups (shown in group headers)
            fieldDetail("description");  // Item-level detail
            fieldSummary("quantity");    // Detail + subtotals + grand total
            fieldSummary("amount");      // Detail + subtotals + grand total
        }

        private Chart createSalesChart() {
            Chart chart = new Chart(ChartType.PIE);
            chart.setTitle("Sales by Region");
            ChartData data = new ChartData("Region", "Amount");
            data.setSeries("Sales");
            data.addRow("North", 44000);
            data.addRow("South", 43000);
            data.addRow("East", 46000);
            data.addRow("West", 41500);
            chart.setData(data);
            return chart;
        }

        @Override
        public int[] getColumnConfig() {
            return new int[] { 200, 80, 120 };
        }
    }

    // =========================================================================
    // SAMPLE DATA
    // =========================================================================

    public static List<Sale> createSampleData() {
        List<Sale> sales = new ArrayList<>();

        // North region
        sales.add(new Sale("North", "Laptop", "Dell XPS 15 Pro", 10, new BigDecimal("15000.00")));
        sales.add(new Sale("North", "Laptop", "MacBook Pro M3", 5, new BigDecimal("7500.00")));
        sales.add(new Sale("North", "Phone", "iPhone 15 Pro", 20, new BigDecimal("10000.00")));
        sales.add(new Sale("North", "Phone", "Samsung Galaxy S24", 15, new BigDecimal("7500.00")));
        sales.add(new Sale("North", "Tablet", "iPad Pro 12.9", 8, new BigDecimal("4000.00")));

        // South region
        sales.add(new Sale("South", "Laptop", "ThinkPad X1 Carbon", 12, new BigDecimal("18000.00")));
        sales.add(new Sale("South", "Phone", "Pixel 8 Pro", 25, new BigDecimal("12500.00")));
        sales.add(new Sale("South", "Phone", "OnePlus 12", 10, new BigDecimal("5000.00")));
        sales.add(new Sale("South", "Tablet", "Galaxy Tab S9", 15, new BigDecimal("7500.00")));

        // East region
        sales.add(new Sale("East", "Laptop", "HP Spectre x360", 8, new BigDecimal("12000.00")));
        sales.add(new Sale("East", "Laptop", "ASUS ROG Zephyrus", 6, new BigDecimal("9000.00")));
        sales.add(new Sale("East", "Phone", "iPhone 15", 30, new BigDecimal("15000.00")));
        sales.add(new Sale("East", "Tablet", "Surface Pro 10", 20, new BigDecimal("10000.00")));

        // West region
        sales.add(new Sale("West", "Laptop", "MacBook Air M3", 15, new BigDecimal("22500.00")));
        sales.add(new Sale("West", "Phone", "Samsung Galaxy Z Flip", 18, new BigDecimal("9000.00")));
        sales.add(new Sale("West", "Tablet", "iPad Air", 12, new BigDecimal("6000.00")));
        sales.add(new Sale("West", "Tablet", "Kindle Scribe", 8, new BigDecimal("4000.00")));

        return sales;
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) throws Exception {
        List<Sale> sales = createSampleData();
        System.out.println("Created " + sales.size() + " sales records");

        // Build summary with two-level grouping
        SummaryResult<Sale, SaleSummary> result = SummaryBuilder
            .compileSummary(SaleSummary.class)
            .createSummaryResult(sales);
        System.out.println("Summary built with " + result.getItems().size() + " rows");

        // Build report
        SalesReportBuilder builder = new SalesReportBuilder();
        builder.setData(result);
        ReportDefinition report = builder.getDefinition();
        System.out.println("Report definition created");

        // Render to PDF
        byte[] pdf = JasperReportsRenderer.renderAsPDF(report);
        System.out.println("PDF rendered: " + pdf.length + " bytes");

        String outputPath = args.length > 0 ? args[0] : "SalesReport.pdf";
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            out.write(pdf);
        }
        System.out.println("PDF written to: " + outputPath);
    }

}
