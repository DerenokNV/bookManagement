package com.example.bookManagement.web.controller;

import com.example.bookManagement.model.Book;
import com.example.bookManagement.service.BookService;
import com.example.bookManagement.web.dto.UpsertBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

  private final BookService service;

  @GetMapping
  public List<Book> bookList() {
    return service.findAll();
  }

  @PostMapping
  public Book createBook( @RequestBody UpsertBookRequest request ) {
    return service.save( request );
  }

  @GetMapping("/findTitleAndAuthor")
  @ResponseBody
  public Book findBookFromTitleAndAuthor( @RequestParam String title,
                                          @RequestParam String author ) {
    return service.findByTitleAndAuthor( title, author );
  }

  @GetMapping("/findCategory/{nameCategory}")
  @ResponseBody
  public List<Book> findListBookFromCategory( @PathVariable(name = "nameCategory") String category ) {
    return service.findByCategoryName( category );
  }

  @PutMapping("/{id}")
  public Book updateBook( @PathVariable Long id,
                          @RequestBody UpsertBookRequest request ) {
    return service.update( id, request );
  }

  @GetMapping("/{id}")
  public Book getBookById( @PathVariable Long id ) {
    return service.findById( id );
  }

  @DeleteMapping("/{id}")
  public void deleteBookById( @PathVariable Long id ) {
    service.deleteById( id );
  }

}
