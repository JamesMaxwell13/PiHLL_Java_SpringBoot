package com.sharesapp.backend.controller;

import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.service.impl.ShareServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @GetMapping("/{id}")
    public ResponseEntity<ShareDto> getShare(@PathVariable Long id) {
        return ResponseEntity.of(shareService.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShareDto>> getAllShares() {
        return ResponseEntity.of(shareService.getAllShares());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShareDto> updateShare(@PathVariable("id") Long id, @RequestBody ShareDto shareDto) {
        return ResponseEntity.of(shareService.updateShare(id, shareDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ShareDto> deleteShare(@PathVariable("id") Long id) {
        return ResponseEntity.of(shareService.deleteShare(id));
    }
}
