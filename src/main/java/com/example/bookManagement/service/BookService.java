package com.example.bookManagement.service;

import com.example.bookManagement.model.Book;
import com.example.bookManagement.web.dto.UpsertBookRequest;

import java.util.List;

public interface BookService {

  List<Book> findAll();

  Book findByTitleAndAuthor( String title, String author );

  List<Book> findByCategoryName( String categotyDescript );

  Book findById( Long id );

  Book save( UpsertBookRequest book );

  Book update( Long id, UpsertBookRequest book );

  void deleteById( Long id );
}
