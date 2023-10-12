package com.yl.reservation.host.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "host")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host {
    @Id
    private String id;
    private String lastName;
    private String firstName;
    private List<Phone> phone;
    private List<Email> email;
    private int beds;
    private boolean crib;
    private boolean fullBath;
    private boolean privateEntrance;
    private String notes;
    private ContactMethod primaryContactMethod;

}
