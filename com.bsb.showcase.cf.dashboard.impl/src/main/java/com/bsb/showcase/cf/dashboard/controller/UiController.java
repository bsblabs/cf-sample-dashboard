package com.bsb.showcase.cf.dashboard.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bsb.showcase.cf.dashboard.security.DashboardOAuth2AuthenticationDetails;

/**
 * Controller for the UI.
 *
 * @author Sebastien Gerard
 */
@Controller
@SuppressWarnings("unused")
public class UiController {

    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final DashboardOAuth2AuthenticationDetails details
              = (DashboardOAuth2AuthenticationDetails) authentication.getDetails();

        model.put("userFullName", details.getUserFullName());

        return "home";
    }
}
