# Report XML Configuration

Reports can be defined in XML and loaded via `ReportReader`.

## Basic Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<report name="SalesReport">
    <data>
        <dataSourceProvider type="hibernate" fromClass="com.example.Sale"/>
        <groups>
            <group name="region"/>
            <group name="category" pattern="MM/yyyy"/>
        </groups>
        <filters>
            <filter name="date" requiredFilter="true" preSelectDate="THIS_MONTH"/>
            <filter name="status" fixedCriteria="ACTIVE"/>
        </filters>
        <calculatedFields>
            <calculatedField name="profit" expression="revenue - cost" displayName="Profit"/>
        </calculatedFields>
    </data>
    <layout>
        <fieldDetail name="product"/>
        <fieldDetail name="quantity" aggregate="true" aggregateType="SUM"/>
        <fieldDetail name="amount" aggregate="true" pattern="#,##0.00"/>
    </layout>
    <charts>
        <chart type="pie" title="By Region" groupProperty="region" valueProperty="amount"/>
    </charts>
</report>
```

---

## Element Reference

### `<report>`

Root element.

| Attribute | Required | Description |
|-----------|----------|-------------|
| `name` | Yes | Report identifier |

---

### `<data>`

Container for data configuration.

### `<dataSourceProvider>`

| Attribute | Required | Description |
|-----------|----------|-------------|
| `type` | Yes | Provider type (`hibernate`) |
| `fromClass` | Yes | Fully qualified entity class name |

---

## How Data Retrieval Works

The `<dataSourceProvider>` element configures how data is fetched from your database. Understanding this flow helps you build effective reports.

### Query Building Process

When a report is generated, the framework:

1. **Analyzes the entity class** - Reads properties from `fromClass` (e.g., `com.example.Order`)
2. **Builds the SELECT clause** - Includes properties needed for layout, groups, and charts
3. **Constructs joins** - Automatically joins related entities (e.g., `customer.region` joins the Customer table)
4. **Applies filters** - Adds WHERE clauses from user selections and fixed criteria
5. **Adds grouping** - Applies GROUP BY for report groups
6. **Executes query** - Returns data up to the specified limit

### How Filters Are Applied

Filters translate to HQL/SQL WHERE clauses:

```xml
<filters>
    <!-- User-selected filter: WHERE orderDate BETWEEN :date_begin AND :date_end -->
    <filter name="orderDate" requiredFilter="true" preSelectDate="THIS_MONTH"/>

    <!-- Multi-select: WHERE region IN (:region_values) -->
    <filter name="customer.region" filterSelectMultiple="true"/>

    <!-- Fixed criteria (always applied): WHERE status = 'COMPLETED' -->
    <filter name="status" fixedCriteria="COMPLETED"/>
</filters>
```

**Filter value mapping:**

| Filter Type | Query Generated |
|-------------|-----------------|
| Date range | `property BETWEEN :begin AND :end` |
| Single value | `property = :value` |
| Multi-select | `property IN (:values)` |
| Fixed criteria | `property = 'fixedValue'` |
| Null value | Filter is skipped |

### Relationship Navigation

Use dot notation to navigate entity relationships:

```xml
<dataSourceProvider type="hibernate" fromClass="com.example.Order"/>

<filters>
    <!-- Navigates: Order → Customer → Region -->
    <filter name="customer.region"/>
</filters>

<layout>
    <!-- Navigates: Order → Customer → name -->
    <fieldDetail name="customer.name" label="Customer"/>

    <!-- Navigates: Order → Product → Category → name -->
    <fieldDetail name="product.category.name" label="Category"/>
</layout>
```

The framework automatically:
- Generates appropriate JOINs (typically LEFT JOINs)
- Handles null values in the chain
- Resolves display names from `@DisplayName` annotations

### Transient and Computed Properties

Properties not mapped to database columns are loaded after the main query:

```java
@Entity
public class Order {

    @Transient  // Not in database
    public BigDecimal getProfit() {
        return totalAmount.subtract(cost);
    }

