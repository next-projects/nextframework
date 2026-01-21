# CrudController

Base class for standard CRUD (Create, Read, Update, Delete) operations. Provides pre-built actions for listing, viewing, creating, editing, and deleting records.

---

## Quick Start

```java
@Controller(path = "/admin/products")
public class ProductController extends CrudController<Product> {
    // Ready to use with standard CRUD operations
}
```

This provides:
- `/admin/products` - List products
- `/admin/products?action=form` - New product form
- `/admin/products?action=form&id=5` - Edit product form
- `/admin/products?action=view&id=5` - View product (read-only)
- `/admin/products?action=save` - Save product (POST)
- `/admin/products?action=delete&id=5` - Delete product

**Note:** Path must include module (e.g., `admin`) as first segment.

---

## Generic Parameters

```java
CrudController<BEAN>
```

| Parameter | Description |
|-----------|-------------|
| `BEAN` | Entity class for CRUD operations |

With filter:

```java
CrudController<FILTER, BEAN>
```

| Parameter | Description |
|-----------|-------------|
| `FILTER` | Filter class for list queries |
| `BEAN` | Entity class |

With separate form bean:

```java
CrudController<FILTER, FORMBEAN, BEAN>
```

| Parameter | Description |
|-----------|-------------|
| `FILTER` | Filter class |
| `FORMBEAN` | Form binding class |
| `BEAN` | Entity class |

---

## Built-in Actions

### doList (Default Action)

Displays list of records with optional filtering.

```java
// URL: /products
// URL: /products?action=list
// URL: /products?action=list&name=Widget&minPrice=10

@DefaultAction
@Action("list")
protected ModelAndView doList(WebRequestContext request, FILTER filter) {
    List<BEAN> items = loadListModel(request, filter);
    setAttribute("lista", items);
    return view("list");
}
```

### doForm

Displays form for creating or editing.

```java
// URL: /products?action=form (new)
// URL: /products?action=form&id=5 (edit)

@Action("form")
protected ModelAndView doForm(WebRequestContext request, FORMBEAN form) {
    if (isUpdate(form)) {
        form = loadFormModel(request, form);
    }
    return view("form");
}
```

### doCreate

Creates a new record (same as doForm for new).

```java
// URL: /products?action=create

@Action("create")
protected ModelAndView doCreate(WebRequestContext request, FORMBEAN form) {
    return doForm(request, form);
}
```

### doUpdate

Loads existing record for editing.

```java
// URL: /products?action=update&id=5

@Action("update")
protected ModelAndView doUpdate(WebRequestContext request, FORMBEAN form) {
    form = loadFormModel(request, form);
    return view("form");
}
```

### doView

Displays record in read-only mode.

```java
// URL: /products?action=view&id=5

@Action("view")
protected ModelAndView doView(WebRequestContext request, FORMBEAN form) {
    form = loadFormModel(request, form);
    setAttribute("viewOnly", true);
    return view("form");
}
```

### doSave

Saves (creates or updates) a record.

```java
// URL: /products?action=save (POST)

@Action("save")
@Input("form")
protected ModelAndView doSave(WebRequestContext request, FORMBEAN form) {
    BEAN bean = convertToBean(form);
    save(request, bean);
    addMessage("Record saved");
    return redirectToList();
}
```

### doDelete

Deletes a record.

```java
// URL: /products?action=delete&id=5

@Action("delete")
@OnErrors("list")
protected ModelAndView doDelete(WebRequestContext request, FORMBEAN form) {
    BEAN bean = loadFormModel(request, form);
    delete(request, bean);
    addMessage("Record deleted");
    return redirectToList();
}
```

---

## Overridable Methods

### Loading Data

```java
@Override
protected List<Product> loadListModel(WebRequestContext request, ProductFilter filter) {
    // Custom list loading logic
    return productDAO.findByFilter(filter);
}

@Override
protected Product loadFormModel(WebRequestContext request, Product product) {
    // Custom form loading logic
    return productDAO.findById(product.getId());
}
```

### Saving and Deleting

```java
@Override
protected void save(WebRequestContext request, Product product) {
    // Custom save logic
    productDAO.saveOrUpdate(product);
}

@Override
protected void delete(WebRequestContext request, Product product) {
    // Custom delete logic
    productDAO.delete(product);
}
```

### Validation

```java
@Override
protected void customValidation(WebRequestContext request,
                                Object command,
                                BindException errors) {
    Product product = (Product) command;

    if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
        errors.rejectValue("price", "invalid", "Price must be positive");
    }

    if (productDAO.existsByName(product.getName(), product.getId())) {
        errors.rejectValue("name", "duplicate", "Product name already exists");
    }
}
```

### Form Setup

```java
@Override
protected void setupForm(WebRequestContext request, Product product) {
    // Called before displaying form
    setAttribute("categories", categoryDAO.findAll());
    setAttribute("suppliers", supplierDAO.findAll());
}
```

