package com.bossabox.supervuttr.controller;

import com.bossabox.supervuttr.controller.dtos.ToolDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/tools")
public class ToolController {

    @GetMapping
    public CollectionModel<ToolDTO> getAllTools() {
        throw new UnsupportedOperationException();
    }

    @GetMapping
    public CollectionModel<ToolDTO> getToolsByTag(@RequestParam String tag) {
        throw new UnsupportedOperationException();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ToolDTO createTool(@RequestBody ToolDTO toolDTO) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteTool(@PathVariable String id) {
        throw new UnsupportedOperationException();
    }

}
