package com.bossabox.supervuttr.controller;

import com.bossabox.supervuttr.controller.dtos.ToolDTO;
import com.bossabox.supervuttr.data.Tool;
import com.bossabox.supervuttr.service.ToolService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ToolController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureJsonTesters
public class ToolControllerTest {

    @Autowired
    private ToolController toolController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ToolService toolService;

    @Autowired
    private JacksonTester<ToolDTO> dtoJson;

    @Test
    public void test_getAllTools() throws Exception {

        mvc.perform(get(new URI("/api/tools")))
                .andExpect(status().isOk());

        verify(toolService, atLeastOnce()).getAllTools();
    }

    @Test
    public void test_getToolsByTags() throws Exception {
        mvc.perform(get(new URI("/api/tools"))
                .param("tag", "tag1"))
                .andExpect(status().isOk());

        verify(toolService, atLeast(1))
                .getToolsByTag(Arrays.asList("tag1"));
    }

    @Test
    public void test_createTools() throws Exception {
        var json = new JSONObject();
        json.put("title", "my title");
        json.put("description", "my description");
        json.put("link", "my link");
        var arr = new JSONArray();
        arr.put("tag1");
        arr.put("tag2");
        json.put("tags", arr);


        var tool = new Tool();
        tool.setId("id1");
        tool.setTitle("my title");
        tool.setDescription("my description");
        tool.setLink(new URI("www.example.com"));
        tool.setTags(Set.of("tag1", "tag2"));

        when(toolService.saveTool(any())).thenReturn(tool);

        // Test for invalid link
        mvc.perform(post(new URI("/api/tools"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest());

        verify(toolService, atMost(0)).saveTool(any());

        // Test for right link
        json.put("link", "www.example.com");

        var content = mvc.perform(post(new URI("/api/tools"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var resultDTO = dtoJson.parse(content).getObject();

        assertEquals(tool.getTitle(), resultDTO.getTitle());
        assertEquals(tool.getDescription(), resultDTO.getDescription());
        assertEquals(tool.getLink().toString(), resultDTO.getLink());
        assertTrue(resultDTO.getTags().contains("tag1"));
        assertTrue(resultDTO.getTags().contains("tag2"));
        assertEquals(tool.getId(), resultDTO.getId());

        verify(toolService, atLeastOnce()).saveTool(any());
    }

    @Test
    public void test_deleteTools() throws Exception {
        mvc.perform(delete(new URI("/api/tools/" + "id1")))
                .andExpect(status().isNoContent());

        verify(toolService, atLeastOnce()).deleteTool(eq("id1"));
    }

}
