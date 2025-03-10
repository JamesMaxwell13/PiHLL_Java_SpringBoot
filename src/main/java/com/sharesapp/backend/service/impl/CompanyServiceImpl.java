package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.aspect.annotation.Logging;
import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.exceptions.BadRequestException;
import com.sharesapp.backend.exceptions.NotFoundException;
import com.sharesapp.backend.model.Company;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.repository.CompanyRepository;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.service.CompanyService;
import com.sharesapp.backend.utils.cache.GenericCache;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
  private static final String COMPANY_ERROR_MESSAGE = "There is no company with id = ";
  private final CompanyRepository companyRepository;
  private final ShareRepository shareRepository;
  private final GenericCache<Long, Company> cache;
  private final ModelMapper modelMapper;

  @Autowired
  public CompanyServiceImpl(CompanyRepository companyRepository, ShareRepository shareRepository,
                            GenericCache<Long, Company> cache,
                            ModelMapper modelMapper) {
    this.companyRepository = companyRepository;
    this.shareRepository = shareRepository;
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
    cache.put(savedCompany.getId(), savedCompany);
    return Optional.of(modelMapper.map(savedCompany, CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<List<CompanyDto>> createManyCompanies(List<CompanyDto> createCompanies)
      throws BadRequestException {
    if (createCompanies.stream().anyMatch(c -> c.getName().isEmpty())) {
      throw new BadRequestException("Wrong shares or its name");
    }
    List<Company> companies =
        createCompanies.stream()
            .map(c -> (companyRepository.save(modelMapper.map(c, Company.class))))
            .toList();
    cache.clear();
    companies.forEach(s -> cache.put(s.getId(), s));
    return Optional.of(Arrays.asList(modelMapper.map(companies, CompanyDto[].class)));
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
    companies.sort(Comparator.comparing(Company::getId));
    return Optional.of(Arrays.asList(modelMapper.map(companies, CompanyDto[].class)));
  }

  @Logging
  @Override
  public Optional<CompanyDto> updateCompany(Long id, CompanyDto companyDto)
      throws BadRequestException {
    Company company = cache.get(id).orElseGet(() -> companyRepository.findById(id).orElse(null));
    if (company == null || companyDto.getName().isEmpty()) {
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
    Company company = cache.get(id).orElseGet(() -> companyRepository.findById(id).orElse(null));
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE, id);
    }
    List<Share> shares = new ArrayList<>(company.getShares());
    shares.forEach(s -> {
      company.removeShare(s.getId());
      companyRepository.saveAndFlush(company);
      shareRepository.deleteById(s.getId());
    });
    cache.remove(id);
    companyRepository.deleteById(id);
    return Optional.of(modelMapper.map(company, CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<List<ShareDto>> getShares(Long id) {
    Company company = companyRepository.findById(id).orElse(null);
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE, id);
    }
    if (company.getShares().isEmpty()) {
      throw new NotFoundException("There is no shares");
    }
    cache.put(id, company);
    List<Share> shares = new ArrayList<>(company.getShares());
    shares.sort(Comparator.comparing(Share::getId));
    return Optional.of(Arrays.asList(modelMapper.map(shares, ShareDto[].class)));
  }
}