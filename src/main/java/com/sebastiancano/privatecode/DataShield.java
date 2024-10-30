package com.sebastiancano.privatecode;

import com.sebastiancano.privatecode.config.ConfigPrivaCode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La clase {@code DataShield} se encarga de manejar datos sensibles
 * en un conjunto de encabezados, proporcionando funcionalidades para
 * filtrar y enmascarar dichos datos según configuraciones predefinidas.
 * Esta clase utiliza la configuración de {@link ConfigPrivaCode} para
 * definir qué encabezados son sensibles y cómo deben ser tratados.
 * @author jhocano
 *
 */
@Component
public class DataShield {

    private final ConfigPrivaCode configPrivaCode;

    /**
     * Constructor que inyecta la configuración de privacidad.
     *
     * @param configPrivaCode la configuración que define los encabezados sensibles y sus tratamientos.
     */
    @Autowired
    public DataShield(ConfigPrivaCode configPrivaCode) {
        this.configPrivaCode = configPrivaCode;
    }

    /**
     * Elimina los encabezados sensibles del mapa proporcionado.
     * Puede ser utilizado para limpiar encabezados o parámetros de consulta
     * en respuestas o registros.
     *
     * @param headers el mapa de encabezados del cual se eliminarán los sensibles.
     * @return un nuevo mapa sin los encabezados sensibles.
     */
    public Map<String, String> filterSensitiveDataHeaders(Map<String, String> headers) {
        Map<String, String> mapHeaders = new HashMap<>(headers);
        List<String> sensitiveHeaders = configPrivaCode.getSensitiveHeadersList();

        for (String key : sensitiveHeaders) {
            mapHeaders.remove(key);
        }
        return mapHeaders;
    }

    /**
     * Enmascara los datos sensibles de los encabezados en el mapa proporcionado.
     * Esto se puede aplicar en encabezados o parámetros de consulta como respuesta o registros.
     *
     * @param headers el mapa de encabezados cuyas entradas sensibles serán enmascaradas.
     * @return un nuevo mapa con los encabezados sensibles enmascarados.
     */
    public Map<String, String> maskSensitiveDataHeaders(Map<String, String> headers) {
        Map<String, String> mapHeaders = new HashMap<>(headers);
        List<String> sensitiveHeaders = configPrivaCode.getSensitiveHeadersList();

        for (String key : sensitiveHeaders) {
            if (mapHeaders.containsKey(key)) {
                mapHeaders.put(key, maskValue(mapHeaders.get(key)));
            }
        }

        return mapHeaders;
    }

    /**
     * Enmascara un valor de cadena utilizando configuraciones predeterminadas
     * definidas en {@link ConfigPrivaCode}. Este method delega la lógica de enmascarado
     * al method {@code maskValue(String, int, boolean, int, boolean, int, String)}
     * utilizando las propiedades configuradas.
     *
     * @param value el valor a enmascarar, puede ser nulo.
     * @return el valor enmascarado según las configuraciones predeterminadas.
     * Si el valor es nulo, se retornará un string vacío.
     */
    public String maskValue(String value) {
        return maskValue(
                value,
                configPrivaCode.getMinLength(),
                configPrivaCode.isKeepStart(),
                configPrivaCode.getKeepStartCount(),
                configPrivaCode.isKeepEnd(),
                configPrivaCode.getKeepEndCount(),
                configPrivaCode.getMaskValue()
        );
    }

