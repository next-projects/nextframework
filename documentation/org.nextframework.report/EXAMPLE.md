# Sales Report Example

This example demonstrates how to use the Report module to generate a PDF report with:
- Entity class for data
- Summary class with two-level grouping and scoped variables for subtotals
- Report builder with chart (first page only) and automatic sums
- PDF rendering

## Files

- [Sale.java](Sale.java) - Entity class (data model)
- [SalesReportExample.java](SalesReportExample.java) - Main example with Summary and Report Builder
- [SalesReport.pdf](SalesReport.pdf) - Generated PDF output

## Running the Example

```bash
cd documentation/org.nextframework.report
bash run-example.sh
```

## Code Structure

### 1. Entity Class (Sale.java)

A simple POJO representing a sale:

```java
public class Sale {
    private String region;
    private String product;
    private String description;
    private Integer quantity;
    private BigDecimal amount;
    // constructor and getters...
}
```

**Important:** The entity class must be a public top-level class for Summary runtime compilation to work.

### 2. Summary Class with Two-Level Grouping

Defines how data is grouped and which variables are summed:

```java
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

    // Base accessor
    public Integer getQuantity() {
        return getCurrent().getQuantity();
    }

    // Scoped variables for subtotals at each group level
    @Variable(calculation = CalculationType.SUM, scopeGroup = "report")
    public Integer getQuantityReport() { return getQuantity(); }

    @Variable(calculation = CalculationType.SUM, scopeGroup = "region")
    public Integer getQuantityRegion() { return getQuantity(); }

    @Variable(calculation = CalculationType.SUM, scopeGroup = "product")
    public Integer getQuantityProduct() { return getQuantity(); }

    // Same pattern for amount with CalculationType.SUM...
}
```

**Key concept:** For `fieldSummary("quantity")` to generate subtotals, you need scoped variables with `calculation = CalculationType.SUM`:
- `getQuantityReport()` with `@Variable(calculation = SUM, scopeGroup = "report")` - for grand total
- `getQuantityRegion()` with `@Variable(calculation = SUM, scopeGroup = "region")` - for region subtotals
- `getQuantityProduct()` with `@Variable(calculation = SUM, scopeGroup = "product")` - for product subtotals

### 3. Report Builder with Chart

Defines the report layout:

```java
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
        // ...
        chart.setData(data);
        return chart;
    }

    @Override
    public int[] getColumnConfig() {
        return new int[] { 200, 80, 120 };
    }
}
```

**Note:** Groups (`@Group`) are automatically shown in group headers, so you only need `fieldDetail` for non-group detail fields.

### 4. Main Method

Ties everything together:

```java
// Create sample data
List<Sale> sales = createSampleData();

// Build summary (groups data)
SummaryResult<Sale, SaleSummary> result = SummaryBuilder
    .compileSummary(SaleSummary.class)
    .createSummaryResult(sales);

// Build report
SalesReportBuilder builder = new SalesReportBuilder();
builder.setData(result);
ReportDefinition report = builder.getDefinition();

// Render to PDF
byte[] pdf = JasperReportsRenderer.renderAsPDF(report);
```

## Generated Report

The [generated PDF](SalesReport.pdf) includes:
- Title and subtitle
- Pie chart showing sales by region (first page only)
- **Group headers** for region and product
- **Detail rows** with description, quantity, and amount
- **Subtotals** when product changes (within region)
- **Subtotals** when region changes
- **Grand totals** at the end
- Page numbers and generation date

## Key Sections

| Section | Method | Shows On |
|---------|--------|----------|
| Title | `setTitle()` | First page |
| First Page Header | `getSectionFirstPageHeader()` | First page only |
| Page Header | `getSectionPageHeader()` | Every page |
| Group Headers | Auto from `@Group` | When group value changes |
| Detail | `fieldDetail()` | Every row |
| Group Footers | Auto from `fieldSummary()` | When group ends |
| Summary | `fieldSummary()` | End of report |

## fieldDetail vs fieldSummary

| Method | Header | Detail Row | Group Footers | Report Total |
|--------|--------|------------|---------------|--------------|
| `fieldDetail("description")` | ✓ | ✓ | - | - |
| `fieldSummary("quantity")` | ✓ | ✓ | ✓ (all levels) | ✓ |

Use `fieldDetail` for text columns, `fieldSummary` for numeric columns that need totals.
