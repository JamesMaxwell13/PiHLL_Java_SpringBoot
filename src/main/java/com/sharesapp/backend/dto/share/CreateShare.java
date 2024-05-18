package com.sharesapp.backend.dto.share;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class CreateShare extends ShareDto {
  private Long companyId;
}
