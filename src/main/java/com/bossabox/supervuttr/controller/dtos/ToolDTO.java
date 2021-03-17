package com.bossabox.supervuttr.controller.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.net.URI;
import java.util.Set;

@Data
public class ToolDTO extends RepresentationModel<ToolDTO> {
    private String id;
    private String title;
    private URI link;
    private String description;
    private Set<String> tags;
}
