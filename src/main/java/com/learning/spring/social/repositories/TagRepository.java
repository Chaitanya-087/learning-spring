package com.learning.spring.social.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.spring.social.entities.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer>  {
    
}
