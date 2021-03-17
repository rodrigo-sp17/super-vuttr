package com.bossabox.supervuttr.repository;

import com.bossabox.supervuttr.data.Tool;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ToolRepository extends MongoRepository<Tool, String> {

    @Query("{'tags': { $all : ?0 }}")
    List<Tool> findToolsWithTags(Collection<String> tags);
}
