package com.yl.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private ObjectId id;
    private String userId;
    private String lastName;
    private String firstName;
    private List<Phone> phone;
    private List<Email> email;
    private ContactMethod primaryContactMethod;
    private String createdDate;
    private String lastUpdated;
}
