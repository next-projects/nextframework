# org.nextframework.types

## Overview

Domain value types with built-in validation, formatting, and Hibernate persistence. Use these instead of raw Strings for type safety and automatic formatting.

```java
Money price = new Money(1500.50);
Money tax = new Money(150.00);
Money total = price.add(tax);  // 1650.50

SimpleTime time = new SimpleTime("14:30");
System.out.println(time);  // Output: 14:30
```

---

## Available Types

| Type | Description | Storage | Format |
|------|-------------|---------|--------|
| `Money` | Currency with arithmetic | BIGINT (cents) | 1,234.56 |
| `SimpleTime` | Time without date | TIMESTAMP | HH:mm |
| `Password` | Password field | String | (masked) |

---

## Money Arithmetic

```java
Money a = new Money(100.00);
Money b = new Money(25.50);

Money sum = a.add(b);        // 125.50
Money diff = a.subtract(b);  // 74.50
Money prod = a.multiply(b);  // 2550.00
Money quot = a.divide(b);    // 3.92...

Money rounded = sum.round(); // Round to 2 decimal places
```

---

## Hibernate Persistence

Types implement `UserType` and persist automatically:

```java
@Entity
public class Event {

    @Type(type = "org.nextframework.types.Money")
    private Money price;  // Stored as BIGINT (cents)

    @Type(type = "org.nextframework.types.SimpleTime")
    private SimpleTime startTime;
}
```

---

## Brazilian Types

🇧🇷 Types for Brazilian documents with built-in validation (check digits) and formatting.

| Type | Description | Storage | Format |
|------|-------------|---------|--------|
| `Cpf` | Individual tax ID | 11 digits | 000.000.000-00 |
| `Cnpj` | Company tax ID | 14 digits | 00.000.000/0000-00 |
| `Cep` | Postal code | 8 digits | 00000-000 |
| `InscricaoEstadual` | State registration | varies | varies by state |
| `PhoneBrazil` | Phone number | 10-11 digits | (00) 00000-0000 |

```java
// Validated on construction, formatted on toString()
Cpf cpf = new Cpf("07357279618");
System.out.println(cpf);  // Output: 073.572.796-18

// Invalid CPF throws IllegalArgumentException
Cpf invalid = new Cpf("111.111.111-11");  // Invalid check digits

// Skip validation when needed
Cpf cpf = new Cpf("07357279618", false);
```
