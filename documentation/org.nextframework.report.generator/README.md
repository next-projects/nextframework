# org.nextframework.report.generator

## Overview

Dynamic report generation from entity classes. Define reports in XML with filters, grouping, aggregation, and charts - the framework handles query building, data retrieval, and rendering.

```xml
<report name="SalesReport">
    <data>
        <dataSourceProvider type="hibernate" fromClass="com.example.Sale"/>
        <groups>
            <group name="region"/>
            <group name="product"/>
        </groups>
        <filters>
            <filter name="date" requiredFilter="true" preSelectDate="THIS_MONTH"/>
            <filter name="region" filterSelectMultiple="true"/>
        </filters>
    </data>
    <layout>
        <fieldDetail name="description"/>
        <fieldDetail name="quantity" aggregate="true" aggregateType="SUM"/>
        <fieldDetail name="amount" aggregate="true" aggregateType="SUM" pattern="#,##0.00"/>
    </layout>
    <charts>
        <chart type="pie" title="Sales by Region" groupProperty="region"
               valueProperty="amount" valueAggregate="SUM"/>
    </charts>
</report>
```

```java
// Load and generate report from XML
ReportReader reader = new ReportReader(xmlString);
ReportElement report = reader.getReportElement();

ReportGenerator generator = new ReportGenerator(report);
ReportSpec spec = generator.generateReportSpec(filterValues, locale, 10000);

byte[] pdf = JasperReportsRenderer.renderAsPDF(spec.getReportBuilder().getDefinition());
```

See [XML-FORMAT.md](XML-FORMAT.md) for complete XML reference.

### Programmatic Definition

Build reports entirely in Java:

```java
// Create report
ReportElement report = new ReportElement("SalesReport");
report.setReportTitle("Sales Report");

// Configure data source
DataElement data = new DataElement();
data.setMainType(Sale.class);

// Add groups
GroupElement regionGroup = new GroupElement();
regionGroup.setName("region");
data.getGroups().add(regionGroup);

// Add filters
FilterElement dateFilter = new FilterElement();
dateFilter.setName("date");
dateFilter.setRequiredFilter("true");
dateFilter.setPreSelectDate("THIS_MONTH");
data.getFilters().add(dateFilter);

report.setData(data);

// Define layout columns
LayoutElement layout = new LayoutElement();
layout.getItems().add(new FieldDetailElement("description"));

FieldDetailElement quantity = new FieldDetailElement("quantity");
quantity.setAggregate("true");
quantity.setAggregateType("SUM");
layout.getItems().add(quantity);

FieldDetailElement amount = new FieldDetailElement("amount");
amount.setAggregate("true");
amount.setAggregateType("SUM");
amount.setPattern("#,##0.00");
layout.getItems().add(amount);

report.setLayout(layout);

// Add chart
ChartElement chart = new ChartElement();
chart.setType("pie");
chart.setTitle("Sales by Region");
chart.setGroupProperty("region");
chart.setValueProperty("amount");
chart.setValueAggregate("SUM");

ChartsElement charts = new ChartsElement();
charts.getItems().add(chart);
report.setCharts(charts);

// Generate report
ReportGenerator generator = new ReportGenerator(report);
ReportSpec spec = generator.generateReportSpec(filterValues, locale, 10000);
byte[] pdf = JasperReportsRenderer.renderAsPDF(spec.getReportBuilder().getDefinition());
```

---

## Key Concepts

### ReportElement

The root container defining a complete report:

```java
ReportElement report = new ReportElement("MyReport");
report.setReportTitle("My Report Title");
report.setData(dataElement);       // Data source, filters, groups
report.setLayout(layoutElement);   // Columns and formatting
report.setCharts(chartsElement);   // Optional charts
```

### DataElement

Configures data retrieval:

```java
DataElement data = new DataElement();
data.setMainType(Order.class);           // Entity class
data.getFilters().add(...);              // User filters
data.getGroups().add(...);               // Grouping levels
data.getCalculatedFields().add(...);     // Computed fields
```

### LayoutElement

Defines report columns:

```java
LayoutElement layout = new LayoutElement();
layout.getItems().add(new FieldDetailElement("customerName"));
layout.getItems().add(new FieldDetailElement("orderDate"));

// With aggregation
FieldDetailElement amount = new FieldDetailElement("totalAmount");
amount.setAggregate("true");
amount.setAggregateType("SUM");  // SUM, AVERAGE, MAX, MIN
layout.getItems().add(amount);
```

---

## Filters

### FilterElement

```java
FilterElement filter = new FilterElement(
    "propertyName",      // Entity property
    "Display Name",      // Label shown to user
    "true",              // selectMultiple
    "THIS_MONTH",        // preSelectDate (auto-filter)
    null,                // preSelectEntity
    null,                // fixedCriteria
    "true"               // required
);
```

### Date Auto-Filters

Pre-select date ranges automatically:

| Value | Description |
|-------|-------------|
| `TODAY` | Current day |
| `THIS_WEEK` | Current week |
| `THIS_MONTH` | Current month |
| `THIS_YEAR` | Current year |
| `LAST_7_DAYS` | Past 7 days |
| `LAST_30_DAYS` | Past 30 days |

### Fixed Criteria

Apply filters that users cannot change:

```java
filter.setFixedCriteria("ACTIVE");  // Always filter by status=ACTIVE
```

---

## Grouping

### GroupElement

```java
GroupElement group = new GroupElement();
group.setName("category");
group.setPattern("MM/yyyy");  // For date grouping
data.getGroups().add(group);
```

