package com.phonecompany.billing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CallDetail {

    private final String phoneNumber;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

}
