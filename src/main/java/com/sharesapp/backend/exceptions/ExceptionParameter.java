package com.sharesapp.backend.exceptions;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class ExceptionParameter {
  private Date timestamp;
  private String message;
  private String details;
}