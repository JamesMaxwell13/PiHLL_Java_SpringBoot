package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.model.Company;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.repository.CompanyRepository;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.service.ShareService;
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
public class ShareServiceImpl implements ShareService {
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

  @Override
  public Optional<ShareDto> createShare(CreateShare createShare) {
    Share share = modelMapper.map(createShare, Share.class);
    Company company = companyRepository.findById(share.getCompany().getId()).orElse(null);
    if (company == null) {
      return Optional.empty();
    }
    company.addShare(share);
    companyRepository.save(company);
    Share savedShare = shareRepository.save(share);
    cache.clear();
    return Optional.of(modelMapper.map(savedShare, ShareDto.class));
  }

  @Override
  public Optional<ShareDto> getById(Long id) {
    Share share = cache.get(id).orElseGet(() -> shareRepository.findById(id).orElse(null));
    if (share == null) {
      return Optional.empty();
    }
    cache.put(id, share);
    return Optional.of(modelMapper.map(share, ShareDto.class));
  }

  @Override
  public Optional<CompanyDto> getCompany(Long id) {
    Share share = cache.get(id).orElseGet(() -> shareRepository.findById(id).orElse(null));
    if (share == null) {
      return Optional.empty();
    }
    cache.put(id, share);
    return Optional.of(modelMapper.map(share.getCompany(), CompanyDto.class));
  }

  @Override
  public Optional<List<ShareDto>> getAllShares() {
    List<Share> shares = shareRepository.findAll();
    if (shares.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Arrays.asList(modelMapper.map(shares, ShareDto[].class)));
  }

  @Override
  public Optional<ShareDto> updateShare(Long id, ShareDto shareDto) {
    Share share = shareRepository.findById(id).orElse(null);
    if (share == null) {
      return Optional.empty();
    }
    Company company = companyRepository.findById(share.getCompany().getId()).orElse(null);
    if (company == null) {
      return Optional.empty();
    }
    cache.remove(id);
    company.removeShare(share.getId());
    companyRepository.save(company);
    shareDto.setId(id);
    Share updatedShare = shareRepository.save(modelMapper.map(shareDto, Share.class));
    company.addShare(updatedShare);
    companyRepository.save(company);
    cache.put(id, updatedShare);
    return Optional.of(modelMapper.map(updatedShare, ShareDto.class));
  }

  @Override
  public Optional<ShareDto> deleteShare(Long id) {
    Share share = shareRepository.findById(id).orElse(null);
    if (share == null) {
      return Optional.empty();
    }
    Company company = companyRepository.findById(share.getCompany().getId()).orElse(null);
    if (company == null) {
      return Optional.empty();
    }
    company.removeShare(share.getId());
    companyRepository.save(company);
    shareRepository.deleteById(id);
    cache.remove(id);
    return Optional.of(modelMapper.map(share, ShareDto.class));
  }
}
