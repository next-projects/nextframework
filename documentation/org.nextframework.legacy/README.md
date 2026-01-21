# org.nextframework.legacy

## Overview

Backward compatibility module providing legacy APIs for JasperReports-based PDF generation and RTF template processing. This module maintains support for older application code while the framework evolves.

**Note:** This module is maintained for compatibility. For new projects, consider using `org.nextframework.report` and `org.nextframework.report.generator` instead.

---

## Features

| Feature | Description | Status |
|---------|-------------|--------|
| JasperReports PDF | Generate PDFs from .jasper templates | Active |
| RTF Templates | Generate RTF documents from templates | Deprecated |

---

## JasperReports Integration

### ReportController

Base controller for generating PDF reports using JasperReports (.jasper files).

```java
@Controller(path = "/admin/salesReport")
public class SalesReport extends ReportController<SalesFilter> {

    @Autowired
    private SalesDAO salesDAO;

    @Override
    public IReport createReport(WebRequestContext request, SalesFilter filter) {
        List<Sale> sales = salesDAO.findByFilter(filter);

        Report report = new Report("sales", sales);
        report.addParameter("title", "Sales Report");
        report.addParameter("generatedAt", new Date());

        return report;
    }
}
```

### Naming Convention

Controller class name must end with `Report` suffix:
- `SalesReport` → template name: `sales`
- `MonthlyInventoryReport` → template name: `monthlyInventory`

Or set the name explicitly:

```java
public SalesReport() {
    setName("custom-report-name");
}
```

### Template Location

Templates must be placed in:
```
/WEB-INF/relatorio/{name}.jasper
```

Example: `SalesReport` → `/WEB-INF/relatorio/sales.jasper`

### Report Class

Model for report data:

```java
// Simple report with collection data
Report report = new Report("sales", salesList);

// With parameters
Map<String, Object> params = new HashMap<>();
params.put("title", "Monthly Report");
params.put("date", new Date());
Report report = new Report("sales", params, salesList);

// Supported data sources
new Report("name", collection);      // Collection
new Report("name", array);           // Object[]
new Report("name", iterator);        // Iterator
new Report("name", resultSet);       // ResultSet
new Report("name", jrDataSource);    // JRDataSource
```

### Subreports

```java
Report mainReport = new Report("invoice", invoiceData);

Report itemsSubreport = new Report("invoice-items", invoice.getItems());
mainReport.addSubReport("ITEMS_SUBREPORT", itemsSubreport);

return mainReport;
```

### Actions

| Action | URL | Description |
|--------|-----|-------------|
| `doFilter` (default) | `/admin/salesReport` | Display filter form |
| `doGenerate` | `/admin/salesReport?action=doGenerate` | Generate PDF |

### Filter View

Create filter JSP at:
```
/WEB-INF/jsp/{module}/relatorio/{name}.jsp
```

Example: `/WEB-INF/jsp/admin/relatorio/sales.jsp`

```jsp
<t:simplePanel title="Sales Report">
    <t:property name="filter.startDate" />
    <t:property name="filter.endDate" />
    <t:property name="filter.category" />
    <n:submit action="doGenerate">Generate PDF</n:submit>
</t:simplePanel>
```

### Programmatic Generation

Generate PDFs without a controller:

```java
Report report = new Report("invoice", invoiceData);
report.addParameter("customer", customer.getName());

byte[] pdfBytes = LegacyReportUtils.getReportGenerator().toPdf(report);

// Save or send
Files.write(Paths.get("invoice.pdf"), pdfBytes);
```

**Note:** `LegacyReportUtils` requires web context (ServletContext).

---

## RTF Templates (Deprecated)

**Warning:** RTF generation is deprecated. Use for legacy code maintenance only.

### RTFController

```java
@Deprecated
@Controller(path = "/admin/contractRTF")
public class ContractRTF extends RTFController<ContractFilter> {

    @Override
    public RTF createRTF(WebRequestContext request, ContractFilter filter) {
        RTF rtf = new RTF("contract");

        Map<String, String> params = new HashMap<>();
        params.put("customerName", filter.getCustomer().getName());
        params.put("contractDate", formatDate(new Date()));
        params.put("value", formatCurrency(filter.getValue()));
        rtf.setParameterMap(params);

        return rtf;
    }
}
```

### Template Location

```
/WEB-INF/rtf/{name}.rtf
```

### Template Syntax

RTF templates use simple tag replacement:

```rtf
{\rtf1
Contract for <customerName>

Date: <contractDate>
Value: <value>
}
```

Tags `<tagName>` are replaced with values from the parameter map.

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  ReportController / RTFController                           │
│  (extends ResourceSenderController)                         │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  createReport() / createRTF()                               │
│  Returns IReport or RTF model                               │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  LegacyReportUtils / LegacyRtfUtils                         │
│  Static singleton access to generators                      │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  ReportGenerator / RTFGenerator                             │
│  Generates PDF bytes or RTF bytes                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  Resource (PDF or RTF)                                      │
│  Sent to browser for download                               │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Classes

### Report Package

| Class | Description |
|-------|-------------|
| `ReportController<FILTER>` | Base controller for PDF reports |
| `Report` | Report model with data and parameters |
| `IReport` | Report interface |
| `ReportGenerator` | Converts IReport to PDF bytes |
| `LegacyReportUtils` | Static access to ReportGenerator |
| `ReportNameResolver` | Resolves template paths |

### RTF Package (Deprecated)

| Class | Description |
|-------|-------------|
| `RTFController<FILTER>` | Base controller for RTF (deprecated) |
| `RTF` | RTF model with parameters |
| `RTFGenerator` | Template substitution engine |
| `LegacyRtfUtils` | Static access to RTFGenerator |

---

## Migration Guide

### From Legacy to New Report Module

**Legacy approach:**
```java
public class SalesReport extends ReportController<SalesFilter> {
    @Override
    public IReport createReport(WebRequestContext request, SalesFilter filter) {
        return new Report("sales", salesDAO.find(filter));
    }
}
```

**New approach (org.nextframework.report.generator):**
```java
@Controller(path = "/admin/sales")
public class SalesReportController extends ReportDesignController<Sale, SalesFilter> {
    // Uses XML-based report definitions with dynamic UI
}
```

See [org.nextframework.report.generator](../org.nextframework.report.generator/README.md) for the modern approach.

---

## Dependencies

- `org.nextframework.controller` - ResourceSenderController base class
- `org.nextframework.report` - Report infrastructure
- JasperReports library (external)
