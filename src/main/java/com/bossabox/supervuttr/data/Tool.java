package com.bossabox.supervuttr.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

import java.net.URI;
import java.util.Set;

@Document(collection = "tools")
@Data
public class Tool {
    @Id
    private String id;
    private String title;
    private URI link;
    private String description;
    private Set<String> tags;

}
