package com.yl.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Phone {
  public enum PhoneType{
    HOME,
    WORK,
    MOBILE
  }
  private PhoneType phoneType;
  private String value;
  private boolean isPrimary;

}
