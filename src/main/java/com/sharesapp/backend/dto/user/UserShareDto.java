package com.sharesapp.backend.dto.user;

import com.sharesapp.backend.dto.share.ShareCompanyDto;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserShareDto extends UserDto {
  private List<ShareCompanyDto> shares;
}
