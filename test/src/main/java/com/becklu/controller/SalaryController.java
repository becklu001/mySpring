package com.becklu.controller;

import com.becklu.beans.Autowired;
import com.becklu.service.SalaryService;
import com.becklu.web.mvc.Controller;
import com.becklu.web.mvc.RequestMapping;
import com.becklu.web.mvc.RequestParam;

@Controller
public class SalaryController {
    @Autowired
    SalaryService salaryService;

    @RequestMapping("/get_salary.json")
    public Integer getSalaryByExp(@RequestParam("name") String name,
                                  @RequestParam("exp") String exp){
        return salaryService.calSalary(Integer.parseInt(exp));
    }
}
