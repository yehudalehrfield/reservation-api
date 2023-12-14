package com.yl.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Email {
  public enum EmailType{
    PERSONAL,
    WORK
  }
  private EmailType type;
  private String value;
  private boolean isPrimary;
}
