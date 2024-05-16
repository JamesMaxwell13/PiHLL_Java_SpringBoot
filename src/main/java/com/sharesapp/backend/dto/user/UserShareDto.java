package com.sharesapp.backend.dto.user;

import com.sharesapp.backend.dto.share.ShareCompanyDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserShareDto extends UserDto {
    private List<ShareCompanyDto> shares;
}
