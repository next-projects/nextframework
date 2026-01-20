# org.nextframework.dao

## Overview

Generic DAO layer with CRUD operations, pagination, and query customization. Extend `GenericDAO` for any entity - no boilerplate needed.

```java
@Component
public class PersonDAO extends GenericDAO<Person> {
}

// Use it
@Autowired PersonDAO personDAO;

personDAO.saveOrUpdate(person);
Person loaded = personDAO.loadById(1L);
List<Person> all = personDAO.findAll();
```

---

## GenericDAO

Extend for any entity to get full CRUD support:

```java
@Component
@DefaultOrderBy("name ASC")
public class ProductDAO extends GenericDAO<Product> {
}
```

### Basic Operations

```java
// Save or update
dao.saveOrUpdate(entity);
dao.bulkSaveOrUpdate(entityList);

// Load
Product p = dao.loadById(1L);
Product p = dao.load(entity);  // Reloads from DB

// Find
List<Product> all = dao.findAll();
List<Product> active = dao.findByProperty("active", true);
Product unique = dao.findByPropertyUnique("sku", "ABC123");

// Delete
dao.delete(entity);
```

### Query Methods

```java
// For dropdowns/combos (loads id + description only)
List<Product> options = dao.findForCombo();

// Find by related entity
List<Product> byCategory = dao.findBy(category);

// Custom query
QueryBuilder<Product> qb = dao.query()
    .where("price > ?", 100)
    .orderBy("name");
List<Product> results = qb.list();
```

---

## Pagination

Use `ListViewFilter` for paginated lists:

```java
ListViewFilter filter = new ListViewFilter();
filter.setOrderBy("name");
filter.setPageSize(20);

ResultList<Product> results = dao.loadListModel(filter);
List<Product> page = results.list();

// Navigate
if (results.hasNextPage()) {
    results.nextPage();
    List<Product> nextPage = results.list();
}
```

---

## Customizing Queries

Override hooks to customize behavior:

```java
@Component
public class ProductDAO extends GenericDAO<Product> {

    // Customize list queries (for grids)
    @Override
    public void updateListQuery(QueryBuilder<Product> query, ListViewFilter filter) {
        ProductFilter pf = (ProductFilter) filter;  // Cast to your filter type
        query.leftOuterJoinFetch("category");
        if (pf.getName() != null) {
            query.whereLike("name", pf.getName());
        }
        if (pf.getCategory() != null) {
            query.where("category = ?", pf.getCategory());
        }
    }

    // Customize combo/dropdown queries (used by view layer for <n:input type="select-one">)
    @Override
    protected void updateFindForComboQuery(QueryBuilder<Product> query) {
        query.where("active = ?", true);  // Only show active products in dropdowns
    }

    // Customize form loading (eager-load relationships)
    @Override
    public void updateFormQuery(QueryBuilder<Product> query) {
        query.leftOuterJoinFetch("category");
        query.leftOuterJoinFetch("supplier");
    }

    // Customize save behavior
    @Override
    public void updateSaveOrUpdate(SaveOrUpdateStrategy save) {
        save.saveOrUpdateManaged("variants");
    }
}
```

The `ListViewFilter` is a base class with pagination/ordering. Extend it with your filter fields:

```java
public class ProductFilter extends ListViewFilter {
    private String name;
    private Category category;
    // getters/setters
}
```

Use with `CrudController<ProductFilter, Product, Product>` - see the controller module.

### Custom Methods

Add your own query methods:

```java
@Component
public class ProductDAO extends GenericDAO<Product> {

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return query()
            .where("price >= ?", min)
            .where("price <= ?", max)
            .orderBy("price")
            .list();
    }

    public List<Product> findLowStock(int threshold) {
        return query()
            .where("stockQuantity < ?", threshold)
            .where("active = ?", true)
            .list();
    }
}
```

### Transactions

Use `getTransactionTemplate()` to wrap multiple operations in a transaction:

```java
@Component
public class OrderDAO extends GenericDAO<Order> {

    public void transferStock(Product from, Product to, int qty) {
        getTransactionTemplate().execute(status -> {
            from.setStock(from.getStock() - qty);
            to.setStock(to.getStock() + qty);
            saveOrUpdate(from);
            saveOrUpdate(to);
            return null;
        });
    }
}
```

---

## @DefaultOrderBy

Set default ordering for `findAll()` and `findForCombo()`:

```java
@Component
@DefaultOrderBy("name ASC")
public class CategoryDAO extends GenericDAO<Category> {
}

// findAll() now returns ordered by name
List<Category> categories = dao.findAll();
```

---

## Loading Partial Data

Load only specific attributes:

```java
// Load specific fields
Product p = dao.load(entity, new String[]{"name", "price"});

// Load additional attributes into existing entity
dao.loadAttributes(entity, new String[]{"description", "details"});

// Load with id and description (for references)
Product ref = dao.loadWithIdAndDescription(entity);
```

---

## HibernateUtils

Utilities for working with Hibernate entities:

```java
// Get entity ID
Object id = HibernateUtils.getId(entity);

// Check if entity is persisted
boolean isNew = DAOUtils.isTransient(entity);

// Handle lazy proxies
boolean isLazy = HibernateUtils.isLazy(entity.getCategory());
Category real = HibernateUtils.getLazyValue(entity.getCategory());

// Get real class (unwrap proxy)
Class<?> realClass = HibernateUtils.getRealClass(entity);

// Compare entities (handles proxies)
boolean same = HibernateUtils.equals(entity1, entity2);
```

---

## DAOUtils

Dynamically retrieve DAOs:

```java
// Get DAO by entity class (looks for PersonDAO bean)
DAO<Person> dao = DAOUtils.getDAOForClass(Person.class);

// Check if entity is transient (not yet persisted)
if (DAOUtils.isTransient(entity)) {
    // New entity
}
```

