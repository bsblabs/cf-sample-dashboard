package com.bsb.showcase.cf.dashboard.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Technical controller accessed by non-human users.
 *
 * @author Sebastien Gerard
 */
@RestController
@SuppressWarnings("unused")
public class TechController {

    @RequestMapping("/services/v1/ping")
    public Map<?, ?> ping() {
        return Collections.singletonMap("message", "Hello World!");
    }
}
