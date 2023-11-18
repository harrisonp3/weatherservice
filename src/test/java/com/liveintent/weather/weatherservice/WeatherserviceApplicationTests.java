package com.liveintent.weather.weatherservice;

import com.liveintent.weather.weatherservice.service.WeatherService;
import com.liveintent.weather.weatherservice.web.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.json.simple.JSONObject;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherserviceApplicationTests {


	@Autowired
	private WeatherController controller;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private WeatherService service;

	@Test
	void contextLoads() throws Exception {
		System.out.println("HPAUP HERE I AM RUNNING TEST ************************");
		assertThat(controller).isNotNull();
	}
	/**@Test
	void contextLoads() {
	}*/

	@Test
	void shouldReturnHumidityInMessage() throws Exception {
		// default response from frontend should include this value, otherwise something is broken
		this.mockMvc.perform(get("/api/forecast?units=I&city=miami")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("humidity")));
	}

	@Test
	void getSafelyExtractValueAsStringMethodShouldReturnEmptyString() throws Exception {
		JSONObject item = new JSONObject();
		System.out.println("hpau pchanges reflected here ..... **** %%%% &&&");
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
		//@todo hpaup note that this isn't passing because the method only returns strings if string is found
		JSONObject item = new JSONObject();
		item.put("dummy_key1", (double) 1);
		assertEquals("", getSafelyExtractValueAsStringMethod().invoke(service, item, "dummy_key1"));
	}
	@Test
	void getSafelyExtractValueAsStringMethodShouldReturnStringValueAsString() throws Exception {
		//@todo hpaup note that this isn't passing because the method only returns strings if string is found
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
	/**
	 * private Method getDoubleIntegerMethod() throws NoSuchMethodException {
	 *     Method method = Utils.class.getDeclaredMethod("doubleInteger", Integer.class);
	 *     method.setAccessible(true);
	 *     return method;
	 * }
	 */

}
