package com.kristiGoydykova.restTemplate.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kristiGoydykova.restTemplate.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class RestTemplateApplication {

	private static final String GET_POST_PUT_Url = "http://91.241.64.178:7081/api/users";
	private static final String DELETE_Url = "http://91.241.64.178:7081/api/users/{id}";


	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(RestTemplateApplication.class, args);

		//HttpHeader
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		//HttpEntity для request
		HttpEntity<String> entityString = new HttpEntity<>(httpHeaders);

		//RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		//Отправляем request методом GET, вместе с Header'ами
		ResponseEntity<String> responseEntity = restTemplate.exchange(GET_POST_PUT_Url, HttpMethod.GET, entityString, String.class);

		//Получаем set-cookie
		String cookies = responseEntity.getHeaders().getFirst("Set-Cookie");

		//Смотрим session id и тело ответа
		System.out.println("Cookies: " + cookies);
		System.out.println("Тело ответа: " + responseEntity.getBody());

		//Устанавливаем куки и header для всех запросов RestTemplate
		// Поэтому такая запись станет не нужна: httpHeaders.set("Cookie", cookies);
		restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
				request.getHeaders().set("Cookie", cookies);
				return execution.execute(request, body);
			}
		});

		//POST
		User newUser = new User(3L, "James", "Brown", (byte) 22);
//		httpHeaders.set("Cookie", cookies);
		HttpEntity<User> postUser = new HttpEntity<>(newUser, httpHeaders);
		ResponseEntity<String> resultPOST = restTemplate.exchange(GET_POST_PUT_Url, HttpMethod.POST, postUser, String.class);
		System.out.println("1 Результат: " + resultPOST.getBody());


		//PUT
		User changedUser  = new User(3L, "Thomas", "Shelby", (byte) 22);
//		httpHeaders.set("Cookie", cookies);
		HttpEntity<User> putUser = new HttpEntity<>(changedUser, httpHeaders);
		ResponseEntity<String> resultPut = restTemplate.exchange(GET_POST_PUT_Url, HttpMethod.PUT, putUser, String.class);
		System.out.println("2 Результат: " + resultPut.getBody());

		//DELETE

		Map<String, Long> params = new HashMap<>();
		params.put("id", 3L);
//		httpHeaders.set("Cookie", cookies);
		ResponseEntity<String> resultDELETE = restTemplate.exchange(DELETE_Url, HttpMethod.DELETE, null, String.class, params);
		System.out.println("3 Результат: " + resultDELETE.getBody());

	}
}
