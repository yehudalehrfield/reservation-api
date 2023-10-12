package com.yl.reservation.host.model;

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
  private PhoneType type;
  private String value;
  private boolean isPrimary;

}
