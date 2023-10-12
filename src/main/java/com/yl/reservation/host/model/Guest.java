package com.yl.reservation.host.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Guest {
    private String lastName;
    private String firstName;
    private List<Phone> phone;
    private List<Phone> email;
    private int numAdults;
    private int numChildren;
    private boolean crib;
    private int numNights;
    private ContactMethod primaryContactMethod;

}
