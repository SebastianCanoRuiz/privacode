package com.sebastiancano.privatecode;

import com.sebastiancano.privatecode.config.ConfigPrivaCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

class DataShieldTest {
    @Mock
    private ConfigPrivaCode configPrivaCode;

    @InjectMocks
    private DataShield dataShield;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFilterSensitiveDataHeaders() {
        when(configPrivaCode.getSensitiveHeadersList()).thenReturn(Arrays.asList("Password", "Credit-Card"));

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", "12345");
        headers.put("Password", "secret");
        headers.put("Credit-Card", "1234-5678-9012-3456");

        Map<String, String> filteredHeaders = dataShield.filterSensitiveDataHeaders(headers);

        Assertions.assertFalse(filteredHeaders.containsKey("Password"));
        Assertions.assertFalse(filteredHeaders.containsKey("Credit-Card"));
        Assertions.assertTrue(filteredHeaders.containsKey("User-Id"));
    }

    @Test
    public void testMaskSensitiveDataHeaders() {
        when(configPrivaCode.getSensitiveHeadersList()).thenReturn(Arrays.asList("Password", "Credit-Card"));
        when(configPrivaCode.getMinLength()).thenReturn(2);
        when(configPrivaCode.getMaskValue()).thenReturn("*");

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", "12345");
        headers.put("Password", "secret123");
        headers.put("Credit-Card", "1234-5678-9012-3456");

        Map<String, String> maskedHeaders = dataShield.maskSensitiveDataHeaders(headers);

        Assertions.assertEquals("*********", maskedHeaders.get("Password"));
        Assertions.assertEquals("*******************", maskedHeaders.get("Credit-Card"));
        Assertions.assertEquals("12345", maskedHeaders.get("User-Id"));
    }

    @Test
    public void testMaskValue() {
        when(configPrivaCode.getMinLength()).thenReturn(6);
        when(configPrivaCode.isKeepStart()).thenReturn(true);
        when(configPrivaCode.getKeepStartCount()).thenReturn(2);
        when(configPrivaCode.isKeepEnd()).thenReturn(true);
        when(configPrivaCode.getKeepEndCount()).thenReturn(2);
        when(configPrivaCode.getMaskValue()).thenReturn("*");

        String maskedValue = dataShield.maskValue("SensitiveData123");

        Assertions.assertEquals("Se************23", maskedValue);
    }

    @Test
    public void testGetMaskingConfigSummary() {
        when(configPrivaCode.getMinLength()).thenReturn(6);
        when(configPrivaCode.isKeepStart()).thenReturn(true);
        when(configPrivaCode.getKeepStartCount()).thenReturn(2);
        when(configPrivaCode.isKeepEnd()).thenReturn(true);
        when(configPrivaCode.getKeepEndCount()).thenReturn(2);
        when(configPrivaCode.getMaskValue()).thenReturn("*");

        String summary = dataShield.getMaskingConfigSummary();

        Assertions.assertTrue(summary.contains("Longitud Mínima Para Enmascarar: 6"));
        Assertions.assertTrue(summary.contains("Conservar Inicio: Sí (2 caracteres)"));
        Assertions.assertTrue(summary.contains("Conservar Fin: Sí (2 caracteres)"));
        Assertions.assertTrue(summary.contains("Valor de Enmascarado: '*'"));
    }

    @Test
    public void testMaskJsonData() {
        when(configPrivaCode.getSensitiveHeadersList()).thenReturn(Arrays.asList("Password", "User-Id"));
        when(configPrivaCode.getMinLength()).thenReturn(6);
        when(configPrivaCode.getMaskValue()).thenReturn("*");

        String jsonData = "{ \"User-Id\": \"secretUser\", \"Password\": \"secretPass\", \"Other-Info\": \"No sensitive info\" }";

        String maskedJson = dataShield.maskJsonData(jsonData);

        Assertions.assertTrue(maskedJson.contains("\"User-Id\":\"**********\"")); // Comprobar enmascaramiento
        Assertions.assertTrue(maskedJson.contains("\"Password\":\"**********\""));
        Assertions.assertTrue(maskedJson.contains("\"Other-Info\":\"No sensitive info\""));
    }

    @Test
    public void testMaskValueWithNull() {
        String result = dataShield.maskValue(null);
        Assertions.assertEquals("", result);
    }

    @Test
    public void testMaskValuesWithNull() {
        List<String> maskedValues = dataShield.maskValues(null);
        Assertions.assertTrue(maskedValues.isEmpty());
    }
}