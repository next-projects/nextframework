# AJAX Tags

Tags for asynchronous operations and dynamic updates.

```jsp
<%@ taglib prefix="ajax" uri="ajax"%>
```

---

## ajax:call

Generates a JavaScript function for making AJAX calls to the server.

```jsp
<ajax:call functionName="loadProducts"
           url="/admin/products"
           action="ajaxList"
           callback="displayProducts"/>

<script>
    function displayProducts(response) {
        // Handle response
        document.getElementById('productList').innerHTML = response;
    }

    // Call the generated function
    loadProducts();
</script>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `functionName` | String | Name of the generated JavaScript function |
| `url` | String | Target URL |
| `action` | String | Action parameter |
| `parameters` | String | Additional parameters |
| `callback` | String | JavaScript callback function name |

### Generated Function

The tag generates a JavaScript function like:

```javascript
function loadProducts(additionalParams) {
    // Makes AJAX request to /admin/products?action=ajaxList
    // Calls displayProducts(response) on success
}
```

### Examples

#### Load Data on Page Load

```jsp
<ajax:call functionName="loadDashboard"
           url="/dashboard"
           action="getData"
           callback="renderDashboard"/>

<div id="dashboardContent">Loading...</div>

<script>
    function renderDashboard(data) {
        document.getElementById('dashboardContent').innerHTML = data;
    }

    // Load on page ready
    document.addEventListener('DOMContentLoaded', loadDashboard);
</script>
```

#### Load on Button Click

```jsp
<ajax:call functionName="refreshTable"
           url="/products"
           action="ajaxList"
           callback="updateTable"/>

<button onclick="refreshTable()">Refresh</button>
<div id="tableContainer"></div>

<script>
    function updateTable(html) {
        document.getElementById('tableContainer').innerHTML = html;
    }
</script>
```

#### With Parameters

```jsp
<ajax:call functionName="searchProducts"
           url="/products"
           action="ajaxSearch"
           callback="showResults"/>

<input type="text" id="searchTerm" onkeyup="doSearch()"/>
<div id="searchResults"></div>

<script>
    function doSearch() {
        var term = document.getElementById('searchTerm').value;
        searchProducts('term=' + encodeURIComponent(term));
    }

    function showResults(html) {
        document.getElementById('searchResults').innerHTML = html;
    }
</script>
```

---

## n:comboReloadGroup with AJAX

Enable AJAX-based cascade reloading for select inputs.

```jsp
<n:comboReloadGroup useAjax="true">
    <n:input name="country"
             itens="${countries}"
             reloadOnChange="true"/>
    <n:input name="state"
             itens="${states}"/>
    <n:input name="city"
             itens="${cities}"/>
</n:comboReloadGroup>
```

### How It Works

1. User changes `country` select
2. AJAX call sends current form data to server
3. Controller reloads `states` based on selected country
4. Server returns updated options
5. `state` and `city` selects are updated

### Controller Support

```java
@Controller(path = "/admin/address")
public class AddressController extends MultiActionController {

    @DefaultAction
    public ModelAndView form(WebRequestContext request, AddressForm form) {
        // Load countries
        request.setAttribute("countries", countryDAO.findAll());

        // Load states based on selected country
        if (form.getCountry() != null) {
            request.setAttribute("states", stateDAO.findByCountry(form.getCountry()));
        }

        // Load cities based on selected state
        if (form.getState() != null) {
            request.setAttribute("cities", cityDAO.findByState(form.getState()));
        }

        return new ModelAndView("address/form");
    }
}
```

### onLoadItens Callback

Execute JavaScript after options are loaded:

```jsp
<n:input name="state"
         itens="${states}"
         onLoadItens="onStatesLoaded()"/>

<script>
    function onStatesLoaded() {
        console.log('States loaded');
        // Additional logic
    }
</script>
```

---

## n:suggest (Autocomplete)

AJAX-powered autocomplete input.

```jsp
<n:suggestProvider name="cityProvider" dataSource="${cities}"/>

<n:input name="city" type="suggest" suggestProvider="cityProvider"/>
```

### Components

#### n:suggestProvider

Defines the data source for suggestions:

```jsp
<n:suggestProvider name="productProvider" dataSource="${products}"/>
```

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | String | Provider name (referenced by input) |
| `dataSource` | Collection | Data for suggestions |

#### Using with InputTag

```jsp
<n:input name="product"
         type="suggest"
         suggestProvider="productProvider"/>
```

### Server-Side Suggest

For large datasets, implement server-side filtering:

```java
public class ProductSuggestCallback implements SuggestCallback {

    @Autowired
    private ProductDAO productDAO;

