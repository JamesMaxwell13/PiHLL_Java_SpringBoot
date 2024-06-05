package com.sharesapp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
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
import com.sharesapp.backend.service.impl.ShareServiceImpl;
import com.sharesapp.backend.utils.cache.GenericCache;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
class ShareServiceTests {
  private Company company;
  private Share share;
  private CreateShare createShare = new CreateShare();
  @Mock
  private ShareRepository shareRepository;
  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private GenericCache<Long, Share> cache;
  @InjectMocks
  private ShareServiceImpl shareService;
  @Spy
  private ModelMapper modelMapper = new ModelMapper();

  @BeforeEach
  public void setUp() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    share = new Share(1L, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
        Instant.parse("2007-12-03T10:15:30.00Z"), "Symbol", new HashSet<>(), null);
    company = new Company(1L, "Company Name", 1D, "Adress", "Website", new HashSet<>());
    createShare = modelMapper.map(share, CreateShare.class);
    createShare.setCompanyId(1L);
  }

  @Test
  void testCreateShare() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.saveAndFlush(any(Share.class))).thenReturn(share);
    when(companyRepository.saveAndFlush(any(Company.class))).thenReturn(company);
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));

    Optional<ShareDto> result = shareService.createShare(createShare);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(share, ShareDto.class), result.get());
    verify(shareRepository, times(1)).saveAndFlush(any(Share.class));
    verify(companyRepository, times(1)).saveAndFlush(any(Company.class));
    verify(cache, times(1)).put(1L, share);

    Optional<Share> cacheShare = cache.get(share.getId());
    assertTrue(cacheShare.isPresent());
    assertEquals(share, cacheShare.get());
    verify(cache, times(1)).clear();
  }

  @Test
  void testCreateShareThrowCompanyId() {
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));
    createShare.setCompanyId(null);
    assertThrows(BadRequestException.class, () -> shareService.createShare(createShare));
  }

  @Test
  void testCreateShareThrowLastSalePrice() {
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));
    createShare.setLastSalePrice(null);
    assertThrows(BadRequestException.class, () -> shareService.createShare(createShare));
  }

  @Test
  void testCreateShareThrowSymbol() {
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));
    createShare.setSymbol("");
    assertThrows(BadRequestException.class, () -> shareService.createShare(createShare));
  }

  @Test
  void testCreateShareThrowCompany() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> shareService.createShare(createShare));
    verify(companyRepository, times(1)).findById(anyLong());
  }

  @Test
  void testCreateManyShares() {
    List<Share> shares = new ArrayList<>();
    shares.add(share);
    company.addShare(share);
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.saveAll(anyList())).thenReturn(shares);
    when(companyRepository.save(any(Company.class))).thenReturn(company);
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));

    List<CreateShare> createShares =
        shares.stream().map(s -> modelMapper.map(s, CreateShare.class)).toList();
    createShares.forEach(s -> s.setCompanyId(1L));
    Optional<List<ShareDto>> result = shareService.createManyShares(createShares);

    assertTrue(result.isPresent());
    assertEquals(shares.stream().map(u -> modelMapper.map(u, ShareDto.class)).toList(),
        result.get());
    verify(shareRepository, times(1)).saveAll(anyList());
    verify(companyRepository, times(1)).save(any(Company.class));

    Optional<Share> cacheShare = cache.get(share.getId());
    assertTrue(cacheShare.isPresent());
    assertEquals(share, cacheShare.get());
    verify(cache, times(1)).clear();
  }

  @Test
  void testCreateManyShareThrowCompanyId() {
    createShare.setCompanyId(null);
    List<CreateShare> shares = List.of(createShare, createShare, createShare);
    assertThrows(BadRequestException.class, () -> shareService.createManyShares(shares));
  }

  @Test
  void testCreateManyShareThrowLastSalePrice() {
    createShare.setLastSalePrice(null);
    List<CreateShare> shares = List.of(createShare, createShare, createShare);
    assertThrows(BadRequestException.class, () -> shareService.createManyShares(shares));
  }

  @Test
  void testCreateManyShareThrowSymbol() {
    createShare.setSymbol("");
    List<CreateShare> shares = List.of(createShare, createShare, createShare);
    assertThrows(BadRequestException.class, () -> shareService.createManyShares(shares));
  }

  @Test
  void testCreateManyShareThrowCompany() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    List<CreateShare> shares = List.of(createShare, createShare, createShare);
    assertThrows(NotFoundException.class, () -> shareService.createManyShares(shares));
  }

  @Test
  void testGetShareById() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));

    Optional<ShareDto> result = shareService.getById(1L);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(share, ShareDto.class), result.get());
    verify(cache, times(1)).get(1L);
    verify(cache, times(1)).put(1L, share);
  }

  @Test
  void testGetShareByIdThrow() {
    when(shareRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> shareService.getById(1L));
  }

  @Test
  void testGetAllShares() {
    List<Share> shares = new ArrayList<>();
    shares.add(share);
    when(shareRepository.findAll()).thenReturn(shares);

    Optional<List<ShareDto>> result = shareService.getAllShares();

    assertTrue(result.isPresent());
    assertEquals(List.of(modelMapper.map(share, ShareDto.class)), result.get());
    verify(shareRepository, times(1)).findAll();
  }

  @Test
  void testGetAllSharesThrow() {
    when(shareRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> shareService.getAllShares());
  }

  @Test
  void testUpdateShare() {
    when(shareRepository.save(any(Share.class))).thenReturn(share);
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));
    when(companyRepository.save(any(Company.class))).thenReturn(company);
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));

    share.setCompany(company);
    Optional<ShareDto> result =
        shareService.updateShare(1L, modelMapper.map(share, ShareDto.class));

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(share, ShareDto.class), result.get());
    verify(shareRepository, times(1)).save(any(Share.class));
    verify(companyRepository, times(1)).save(any(Company.class));
    verify(cache, times(1)).remove(1L);
    verify(cache, times(1)).put(1L, share);
  }

  @Test
  void testUpdateShareThrowLastSalePrice() {
    when(shareRepository.save(any(Share.class))).thenThrow(new BadRequestException("Error"));
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));

    Long id = share.getId();
    share.setLastSalePrice(null);
    ShareDto shareDto = modelMapper.map(share, ShareDto.class);
    assertThrows(BadRequestException.class, () -> shareService.updateShare(id, shareDto));
  }

  @Test
  void testUpdateShareThrowSymbol() {
    when(shareRepository.save(any(Share.class))).thenThrow(new BadRequestException("Error"));
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));

    Long id = share.getId();
    share.setSymbol("");
    ShareDto shareDto = modelMapper.map(share, ShareDto.class);
    assertThrows(BadRequestException.class, () -> shareService.updateShare(id, shareDto));
  }

  @Test
  void testUpdateShareThrowNull() {
    when(shareRepository.save(any(Share.class))).thenThrow(new BadRequestException("Error"));
    when(shareRepository.findById(1L)).thenReturn(Optional.empty());
    when(cache.get(1L)).thenReturn(Optional.empty());

    Long id = share.getId();
    ShareDto shareDto = modelMapper.map(share, ShareDto.class);
    assertThrows(BadRequestException.class, () -> shareService.updateShare(id, shareDto));
  }

  @Test
  void testUpdateShareThrowCompany() {
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

    share.setCompany(company);
    shareRepository.save(share);
    Long id = share.getId();
    ShareDto shareDto = modelMapper.map(share, ShareDto.class);
    assertThrows(NotFoundException.class, () -> shareService.updateShare(id, shareDto));
  }

  @Test
  void testDeleteShare() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.save(any(Share.class))).thenReturn(share);
    when(companyRepository.save(any(Company.class))).thenReturn(company);
    when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));
    doNothing().when(shareRepository).deleteById(anyLong());
    doNothing().when(cache).remove(anyLong());

    share.setCompany(company);
    Optional<ShareDto> result = shareService.deleteShare(1L);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(share, ShareDto.class), result.get());
    verify(shareRepository, times(1)).deleteById(1L);
    verify(companyRepository, times(1)).save(any(Company.class));
    verify(cache, times(1)).remove(1L);
  }

  @Test
  void testDeleteShareThrowShare() {
    when(shareRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(cache.get(anyLong())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> shareService.deleteShare(1L));

    verify(shareRepository, times(1)).findById(1L);
    verify(cache, times(1)).get(1L);
  }

  @Test
  void testDeleteShareThrowRepository() {
    when(shareRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> shareService.getById(1L));
  }

  @Test
  void testDeleteShareThrowCache() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> shareService.getById(1L));
  }

  @Test
  void testDeleteShareThrowCompany() {
    when(shareRepository.findById(anyLong())).thenReturn(Optional.ofNullable(share));
    when(cache.get(anyLong())).thenReturn(Optional.ofNullable(share));
    when(shareRepository.findById(anyLong())).thenReturn(Optional.ofNullable(share));

    share.setCompany(company);
    assertThrows(NotFoundException.class, () -> shareService.deleteShare(1L));

    verify(cache, times(1)).get(1L);
  }

  @Test
  void testGetCompany() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(share));
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));

    share.setCompany(company);
    Optional<CompanyDto> result = shareService.getCompany(share.getId());

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(company, CompanyDto.class), result.get());
    verify(cache, times(1)).put(1L, share);
  }

  @Test
  void testGetCompanyThrowShare() {
    when(shareRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(cache.get(anyLong())).thenReturn(Optional.empty());

    Long id = share.getId();
    assertThrows(NotFoundException.class, () -> shareService.getCompany(id));

    verify(shareRepository, times(1)).findById(1L);
    verify(cache, times(1)).get(1L);
  }
}
