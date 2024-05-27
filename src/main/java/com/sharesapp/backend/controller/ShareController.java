package com.sharesapp.backend.controller;

import com.sharesapp.backend.aspect.annotation.RequestCounting;
import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.service.impl.ShareServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestCounting
@RequestMapping("/api/share")
public class ShareController {
  private final ShareServiceImpl shareService;

  @Autowired
  public ShareController(ShareServiceImpl shareService) {
    this.shareService = shareService;
  }

  @PostMapping
  public ResponseEntity<ShareDto> createShare(@RequestBody CreateShare createShare) {
    return ResponseEntity.of(shareService.createShare(createShare));
  }

  @PostMapping("/many")
  public ResponseEntity<List<ShareDto>> createManyShares(
      @RequestBody List<CreateShare> createShare) {
    return ResponseEntity.of(shareService.createManyShares(createShare));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ShareDto> getShare(@PathVariable Long id) {
    return ResponseEntity.of(shareService.getById(id));
  }

  @GetMapping("/{id}/company")
  public ResponseEntity<CompanyDto> getCompany(@PathVariable Long id) {
    return ResponseEntity.of(shareService.getCompany(id));
  }

  @GetMapping("/all")
  public ResponseEntity<List<ShareDto>> getAllShares() {
    return ResponseEntity.of(shareService.getAllShares());
  }

  @PutMapping("/{id}")
  public ResponseEntity<ShareDto> updateShare(@PathVariable("id") Long id,
                                              @RequestBody ShareDto shareDto) {
    return ResponseEntity.of(shareService.updateShare(id, shareDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ShareDto> deleteShare(@PathVariable("id") Long id) {
    return ResponseEntity.of(shareService.deleteShare(id));
  }
}
