package com.yl.reservation.service;

import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostSearchResponse {
    private String message;
    private List<HostDetails> hostDetails;
}
