package com.sharesapp.backend.dto.share;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareDto {
    private Long id;

    private Float prevClose;

    private Float high;

    private Float low;

    private Float lastSalePrice;

    private Instant lastUpdated;

    private String symbol;
}

