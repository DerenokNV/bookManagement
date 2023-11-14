package com.example.bookManagement.repository;

import com.example.bookManagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByDescription( String description );

  @Query( value = "select * from app_schema.category ct where trim( upper( ct.description ) ) = trim( upper( :description ) )",
          nativeQuery = true )
  Optional<Category> customFindByDescriptio( String description );

}
