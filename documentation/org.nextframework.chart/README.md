# org.nextframework.chart

## Overview

Chart data model with multiple rendering backends: Google Charts (JavaScript) or JFreeChart (SVG/PNG).

```java
Chart chart = new Chart(ChartType.BAR, "Sales by Region", "600", "400");
chart.getData().setSeries("Q1", "Q2", "Q3");
chart.getData().addRow("North", 100, 150, 120);
chart.getData().addRow("South", 80, 90, 110);
chart.getData().addRow("East", 120, 100, 140);

// Render as Google Charts JavaScript
String js = (String) ChartRendererFactory.getRendererForOutput("GOOGLE-TOOLS").renderChart(chart);

// Render as SVG
byte[] svg = ChartRendererJFreeChart.renderAsSVG(chart);

// Render as PNG
byte[] png = ChartRendererJFreeChart.renderAsImage(chart);
```

---

## Rendering

### Google Charts (JavaScript)

Generates JavaScript code using the `jsbuilder` module. Include in HTML pages:

```java
Chart chart = new Chart(ChartType.PIE, "Market Share", "600", "400");
chart.setId("chart_div");  // Must match the div id
chart.getData().setSeries("Share");
chart.getData().addRow("Product A", 45);
chart.getData().addRow("Product B", 30);
chart.getData().addRow("Product C", 25);

String javascript = new GoogleToolsChartBuilder(chart).toString();
```

```html
<script src="https://www.gstatic.com/charts/loader.js"></script>
<div id="chart_div"></div>
<script>
    <%= javascript %>
</script>
```

### JFreeChart (SVG/PNG)

Server-side rendering using JFreeChart + Apache Batik:

```java
Chart chart = new Chart(ChartType.BAR, "Sales", "800", "600");
// ... add data ...

// SVG output (vector, scalable)
byte[] svg = ChartRendererJFreeChart.renderAsSVG(chart);

// PNG output (raster image)
byte[] png = ChartRendererJFreeChart.renderAsImage(chart);

// JFreeChart object for further customization
JFreeChart jfree = (JFreeChart) ChartRendererFactory
    .getRendererForOutput("JFREECHART")
    .renderChart(chart);
```

---

## ChartRendererFactory

Pluggable renderer registry. Use when you need to switch renderers dynamically or register custom ones.

```java
// Get renderer by output type
ChartRenderer renderer = ChartRendererFactory.getRendererForOutput("GOOGLE-TOOLS");
ChartRenderer renderer = ChartRendererFactory.getRendererForOutput("JFREECHART");

// Render
Object result = renderer.renderChart(chart);
```

### Built-in Renderers

| Output Type | Renderer | Returns |
|-------------|----------|---------|
| `GOOGLE-TOOLS` | `ChartRendererGoogleTools` | `String` (JavaScript) |
| `JFREECHART` | `ChartRendererJFreeChart` | `JFreeChart` object |

### Custom Renderer

```java
public class MyPdfRenderer implements ChartRenderer {

    public String getOutputType() {
        return "PDF";
    }

    public Object renderChart(Chart chart) {
        // Generate PDF bytes
        return pdfBytes;
    }
}

// Register
ChartRendererFactory.registerRenderer(new MyPdfRenderer());

// Use
byte[] pdf = (byte[]) ChartRendererFactory.getRendererForOutput("PDF").renderChart(chart);
```

---

## Chart Types

```java
ChartType.PIE          // Pie chart
ChartType.BAR          // Horizontal bars
ChartType.COLUMN       // Vertical bars
ChartType.LINE         // Line chart
ChartType.CURVED_LINE  // Smooth line chart
ChartType.AREA         // Area chart
ChartType.SCATTER      // Scatter plot
ChartType.COMBO        // Mixed types (line + column)
```

---

## Building Chart Data

### Basic Structure

```java
Chart chart = new Chart(ChartType.COLUMN, "Monthly Sales");
chart.setDimension("800px", "400px");

ChartData data = chart.getData();
data.setGroupTitle("Month");
data.setSeriesTitle("Revenue");
data.setSeries("Product A", "Product B");  // Define series first
data.addRow("Jan", 100, 80);
data.addRow("Feb", 120, 90);
data.addRow("Mar", 90, 110);
```

### Pie Chart (Single Series)

```java
Chart chart = new Chart(ChartType.PIE, "Market Share");
chart.getData().setSeries("Share");
chart.getData().addRow("Company A", 45);
chart.getData().addRow("Company B", 30);
chart.getData().addRow("Company C", 25);
```

---

## Data Manipulation

