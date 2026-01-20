# org.nextframework.report

## Overview

Report definition and rendering framework. Define reports programmatically with a fluent builder API, render to PDF, HTML, or Excel via JasperReports.

The generated report includes out of the box:
- Title and subtitle
- Logo (customizable, default provided)
- Column headers with styling
- Page numbers ("Page X of Y")
- Generation date/time in footer
- Group headers with alternating styles
- Proper formatting for dates, numbers, currency

```java
// Report builder
public class SalesReportBuilder extends LayoutReportBuilder {

    @Override
    protected void layoutReport() {
        setTitle("Sales Report");
        setSubtitle("Q1 2024");

        fieldDetail("product");   // Label: "Product Name"
        fieldDetail("quantity");  // Label: "quantity", right-aligned
        fieldDetail("total");     // Label: "total", right-aligned, formatted
    }
}

// Entity class
public class Sale {

    @DisplayName("Product Name")
    public String getProduct() { return product; }

    public Integer getQuantity() { return quantity; }

    public BigDecimal getTotal() { return total; }
}

// Build and render
SalesReportBuilder builder = new SalesReportBuilder();
builder.setData(salesList);
ReportDefinition report = builder.getDefinition();

byte[] pdf = (byte[]) ReportRendererFactory.getRenderer("PDF").renderReport(report);
String html = (String) ReportRendererFactory.getRenderer("HTML").renderReport(report);
```

Labels, alignment, and formatting are auto-detected from bean properties.

---

## LayoutReportBuilder

Extend for column-based reports:

```java
public class ProductReportBuilder extends LayoutReportBuilder {

    @Override
    protected void layoutReport() {
        fieldDetail("code");
        fieldDetail("name");
        fieldDetail("price");
        fieldDetail("stockQuantity");
    }
}
```

### Column Builder

Each `fieldDetail()` / `fieldSummary()` call creates a new column. For more control, use `column()` explicitly:

```java
@Override
protected void layoutReport() {
    column().fieldDetail("name");                          // Default alignment
    column(ReportAlignment.RIGHT).fieldDetail("price");    // Right-aligned
    column().fieldDetail("code").fieldSummary("total");    // Chain multiple fields
}
```

### Custom Column Widths

Override `getColumnConfig()` if needed:

```java
@Override
public int[] getColumnConfig() {
    return new int[] { 60, 200, 80, 60 };
}
```

### Setting Data

```java
ProductReportBuilder builder = new ProductReportBuilder();
builder.setData(productList);  // Raw list
// or
builder.setData(summaryResult);  // From Summary module for aggregations
```

---

## Report Elements

### Labels and Fields

```java
label("Static Text");           // Static label
field("row.productName");        // Field from current row
field("summary.total");          // Field from summary
field("param.reportDate");       // Field from parameters
```

### Grids

Organize elements in columns:

```java
grid(
    label("Name:"), field("row.name"),
    label("Date:"), field("row.date")
);
```

### Charts

Embed charts in reports. Charts are created from report data automatically:

```java
public class SalesReportBuilder extends LayoutReportBuilder {

    @Override
    protected void layoutReport() {
        setTitle("Sales Report");

        // Add chart at the top of the report
        addItem(chart(ChartType.PIE, "Sales by Region", "region", null, "amount"));

        // Then the data columns
        fieldDetail("region");
        fieldDetail("product");
        fieldSummary("totalAmount");
    }
}
```

Chart methods available:

```java
chart(ChartType.PIE, "Title", "groupProperty");                          // Simple pie
chart(ChartType.BAR, "Title", "groupProperty", "seriesProperty", "valueProperty");
chartPropertiesAsSeries(ChartType.BAR, "Title", "groupProperty", "prop1", "prop2");
```

The `chart()` methods use the same data passed to `setData()`, automatically aggregating values.

### Using an Existing Chart Object

You can also pass a `Chart` object from `org.nextframework.chart`:

```java
// Create chart with full control
Chart myChart = new Chart(ChartType.BAR, "Custom Chart", "500", "300");
myChart.getData().setSeries("Sales", "Returns");
myChart.getData().addRow("Jan", 100, 10);
myChart.getData().addRow("Feb", 150, 15);
myChart.getStyle().set3d(true);

// Add to report
addItem(new ReportChart(myChart));
```

This gives you access to all `Chart` styling and data manipulation features from the chart module.

---

## Low-Level API

For more control, access `ReportDefinition` directly via `getDefinition()`:

```java
// Add items to specific sections
getDefinition().addTitleItem(element);
getDefinition().addItem(element, ReportSectionType.SUMARY, 0);
getDefinition().addItem(element, section, column);

// Access sections directly
ReportSection detail = getDefinition().getSectionDetail();
ReportSection summary = getDefinition().getSectionSummary();
```

### Subreports

Nest reports:

```java
subreport(new DetailReportBuilder());
```

---

## Styling

Fluent API for element styling:

```java
ReportLabel title = label("Sales Report");
title.getStyle()
    .setAlignment(ReportAlignment.CENTER)
    .setForegroundColor(Color.BLUE)
    .setBackgroundColor(Color.LIGHT_GRAY)
    .setBorderBottom(new Border(1, Color.BLACK))
    .setPaddingTop(10)
    .setPaddingBottom(10);
```

### Style Properties

