package com.bossabox.supervuttr.service;

import com.bossabox.supervuttr.data.Tool;
import com.bossabox.supervuttr.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles Tool objects CRUD operations
 */
@Service
public class ToolService {

    @Autowired
    private ToolRepository toolRepository;

    /**
     * Gets all tools from the database
     * @return List of tools
     */
    public List<Tool> getAllTools() {
    }

    /**
     * Gets a tool with the provided id from the database
     * @param id the id in String format
     * @return the Tool with the provided id
     * @throws ToolNotFoundException if the tool does not exist
     */
    public Tool getToolById(String id) {
    }

    /**
     * Saves the provided tool in the database
     * @param tool the Tool object to add to the database
     * @return the saved Tool, with the inserted Id
     */
    @Transactional
    public Tool saveTool(Tool tool) {
    }

    /**
     * Deletes a tool from the database
     * @param toolId the String id of the tool to delete
     */
    @Transactional
    public void deleteTool(String toolId) {
    }
}
