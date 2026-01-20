# org.nextframework.validation

## Overview

Annotation-based validation with automatic JavaScript generation. Annotate your bean properties and get both server-side and client-side validation.

```java
public class User {

    @Required
    @Email
    public String getEmail() { return email; }

    @Required
    @MinLength(3)
    @MaxLength(50)
    public String getName() { return name; }

    @MinValue(18)
    public Integer getAge() { return age; }
}
```

The framework validates on the server and generates JavaScript validation for forms automatically.

---

## Annotations

### Constraint Annotations

| Annotation | Description |
|------------|-------------|
| `@Required` | Field cannot be null, empty string, or empty collection |
| `@Email` | Must be a valid email address |
| `@MinLength(n)` | String minimum length |
| `@MaxLength(n)` | String maximum length |
| `@MinValue(n)` | Numeric minimum value |
| `@MaxValue(n)` | Numeric maximum value |
| `@Year` | Valid year (1950-2050) |
| `@Regex(pattern)` | Must match regular expression |

### Usage

Apply to getter methods:

```java
public class Product {

    @Required
    @MaxLength(100)
    public String getName() { return name; }

    @Required
    @MinValue(0)
    public BigDecimal getPrice() { return price; }

    @Email
    public String getContactEmail() { return contactEmail; }

    @Regex("[A-Z]{3}-[0-9]{4}")
    public String getCode() { return code; }
}
```

---

## Type Validators

Certain types are validated automatically based on their class:

| Type | Validation |
|------|------------|
| `Integer`, `Long`, `Short`, `Byte` | Numeric format |
| `Float`, `Double` | Decimal format |
| `Date` | Date format |
| `Time` | Time format |

### 🇧🇷 Brazilian Types

| Type | Validation |
|------|------------|
| `Cpf` | Valid CPF with check digits |
| `Cnpj` | Valid CNPJ with check digits |
| `Cep` | Valid postal code format |
| `PhoneBrazil` | Valid phone format |
| `InscricaoEstadual` | Valid state registration |

```java
public class Company {

    @Required
    public Cnpj getCnpj() { return cnpj; }  // Automatically validated

    @Required
    public PhoneBrazil getPhone() { return phone; }  // Automatically validated
}
```

---

## Custom Validators

Implement `PropertyValidator` and register it:

```java
public class UppercaseValidator implements PropertyValidator<Uppercase> {

    @Override
    public void validate(Object value, String fieldName, Errors errors, Uppercase annotation) {
        if (value != null && !value.toString().equals(value.toString().toUpperCase())) {
            errors.rejectValue(fieldName, "uppercase", "Must be uppercase");
        }
    }

    @Override
    public String getJavascriptFunction(Uppercase annotation) {
        return "function(value) { return value === value.toUpperCase(); }";
    }

    @Override
    public String getMessage(String fieldDisplayName, Uppercase annotation) {
        return fieldDisplayName + " must be uppercase";
    }
}

// Register
ValidatorRegistry registry = ...;
registry.register(Uppercase.class, new UppercaseValidator());
```

---

## JavaScript Generation

The framework automatically generates client-side validation. The view layer uses `JavascriptValidationFunctionBuilder` to create form validation functions that mirror server-side rules.

When using Next's form tags, JavaScript validation is included automatically - no extra configuration needed.

