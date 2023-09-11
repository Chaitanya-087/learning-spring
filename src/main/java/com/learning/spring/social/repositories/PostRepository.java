package com.learning.spring.social.repositories;

import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Post;

public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query(value = "SELECT * FROM posts p WHERE LOWER(CONCAT(p.title,p.content)) LIKE %?1%", nativeQuery = true)
    List<Post> findAllByPattern(String pattern);

    @Query(value = "SELECT p " +
            "FROM Post p " +
            "JOIN p.tags t " +
            "WHERE t.name = ?1")
    List<Post> findPostsByTagName(String tag);

    @Query(value = "SELECT p FROM Post p WHERE p.author.name = ?1")
    List<Post> findPostsByUser(String username);
}
