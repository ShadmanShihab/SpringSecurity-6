package com.app.onlinejobportal.service;

import com.app.onlinejobportal.model.Company;
import com.app.onlinejobportal.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompanyService {
  @Autowired
  CompanyRepository companyRepository;

  public Company addCompany(Company company) {
    log.info("addCompany");
    companyRepository.save(company);
    return company;
  }
}
