package com.learning.spring.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDao {
    

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StudentDao() {
    }

    public int createTable() {
        String query = "CREATE TABLE IF NOT EXISTS Students(id int primary key auto_increment, name varchar(255), score int)";
        System.out.println("table created....");
        return jdbcTemplate.update(query);
    }
}
