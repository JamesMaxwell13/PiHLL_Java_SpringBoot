package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.aspect.annotation.Logging;
import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.exceptions.BadRequestException;
import com.sharesapp.backend.exceptions.NotFoundException;
import com.sharesapp.backend.model.Company;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.repository.CompanyRepository;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.service.ShareService;
import com.sharesapp.backend.utils.cache.GenericCache;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ShareServiceImpl implements ShareService {
  private static final String SHARE_ERROR_MESSAGE = "There is no share with id = ";
  private static final String COMPANY_ERROR_MESSAGE = "There is no such company";
  private final ShareRepository shareRepository;
  private final CompanyRepository companyRepository;
  private final GenericCache<Long, Share> cache;
  private final ModelMapper modelMapper;


  @Autowired
  public ShareServiceImpl(ShareRepository shareRepository, ModelMapper modelMapper,
                          CompanyRepository companyRepository, GenericCache<Long, Share> cache) {
    this.shareRepository = shareRepository;
    this.companyRepository = companyRepository;
    this.modelMapper = modelMapper;
    this.cache = cache;
  }

  @Logging
  @Override
  public Optional<ShareDto> createShare(CreateShare createShare) throws BadRequestException {
    Share share = modelMapper.map(createShare, Share.class);
    Company company = companyRepository.findById(createShare.getCompanyId()).orElse(null);
    if (Optional.ofNullable(createShare.getCompanyId()).isEmpty()
        || Optional.ofNullable(createShare.getLastSalePrice()).isEmpty()
        || createShare.getSymbol().isEmpty()) {
      throw new BadRequestException("Wrong share information");
    }
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE + "with id = ", createShare.getCompanyId());
    }
    company.addShare(share);
    companyRepository.save(company);
    Share savedShare = shareRepository.save(share);
    cache.clear();
    cache.put(savedShare.getId(), savedShare);
    return Optional.of(modelMapper.map(savedShare, ShareDto.class));
  }

  @Logging
  @Override
  public Optional<List<ShareDto>> createManyShares(List<CreateShare> createShares)
      throws BadRequestException {
    if (createShares.stream()
        .anyMatch(s -> (Optional.ofNullable(s.getCompanyId()).isEmpty() || s.getSymbol().isEmpty()
            || Optional.ofNullable(s.getLastSalePrice()).isEmpty()))) {
      throw new BadRequestException("Wrong shares or its name");
    }
    if (createShares.stream()
        .anyMatch(s -> (companyRepository.findById(s.getCompanyId())).isEmpty())) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE);
    }
//    createShares.forEach(s -> companyRepository.save(
//        companyRepository.findById(s.getCompanyId()).get()
//            .addShare(modelMapper.map(s, Share.class))));

//    List<Company> companies = createShares.stream()
//        .map(s -> companyRepository.findById(s.getCompanyId()).get()).toList();
//    companies.forEach(
//        c -> c.addShare(modelMapper.map(createShares.get(companies.indexOf(c)), Share.class)));
//    companyRepository.saveAll(companies);
//    List<Share> shares =
//        createShares.stream().map(s -> modelMapper.map(s, Share.class))
//            .toList();
//    shareRepository.saveAll(shares);
    createShares.forEach(s -> {
      Company company = companyRepository.findById(s.getCompanyId()).orElse(null);
      if (company != null) {
        company.addShare(modelMapper.map(s, Share.class));
        companyRepository.save(company);
      }
    });
    List<Share> shares =
        createShares.stream().map(s -> modelMapper.map(s, Share.class))
            .toList();
    List<Share> savedShares = shareRepository.saveAll(shares);
    cache.clear();
    shares.forEach(s -> cache.put(s.getId(), s));
    return Optional.of(Arrays.asList(modelMapper.map(savedShares, ShareDto[].class)));
  }

  @Logging
  @Override
  public Optional<ShareDto> getById(Long id) throws NotFoundException {
    Share share = cache.get(id).orElseGet(() -> shareRepository.findById(id).orElse(null));
    if (share == null) {
      throw new NotFoundException(SHARE_ERROR_MESSAGE, id);
    }
    cache.put(id, share);
    return Optional.of(modelMapper.map(share, ShareDto.class));
  }

  @Logging
  @Override
  public Optional<CompanyDto> getCompany(Long id) throws NotFoundException {
    Share share = cache.get(id).orElseGet(() -> shareRepository.findById(id).orElse(null));
    if (share == null) {
      throw new NotFoundException(SHARE_ERROR_MESSAGE, id);
    }
    cache.put(id, share);
    return Optional.of(modelMapper.map(share.getCompany(), CompanyDto.class));
  }

  @Logging
  @Override
  public Optional<List<ShareDto>> getAllShares() throws NotFoundException {
    List<Share> shares = shareRepository.findAll();
    if (shares.isEmpty()) {
      throw new NotFoundException("There are no shares");
    }
    shares.sort(Comparator.comparing(Share::getId));
    return Optional.of(Arrays.asList(modelMapper.map(shares, ShareDto[].class)));
  }

  @Logging
  @Override
  public Optional<ShareDto> updateShare(Long id, ShareDto shareDto) throws NotFoundException {
    Share share = cache.get(id).orElseGet(() -> shareRepository.findById(id).orElse(null));
    if (share == null || Optional.ofNullable(shareDto.getLastSalePrice()).isEmpty()
        || shareDto.getSymbol().isEmpty()) {
      throw new BadRequestException("Wrong share information or this share doesn't exist");
    }
    Company company = companyRepository.findById(share.getCompany().getId()).orElse(null);
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE);
    }
    cache.remove(id);
    company.removeShare(share.getId());
    shareDto.setId(id);
    Share updatedShare = shareRepository.save(modelMapper.map(shareDto, Share.class));
    company.addShare(updatedShare);
    companyRepository.save(company);
    cache.put(id, updatedShare);
    return Optional.of(modelMapper.map(updatedShare, ShareDto.class));
  }

  @Logging
  @Override
  public Optional<ShareDto> deleteShare(Long id) throws NotFoundException {
    Share share = cache.get(id).orElseGet(() -> shareRepository.findById(id).orElse(null));
    if (share == null) {
      throw new NotFoundException(SHARE_ERROR_MESSAGE, id);
    }
    Company company = companyRepository.findById(share.getCompany().getId()).orElse(null);
    if (company == null) {
      throw new NotFoundException(COMPANY_ERROR_MESSAGE);
    }
    company.removeShare(share.getId());
    companyRepository.save(company);
    shareRepository.deleteById(id);
    cache.remove(id);
    return Optional.of(modelMapper.map(share, ShareDto.class));
  }
}
