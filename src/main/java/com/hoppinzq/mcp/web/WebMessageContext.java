package com.hoppinzq.mcp.web;

import java.io.Serializable;

public class WebMessageContext {
    private static InheritableThreadLocal<Serializable> blog = new InheritableThreadLocal<Serializable>();

    public static final Serializable getPrincipal() {
        return blog.get();
    }

    public static void exit() {
        blog.set(null);
    }

    public static void enter(Serializable principal) {
        blog.set(principal);
    }
}
