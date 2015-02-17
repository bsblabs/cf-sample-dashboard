package com.bsb.showcase.cf.dashboard.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for web-services.
 *
 * @author Sebastien Gerard
 */
@RestController
public class WebServiceController {

    @RequestMapping("/services/v1/ping")
    public Map<?, ?> ping() {
        return Collections.singletonMap("message", "Hello World!");
    }
}
