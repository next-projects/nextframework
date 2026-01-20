# org.nextframework.persistence

## Overview

Fluent persistence layer over Hibernate. Build type-safe queries and manage entity lifecycles with parent-child relationships.

```java
// Query with joins, filters, pagination
List<Person> people = new QueryBuilder<Person>()
    .from(Person.class)
    .leftOuterJoinFetch("address")
    .where("age > ?", 18)
    .orderBy("name ASC")
    .setPageNumberAndSize(0, 10)
    .list();

// Save entity with child collection (auto-manages inserts/updates/deletes)
new SaveOrUpdateStrategy(order)
    .saveEntity()
    .saveOrUpdateManaged("items")
    .execute();
```

---

## QueryBuilder

Fluent query construction with type safety:

```java
QueryBuilder<Product> qb = new QueryBuilder<Product>()
    .from(Product.class)
    .select("product.name, product.price")
    .where("product.active = ?", true)
    .orderBy("product.name");

List<Product> products = qb.list();
Product single = qb.unique();
```

### Joins

```java
qb.join("category");                    // INNER JOIN
qb.joinFetch("category");               // INNER JOIN FETCH
qb.leftOuterJoin("supplier");           // LEFT OUTER JOIN
qb.leftOuterJoinFetch("supplier");      // LEFT OUTER JOIN FETCH
```

### Where Clauses

```java
qb.where("price > ?", minPrice);
qb.whereLike("name", searchTerm);                    // LIKE '%term%'
qb.whereLikeIgnoreAll("name", term);                 // Accent-insensitive
qb.whereIn("id", idList);
qb.whereIntervalMatches("startDate", "endDate", start, end);
```

### Pagination

```java
qb.setPageNumberAndSize(0, 20);   // Page 0, 20 items per page
qb.setFirstResult(40);
qb.setMaxResults(20);
```

### Collection Fetching

Fetch lazy collections without N+1 queries:

```java
qb.fetchCollection("addresses");   // Load collection with query
```

---

## SaveOrUpdateStrategy

Manage entity persistence with parent-child relationships. Use `saveOrUpdateManaged` for collections - it handles everything automatically:

```java
new SaveOrUpdateStrategy(invoice)
    .saveEntity()
    .saveOrUpdateManaged("items")
    .execute();
```

This automatically:
- Discovers the parent property via Hibernate metadata
- Sets the back-reference on each child (`item.setInvoice(invoice)`)
- Inserts new items
- Updates existing items
- Deletes items removed from the collection

### Insert Only (No Delete)

Use `setParent` with `saveCollection` when you only want to insert new items:

```java
new SaveOrUpdateStrategy(invoice)
    .setParent("items", "invoice")   // Manually set back-references
    .saveEntity()
    .saveCollection("items")          // Insert all items (no delete)
    .execute();
```

### Chaining Strategies

```java
new SaveOrUpdateStrategy(order)
    .saveEntity()
    .attach(new SaveOrUpdateStrategy(payment).saveEntity())
    .execute();
```

### Lifecycle Listeners

```java
strategy.saveCollection("items", new CollectionItemSaveOrUpdateListener<Item>() {
    public void onSaveOrUpdate(Item item, SaveOrUpdateStrategyChain chain) {
        item.setUpdatedAt(new Date());
        chain.continueChain();
    }
});
```

---

## HibernateCommand

Execute operations within a Hibernate session:

```java
Object result = sessionProvider.execute(new HibernateCommand() {
    public Object doInHibernate(Session session) {
        return session.createQuery("from Product").list();
    }
});
```

### With Transaction

```java
transactionProvider.executeInTransaction(new HibernateTransactionCommand<TransactionStatus>() {
    public Object doInHibernate(Session session, TransactionStatus tx) {
        session.save(entity);
        return entity.getId();
    }
});
```

---

## PersistenceUtils

Utility methods for entity introspection:

```java
// Get entity ID
Object id = PersistenceUtils.getId(entity);

// Get ID property name
String idProp = PersistenceUtils.getIdPropertyName(Entity.class);

// Get collection properties (inverse relationships)
Set<String> collections = PersistenceUtils.getCollectionProperties(Entity.class);

// Normalize text (remove accents)
String normalized = PersistenceUtils.removeAccents("São Paulo");  // "Sao Paulo"
```

---

## Custom Collection Fetching

When using `fetchCollection`, you can customize how the collection is loaded:

```java
qb.fetchCollection("orderItems", (query, owner, prop, type) -> {
    query.leftOuterJoinFetch("product");  // Eager-load nested relationship
    query.where("status = ?", "ACTIVE");   // Filter the collection
    return query.list();
});
```

Useful for filtering collections, eager-loading nested relationships, or custom sorting.

---

## Select with Projections

When selecting specific fields, Hibernate normally returns `Object[]` arrays. Next automatically translates these back to typed objects:

```java
List<Person> people = new QueryBuilder<Person>()
    .from(Person.class)
    .select("person.name, person.age, address.city")
    .leftOuterJoin("address address")
    .list();  // Returns Person objects, not Object[]
```

The translator uses aliases and join structure to reconstruct the object graph from the selected fields.

---

## PersistenceConfiguration

Configure persistence contexts:

```java
PersistenceConfiguration config = PersistenceConfiguration.getConfig();

// Set session provider for context
config.setSessionProvider("myContext", sessionProviderFactory);

// Set collection fetcher
config.setCollectionFetcher("myContext", collectionFetcherFactory);

// Configure accent removal function (database-specific)
config.setRemoveAccentFunction("UNACCENT");  // PostgreSQL
```

