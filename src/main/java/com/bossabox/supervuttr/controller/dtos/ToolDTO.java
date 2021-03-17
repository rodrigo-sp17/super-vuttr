package com.bossabox.supervuttr.controller.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.net.URI;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolDTO extends RepresentationModel<ToolDTO> {
    private String id;
    private String title;
    private String link;
    private String description;
    private Set<String> tags;
}
