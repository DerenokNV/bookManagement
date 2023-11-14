package com.example.bookManagement.web.dto;

import lombok.Data;

@Data
public class UpsertBookRequest {

  private String title;

  private String author;

  private String categoryDescription;
}
