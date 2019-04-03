package com.blockchain.larisa.controller;

import com.blockchain.larisa.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @RequestMapping("/stop/{contractName}")
    @ResponseBody
    public String stop(@PathVariable("contractName") String contractName) {
        return adminService.stop(contractName);
    }

    @RequestMapping("/resume/{contractName}")
    @ResponseBody
    public String resume(@PathVariable("contractName") String contractName) {
        return adminService.resume(contractName);
    }

    @RequestMapping("/reset/{contractName}")
    @ResponseBody
    public String reset(@PathVariable("contractName") String contractName) {
        return adminService.reset(contractName);
    }

    @RequestMapping("/cancel/{coinName}")
    @ResponseBody
    public String cancelAll(@PathVariable("coinName") String coinName) {
        return adminService.cancelAll(coinName);
    }

    @RequestMapping("/{contractName}/{id}")
    @ResponseBody
    public String query(@PathVariable("contractName") String name, @PathVariable("id") Long id) {
        return adminService.query(name, id);
    }


}