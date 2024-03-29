package com.yl.reservation.service.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostSearchResponse {
    private String message;
    private List<HostDetails> hostDetailsList;
}
