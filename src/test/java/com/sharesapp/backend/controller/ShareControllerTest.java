package com.sharesapp.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.service.impl.ShareServiceImpl;
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
class ShareControllerTest {
  private final ShareDto share = new ShareDto();
  private final CreateShare createShare = new CreateShare();
  private final CompanyDto company = new CompanyDto();
  @Mock
  private ShareServiceImpl shareService;
  @InjectMocks
  private ShareController shareController;

  @BeforeEach
  public void setUp() {
    share.setId(1L);
    share.setPrevClosePrice(1.0f);
    share.setHighPrice(1.0f);
    share.setLowPrice(1.0f);
    share.setOpenPrice(1.0f);
    share.setLastSalePrice(1.0f);
    share.setLastTimeUpdated(Instant.parse("2021-01-01T00:00:00Z"));
    share.setSymbol("Test Symbol");

    company.setId(1L);
    company.setName("Test Company Name");
  }

  @Test
  void testGetAllShares() {
    when(shareService.getAllShares()).thenReturn(Optional.of(Arrays.asList(share, share, share)));

    ResponseEntity<List<ShareDto>> result = shareController.getAllShares();

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(share, result.getBody().get(1));
  }

  @Test
  void testGetShareById() {
    when(shareService.getById(1L)).thenReturn(Optional.of(share));

    ResponseEntity<ShareDto> result = shareController.getShare(1L);

    assertEquals(share, result.getBody());
  }

  @Test
  void createShare() {
    when(shareService.createShare(createShare)).thenReturn(Optional.of(share));

    ResponseEntity<ShareDto> result = shareController.createShare(createShare);

    assertEquals(share, result.getBody());
  }

  @Test
  void testCreateManyShares() {
    when(shareService.createManyShares(Arrays.asList(createShare, createShare, createShare)))
        .thenReturn(Optional.of(Arrays.asList(share, share, share)));

    ResponseEntity<List<ShareDto>> result = shareController.createManyShares(
        Arrays.asList(createShare, createShare, createShare));

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(share, result.getBody().get(1));
  }

  @Test
  void testUpdateShare() {
    when(shareService.updateShare(1L, share)).thenReturn(Optional.of(share));

    ResponseEntity<ShareDto> result = shareController.updateShare(1L, share);

    assertEquals(share, result.getBody());
  }

  @Test
  void testDeleteUser() {
    when(shareService.deleteShare(1L)).thenReturn(Optional.of(share));

    ResponseEntity<ShareDto> result = shareController.deleteShare(1L);

    assertEquals(share, result.getBody());
  }

  @Test
  void testGetCompany() {
    when(shareService.getCompany(1L)).thenReturn(Optional.of(company));

    ResponseEntity<CompanyDto> result = shareController.getCompany(1L);

    assertEquals(company, result.getBody());
  }
  }
