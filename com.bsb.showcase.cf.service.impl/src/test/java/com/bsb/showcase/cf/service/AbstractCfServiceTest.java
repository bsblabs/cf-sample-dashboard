package com.bsb.showcase.cf.service;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Abstract class for dashboard tests.
 *
 * @author Sebastien Gerard
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleCfServiceApplication.class)
@WebAppConfiguration
public abstract class AbstractCfServiceTest {
}
