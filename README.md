# PrivaCode

**PrivaCode** is a Java library for masking and filtering sensitive data, particularly in strings, headers, and JSON data, based on configurable privacy settings. This library provides a flexible way to remove or obscure sensitive information before it is logged, displayed, or stored, helping to ensure data privacy and security.

## Features

- **Masking Values**: Mask strings according to configurations for minimum length, characters to retain, and masking character.
- **JSON Compatible**: Mask sensitive data within JSON objects.
- **Header Management**: Filter or mask sensitive headers in key-value pairs.
- **Configuration Summary**: Retrieve a summary of masking configurations for easy review.

---

## Installation

Include the library in your project dependencies. Ensure you have Google Gson if you plan to use JSON masking.

---

## Usage

Instantiate the `DataShield` class by injecting a `ConfigPrivaCode` configuration instance. Use the methods to filter and mask data according to your needs.

```java
ConfigPrivaCode config = new ConfigPrivaCode(); // Configure settings as needed
DataShield dataShield = new DataShield(config);
