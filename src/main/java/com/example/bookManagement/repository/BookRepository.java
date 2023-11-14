package com.example.bookManagement.repository;

import com.example.bookManagement.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByTitleAndAuthor( String title, String author );

  List<Book> findByCategoryId( Long categoryId );

  int countAllByCategoryId( Long id );

}
