package com.sharesapp.backend.service;

import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;

import java.util.List;
import java.util.Optional;

public interface ShareService {
    Optional<ShareDto> createShare(CreateShare createShare);
    Optional<ShareDto> getById(Long id);
    Optional<List<ShareDto>> getAllShares();
    Optional<ShareDto> updateShare(Long id, ShareDto shareDto);
    Optional<ShareDto> deleteShare(Long id);
}
