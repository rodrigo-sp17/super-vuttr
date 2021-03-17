package com.bossabox.supervuttr.service;

import com.bossabox.supervuttr.data.Tool;
import com.bossabox.supervuttr.error.ToolNotFoundException;
import com.bossabox.supervuttr.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
        return toolRepository.findAll();
    }

    /**
     * Gets a tool with the provided id from the database
     * @param id the id in String format
     * @return the Tool with the provided id
     * @throws ToolNotFoundException if the tool does not exist
     */
    public Tool getToolById(String id) {
        var result = toolRepository.findById(id);
        return result.orElseThrow(ToolNotFoundException::new);
    }

    /**
     * Returns a List of Tool objects that contain all of the tags provided, in any order
     * @param tags the Collection of tag strings to search for
     * @return List of Tool objects that match all of the tags, in any order
     */
    public List<Tool> getToolsByTag(Collection<String> tags) {
        return toolRepository.findToolsWithTags(tags);
    }

    /**
     * Saves the provided tool in the database
     * @param tool the Tool object to add to the database
     * @return the saved Tool, with the inserted Id
     */
    @Transactional
    public Tool saveTool(Tool tool) {
        return toolRepository.save(tool);
    }

    /**
     * Deletes a tool from the database
     * @param toolId the String id of the tool to delete
     * @throws ToolNotFoundException if the Tool does not exist
     */
    @Transactional
    public void deleteTool(String toolId) {
        var toolToDelete = toolRepository.findById(toolId)
                .orElseThrow(ToolNotFoundException::new);

        toolRepository.delete(toolToDelete);
    }
}
