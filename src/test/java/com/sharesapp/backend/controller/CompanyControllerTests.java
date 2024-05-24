package com.sharesapp.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.service.impl.CompanyServiceImpl;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTests {
  private final CompanyDto company = new CompanyDto();
  private final ShareDto share = new ShareDto();

  @Mock
  private CompanyServiceImpl companyService;
  @InjectMocks
  private CompanyController companyController;

  @BeforeEach
  public void setUp() {
    company.setId(1L);
    company.setName("Test Company Name");

    share.setId(1L);
    share.setPrevClosePrice(1.0f);
    share.setHighPrice(1.0f);
    share.setLowPrice(1.0f);
    share.setOpenPrice(1.0f);
    share.setLastSalePrice(1.0f);
    share.setLastTimeUpdated(Instant.parse("2021-01-01T00:00:00Z"));
    share.setSymbol("Test Symbol");
  }

  @Test
  void testGetAllCompanies() {
    when(companyService.getAllCompanies()).thenReturn(
        Optional.of(Arrays.asList(company, company, company)));

    ResponseEntity<List<CompanyDto>> result = companyController.getAllCompanies();

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(company, result.getBody().get(1));
  }

  @Test
  void testCreateCompany() {
    when(companyService.createCompany(company)).thenReturn(Optional.of(company));

    ResponseEntity<CompanyDto> result = companyController.createCompany(company);

    assertEquals(company, result.getBody());
  }

  @Test
  void testCreateManyCompanies() {
    when(companyService.createManyCompanies(Arrays.asList(company, company, company)))
        .thenReturn(Optional.of(Arrays.asList(company, company, company)));

    ResponseEntity<List<CompanyDto>> result =
        companyController.createManyCompanies(Arrays.asList(company, company, company));

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(company, result.getBody().get(1));
  }

  @Test
  void testGetCompanyById() {
    when(companyService.getById(1L)).thenReturn(Optional.of(company));

    ResponseEntity<CompanyDto> result = companyController.getCompany(1L);

    assertEquals(company, result.getBody());
  }

  @Test
  void testUpdateCompany() {
    when(companyService.updateCompany(1L, company)).thenReturn(Optional.of(company));

    ResponseEntity<CompanyDto> result = companyController.updateCompany(1L, company);

    assertEquals(company, result.getBody());
  }

  @Test
  void testDeleteCompany() {
    when(companyService.deleteCompany(1L)).thenReturn(Optional.of(company));

    ResponseEntity<CompanyDto> result = companyController.deleteCompany(1L);

    assertEquals(company, result.getBody());
  }

  @Test
  void testGetShares() {
    when(companyService.getShares(1L)).thenReturn(Optional.of(Arrays.asList(share, share, share)));

    ResponseEntity<List<ShareDto>> result = companyController.getShares(1L);

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(share, result.getBody().get(1));
  }
}
