package com.bossabox.supervuttr.repository;

import com.bossabox.supervuttr.data.Tool;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends MongoRepository<Tool, String> {

    List<Tool> findByOwnerId(String ownerId);
    Optional<Tool> findByIdAndOwnerId(String id, String ownerId);

    @Query("{$and: [{'ownerId': ?1}, {'tags': {$all: ?0}}] }")
    List<Tool> findToolsWithTags(Collection<String> tags, String ownerId);
}
