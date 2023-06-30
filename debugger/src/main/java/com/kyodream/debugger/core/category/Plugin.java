package com.kyodream.debugger.core.category;

/**
 * SpringMvc/ Jersey / Struts
 */
public interface Plugin {
    public String getPrefix();
    public void registryPrefix(String prefix);
}