### Before/After Hooks

```java
@Override
protected void beforeSave(WebRequestContext request, Product product) {
    // Called before save
    product.setModifiedDate(new Date());
    product.setModifiedBy(getCurrentUser());
}

@Override
protected void afterSave(WebRequestContext request, Product product) {
    // Called after save
    auditService.log("Product saved: " + product.getId());
}

@Override
protected void beforeDelete(WebRequestContext request, Product product) {
    // Called before delete
    if (product.hasOrders()) {
        throw new BusinessException("Cannot delete product with orders");
    }
}
```

---

## Filter Class

Create a filter class for list searching:

```java
public class ProductFilter implements ListViewFilter {

    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Category category;
    private Boolean active;

    // Pagination (from ListViewFilter)
    private Integer page;
    private Integer pageSize;

    // Getters and setters...
}
```

Use in controller:

```java
@Controller(path = "/admin/products")
public class ProductController extends CrudController<ProductFilter, Product> {

    @Override
    protected List<Product> loadListModel(WebRequestContext request, ProductFilter filter) {
        return productDAO.findByFilter(filter);
    }
}
```

---

## Form Bean Pattern

When form differs from entity:

```java
public class ProductForm {
    private Integer id;
    private String name;
    private String price;  // String for formatting
    private Integer categoryId;
    private List<Integer> supplierIds;

    // Getters and setters...
}

@Controller(path = "/admin/products")
public class ProductController extends CrudController<ProductFilter, ProductForm, Product> {

    @Override
    protected Product convertToBean(ProductForm form) {
        Product product = new Product();
        product.setId(form.getId());
        product.setName(form.getName());
        product.setPrice(new BigDecimal(form.getPrice()));
        product.setCategory(categoryDAO.findById(form.getCategoryId()));
        product.setSuppliers(supplierDAO.findByIds(form.getSupplierIds()));
        return product;
    }

    @Override
    protected ProductForm convertToForm(Product product) {
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setPrice(product.getPrice().toString());
        form.setCategoryId(product.getCategory().getId());
        form.setSupplierIds(product.getSuppliers().stream()
            .map(Supplier::getId)
            .collect(Collectors.toList()));
        return form;
    }
}
```

---

## GenericService Integration

CrudController integrates with GenericService:

```java
@Controller(path = "/admin/products")
public class ProductController extends CrudController<Product> {

    @Autowired
    private ProductService productService;

    @Override
    protected GenericService<Product> getService() {
        return productService;
    }
}
```

With service:

```java
@Service
public class ProductService extends GenericService<Product> {

    @Override
    public ListModel<Product> loadListModel(ListViewFilter filter) {
        // Custom list loading
    }

    @Override
    public Product loadFormModel(Product bean) {
        // Load with associations
    }
}
```

---

## View Resolution

Default view names:

| Action | View Name |
|--------|-----------|
| list | `{controllerPath}/list` |
| form/create/update | `{controllerPath}/form` |
| view | `{controllerPath}/form` (with viewOnly=true) |

Override view names:

```java
@Override
protected String getListView() {
    return "admin/products/customList";
}

@Override
protected String getFormView() {
    return "admin/products/customForm";
}
```

---

## Complete Example

```java
@Controller(path = "/admin/products")
public class ProductController extends CrudController<ProductFilter, Product> {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    protected List<Product> loadListModel(WebRequestContext request, ProductFilter filter) {
        return productDAO.findByFilter(filter);
    }

    @Override
    protected Product loadFormModel(WebRequestContext request, Product product) {
        return productDAO.findByIdWithDetails(product.getId());
    }

    @Override
    protected void setupForm(WebRequestContext request, Product product) {
        setAttribute("categories", categoryDAO.findAllActive());
    }

    @Override
    protected void customValidation(WebRequestContext request,
                                    Object command,
                                    BindException errors) {
        Product product = (Product) command;
        if (productDAO.existsByName(product.getName(), product.getId())) {
            errors.rejectValue("name", "duplicate", "Name already exists");
        }
    }

    @Override
    protected void beforeSave(WebRequestContext request, Product product) {
        product.setLastModified(new Date());
    }

    @Override
    protected void save(WebRequestContext request, Product product) {
        productDAO.saveOrUpdate(product);
    }

    @Override
    protected void delete(WebRequestContext request, Product product) {
        productDAO.delete(product);
    }
}
```

---

## Authorization

CrudController uses `CrudAuthorizationModule` by default:

| Action | Required Permission |
|--------|---------------------|
| list, view | READ |
| create | CREATE |
| update | UPDATE |
| form, save | CREATE or UPDATE |
| delete | DELETE |

Override authorization:

```java
@Controller(path = "/admin/products", authorizationModule = CustomAuthorizationModule.class)
public class ProductController extends CrudController<Product> {
}
```
