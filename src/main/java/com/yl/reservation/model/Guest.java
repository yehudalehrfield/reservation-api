package com.yl.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="guest")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest {
    @Id
    private String id;
    private String guestId;
    private String userId;
    private String nickName;
    private int numAdults;
    private int numChildren;
    private Boolean crib;
    private String notes;
    private String createdDate;
    private String lastUpdated;
}
