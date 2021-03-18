package com.bossabox.supervuttr.repository;

import com.bossabox.supervuttr.data.Tool;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ToolRepositoryTest {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private MongoClient mongoClient;

    // Prepopulates the database with test data
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
        tool3.put("_id", "threeid");
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
    public void test_insertTool() throws Exception {
        var tool = new Tool();
        tool.setTitle("Title4");
        tool.setDescription("Some description");
        tool.setLink(new URI("http://example.com"));
        tool.setTags(Set.of("tag6", "tag7"));

        var result = toolRepository.insert(tool);
        System.out.println(result);
        assertNotNull(result);

        var query = new Document("title", "Title4");
        var doc = mongoClient
                .getDatabase("vuttr-test")
                .getCollection("tools")
                .find(query)
                .first();

        assertNotNull(doc);
        assertEquals("Title4", doc.getString("title"));
    }

    @Test
    public void test_findAllTools() {
        var tools = toolRepository.findAll();
        assertTrue(tools.size() >= 2);

        var titles = tools.stream()
                .map(Tool::getTitle)
                .collect(Collectors.toList());

        System.out.println(titles);

        assertTrue(titles.contains("Tool1 Title"));
        assertTrue(titles.contains("Tool2 Title"));
    }

    @Test
    public void test_findToolByTags() {
        var wrongTags = Arrays.asList("tag1", "tag6");
        var rightTags = Arrays.asList("tag3", "tag1");

        var noTools = toolRepository.findToolsWithTags(wrongTags);
        var tools = toolRepository.findToolsWithTags(rightTags);

        assertTrue(noTools.isEmpty());
        assertEquals(1, tools.size());

        var tool = tools.get(0);
        assertEquals("Tool2 Title", tool.getTitle());
    }

    @Test
    public void test_deleteTool() {
        var returnedTool = toolRepository.findById("threeid");
        assertTrue(returnedTool.isPresent());

        toolRepository.delete(returnedTool.get());

        var result = toolRepository.findById("threeid");
        assertTrue(result.isEmpty());
    }
}
