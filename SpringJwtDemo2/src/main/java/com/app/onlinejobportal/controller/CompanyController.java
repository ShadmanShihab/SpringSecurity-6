package com.app.onlinejobportal.controller;

import com.app.onlinejobportal.model.Company;
import com.app.onlinejobportal.service.CompanyService;
import com.app.onlinejobportal.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

//Get - Get data from server
//POST - Save data in server
//PUT / PATCH - Update data in server
//DELETE - Delete data from server

@Slf4j
@RestController
public class CompanyController {
  @Autowired
  CompanyService companyService;
  @Qualifier("adminServiceImpl")
  @Autowired
  UserService userService;

  @GetMapping("/company")
  public String index() {
    return "hello this is company controller";

  }

  @PostMapping("/company")
  public String addCompany() {
//    companyService.addCompany(company);
    log.info("add company");
    return "success";
  }


}