    /**
     * Enmascara un valor de cadena basado en las configuraciones de conservación de caracteres
     * al inicio y al final, así como el valor de enmascarado.
     *
     * @param value          el valor a enmascarar, puede ser nulo.
     * @param minLength      la longitud mínima que el valor debe tener para aplicar el enmascarado.
     * @param keepStart      indica si se deben conservar caracteres al inicio del valor.
     * @param keepStartCount el número de caracteres a conservar al inicio.
     * @param keepEnd        indica si se deben conservar caracteres al final del valor.
     * @param keepEndCount   el número de caracteres a conservar al final.
     * @param maskValue      el valor que se utilizará para enmascarar los caracteres intermedios.
     * @return el valor enmascarado según las configuraciones dadas.
     */
    public String maskValue(String value,
                            int minLength,
                            boolean keepStart,
                            int keepStartCount,
                            boolean keepEnd,
                            int keepEndCount,
                            String maskValue) {

        if (value == null) {
            return "";
        }
        if (value.length() < minLength) {
            return value;
        }

        int length = value.length();

        keepStartCount = adjustKeepCount(keepStart, keepStartCount, length);
        keepEndCount = adjustKeepCount(keepEnd, keepEndCount, length);

        handleOverlap(keepStart, keepEnd, length, keepStartCount, keepEndCount);

        StringBuilder maskedValue = new StringBuilder();

        if (keepStart && keepStartCount > 0) {
            maskedValue.append(value, 0, keepStartCount);
        }

        int maskLength = length - keepStartCount - keepEndCount;
        maskedValue.append(maskValue.repeat(Math.max(0, maskLength)));

        if (keepEnd && keepEndCount > 0) {
            maskedValue.append(value.substring(length - keepEndCount));
        }

        return maskedValue.toString();
    }

    private int adjustKeepCount(boolean keepFlag, int keepCount, int length) {
        return keepFlag && keepCount > length ? length : keepCount;
    }

    private void handleOverlap(boolean keepStart, boolean keepEnd, int length,
                               int keepStartCount, int keepEndCount) {
        if (keepStart && keepEnd && (keepStartCount + keepEndCount > length)) {
            int overlap = keepStartCount + keepEndCount - length;
            keepStartCount -= overlap / 2;
            keepEndCount -= overlap - overlap / 2;
        }
    }

    /**
     * Enmascara una lista de valores utilizando las configuraciones definidas en {@link ConfigPrivaCode}.
     *
     * @param values la lista de valores a enmascarar.
     * @return una lista de valores enmascarados. Si la lista de entrada es nula, se devuelve una lista vacía.
     */
    public List<String> maskValues(List<String> values) {
        if (values == null) {
            return new ArrayList<>(); // Retorna una lista vacía si la entrada es nula
        }

        List<String> maskedValues = new ArrayList<>();
        for (String value : values) {
            maskedValues.add(maskValue(value));
        }
        return maskedValues;
    }

    /**
     * Obtiene un resumen de las configuraciones actuales de enmascarado.
     * Este méthod proporciona detalles sobre la longitud mínima, la configuración
     * de conservación de caracteres al inicio y al final, y el valor utilizado
     * para el enmascarado.
     *
     * @return un String que contiene un resumen de las configuraciones actuales
     *         de enmascarado.
     */
    public String getMaskingConfigSummary() {
        return String.format("""
                        Resumen de Configuraciones de Enmascarado:
                        Longitud Mínima Para Enmascarar: %d
                        Conservar Inicio: %s (%d caracteres)
                        Conservar Fin: %s (%d caracteres)
                        Valor de Enmascarado: '%s'""",
                configPrivaCode.getMinLength(),
                configPrivaCode.isKeepStart() ? "Sí" : "No", configPrivaCode.getKeepStartCount(),
                configPrivaCode.isKeepEnd() ? "Sí" : "No", configPrivaCode.getKeepEndCount(),
                configPrivaCode.getMaskValue());
    }
    /**
     * Enmascara los valores sensibles en un objeto JSON de acuerdo con las configuraciones establecidas.
     *
     * @param jsonString la cadena JSON que contiene los datos a enmascarar.
     * @return una nueva cadena JSON con los valores sensibles enmascarados.
     * @throws JsonSyntaxException si la cadena JSON no es válida.
     */
    public String maskJsonData(String jsonString) throws JsonSyntaxException {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject maskedJson = jsonObject.deepCopy();
        List<String> sensitiveHeaders = configPrivaCode.getSensitiveHeadersList();

        for (String key : sensitiveHeaders) {
            if (maskedJson.has(key)) {
                String originalValue = maskedJson.get(key).getAsString();
                String maskedValue = maskValue(originalValue);
                maskedJson.addProperty(key, maskedValue);
            }
        }

        return maskedJson.toString();
    }

}