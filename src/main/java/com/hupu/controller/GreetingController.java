package com.hupu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("topic")
public class GreetingController {

	@RequestMapping(value = "/greetings", method = RequestMethod.GET)
	public String greeting(@RequestBody String body) {
		System.out.println(body);
		return body;
	}

}