```java
ChartData data = chart.getData();

// Swap rows and series
ChartData inverted = data.invert();

// Limit number of series (groups extras into "Others")
data.regroup(5);

// Remove empty entries
data.removeEmptyGroups();
data.removeEmptySeries();
```

---

## Combo Charts

Mix different chart types in one chart:

```java
Chart chart = new Chart(ChartType.COMBO, "Sales vs Target");
chart.getData().setSeries("Sales", "Target");
chart.getData().addRow("Jan", 100, 90);
chart.getData().addRow("Feb", 120, 100);

chart.setComboDefaultChartType(ChartType.COLUMN);  // Sales as columns
chart.setComboSerieType(1, ChartType.LINE);        // Target as line
```

---

## Formatting

```java
// Custom formatters for labels
chart.setGroupFormatter(myPropertyEditor);
chart.setSeriesFormatter(myPropertyEditor);
chart.setValuesFormatter(myPropertyEditor);

// Or use patterns
chart.setGroupPattern("MMM/yyyy");
chart.setValuePattern("#,##0.00");
```

---

## Styling (ChartStyle)

Customize chart appearance via `chart.getStyle()`:

```java
ChartStyle style = chart.getStyle();

style.set3d(true);                              // 3D mode
style.setColors(new Color[]{Color.RED, Color.BLUE});  // Custom colors
style.setLegendPosition(LegendPosition.BOTTOM); // TOP, BOTTOM, RIGHT, NONE, IN
style.setBackgroundColor(Color.WHITE);
style.setLineWidth(2);
style.setPointSize(5);

// Text styling
style.setTitleTextStyle(new TextStyle(Color.BLACK, "Arial", "14px"));
style.setLegendTextStyle(new TextStyle(Color.GRAY));

// Axis visibility
style.setGroupAxisVisible(false);
style.setValueAxisVisible(false);

// Padding (Google Charts)
style.setTopPadding("20");
style.setLeftPadding("50");

// Bar width
style.getBar().setGroupWidth("80%");
```

---

## Time Series (ChartDataTimeSeries)

For time-based data with automatic grouping by time periods:

```java
ChartDataTimeSeries data = new ChartDataTimeSeries("Date", "Values");
data.setSeries("Sales", "Returns");
data.addRow(date1, 100, 10);
data.addRow(date2, 150, 15);
data.addRow(date3, 120, 8);
// ...

// Group by month (aggregates values using SUM by default)
data.groupBy(Calendar.MONTH);

// Or with custom aggregate function
data.groupBy(Calendar.MONTH, new ChartAverageAggregateFunction());

Chart chart = new Chart(ChartType.LINE, "Monthly Sales");
chart.setData(data);
```

---

## Aggregate Functions

For aggregating values when building charts from raw data:

| Function | Description |
|----------|-------------|
| `ChartSumAggregateFunction` | Sum of values |
| `ChartAverageAggregateFunction` | Average |
| `ChartAverageNotNullAggregateFunction` | Average (ignoring nulls) |
| `ChartMinAggregateFunction` | Minimum value |
| `ChartMaxAggregateFunction` | Maximum value |

---

## Integration with Summary

Use the `summary` module to aggregate data, then build charts from the results:

```java
// 1. Define summary with groups and calculated fields
public class SalesSummary extends Summary<Sale> {

    @Group(1)
    public String getRegion() { return getCurrent().getRegion(); }

    @Variable(calculation = SUM, scope = GROUP)
    public Double getTotal() { return getCurrent().getAmount(); }

    @Variable(calculation = SUM, scope = GROUP)
    public Integer getQuantity() { return getCurrent().getQuantity(); }
}

// 2. Run the summary
SummaryResult<Sale, SalesSummary> result = SummaryResult.createFrom(sales, SalesSummary.class);

// 3. Get group summaries and build chart
List<SummaryRow<Sale, SalesSummary>> rows = result.getSummariesForGroup("region");

ChartData data = ChartDataBuilder.buildPropertiesAsSeries(
    rows,
    "summary.region",           // Group property (X-axis)
    "summary.total",            // Series 1 (values)
    "summary.quantity"          // Series 2 (values)
);

Chart chart = new Chart(ChartType.BAR, "Sales by Region");
chart.setData(data);
```

### From List of Objects

Build charts directly from any list of objects:

```java
// Count items by property
ChartData data = ChartDataBuilder.build(sales, "region");

// Group by one property, series by another, count
ChartData data = ChartDataBuilder.build(sales, "region", "product");

// Group, series, and sum a value property
ChartData data = ChartDataBuilder.buildSum(sales, "region", "product", "amount");
```
