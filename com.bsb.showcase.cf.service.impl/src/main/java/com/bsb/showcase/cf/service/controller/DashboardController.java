package com.bsb.showcase.cf.service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bsb.showcase.cf.service.security.DashboardAuthenticationDetails;

/**
 * Controller for the dashboard.
 *
 * @author Sebastien Gerard
 */
@Controller
@RequestMapping("/dashboard/")
public class DashboardController {

    @RequestMapping("")
    public ModelAndView home(ModelAndView modelAndView) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        modelAndView.addObject("userFullName",
              ((DashboardAuthenticationDetails) authentication.getDetails()).getUserFullName());

        modelAndView.setViewName("home");

        return modelAndView;
    }
}
