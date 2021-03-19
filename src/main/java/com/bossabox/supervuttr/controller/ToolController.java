package com.bossabox.supervuttr.controller;

import com.bossabox.supervuttr.controller.dtos.ToolDTO;
import com.bossabox.supervuttr.data.Tool;
import com.bossabox.supervuttr.security.UserPrincipal;
import com.bossabox.supervuttr.service.ToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    public final static Logger log = LoggerFactory.getLogger(ToolController.class.getSimpleName());

    private final Link ALL_TOOLS_LINK = linkTo(methodOn(ToolController.class)
                .getAllTools(null))
                .withRel("tools");

    @Autowired
    private ToolService toolService;


    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public CollectionModel<ToolDTO> getAllTools(Authentication auth) {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();

        var tools = toolService.getAllTools(userPrincipal.getId());
        var dtos = getDtosFromTools(tools);

        return CollectionModel.of(dtos).add(ALL_TOOLS_LINK.withSelfRel());
    }

    @GetMapping(params = {"id"})
    @ResponseStatus(HttpStatus.OK)
    public ToolDTO getToolById(@RequestParam String id, Authentication auth) {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();
        var tool = toolService.getToolById(id, userPrincipal.getId());

        return toolToDto(tool).add(ALL_TOOLS_LINK);
    }

    @GetMapping(params = {"tag"})
    @ResponseStatus(code = HttpStatus.OK)
    public CollectionModel<ToolDTO> getToolsByTag(@RequestParam String tag, Authentication auth) {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();
        var tools = toolService.getToolsByTag(Arrays.asList(tag), userPrincipal.getId());
        var dtos = getDtosFromTools(tools);

        return CollectionModel.of(dtos).add(ALL_TOOLS_LINK);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ToolDTO createTool(@RequestBody ToolDTO toolDTO, Authentication auth) {
        // Validates link
        Tool tool;
        try {
            tool = dtoToTool(toolDTO);
        } catch (URISyntaxException u) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not a valid URL link");
        }

        // Ensures the id is null, so a new document is created
        tool.setId(null);

        var userPrincipal = (UserPrincipal) auth.getPrincipal();

        var savedTool = toolService.createTool(tool, userPrincipal.getId());
        log.info("Tool created");

        return toolToDto(savedTool);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public RepresentationModel<?> deleteTool(@PathVariable String id, Authentication auth) {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();
        toolService.deleteTool(id, userPrincipal.getId());
        log.info("Tool deleted");

        return RepresentationModel.of(Collections.emptyList()).add(ALL_TOOLS_LINK);
    }


    // Private Methods

    private List<ToolDTO> getDtosFromTools(List<Tool> tools) {
        return tools.stream()
                .map(this::toolToDto)
                .collect(Collectors.toList());
    }

    private ToolDTO toolToDto(Tool tool) {
        var dto = new ToolDTO();
        BeanUtils.copyProperties(tool, dto);
        dto.setLink(tool.getLink().toString());

        return dto.add(
                linkTo(methodOn(ToolController.class).getToolById(dto.getId(),
                        null)).withSelfRel(),
                linkTo(methodOn(ToolController.class).deleteTool(dto.getId(),
                        null)).withRel("delete"));
    }

    private Tool dtoToTool(ToolDTO dto) throws URISyntaxException {
        var tool = new Tool();
        BeanUtils.copyProperties(dto, tool);
        tool.setLink(new URI(dto.getLink()));

        // Ensures all tags are lowercase to avoid duplication
        tool.setTags(dto.getTags().stream()
                .map(String::toLowerCase)
                    .collect(Collectors.toSet()));
        return tool;
    }

}