    @Override
    public List<?> getSuggestions(SuggestContext context) {
        String term = context.getTerm();
        return productDAO.findByNameLike(term, 10);  // Limit to 10 results
    }
}
```

---

## n:progressBar

Real-time progress display using AJAX polling.

```jsp
<n:progressBar progressMonitor="${progressMonitor}"
               onComplete="onProcessComplete()"
               onError="onProcessError()"/>

<script>
    function onProcessComplete() {
        alert('Process completed!');
        window.location.reload();
    }

    function onProcessError() {
        alert('An error occurred');
    }
</script>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `progressMonitor` | ProgressMonitor | Progress tracking object |
| `onComplete` | String | JavaScript on completion |
| `onError` | String | JavaScript on error |

### Server-Side Progress Monitor

```java
@Controller(path = "/admin/import")
public class ImportController extends MultiActionController {

    public ModelAndView startImport(WebRequestContext request) {
        ProgressMonitor monitor = new ProgressMonitor();
        request.getSession().setAttribute("importMonitor", monitor);

        // Start background process
        importService.importDataAsync(file, monitor);

        request.setAttribute("progressMonitor", monitor);
        return new ModelAndView("import/progress");
    }
}

// In ImportService
public void importDataAsync(File file, ProgressMonitor monitor) {
    new Thread(() -> {
        try {
            List<Record> records = parseFile(file);
            monitor.setTotal(records.size());

            for (int i = 0; i < records.size(); i++) {
                processRecord(records.get(i));
                monitor.setProgress(i + 1);
                monitor.setMessage("Processing record " + (i + 1));
            }

            monitor.setComplete(true);
        } catch (Exception e) {
            monitor.setError(true);
            monitor.setMessage(e.getMessage());
        }
    }).start();
}
```

---

## AJAX Callbacks

The framework provides callback classes for common AJAX patterns:

### ComboCallback

For cascade combo reloading:

```java
public class StateComboCallback implements ComboCallback {

    @Autowired
    private StateDAO stateDAO;

    @Override
    public List<?> getItems(ComboFilter filter) {
        Integer countryId = filter.getParameterAsInteger("country.id");
        if (countryId != null) {
            return stateDAO.findByCountryId(countryId);
        }
        return Collections.emptyList();
    }
}
```

### SuggestCallback

For autocomplete suggestions:

```java
public class CustomerSuggestCallback implements SuggestCallback {

    @Autowired
    private CustomerDAO customerDAO;

    @Override
    public List<?> getSuggestions(SuggestContext context) {
        return customerDAO.findByNameContaining(context.getTerm());
    }
}
```

### ProgressBarCallback

For progress monitoring:

```java
public class ImportProgressCallback implements ProgressBarCallback {

    @Override
    public ProgressInfo getProgress(HttpServletRequest request) {
        ProgressMonitor monitor =
            (ProgressMonitor) request.getSession().getAttribute("importMonitor");

        ProgressInfo info = new ProgressInfo();
        info.setProgress(monitor.getProgress());
        info.setTotal(monitor.getTotal());
        info.setMessage(monitor.getMessage());
        info.setComplete(monitor.isComplete());
        info.setError(monitor.isError());
        return info;
    }
}
```

---

## Patterns

### Dynamic Form Section

```jsp
<ajax:call functionName="loadAddressForm"
           url="/address"
           action="ajaxForm"
           callback="showAddressForm"/>

<select onchange="loadAddressForm('type=' + this.value)">
    <option value="">Select Type</option>
    <option value="shipping">Shipping</option>
    <option value="billing">Billing</option>
</select>

<div id="addressFormContainer"></div>

<script>
    function showAddressForm(html) {
        document.getElementById('addressFormContainer').innerHTML = html;
    }
</script>
```

### Live Search

```jsp
<ajax:call functionName="searchProducts"
           url="/products"
           action="ajaxSearch"
           callback="displayResults"/>

<input type="text"
       id="searchInput"
       onkeyup="debounceSearch()"
       placeholder="Search products..."/>

<div id="searchResults"></div>

<script>
    var searchTimeout;

    function debounceSearch() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(function() {
            var term = document.getElementById('searchInput').value;
            if (term.length >= 2) {
                searchProducts('term=' + encodeURIComponent(term));
            }
        }, 300);
    }

    function displayResults(html) {
        document.getElementById('searchResults').innerHTML = html;
    }
</script>
```

### Infinite Scroll

```jsp
<ajax:call functionName="loadMoreItems"
           url="/items"
           action="ajaxLoadMore"
           callback="appendItems"/>

<div id="itemList">
    <!-- Initial items -->
</div>

<script>
    var page = 1;

    window.addEventListener('scroll', function() {
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
            page++;
            loadMoreItems('page=' + page);
        }
    });

    function appendItems(html) {
        document.getElementById('itemList').insertAdjacentHTML('beforeend', html);
    }
</script>
```
