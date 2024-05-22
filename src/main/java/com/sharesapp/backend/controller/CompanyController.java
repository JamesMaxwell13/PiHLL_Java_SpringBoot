package com.sharesapp.backend.controller;

import com.sharesapp.backend.dto.CompanyDto;
import com.sharesapp.backend.dto.share.CreateShare;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.service.impl.CompanyServiceImpl;
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
@RequestMapping("/api/company")
public class CompanyController {
  private final CompanyServiceImpl companyService;

  @Autowired
  public CompanyController(CompanyServiceImpl companyService) {
    this.companyService = companyService;
  }

  @PostMapping
  public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companyDto) {
    return ResponseEntity.of(companyService.createCompany(companyDto));
  }

  @PostMapping("/many")
  public ResponseEntity<List<CompanyDto>> createManyCompanies(
      @RequestBody List<CompanyDto> createCompanies) {
    return ResponseEntity.of(companyService.createManyCompanies(createCompanies));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompanyDto> getCompany(@PathVariable Long id) {
    return ResponseEntity.of(companyService.getById(id));
  }

  @GetMapping("/all")
  public ResponseEntity<List<CompanyDto>> getAllCompanies() {
    return ResponseEntity.of(companyService.getAllCompanies());
  }

  @PutMapping("/{id}")
  public ResponseEntity<CompanyDto> updateCompany(@PathVariable("id") Long id,
                                                  @RequestBody CompanyDto companyDto) {
    return ResponseEntity.of(companyService.updateCompany(id, companyDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CompanyDto> deleteCompany(@PathVariable("id") Long id) {
    return ResponseEntity.of(companyService.deleteCompany(id));
  }

  @GetMapping("/{id}/shares")
  public ResponseEntity<List<ShareDto>> getShares(@PathVariable("id") Long id) {
    return ResponseEntity.of(companyService.getShares(id));
  }
}
