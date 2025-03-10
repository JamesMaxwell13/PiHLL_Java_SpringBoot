package com.sharesapp.backend.dto.share;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareDto {
  private Long id;

  private Float prevClosePrice;

  private Float highPrice;

  private Float lowPrice;

  private Float openPrice;

  private Float lastSalePrice;

  private Instant lastTimeUpdated;

  private String symbol;
}

