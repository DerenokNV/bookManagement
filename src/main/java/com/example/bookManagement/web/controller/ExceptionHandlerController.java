package com.example.bookManagement.web.controller;

import com.example.bookManagement.web.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorResponse notFound( EntityNotFoundException ex ) {
    log.error( "GlobalExceptionHandler. Error when trying to get entity (Ошибка при попытке получить сущность): ", ex );
    return new ErrorResponse( ex.getLocalizedMessage() );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse notValid( MethodArgumentNotValidException ex ) {
    log.error( "GlobalExceptionHandler. notValid: ", ex );
    BindingResult bindingResult = ex.getBindingResult();
    List<String> errorMessages = bindingResult.getAllErrors().stream()
            .map( DefaultMessageSourceResolvable::getDefaultMessage )
            .toList();

    String errorMessage = String.join( ";", errorMessages );

    return new ErrorResponse( errorMessage );
  }

  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse invocationTarget( NullPointerException ex) {
    log.error( "GlobalExceptionHandler. NPE: ", ex );
    return new ErrorResponse( ex.getLocalizedMessage() );
  }

  @ExceptionHandler(HttpMessageNotWritableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse httpMessageNotWritableException( HttpMessageNotWritableException ex) {
    log.error( "GlobalExceptionHandler. HttpMessageNotWritableException: ", ex );
    return new ErrorResponse( ex.getLocalizedMessage() );
  }

}
