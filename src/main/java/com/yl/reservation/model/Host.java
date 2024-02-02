package com.yl.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "host")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host {
    @Id
    private String id;
    private String hostId;
    private String userId;
    private Address address;
    private int beds;
    private Boolean crib;
    private Boolean fullBath;
    private Boolean privateEntrance;
    private String notes;
    private String createdDate;
    private String lastUpdated;

}