    @Transient
    public String getStatusLabel() {
        return status.getDisplayName();
    }
}
```

These properties:
- Cannot be used in filters (no corresponding column to query)
- Can be used in layout columns
- Are computed in memory after data retrieval

### Performance Considerations

**Limit results:** Always specify a reasonable `maxResults` in `generateReportSpec()`:

```java
// Limit to 10,000 rows to prevent memory issues
ReportSpec spec = generator.generateReportSpec(filters, locale, 10000);
```

**Use indexed columns for filters:** Required filters on indexed date/status columns improve query performance.

**Avoid deep navigation in large datasets:** Each dot in a property path may add a JOIN.

---

### `<group>`

| Attribute | Required | Description |
|-----------|----------|-------------|
| `name` | Yes | Property to group by |
| `pattern` | No | Format pattern (for dates: `MM/yyyy`, `yyyy`, etc.) |

### `<filter>`

| Attribute | Required | Description |
|-----------|----------|-------------|
| `name` | Yes | Property to filter |
| `filterDisplayName` | No | Label shown to user |
| `filterSelectMultiple` | No | Allow multiple selection (`true`/`false`) |
| `preSelectDate` | No | Auto-select date range (see values below) |
| `preSelectEntity` | No | Pre-select entity value |
| `fixedCriteria` | No | Fixed filter value (user cannot change) |
| `requiredFilter` | No | Filter is mandatory (`true`/`false`) |

**preSelectDate values:**
- `TODAY`, `THIS_WEEK`, `THIS_MONTH`, `THIS_YEAR`
- `LAST_7_DAYS`, `LAST_30_DAYS`

### `<calculatedField>`

| Attribute | Required | Description |
|-----------|----------|-------------|
| `name` | Yes | Field identifier |
| `expression` | Yes | Calculation expression (`revenue - cost`) |
| `displayName` | Yes | Column header label |
| `formatAs` | No | `number` or `time` |
| `formatTimeDetail` | No | For time: `minutes`, `hours`, `days` |
| `processors` | No | Custom processor classes |

---

### `<layout>`

Container for columns.

### `<fieldDetail>`

| Attribute | Required | Description |
|-----------|----------|-------------|
| `name` | Yes | Property name |
| `label` | No | Column header (defaults to property display name) |
| `pattern` | No | Format pattern |
| `aggregate` | No | Enable aggregation (`true`/`false`) |
| `aggregateType` | No | `SUM`, `AVERAGE`, `MAX`, `MIN` |

**Pattern formats:**
- Decimal: `#,##0.00`, `#,##0`
- Date: `dd/MM/yyyy`, `MM/yyyy`
- Custom: `cMyFormatterClass` (prefix `c` + class simple name)

---

### `<charts>`

Container for charts.

### `<chart>`

| Attribute | Required | Description |
|-----------|----------|-------------|
| `type` | Yes | `bar`, `line`, `pie`, `area` |
| `title` | No | Chart title |
| `groupProperty` | Yes | Property for X-axis/categories |
| `groupLevel` | No | Aggregation level |
| `groupTitle` | No | X-axis label |
| `seriesProperty` | No | Property for series grouping |
| `seriesTitle` | No | Series label |
| `valueProperty` | Yes | Property for values |
| `valueAggregate` | No | `SUM`, `AVERAGE`, `MAX`, `MIN`, `COUNT` |
| `propertiesAsSeries` | No | Comma-separated properties as series |
| `seriesLimitType` | No | `showall`, `limit`, `group` |
| `ignoreEmptySeriesAndGroups` | No | Skip empty values (`true`/`false`) |

### `<serie>` (child of `<chart>`)

| Attribute | Required | Description |
|-----------|----------|-------------|
| `name` | Yes | Series identifier |
| `label` | No | Series label |
| `valueProperty` | Yes | Property for this series |

---

## Complete Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<report name="MonthlySalesReport">
    <data>
        <dataSourceProvider type="hibernate" fromClass="com.example.domain.Order"/>

        <groups>
            <group name="orderDate" pattern="MM/yyyy"/>
            <group name="customer.region"/>
        </groups>

        <filters>
            <filter name="orderDate"
                    filterDisplayName="Order Date"
                    requiredFilter="true"
                    preSelectDate="THIS_MONTH"/>
            <filter name="customer.region"
                    filterDisplayName="Region"
                    filterSelectMultiple="true"/>
            <filter name="status"
                    fixedCriteria="COMPLETED"/>
        </filters>

        <calculatedFields>
            <calculatedField name="profit"
                             expression="totalAmount - cost"
                             displayName="Profit"
                             formatAs="number"/>
            <calculatedField name="processingTime"
                             expression="completedDate - createdDate"
                             displayName="Processing Time"
                             formatAs="time"
                             formatTimeDetail="hours"/>
        </calculatedFields>
    </data>

    <layout>
        <fieldDetail name="orderNumber"/>
        <fieldDetail name="customer.name" label="Customer"/>
        <fieldDetail name="orderDate" pattern="dd/MM/yyyy"/>
        <fieldDetail name="quantity" aggregate="true" aggregateType="SUM"/>
        <fieldDetail name="totalAmount" aggregate="true" aggregateType="SUM" pattern="#,##0.00"/>
        <fieldDetail name="profit" aggregate="true" aggregateType="SUM" pattern="#,##0.00"/>
    </layout>

    <charts>
        <chart type="bar"
               title="Monthly Sales"
               groupProperty="orderDate"
               valueProperty="totalAmount"
               valueAggregate="SUM"/>
        <chart type="pie"
               title="Sales by Region"
               groupProperty="customer.region"
               valueProperty="totalAmount"
               valueAggregate="SUM"/>
    </charts>
</report>
```

---

## Loading from XML

```java
// From string
String xml = "...";
ReportReader reader = new ReportReader(xml);
ReportElement report = reader.getReportElement();

// From input stream
InputStream in = getClass().getResourceAsStream("/reports/sales.xml");
ReportReader reader = new ReportReader(in);
ReportElement report = reader.getReportElement();

// Generate report
ReportGenerator generator = new ReportGenerator(report);
ReportSpec spec = generator.generateReportSpec(filterMap, locale, maxResults);
```
