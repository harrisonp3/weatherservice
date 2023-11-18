package com.liveintent.weather.weatherservice;

import com.liveintent.weather.weatherservice.service.WeatherService;
import com.liveintent.weather.weatherservice.web.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.json.simple.JSONObject;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherserviceApplicationTests {
	@Autowired
	private WeatherController controller;
	@MockBean
	private WeatherService service;

	@Test
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
	@Test
	void getSafelyExtractValueAsStringMethodShouldReturnEmptyString() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", 1);
		assertEquals("", getSafelyExtractValueAsStringMethod().invoke(service, item, "dummy_key2"));
	}
	@Test
	void getSafelyExtractValueAsStringMethodShouldReturnIntValueAsEmptyString() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", 1);
		assertEquals("", getSafelyExtractValueAsStringMethod().invoke(service, item, "dummy_key1"));
	}
	@Test
	void getSafelyExtractValueAsStringMethodShouldReturnDoubleValueAsEmptyString() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", (double) 1);
		assertEquals("", getSafelyExtractValueAsStringMethod().invoke(service, item, "dummy_key1"));
	}
	@Test
	void getSafelyExtractValueAsStringMethodShouldReturnStringValueAsString() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", "testing123");
		assertEquals("testing123", getSafelyExtractValueAsStringMethod().invoke(service, item, "dummy_key1"));
	}
	private Method getSafelyExtractValueAsStringMethod() throws NoSuchMethodException {
		Method method = WeatherService.class.getDeclaredMethod(
				"safelyExtractValueAsString",
				JSONObject.class,
				String.class
		);
		method.setAccessible(true);
		return method;
	}
	private Method getSafelyExtractNumberValueAsLong() throws NoSuchMethodException {
		Method method = WeatherService.class.getDeclaredMethod("safelyExtractNumberValueAsLong", JSONObject.class, String.class);
		method.setAccessible(true);
		return method;
	}
	@Test
	void getSafelyExtractNumberValueAsLongMethodShouldReturnDoubleValueAsLong() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", (double) 1);
		assertEquals((long) 1, getSafelyExtractNumberValueAsLong().invoke(service, item, "dummy_key1"));
	}
	@Test
	void getSafelyExtractNumberValueAsLongMethodShouldReturnStringValueAsLong() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", "1");
		assertEquals((long) 1, getSafelyExtractNumberValueAsLong().invoke(service, item, "dummy_key1"));
	}
	@Test
	void getSafelyExtractNumberValueAsLongMethodShouldReturnLongValueAsLong() throws Exception {
		JSONObject item = new JSONObject();
		item.put("dummy_key1", (long) 1);
		assertEquals((long) 1, getSafelyExtractNumberValueAsLong().invoke(service, item, "dummy_key1"));
	}
	private Method getReplaceSpaces() throws NoSuchMethodException {
		Method method = WeatherService.class.getDeclaredMethod("replaceSpaces", String.class);
		method.setAccessible(true);
		return method;
	}
	@Test
	void getReplaceSpacesMethodShouldReturnStringWithSpacesReplaced() throws Exception {
		String temp = "www.google.com/los angeles";
		assertEquals("www.google.com/los%20angeles", getReplaceSpaces().invoke(service, temp));
	}
	@Test
	void getReplaceSpacesMethodShouldReturnStringThatHadNoSpacesWithNoChanges() throws Exception {
		String temp = "www.google.com/losangeles";
		assertEquals("www.google.com/losangeles", getReplaceSpaces().invoke(service, temp));
	}
}
