package com.coldsteelpope.myrestfulservice.controller;

import com.coldsteelpope.myrestfulservice.bean.HelloWorldBean;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class HelloWorldController
{
    // GET
    // URI - /hello-world
    private MessageSource messageSource;
    public HelloWorldController(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    @GetMapping(path = "/hello-world")
    public String helloWorld()
    {
        return "hello world";
    }

    @GetMapping(path = "/hello-world-bean")
    public HelloWorldBean helloWorldBean()
    {
        return new HelloWorldBean("hello world");
    }

    @GetMapping(path = "/hello-world-bean/path-variable/{name}")
    public HelloWorldBean helloWorldBeanPathVariable(
            @PathVariable String name
    )
    {
        return new HelloWorldBean(String.format("Hello World, %s", name));
    }

    @GetMapping("/hello-world-internalized")
    public String helloWorldInternalized(
            @RequestHeader(name = "Accept-Language", required = false) Locale locale
    )
    {
        return messageSource.getMessage("greeting.message", null, locale);
    }
}