| Property | Description |
|----------|-------------|
| `alignment` | LEFT, CENTER, RIGHT |
| `foregroundColor` | Text color |
| `backgroundColor` | Background color |
| `border*` | Top, Bottom, Left, Right borders |
| `padding*` | Top, Bottom, Left, Right padding |

---

## Report Sections

Reports are organized into sections:

| Section | Description |
|---------|-------------|
| `TITLE` | Report header (once) |
| `FIRST_PAGE_HEADER` | Header for first page only |
| `PAGE_HEADER` | Top of each page |
| `PAGE_FOOTER` | Bottom of each page |
| `COLUMN_HEADER` | Column headers |
| `DETAIL` | Data rows |
| `GROUP_HEADER` | Group start |
| `GROUP_FOOTER` | Group end (subtotals) |
| `SUMARY` | Report summary (totals) |

Access sections via `getDefinition()`:

```java
getDefinition().getSectionFirstPageHeader()  // First page only (e.g., for charts)
getDefinition().getSectionPageHeader()       // Every page
getDefinition().getSectionDetail()           // Data rows
```

---

## Grouping

Group data with headers and footers:

```java
@Override
protected void layoutReport() {
    // Group by category
    startGroup("category");

    column().label("Product").fieldDetail("name");
    column().label("Price").fieldDetail("price");

    // Group footer with subtotal
    groupFooter().label("Subtotal:").field("summary.categoryTotal");
}
```

---

## Rendering

Use `ReportRenderer` to generate output:

```java
ReportDefinition report = builder.getDefinition();

// Get renderer for desired format
ReportRenderer renderer = ReportRendererFactory.getRenderer("PDF");
byte[] pdf = (byte[]) renderer.renderReport(report);

// Or HTML
ReportRenderer htmlRenderer = ReportRendererFactory.getRenderer("HTML");
String html = (String) htmlRenderer.renderReport(report);
```

### Output Formats

| Format | Renderer Output |
|--------|-----------------|
| `PDF` | byte[] |
| `HTML` | String |
| `EXCEL` | byte[] |

---

## Integration with Summary

The report module integrates deeply with the `summary` module. When you pass a `SummaryResult`, the report builder automatically:
- Creates groups based on `@Group` annotations
- Generates subtotals for each group level
- Generates grand totals for the entire report

### Complete Example

```java
// 1. Entity class
public class Sale {

    @DisplayName("Region")
    public String getRegion() { return region; }

    @DisplayName("Product")
    public String getProduct() { return product; }

    @DisplayName("Amount")
    public BigDecimal getAmount() { return amount; }

    @DisplayName("Quantity")
    public Integer getQuantity() { return quantity; }
}

// 2. Summary class - defines groups and calculations
public class SaleSummary extends Summary<Sale> {

    @Group(1)  // First grouping level
    public String getRegion() { return getRow().getRegion(); }

    @Group(2)  // Second grouping level (nested)
    public String getProduct() { return getRow().getProduct(); }

    @Variable(calculation = SUM, scope = GROUP)
    public BigDecimal getTotalAmount() { return getRow().getAmount(); }

    @Variable(calculation = SUM, scope = GROUP)
    public Integer getTotalQuantity() { return getRow().getQuantity(); }

    @Variable(calculation = AVERAGE, scope = GROUP)
    public BigDecimal getAverageAmount() { return getRow().getAmount(); }
}

// 3. Report builder - uses summary for groups and totals
public class SalesReportBuilder extends LayoutReportBuilder {

    @Override
    protected void layoutReport() {
        setTitle("Sales Report");
        setSubtitle("By Region and Product");

        // Each call creates a new column
        // fieldDetail: shows row data only
        // fieldSummary: shows row data + subtotals for each group + grand total

        fieldDetail("region");
        fieldDetail("product");
        fieldSummary("totalQuantity");  // Detail + subtotals + grand total
        fieldSummary("totalAmount");    // Detail + subtotals + grand total
    }
}

// 4. Build and render
List<Sale> sales = salesDAO.findAll();
SummaryResult<Sale, SaleSummary> result = SummaryResult.createFrom(sales, SaleSummary.class);

SalesReportBuilder builder = new SalesReportBuilder();
builder.setData(result);  // Groups are auto-configured from Summary

byte[] pdf = (byte[]) ReportRendererFactory.getRenderer("PDF").renderReport(builder.getDefinition());
```

### What Gets Generated

| Method | Header | Detail Row | Group Footers | Report Total |
|--------|--------|------------|---------------|--------------|
| `fieldDetail("region")` | ✓ | ✓ | - | - |
| `fieldSummary("totalAmount")` | ✓ | ✓ | ✓ (all levels) | ✓ |

The report will display:
- **Detail rows**: Each sale with region, product, quantity, amount
- **Group 2 footer** (Product): Subtotals when product changes
- **Group 1 footer** (Region): Subtotals when region changes
- **Report summary**: Grand totals at the end

All formatting, alignment, and group styling are applied automatically.

---

## Complete Example

See [EXAMPLE.md](EXAMPLE.md) for a complete working example with:
- Entity class ([Sale.java](Sale.java))
- Summary and Report Builder ([SalesReportExample.java](SalesReportExample.java))
- Generated PDF ([SalesReport.pdf](SalesReport.pdf))

Run the example:
```bash
cd documentation/org.nextframework.report
bash run-example.sh
```
