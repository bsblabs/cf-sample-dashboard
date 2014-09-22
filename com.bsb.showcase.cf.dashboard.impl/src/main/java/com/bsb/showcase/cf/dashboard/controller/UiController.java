package com.bsb.showcase.cf.dashboard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bsb.showcase.cf.dashboard.user.User;
import com.bsb.showcase.cf.dashboard.user.UserRepository;

/**
 * Controller for the UI.
 *
 * @author Sebastien Gerard
 */
@Controller
@SuppressWarnings("unused")
public class UiController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final User currentUser = userRepository.findByName(authentication.getName());

        model.put("userFullName", currentUser.getFullName());

        return "home";
    }
}
