# Report Designer Controller

For building a complete report designer UI, extend `ReportDesignController`. The UI is **built-in** - all views are bundled in the module and loaded automatically via `ClasspathModelAndView`.

This provides:

- Entity selection screen (lists classes with `@ReportEntity`)
- Property picker for columns, filters, and groups
- Visual report designer with drag-and-drop
- Filter form for end users
- PDF and HTML generation with progress monitoring

---

## Setup

### 1. Create an entity to store report configurations

Implement `ReportDesignCustomBean` to store the report XML:

```java
@Entity
public class CustomReport implements ReportDesignCustomBean {

    @Id @GeneratedValue
    private Integer id;

    @Lob
    private String xml;

    private Boolean reportPublic;

    private String name;        // Custom field
    private String category;    // Custom field

    // Implement interface methods
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getXml() { return xml; }
    public void setXml(String xml) { this.xml = xml; }
    public Boolean getReportPublic() { return reportPublic; }
    public void setReportPublic(Boolean reportPublic) { this.reportPublic = reportPublic; }

    // Getters/setters for custom fields...
}
```

### 2. Extend ReportDesignController

Implement the 4 required abstract methods:

```java
@Controller(path = "/report")
public class ReportController extends ReportDesignController<CustomReport> {

    @Autowired
    private CustomReportDAO reportDAO;

    @Override
    public String getPathForReportCrud() {
        return "/report";  // Must match @Controller path
    }

    @Override
    protected int getMaxResults() {
        return 10000;
    }

    @Override
    public void persistReport(ReportDesignModel model,
                              ReportElement reportElement,
                              CustomReport customBean) {
        reportDAO.saveOrUpdate(customBean);
    }

    @Override
    protected CustomReport loadPersistedReportById(Integer id) {
        return reportDAO.findById(id);
    }
}
```

| Method | Description |
|--------|-------------|
| `getPathForReportCrud()` | Must match `@Controller(path)` - used for links/redirects |
| `getMaxResults()` | Maximum rows to fetch from database |
| `persistReport(...)` | Save report configuration to database |
| `loadPersistedReportById(...)` | Load report configuration by ID |

---

## Built-in Views

The following JSP views are bundled in the module (no need to create them):

| View | Description |
|------|-------------|
| `design1.jsp` | Entity selection - user picks which entity to report on |
| `design2.jsp` | Property picker - user selects which fields to include |
| `design3.jsp` | Visual designer - configure groups, filters, columns, charts |
| `filter.jsp` | Filter form - end users fill in filter values before running |
| `editXML.jsp` | XML editor - direct editing of report configuration |

Views are loaded from classpath via:

```java
return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design1");
```

---

## Available Actions

The controller provides these endpoints:

| Action | Description |
|--------|-------------|
| `index` | Entity selection screen |
| `selectProperties` | Property picker |
| `designReport` | Visual report designer |
| `showFilterView` | Filter form for users |
| `downloadPdf` | Generate and download PDF |
| `showResults` | Generate and show HTML |

---

## User Flow

```
1. index
   User selects an entity class (e.g., Order, Sale)
        │
        ▼
2. selectProperties
   User picks which properties to include
        │
        ▼
3. designReport
   User configures:
   - Groups (how to organize rows)
   - Filters (what users can filter by)
   - Columns (what to show in the table)
   - Charts (optional visualizations)
        │
        ▼
4. saveReport
   XML configuration is persisted
        │
        ▼
5. showFilterView
   End users see filter form, fill in values
        │
        ▼
6. downloadPdf / showResults
   Report is generated and displayed
```

---

## Optional Overrides

These methods have default implementations but can be overridden for customization:

### getFilterActions

Default returns PDF only. Override to add export formats:

```java
@Override
protected Map<String, String> getFilterActions(ReportDesignModel model) {
    Map<String, String> actions = super.getFilterActions(model);
    actions.put("downloadExcel", "Export Excel");
    return actions;
}

// Then create the action method
@OnErrors("showFilterView")
@Input("showFilterView")
public ModelAndView downloadExcel(WebRequestContext request, ReportDesignModel model) throws Exception {
    ReportDesignTask task = new ReportDesignTask() {
        @Override
        public Object convertResults(ReportDefinition definition, IProgressMonitor monitor) {
            // definition contains the report data and structure
            // - definition.getReportName()
            // - definition.getData() -> report rows
            // - definition.getColumns() -> column definitions
            // Use your preferred Excel library (Apache POI, etc.) to generate bytes
            byte[] excelBytes = myExcelService.generate(definition);
            return new Resource("application/vnd.ms-excel", "report.xlsx", excelBytes);
        }

        @Override
        public ModelAndView showResults(WebRequestContext request,
                                        ReportDesignModel model, Object data) {
            return new ResourceModelAndView((Resource) data);
        }
    };
    return executeTask(request, model, task);
}
```

### validate

Default returns `null` (no validation). Override to validate filters:

```java
@Override
protected String validate(ReportDesignModel model, Map<String, Object> filterMap) {
    if (filterMap.get("startDate") == null) {
        return "Start date is required";  // Error message
    }
    return null;  // No error
}
```

### debugMode

Default returns `false`. Override to enable debug output:

```java
@Override
protected boolean debugMode() {
    return true;
}

@Override
protected void debugSource(String title, byte[] jrxml, IProgressMonitor monitor) {
    Files.write(Paths.get("/tmp/" + title + ".jrxml"), jrxml);
}
```

---

## ReportDesignCustomBean Interface

The interface your entity must implement:

```java
public interface ReportDesignCustomBean {
    Integer getId();
    void setId(Integer id);

    String getXml();           // The report XML configuration
    void setXml(String xml);

    Boolean getReportPublic(); // Whether report is visible to all users
    void setReportPublic(Boolean reportPublic);
}
```

You can add any additional fields to your entity (name, description, category, owner, etc.).
