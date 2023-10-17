package com.yl.reservation.host.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private State state;
    private String zip;

    @Override
    public String toString() {
        return addressLine1 + (addressLine2 == null ? "" : " " + addressLine2) +
                ", " + city + " " + state + ", " + zip;
    }
}
