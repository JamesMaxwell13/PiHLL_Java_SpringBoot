package com.sharesapp.backend.dto.share;

import com.sharesapp.backend.dto.CompanyDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShareCompanyDto extends ShareDto{
    private CompanyDto company;
}
