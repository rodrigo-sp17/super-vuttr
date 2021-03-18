package com.bossabox.supervuttr;

import com.bossabox.supervuttr.controller.dtos.ToolDTO;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
public class SuperVuttrApplicationTests {

	private static final String URI = "http://localhost:";
	private static final String API = "/api/tools";

	@LocalServerPort
	private int port;

	@Autowired
	private JacksonTester<ToolDTO> dtoJson;

	@Autowired
	private TestRestTemplate restTemplate;


	@BeforeAll
	public static void setup(@Autowired MongoClient client) throws Exception {
		var tool1 = new Document();
		tool1.put("description", "Some description");
		tool1.put("link", "http://example.com");
		tool1.put("title", "Tool1 Title");
		tool1.put("tags", Arrays.asList("tag1", "tag2"));

		var tool2 = new Document();
		tool2.put("description", "Another description");
		tool2.put("link", "http://example2.com");
		tool2.put("title", "Tool2 Title");
		tool2.put("tags", Arrays.asList("tag1", "tag3"));

		var tool3 = new Document();
		tool3.put("description", "And another description");
		tool3.put("link", "http://example3.com");
		tool3.put("title", "Tool3 Title");
		tool3.put("tags", Arrays.asList("tag4", "tag5"));

		client.getDatabase("vuttr-test")
				.getCollection("tools")
				.insertMany(Arrays.asList(tool1, tool2, tool3));
	}

	@AfterAll
	public static void cleanup(@Autowired MongoClient client) {
		client.getDatabase("vuttr-test")
				.getCollection("tools")
				.drop();
	}

	@Test
	void contextLoads() {
	}


	@Test
	public void test_getAllTools() throws Exception {
		var uri = URI + port + API;
		var body = restTemplate.getForEntity(uri, String.class).getBody();

		assertTrue(body.contains("Tool1 Title"));
		assertTrue(body.contains("Tool2 Title"));
		assertTrue(body.contains("tag1"));
		assertTrue(body.contains("http://example2.com"));
	}

	@Test
	public void test_getToolByTag() {
		var uri = URI + port + API + "?tag=tag1";
		var body = restTemplate.getForEntity(uri, String.class).getBody();

		assertTrue(body.contains("Tool1 Title"));
		assertTrue(body.contains("Tool2 Title"));
		assertFalse(body.contains("Tool3 Title"));

		var uri2 = URI + port + API + "?tag=tag3";
		var body2 = restTemplate.getForEntity(uri2, String.class).getBody();

		assertFalse(body2.contains("Tool1 Title"));
		assertTrue(body2.contains("Tool2 Title"));
		assertFalse(body2.contains("Tool3 Title"));
	}

	@Test
	public void test_createDeleteTools() throws IOException {
		var dto = new ToolDTO(
				"1",
				"New title",
				"www.example.com",
				"New description",
				Set.of("TAG6", "TAG7")
		);

		var uri = URI + port + API;
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		var entity = new HttpEntity<String>(
				dtoJson.write(dto).getJson(),
				headers);
		var body = restTemplate.postForEntity(
				uri,
				entity,
				String.class)
				.getBody();
		var responseDto = dtoJson.parseObject(body);

		assertEquals(dto.getTitle(), responseDto.getTitle());
		assertEquals(dto.getDescription(), responseDto.getDescription());
		assertTrue(responseDto.getTags().contains("tag6"));
		assertNotNull(responseDto.getId());
		assertNotEquals("1", responseDto.getId());

		var uri2 = URI + port + API + "?id=" + responseDto.getId();
		var body2 = restTemplate.getForEntity(uri2, String.class).getBody();

		var foundDto = dtoJson.parseObject(body2);
		assertEquals(dto.getTitle(), foundDto.getTitle());
		assertEquals(dto.getDescription(), foundDto.getDescription());
		assertTrue(foundDto.getTags().contains("tag6"));

		var uri3 = URI + port + API + "/" + responseDto.getId();
		restTemplate.delete(uri3);

		var body3 = restTemplate.getForEntity(uri2, String.class).getBody();
		assertFalse(body3.contains("New title"));
		assertFalse(body3.contains("New description"));
	}

}
