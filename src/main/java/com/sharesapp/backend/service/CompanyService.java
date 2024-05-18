package com.sharesapp.backend.service;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.ShareDto;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
  Optional<CompanyDto> createCompany(CompanyDto companyDto);

  Optional<CompanyDto> getById(Long id);

  Optional<List<CompanyDto>> getAllCompanies();

  Optional<CompanyDto> updateCompany(Long id, CompanyDto companyDto);

  Optional<CompanyDto> deleteCompany(Long id);

  Optional<List<ShareDto>> getShares(Long id);
}
