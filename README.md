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
```

## Métodos Disponibles

### `filterSensitiveDataHeaders`

Elimina encabezados sensibles del mapa proporcionado. Puede usarse para limpiar datos de encabezado o parámetros de consulta en registros o respuestas.

- **Parámetro**: `Map<String, String> headers` - el mapa de encabezados.
- **Retorno**: `Map<String, String>` - un nuevo mapa sin los encabezados sensibles.

**Ejemplo de Uso**

```java
Map<String, String> headers = Map.of("Authorization", "Bearer abc123", "User-Agent", "MyApp");
Map<String, String> filteredHeaders = dataShield.filterSensitiveDataHeaders(headers);
```

**Salida Esperada**
```java
{ "User-Agent": "MyApp" }
```

### `maskSensitiveDataHeaders`

Enmascara los valores de los encabezados sensibles en el mapa proporcionado.

- **Parámetro**: `Map<String, String> headers` - el mapa de encabezados.
- **Retorno**: `Map<String, String>` - un nuevo mapa con valores enmascarados en los encabezados sensibles.

**Ejemplo de Uso**

```java
Map<String, String> headers = Map.of("Authorization", "Bearer abc123", "User-Agent", "MyApp");
Map<String, String> maskedHeaders = dataShield.maskSensitiveDataHeaders(headers);
```

**Salida Esperada**
```java
{ "Authorization": "Be******", "User-Agent": "MyApp" }
```

### `maskValue`

Enmascara un valor de cadena según las configuraciones de `ConfigPrivaCode`.

- **Parámetro**: `String` - el valor a enmascarar.
- **Retorno**: `String` - el valor enmascarado.

**Ejemplo de Uso**

```java
String maskedValue = dataShield.maskValue("SensitiveData");
```

**Salida Esperada**
```java
"Se******ta"
```

### `maskValue`

Enmascara un valor de cadena utilizando configuraciones personalizadas.

- **Parámetros**:
  - `String value` - el valor a enmascarar (puede ser nulo).
  - `int minLength` - la longitud mínima que el valor debe tener para aplicar el enmascarado.
  - `boolean keepStart` - indica si se deben conservar caracteres al inicio del valor.
  - `int keepStartCount` - el número de caracteres a conservar al inicio.
  - `boolean keepEnd` - indica si se deben conservar caracteres al final del valor.
  - `int keepEndCount` - el número de caracteres a conservar al final.
  - `String maskValue` - el valor que se utilizará para enmascarar los caracteres intermedios.

- **Retorno**: `String` - el valor enmascarado según las configuraciones dadas.

**Ejemplo de Uso**

```java
String maskedValue = dataShield.maskValue("SensitiveData", 5, true, 3, false, 0, "*");
```

**Salida Esperada**
```java
"Sen*****"
```

### `maskValues`

Enmascara un valor de cadena según las configuraciones de `ConfigPrivaCode`.

- **Parámetro**: `List<String>` - la lista de valores a enmascarar.
- **Retorno**: `List<String>` - una lista con los valores enmascarados.

**Ejemplo de Uso**

```java
List<String> values = List.of("SensitiveData1", "SensitiveData2");
List<String> maskedValues = dataShield.maskValues(values);
```

**Salida Esperada**
```java
["Se******a1", "Se******a2"]
```

### `maskJsonData`

Enmascara los valores sensibles en un objeto JSON de acuerdo con las configuraciones.

- **Parámetro**: `String` - la cadena JSON que contiene los datos a enmascarar.
- **Retorno**: `String` - una nueva cadena JSON con los valores sensibles enmascarados.

**Ejemplo de Uso**

```java
String json = "{\"Authorization\": \"Bearer abc123\", \"User-Agent\": \"MyApp\"}";
String maskedJson = dataShield.maskJsonData(json);
```

**Salida Esperada**
```java
{"Authorization": "Be******", "User-Agent": "MyApp"}
```

### `getMaskingConfigSummary`

Proporciona un resumen de las configuraciones actuales de enmascarado.

- **Retorno**: `String` - un resumen de las configuraciones de enmascarado.

**Ejemplo de Uso**

```java
String configSummary = dataShield.getMaskingConfigSummary();
```

**Salida Esperada**
```java
"Resumen de Configuraciones de Enmascarado:
Longitud Mínima Para Enmascarar: 5
Conservar Inicio: Sí (3 caracteres)
Conservar Fin: No (0 caracteres)
Valor de Enmascarado: '#'"
```

## Licencia
Este proyecto está licenciado bajo la `Apache License 2.0`.
