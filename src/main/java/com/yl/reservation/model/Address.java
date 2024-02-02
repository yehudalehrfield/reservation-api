package com.yl.reservation.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Address that = (Address) o;
        return addressLine1.equals(that.addressLine1)
                && (!(addressLine2 != null && that.addressLine2 != null) || addressLine2.equals(that.addressLine2))
                && city.equals(that.city)
                && state.equals(that.state)
                && zip.equals(that.zip);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
