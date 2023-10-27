package com.example.security.controller;




import org.springframework.context.annotation.ComponentScan;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ComponentScan
public class MainController {


    @RequestMapping("/")
    public String indexPage(){
        return "index";
    }


}
