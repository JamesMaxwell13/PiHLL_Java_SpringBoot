package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.model.Company;
import com.sharesapp.backend.repository.CompanyRepository;
import com.sharesapp.backend.service.CompanyService;
import com.sharesapp.backend.utils.cache.GenericCache;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
  private final CompanyRepository companyRepository;
  private final GenericCache<Long, Company> cache;
  private final ModelMapper modelMapper;

  @Autowired
  public CompanyServiceImpl(CompanyRepository companyRepository, GenericCache<Long, Company> cache,
                            ModelMapper modelMapper) {
    this.companyRepository = companyRepository;
    this.cache = cache;
    this.modelMapper = modelMapper;
  }

  @Override
  public Optional<CompanyDto> createCompany(CompanyDto companyDto) {
    Company savedCompany = companyRepository.save(modelMapper.map(companyDto, Company.class));
    cache.clear();
    return Optional.of(modelMapper.map(savedCompany, CompanyDto.class));
  }

  @Override
  public Optional<CompanyDto> getById(Long id) {
    Company company = cache.get(id).orElseGet(() -> companyRepository.findById(id).orElse(null));
    if (company == null) {
      return Optional.empty();
    }
    cache.put(id, company);
    return Optional.of(modelMapper.map(company, CompanyDto.class));
  }

  @Override
  public Optional<List<CompanyDto>> getAllCompanies() {
    List<Company> companies = companyRepository.findAll();
    if (companies.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Arrays.asList(modelMapper.map(companies, CompanyDto[].class)));
  }

  @Override
  public Optional<CompanyDto> updateCompany(Long id, CompanyDto companyDto) {
    Company company = companyRepository.findById(id).orElse(null);
    if (company == null) {
      return Optional.empty();
    }
    cache.remove(id);
    companyDto.setId(id);
    Company updatedCompany = companyRepository.save(modelMapper.map(companyDto, Company.class));
    cache.put(id, updatedCompany);
    return Optional.of(modelMapper.map(updatedCompany, CompanyDto.class));
  }

  @Override
  public Optional<CompanyDto> deleteCompany(Long id) {
    Company company = companyRepository.findById(id).orElse(null);
    if (company == null) {
      return Optional.empty();
    }
    cache.remove(id);
    companyRepository.deleteById(id);
    return Optional.of(modelMapper.map(company, CompanyDto.class));
  }

  @Override
  public Optional<List<ShareDto>> getShares(Long id) {
    Company company = cache.get(id).orElseGet(() -> companyRepository.findById(id).orElse(null));
    if (company == null) {
      return Optional.empty();
    }
    cache.put(id, company);
    return Optional.of(Arrays.asList(modelMapper.map(company.getShares(), ShareDto[].class)));
  }
}