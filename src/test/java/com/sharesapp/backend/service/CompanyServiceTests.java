package com.sharesapp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.exceptions.BadRequestException;
import com.sharesapp.backend.exceptions.NotFoundException;
import com.sharesapp.backend.model.Company;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.repository.CompanyRepository;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.service.impl.CompanyServiceImpl;
import com.sharesapp.backend.utils.cache.GenericCache;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyServiceTests {
  private Company company;
  private Share share;
  private CompanyDto companyDto;
  @Mock
  private ShareRepository shareRepository;
  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private GenericCache<Long, Company> cache;
  @InjectMocks
  private CompanyServiceImpl companyService;
  @Spy
  private ModelMapper modelMapper = new ModelMapper();

  @BeforeEach
  public void setUp() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    company = new Company(1L, "Company Name", 1D, "Adress", "Website", new HashSet<>());
    share = new Share(1L, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
        Instant.parse("2007-12-03T10:15:30.00Z"), "Symbol", new HashSet<>(), null);
    companyDto = modelMapper.map(company, CompanyDto.class);
  }

  @Test
  void testCreateCompany() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(company));
    when(companyRepository.save(any(Company.class))).thenReturn(company);

    Optional<CompanyDto> result = companyService.createCompany(companyDto);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(company, CompanyDto.class), result.get());
    verify(companyRepository, times(1)).save(any(Company.class));
    verify(cache, times(1)).put(1L, company);

    Optional<Company> cacheCompany = cache.get(company.getId());
    assertTrue(cacheCompany.isPresent());
    assertEquals(company, cacheCompany.get());
    verify(cache, times(1)).clear();
  }

  @Test
  void testCreateCompanyThrowName() {
    companyDto.setName("");
    assertThrows(BadRequestException.class, () -> companyService.createCompany(companyDto));
  }

  @Test
  void testCreateManyCompany() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(company));
    when(companyRepository.save(any(Company.class))).thenReturn(company);

    List<CompanyDto> companies = List.of(companyDto, companyDto, companyDto);
    Optional<List<CompanyDto>> result = companyService.createManyCompanies(companies);

    assertTrue(result.isPresent());
    assertEquals(companies, result.get());
    verify(companyRepository, times(3)).save(any(Company.class));
    verify(cache, times(3)).put(1L, company);

    Optional<Company> cacheCompany = cache.get(company.getId());
    assertTrue(cacheCompany.isPresent());
    assertEquals(company, cacheCompany.get());
    verify(cache, times(1)).clear();
  }

  @Test
  void testCreateManyCompanyThrowName() {
    companyDto.setName("");
    List<CompanyDto> companies = List.of(companyDto, companyDto, companyDto);
    assertThrows(BadRequestException.class, () -> companyService.createManyCompanies(companies));
  }

  @Test
  void testGetCompanyById() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(company));
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));

    Optional<CompanyDto> result = companyService.getById(1L);

    assertTrue(result.isPresent());
    assertEquals(companyDto, result.get());
    verify(cache, times(1)).get(1L);
    verify(cache, times(1)).put(1L, company);
  }

  @Test
  void testGetCompanyByIdThrow() {
    when(companyRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> companyService.getById(1L));
  }

  @Test
  void testGetAllCompanies() {
    List<Company> companies = new ArrayList<>();
    companies.add(company);
    when(companyRepository.findAll()).thenReturn(companies);

    Optional<List<CompanyDto>> result = companyService.getAllCompanies();

    assertTrue(result.isPresent());
    assertEquals(List.of(companyDto), result.get());
    verify(companyRepository, times(1)).findAll();
  }

  @Test
  void testGetAllCompaniesThrow() {
    when(companyRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> companyService.getAllCompanies());
  }

  @Test
  void testUpdateCompany() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(company));
    when(companyRepository.save(any(Company.class))).thenReturn(company);

    Optional<CompanyDto> result = companyService.updateCompany(1L, companyDto);

    assertTrue(result.isPresent());
    assertEquals(companyDto, result.get());
    verify(companyRepository, times(1)).save(any(Company.class));
    verify(cache, times(1)).remove(1L);
    verify(cache, times(1)).put(1L, company);
  }

  @Test
  void testUpdateCompanyThrowName() {
    when(companyRepository.findById(1L)).thenReturn(Optional.empty());
    when(cache.get(1L)).thenReturn(Optional.ofNullable(company));

    Long id = company.getId();
    companyDto.setName("");
    assertThrows(BadRequestException.class, () -> companyService.updateCompany(id, companyDto));
  }

  @Test
  void testUpdateCompanyThrowCompany() {
    when(companyRepository.findById(1L)).thenReturn(Optional.empty());
    when(cache.get(1L)).thenReturn(Optional.empty());

    Long id = company.getId();
    assertThrows(BadRequestException.class, () -> companyService.updateCompany(id, companyDto));
  }

  @Test
  void testDeleteCompany() {
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));
    when(cache.get(1L)).thenReturn(Optional.ofNullable(company));
    doNothing().when(companyRepository).deleteById(anyLong());
    doNothing().when(cache).remove(anyLong());

    Optional<CompanyDto> result = companyService.deleteCompany(1L);

    assertTrue(result.isPresent());
    assertEquals(companyDto, result.get());
    verify(cache, times(1)).remove(1L);
  }

  @Test
  void testDeleteShareThrowCompany() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(cache.get(anyLong())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> companyService.deleteCompany(1L));

    verify(companyRepository, times(1)).findById(1L);
    verify(cache, times(1)).get(1L);
  }

  @Test
  void testGetShares() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(company));
    when(cache.get(anyLong())).thenReturn(Optional.ofNullable(company));

    company.addShare(share);
    Optional<List<ShareDto>> result = companyService.getShares(company.getId());

    assertTrue(result.isPresent());
    assertEquals(List.of(modelMapper.map(share, ShareDto.class)), result.get());
    verify(cache, times(1)).put(1L, company);
  }

  @Test
  void testGetSharesThrowCompany() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(cache.get(anyLong())).thenReturn(Optional.empty());

    Long id = company.getId();
    assertThrows(NotFoundException.class, () -> companyService.getShares(id));
  }

  @Test
  void testGetCompanyThrowShares() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(company));
    when(cache.get(anyLong())).thenReturn(Optional.ofNullable(company));

    Long id = company.getId();
    assertThrows(NotFoundException.class, () -> companyService.getShares(id));
  }
}
