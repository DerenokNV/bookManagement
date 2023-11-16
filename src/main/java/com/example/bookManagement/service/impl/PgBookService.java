package com.example.bookManagement.service.impl;

import com.example.bookManagement.configuration.properties.AppCacheProperties;
import com.example.bookManagement.model.Book;
import com.example.bookManagement.model.Category;
import com.example.bookManagement.repository.BookRepository;
import com.example.bookManagement.repository.CategoryRepository;
import com.example.bookManagement.service.BookService;
import com.example.bookManagement.web.dto.UpsertBookRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
@Slf4j
public class PgBookService implements BookService {

  private static final String ERROR_BOOK_IN_CATEGORY = "Книги в категории {0} не найдены, в связи с отсутствием такой категории )";

  private final BookRepository bookRepository;

  private final CategoryRepository categoryRepository;

  @Override
  public List<Book> findAll() {
    return bookRepository.findAll();
  }

  @Override
  @Cacheable(cacheNames = AppCacheProperties.CacheNames.DATABASE_FIND_BY_TITLE_AND_AUTHOR, key = "#title + #author")
  public Book findByTitleAndAuthor( String title, String author ) {
    return bookRepository.findByTitleAndAuthor( title, author ).orElseThrow(
            () -> new EntityNotFoundException( MessageFormat.format( "Книга {0} автора {1} не найдена", title, author ) ) );
  }

  @Override
  @Cacheable(cacheNames = AppCacheProperties.CacheNames.DATABASE_FIND_BY_CATEGORY, key = "#categoryDescription")
  public List<Book> findByCategoryName( String categoryDescription ) {
    Optional<Category> categoryOpt = categoryRepository.customFindByDescriptio( categoryDescription );
    if ( ! categoryOpt.isPresent() ) {
      throw new EntityNotFoundException( MessageFormat.format( ERROR_BOOK_IN_CATEGORY, categoryDescription ) );
    }

    List<Book> booksCategories = bookRepository.findByCategoryId( categoryOpt.get().getId() );
    if ( booksCategories.isEmpty() ) {
      throw new EntityNotFoundException( MessageFormat.format( ERROR_BOOK_IN_CATEGORY, categoryDescription ) );
    } else {
      return booksCategories;
    }
  }

  @Override
  public Book findById( Long id ) {
    return bookRepository.findById( id ).orElseThrow(
            () -> new EntityNotFoundException( MessageFormat.format( "Книга c ID {0} не найдена", id ) ) );
  }

  @Override
  @Caching(evict = {
          @CacheEvict(value = "databaseFindByTitleAndAuthor", key = "#book.getTitle() + #book.getAuthor()", beforeInvocation = true),
          @CacheEvict(value = "databaseFindByCategory", key = "#book.getCategoryDescription()", beforeInvocation = true)
  })
  public Book save( UpsertBookRequest book ) {
    return bookRepository.save( new Book( null, book.getTitle(), book.getAuthor(), getCategory( book.getCategoryDescription() ) ) );
  }

  @Override
  @Caching(evict = {
          @CacheEvict(value = "databaseFindByTitleAndAuthor", key = "#book.getTitle() + #book.getAuthor()", beforeInvocation = true),
          @CacheEvict(value = "databaseFindByCategory", key = "#book.getCategoryDescription()", beforeInvocation = true)
  })
  public Book update( Long id, UpsertBookRequest book ) {
    Book updateBook = findById( id );
    updateBook.setTitle( book.getTitle() );
    updateBook.setAuthor( book.getAuthor() );
    updateBook.setCategory( getCategory( book.getCategoryDescription() ) );

    return bookRepository.save( updateBook );
  }

  private Category getCategory( String categoryDescription ) {
    Optional<Category> insertCategoryOpt = categoryRepository.customFindByDescriptio( categoryDescription );
    if ( insertCategoryOpt.isPresent() ) {
      return insertCategoryOpt.get();
    } else {
      return categoryRepository.save( new Category( null, categoryDescription, null ) );
    }
  }

  @Override
  @Transactional
  @Caching(evict = {
          @CacheEvict(value = "databaseFindByTitleAndAuthor", allEntries = true),
          @CacheEvict(value = "databaseFindByCategory", allEntries = true)
  })
  public void deleteById( Long id ) {
    Book deletedBook = findById( id );
    if ( bookRepository.countAllByCategoryId( deletedBook.getCategory().getId() ) > 1 ) {
      bookRepository.deleteById( id );
    } else {
      bookRepository.deleteById( id );
      categoryRepository.deleteById( deletedBook.getCategory().getId() );
    }
  }
}
