package com.bossabox.supervuttr.service;

import com.bossabox.supervuttr.data.Tool;
import com.bossabox.supervuttr.error.ToolNotFoundException;
import com.bossabox.supervuttr.repository.ToolRepository;
import com.bossabox.supervuttr.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    /**
     * Gets all tools from the database
     * @param ownerId the id of the resource owner
     * @return List of tools
     */
    public List<Tool> getAllTools(String ownerId) {
        return toolRepository.findByOwnerId(ownerId);
    }

    /**
     * Gets a tool with the provided id from the database
     * @param toolId the id in String format
     * @param ownerId the id of the resource owner
     * @return the Tool with the provided id
     * @throws ToolNotFoundException if the tool does not exist
     */
    public Tool getToolById(String toolId, String ownerId) {
        var result = toolRepository.findByIdAndOwnerId(toolId, ownerId);
        return result.orElseThrow(ToolNotFoundException::new);
    }

    /**
     * Returns a List of Tool objects that contain all of the tags provided, in any order
     * @param tags the Collection of tag strings to search for
     * @param ownerId the id of the resource owner
     * @return List of Tool objects that match all of the tags, in any order
     */
    public List<Tool> getToolsByTag(Collection<String> tags, String ownerId) {
        return toolRepository.findToolsWithTags(tags, ownerId);
    }

    /**
     * Saves the provided tool in the database
     * @param tool the Tool object to add to the database
     * @param ownerId the id of the resource owner
     * @return the saved Tool, with the inserted Id
     */
    @Transactional
    public Tool createTool(Tool tool, String ownerId) {
        tool.setId(null);
        tool.setOwnerId(ownerId);
        return toolRepository.save(tool);
    }

    /**
     * Deletes a tool from the database
     * @param toolId the String id of the tool to delete
     * @param ownerId the id of the resource owner
     * @throws ToolNotFoundException if the Tool does not exist or
     * if the ownerId is not the owner of the resource
     */
    @Transactional
    public void deleteTool(String toolId, String ownerId) {
        var toolToDelete = toolRepository.findByIdAndOwnerId(toolId, ownerId)
                .orElseThrow(ToolNotFoundException::new);

        toolRepository.delete(toolToDelete);
    }
}
