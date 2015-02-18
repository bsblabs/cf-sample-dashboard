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

    /**
     * Model attribute referencing the full name of the current user.
     */
    public static final String USER_FULL_NAME = "userFullName";

    /**
     * View name referencing the dashboard home page.
     */
    public static final String HOME_VIEW = "home";

    @RequestMapping("")
    public ModelAndView home(ModelAndView modelAndView) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        modelAndView.addObject(USER_FULL_NAME,
              ((DashboardAuthenticationDetails) authentication.getDetails()).getUserFullName());

        modelAndView.setViewName(HOME_VIEW);

        return modelAndView;
    }
}
