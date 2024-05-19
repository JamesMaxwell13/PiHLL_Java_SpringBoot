package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.aspect.Logging;
import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.exceptions.BadRequestException;
import com.sharesapp.backend.exceptions.NotFoundException;
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

  private static final String COMPANY_ERROR_MESSAGE = "There is no company with id = ";

  @Autowired
  public CompanyServiceImpl(CompanyRepository companyRepository, GenericCache<Long, Company> cache,
                            ModelMapper modelMapper) {
    this.companyRepository = companyRepository;
    this.cache = cache;
    this.modelMapper = modelMapper;
  }

  @Logging
  @Override
  public Optional<CompanyDto> createCompany(CompanyDto companyDto) throws BadRequestException {
    if (companyDto.getName().isEmpty()) {
      throw new BadRequestException("Wrong company name");
    }
    Company savedCompany = companyRepository.save(modelMapper.map(companyDto, Company.class));
    cache.clear();
    return Optional.of(modelMapper.map(savedCompany, CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<CompanyDto> getById(Long id) throws NotFoundException {
    Company company = cache.get(id).orElseGet(() -> companyRepository.findById(id).orElse(null));
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE, id);
    }
    cache.put(id, company);
    return Optional.of(modelMapper.map(company, CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<List<CompanyDto>> getAllCompanies() throws NotFoundException {
    List<Company> companies = companyRepository.findAll();
    if (companies.isEmpty()) {
      throw new NotFoundException("There are no companies");
    }
    return Optional.of(Arrays.asList(modelMapper.map(companies, CompanyDto[].class)));
  }

  @Logging
  @Override
  public Optional<CompanyDto> updateCompany(Long id, CompanyDto companyDto)
      throws BadRequestException {
    Company company = companyRepository.findById(id).orElse(null);
    if (company == null || companyDto.getName() == null || companyDto.getName().isEmpty()) {
      throw new BadRequestException("Wrong company name or there is no such company");
    }
    cache.remove(id);
    companyDto.setId(id);
    Company updatedCompany = companyRepository.save(modelMapper.map(companyDto, Company.class));
    cache.put(id, updatedCompany);
    return Optional.of(modelMapper.map(updatedCompany, CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<CompanyDto> deleteCompany(Long id) throws NotFoundException {
    Company company = companyRepository.findById(id).orElse(null);
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE, id);
    }
    cache.remove(id);
    companyRepository.deleteById(id);
    return Optional.of(modelMapper.map(company, CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<List<ShareDto>> getShares(Long id) {
    Company company = cache.get(id).orElseGet(() -> companyRepository.findById(id).orElse(null));
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE, id);
    }
    if (company.getShares().isEmpty()) {
      throw new NotFoundException("There is no shares");
    }
    cache.put(id, company);
    return Optional.of(Arrays.asList(modelMapper.map(company.getShares(), ShareDto[].class)));
  }
}