package com.github.emile2013.resmonitor;

import java.util.Set;

/**
 *
 * extension,can config some ignore classes
 *
 * @author y.huang
 * @since 2019-12-11
 */
public class ResMonitorExtension {

    private Set<String> ignoreClasses;


    public Set<String> getIgnoreClasses() {
        return ignoreClasses;
    }

    public void setIgnoreClasses(Set<String> ignoreClasses) {
        this.ignoreClasses = ignoreClasses;
    }
}

