package com.ks.demo.vv.config;

import javax.validation.groups.Default;

public interface ValidatedGroup extends Default {
    interface Create extends ValidatedGroup {
    }

    interface Update extends ValidatedGroup {
    }

    interface Query extends ValidatedGroup {
    }

    interface Delete extends ValidatedGroup {
    }
}

