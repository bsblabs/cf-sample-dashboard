package com.bsb.showcase.cf.service.config;

import javax.servlet.Filter;

/**
 * Wrapper around a {@link Filter}. This wrapper is used because if a
 * {@link Filter} is defined as a Spring bean, it will be automatically
 * registered in the security chain.
 *
 * @author Sebastien Gerard
 */
class FilterWrapper {

    static FilterWrapper wrap(Filter filter){
        return new FilterWrapper(filter);
    }

    private final Filter filter;

    FilterWrapper(Filter filter) {
        this.filter = filter;
    }

    Filter unwrap() {
        return filter;
    }
}