Groups create:
- Group headers in report
- Subtotals for aggregated fields
- Chart grouping levels

### Date Patterns

| Pattern | Example Output |
|---------|----------------|
| `MM/yyyy` | 01/2024 |
| `yyyy` | 2024 |
| `dd/MM/yyyy` | 15/01/2024 |

---

## Calculated Fields

Create computed columns:

```java
CalculatedFieldElement calc = new CalculatedFieldElement();
calc.setName("profit");
calc.setDisplayName("Profit");
calc.setExpression("revenue - cost");
calc.setFormatAs("number");  // or "time"
data.getCalculatedFields().add(calc);
```

### Time Calculations

For duration fields:

```java
calc.setExpression("endDate - startDate");
calc.setFormatAs("time");
calc.setFormatTimeDetail("hours");  // minutes, hours, days
```

---

## Charts

### ChartElement

```java
ChartElement chart = new ChartElement();
chart.setType("bar");           // bar, line, pie, area
chart.setTitle("Sales by Region");
chart.setGroupProperty("region");
chart.setValueProperty("amount");
chart.setValueAggregate("SUM");

ChartsElement charts = new ChartsElement();
charts.getItems().add(chart);
report.setCharts(charts);
```

### Chart with Series

```java
chart.setSeriesProperty("product");  // Creates multiple series
chart.setSeriesLimitType("limit");   // showall, limit, group
```

### Explicit Series (ChartSerieElement)

Define series manually:

```java
ChartElement chart = new ChartElement();
chart.setType("bar");
chart.setGroupProperty("month");

ChartSerieElement series1 = new ChartSerieElement();
series1.setName("revenue");
series1.setLabel("Revenue");
series1.setValueProperty("totalRevenue");
chart.getSeries().add(series1);

ChartSerieElement series2 = new ChartSerieElement();
series2.setName("cost");
series2.setLabel("Cost");
series2.setValueProperty("totalCost");
chart.getSeries().add(series2);
```

---

## Annotations

Mark entities and fields for report generation.

### @ReportEntity

Marks an entity class as available for report generation:

```java
@ReportEntity
public class Order {
    // ...
}
```

### @ReportField

Configures how a property appears in reports:

```java
public class Order {

    @ReportField(filter = true, requiredFilter = true)
    public Date getOrderDate() { return orderDate; }

    @ReportField(column = true, suggestedWidth = 150)
    public String getCustomerName() { return customer.getName(); }

    @ReportField(filter = true, column = false)
    public Status getStatus() { return status; }

    @ReportField(usingFields = {"quantity", "unitPrice"})
    public BigDecimal getTotal() { return quantity.multiply(unitPrice); }
}
```

| Attribute | Description |
|-----------|-------------|
| `filter` | Show as filter option |
| `requiredFilter` | Filter is mandatory |
| `column` | Show as report column |
| `suggestedWidth` | Column width hint |
| `usingFields` | Dependencies for calculated fields |

### @ExtendBean

Adds computed properties to entities via service methods:

```java
@Service
public class OrderService {

    @ExtendBean(cacheResult = true)
    public BigDecimal getProfit(Order order) {
        return order.getRevenue().subtract(order.getCost());
    }
}
```

The method becomes available as `profit` property on `Order` in reports.

---

## Report Generation

### ReportGenerator

```java
ReportGenerator generator = new ReportGenerator(reportElement);

// With progress monitoring
ReportGenerator generator = new ReportGenerator(reportElement, progressMonitor);

// Generate report spec
Map<String, Object> filters = new HashMap<>();
filters.put("date_begin", startDate);
filters.put("date_end", endDate);
filters.put("region", selectedRegion);

ReportSpec spec = generator.generateReportSpec(filters, locale, 10000);

// Access results
IReportBuilder builder = spec.getReportBuilder();
DynamicSummary summary = spec.getSummary();
```

### Rendering

```java
// PDF
byte[] pdf = JasperReportsRenderer.renderAsPDF(builder.getDefinition());

// HTML
String html = (String) ReportRendererFactory.getRenderer("HTML")
    .renderReport(builder.getDefinition());
```

---

## Data Source Provider

The module uses `DataSourceProvider` SPI for data retrieval:

```java
public interface DataSourceProvider {
    List<?> getResult(ReportElement element,
                      Map<String, Object> filters,
                      Map<String, Object> fixedCriteria,
                      int limit);
}
```

Default implementation: `HibernateDataSourceProvider`
- Builds HQL queries from filters and groups
- Handles entity relationships automatically
- Supports transient/computed properties

---

## Custom Formatting

### FieldFormatter

Custom formatting for field values:

```java
public class CurrencyFormatter implements FieldFormatter {

    @Override
    public String format(Object value) {
        if (value == null) return "";
        return NumberFormat.getCurrencyInstance().format(value);
    }

    @Override
    public ReportAlignment getAlignment() {
        return ReportAlignment.RIGHT;
    }
}
```

Use in layout with `c` prefix + class name:

```java
FieldDetailElement field = new FieldDetailElement("amount");
field.setPattern("cCurrencyFormatter");  // c + simple class name
```

### FieldProcessor

Process numeric values in calculated fields:

```java
public class PercentageProcessor implements FieldProcessor {

    @Override
    public Double process(Number value) {
        return value.doubleValue() * 100;
    }
}
```

Use in calculated field expression via `processors` attribute.

---

## Dependencies

- `org.nextframework.report` - Report rendering
- `org.nextframework.summary` - Data aggregation
- `org.nextframework.persistence` - Data access
- `org.nextframework.stjs` - JavaScript generation (for UI)
