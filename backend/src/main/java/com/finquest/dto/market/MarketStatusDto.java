package com.finquest.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketStatusDto {
    private String exchange;
    private MarketStatus status;
    private String message;
    private Instant nextOpenTime;
    private Instant nextCloseTime;
    private String timezone;
    private boolean providerLive;
    private String dataSource;
    private boolean isSimulated;
}
