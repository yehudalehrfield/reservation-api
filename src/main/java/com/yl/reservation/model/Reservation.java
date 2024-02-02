package com.yl.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reservation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    private String id;
    private String reservationId;
    private String hostId;
    private String guestId;
    private String startDate;
    private String endDate;
    private String notes;
    private String createdDate;
    private String lastUpdated;
}
