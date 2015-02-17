package com.bsb.showcase.cf.dashboard.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bsb.showcase.cf.dashboard.security.DashboardOAuth2AuthenticationDetails;

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
              ((DashboardOAuth2AuthenticationDetails) authentication.getDetails()).getUserFullName());

        modelAndView.setViewName("home");

        return modelAndView;
    }
}
