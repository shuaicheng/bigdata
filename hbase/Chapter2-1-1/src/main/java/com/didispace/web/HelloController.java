package com.didispace.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.didispace.service.BlogPrefixProperties;
import com.didispace.service.BlogProperties;
import com.didispace.service.User;

/**
 *
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 *
 */
@RestController
public class HelloController {

	@Autowired
	User user;
	
	@Autowired
	BlogProperties blogProperties;
	
	@Autowired
	BlogPrefixProperties blogPrefixProperties;
	
	
    @RequestMapping("/hello")
    public String index() {
    	System.out.println(user.getName());
    	System.out.println(blogProperties);
        return "Hello World";
    }

}