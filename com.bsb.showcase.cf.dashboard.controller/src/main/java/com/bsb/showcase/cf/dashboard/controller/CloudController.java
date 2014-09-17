package com.bsb.showcase.cf.dashboard.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Sebastien Gerard
 */
@Controller
public class CloudController {

    /**
     * Checks that the specified instance id can be accessed by the current user.
     *
     * @param instance the specified instance id
     * @return <tt>true</tt> if it can be accessed, otherwise <tt>false</tt>
     */
    @RequestMapping("/v2/service_instances/{instance}/permissions")
    @ResponseBody
    public Map<?, ?> managed(@PathVariable String instance) {
        return Collections.singletonMap("manage", Boolean.TRUE);
    }
}
