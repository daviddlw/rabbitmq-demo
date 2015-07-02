package com.hupu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StompController {

	@RequestMapping(value = "/stomp", method = RequestMethod.GET)
	public ModelAndView stompView() {
		ModelAndView mav = new ModelAndView("stomp");
		return mav;
	}
}
