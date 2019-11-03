package com.becklu.service;

import com.becklu.beans.Bean;

//声明它是一个Bean
@Bean
public class SalaryService {
    public Integer calSalary(Integer expr){
        return expr * 5000;
    }
}
