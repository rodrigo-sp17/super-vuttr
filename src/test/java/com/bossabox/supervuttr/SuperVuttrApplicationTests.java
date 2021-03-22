package com.bossabox.supervuttr;

import com.bossabox.supervuttr.controller.dtos.ToolDTO;
import com.bossabox.supervuttr.controller.dtos.UserDTO;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class SuperVuttrApplicationTests {

	private static final String URI = "http://localhost:";
	private static final String API_TOOLS = "/api/tools";
	private static final String API_USER = "/api/user";

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JacksonTester<ToolDTO> toolDtoJson;

	@Autowired
	private JacksonTester<UserDTO> userDtoJson;

	@Autowired
	private TestRestTemplate restTemplate;


	@BeforeAll
	public static void setup(@Autowired MongoClient client) throws Exception {
		var tool1 = new Document();
		tool1.put("ownerId", "randomId");
		tool1.put("description", "Some description");
		tool1.put("link", "http://example.com");
		tool1.put("title", "Tool1 Title");
		tool1.put("tags", Arrays.asList("tag1", "tag2"));

		var tool2 = new Document();
		tool2.put("ownerId", "randomId");
		tool2.put("description", "Another description");
		tool2.put("link", "http://example2.com");
		tool2.put("title", "Tool2 Title");
		tool2.put("tags", Arrays.asList("tag1", "tag3"));

		var tool3 = new Document();
		tool3.put("_id", "tool3id");
		tool3.put("ownerId", "randomId");
		tool3.put("description", "And another description");
		tool3.put("link", "http://example3.com");
		tool3.put("title", "Tool3 Title");
		tool3.put("tags", Arrays.asList("tag4", "tag5"));

		var tool4 = new Document();
		tool4.put("_id", "tool4id");
		tool4.put("ownerId", "notExpectedId");
		tool4.put("description", "And....");
		tool4.put("link", "http://example4.com");
		tool4.put("title", "Tool4 Title");
		tool4.put("tags", Arrays.asList("tag11", "tag12"));

		var tool5 = new Document();
		tool5.put("_id", "tool5id");
		tool5.put("ownerId", "randomId");
		tool5.put("description", "And....");
		tool5.put("link", "http://example4.com");
		tool5.put("title", "Tool5 Title");
		tool5.put("tags", Arrays.asList("tag11", "tag12"));

		client.getDatabase("vuttr-test")
				.getCollection("tools")
				.insertMany(Arrays.asList(tool1, tool2, tool3, tool4, tool5));

		var owner = new Document();
		owner.put("_id", "randomId");
		owner.put("username", "the_other_one");
		owner.put("password", "aHashedPassword");

		client.getDatabase("vuttr-test")
				.getCollection("users").insertOne(owner);
	}

	@AfterAll
	public static void cleanup(@Autowired MongoClient client) {
		client.getDatabase("vuttr-test")
				.getCollection("tools")
				.drop();
		client.getDatabase("vuttr-test").getCollection("users").drop();
	}

	@Test
	void contextLoads() {
	}


	@Test
	@WithUserDetails("the_other_one")
	public void test_getAllTools() throws Exception {
		var uri = URI + port + API_TOOLS + "/all";
		mockMvc.perform(get(new URI(uri)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Tool1 Title")))
				.andExpect(content().string(containsString("Tool2 Title")))
				.andExpect(content().string(containsString("tag1")))
				.andExpect(content().string(containsString("http://example2.com")))
				.andExpect(content().string(not(containsString("Tool4 Title"))));
	}

	@Test
	@WithUserDetails("the_other_one")
	public void test_getToolByTag() throws Exception {
		var uri = URI + port + API_TOOLS + "?tag=tag1";
		mockMvc.perform(get(new URI(uri)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Tool1 Title")))
				.andExpect(content().string(containsString("Tool2 Title")))
				.andExpect(content().string(not(containsString("Tool3 Title"))));

		var uri2 = URI + port + API_TOOLS + "?tag=tag3";
		mockMvc.perform(get(new URI(uri2)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Tool2 Title")))
				.andExpect(content().string(not(containsString("Tool1 Title"))))
				.andExpect(content().string(not(containsString("Tool3 Title"))));
	}

	@Test
	@WithUserDetails("the_other_one")
	public void test_createDeleteTools() throws Exception {
		var dto = new ToolDTO(
				"1",
				"New title",
				"www.example.com",
				"New description",
				Set.of("TAG6", "TAG7")
		);

		var uri = URI + port + API_TOOLS;

		var body = mockMvc.perform(post(new URI(uri))
				.contentType(MediaType.APPLICATION_JSON)
				.content(toolDtoJson.write(dto).getJson()))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		var responseDto = toolDtoJson.parseObject(body);

		assertEquals(dto.getTitle(), responseDto.getTitle());
		assertEquals(dto.getDescription(), responseDto.getDescription());
		assertTrue(responseDto.getTags().contains("tag6"));
		assertNotNull(responseDto.getId());
		assertNotEquals("1", responseDto.getId());

		// Fetches the added tool
		var uri2 = URI + port + API_TOOLS + "/" + responseDto.getId();
		var body2 = mockMvc.perform(get(new URI(uri2)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var foundDto = toolDtoJson.parseObject(body2);
		assertEquals(dto.getTitle(), foundDto.getTitle());
		assertEquals(dto.getDescription(), foundDto.getDescription());
		assertTrue(foundDto.getTags().contains("tag6"));

		var uri3 = URI + port + API_TOOLS + "/" + responseDto.getId();
		mockMvc.perform(delete(new URI(uri3)))
				.andExpect(status().isNoContent())
				.andExpect(content().string(not(containsString("New title"))))
				.andExpect(content().string(not(containsString("New description"))));

	}

	@Test
	@WithUserDetails("the_other_one")
	public void test_noUnauthorizedDeletion() throws Exception {
		var uri = URI + port + API_TOOLS + "/" + "tool4id";
		mockMvc.perform(delete(new URI(uri))).andExpect(status().isNotFound());

		var uri2 = URI + port + API_TOOLS + "/" + "tool5id";
		mockMvc.perform(delete(new URI(uri2))).andExpect(status().isNoContent());
	}

	@Test
	public void test_createUser() throws Exception {
		// Checks for forbidden access
		var getUri = URI + port + API_TOOLS + "/all";
		assertEquals(HttpStatus.FORBIDDEN,
				restTemplate.getForEntity(getUri, String.class)
						.getStatusCode());

		var dto = new UserDTO("", "other_user",
				"password", "password");
		var createJson = new JSONObject();
		createJson.put("id", dto.getId());
		createJson.put("username", dto.getUsername());
		createJson.put("password", dto.getPassword());
		createJson.put("confirmPassword", dto.getConfirmPassword());

		// Checks for wrong login
		var login = new JSONObject();
		login.put("username", dto.getUsername());
		login.put("password", dto.getPassword());

		var uri = URI + port + "/login";
		assertNull(restTemplate.postForEntity(uri,
				login.toString(),
				String.class).getHeaders().get("Authorization"));

		// Creates user
		var uri2 = URI + port + API_USER;
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		var entity = new HttpEntity<String>(
				createJson.toString(),
				headers);
		var body =
				restTemplate.postForEntity(uri2, entity, String.class).getBody();
		assertTrue(body.contains(dto.getUsername()));
		assertTrue(body.contains("id"));
		assertFalse(body.contains("password"));
		assertFalse(body.contains("confirmPassword"));

		mockMvc.perform(post(new URI(uri2))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson.toString()))
				.andExpect(status().isConflict());

		// Logins again
		var token = restTemplate.postForEntity(
				uri,
				login.toString(),
				String.class).getHeaders().get("Authorization").get(0);

		assertNotEquals("", token);
		assertTrue(token.startsWith("Bearer "));

		// Attempts to access resource, now logged in
		headers = new HttpHeaders();
		headers.setBearerAuth(token);
		assertEquals(HttpStatus.OK,
				restTemplate.exchange(
						getUri,
						HttpMethod.GET,
						new HttpEntity<>(headers),
						String.class).getStatusCode());
	}

	@Test
	public void test_deleteUser() throws Exception {
		var dto = new UserDTO("", "john_doe",
				"password", "password");

		var createJson = new JSONObject();
		createJson.put("id", dto.getId());
		createJson.put("username", dto.getUsername());
		createJson.put("password", dto.getPassword());
		createJson.put("confirmPassword", dto.getConfirmPassword());

		var uri = URI + port + API_USER;
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		var entity = new HttpEntity<String>(
				createJson.toString(),
				headers);
		restTemplate.postForEntity(uri, entity, String.class);

		var login = new JSONObject();
		login.put("username", dto.getUsername());
		login.put("password", dto.getPassword());
		var loginUri = URI + port + "/login";
		var token = restTemplate.postForEntity(
				loginUri,
				login.toString(),
				String.class).getHeaders().get("Authorization").get(0);

		var authHeader = new HttpHeaders();
		authHeader.setBearerAuth(token);
		var deleteUri = URI + port + API_USER + "/anyuser";
		var code = restTemplate.exchange(deleteUri,
				HttpMethod.DELETE,
				new HttpEntity<>(authHeader),
				String.class
		).getStatusCode();

		assertEquals(HttpStatus.FORBIDDEN, code);

		deleteUri = URI + port + API_USER + "/" + dto.getUsername();
		code = restTemplate.exchange(deleteUri,
				HttpMethod.DELETE,
				new HttpEntity<>(authHeader),
				String.class
		).getStatusCode();

		assertEquals(HttpStatus.NO_CONTENT, code);
	}

	@Test
	public void test_unauthorizedAccess() throws Exception {
		var uri = URI + port + API_TOOLS + "/all";
		assertEquals(HttpStatus.FORBIDDEN,
				restTemplate.getForEntity(uri, String.class)
						.getStatusCode());

		assertEquals(HttpStatus.FORBIDDEN,
				restTemplate.postForEntity(uri,
						"anything",
						String.class)
						.getStatusCode());

		var uri2 = URI + port + API_TOOLS + "?tag=tag1";
		assertEquals(HttpStatus.FORBIDDEN,
				restTemplate.getForEntity(uri2, String.class)
						.getStatusCode());

		var uri3 = URI + port + API_TOOLS + "/" + "anyid";
		mockMvc.perform(delete(new URI(uri3)))
				.andExpect(status().isForbidden());
	}

}
