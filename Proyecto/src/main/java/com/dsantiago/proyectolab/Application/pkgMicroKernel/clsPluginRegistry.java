package com.dsantiago.proyectolab.Application.pkgMicroKernel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dsantiago.proyectolab.Application.pkgPlugins.IQuestionPlugin;

import lombok.extern.slf4j.Slf4j;

/** Registry of question plugins defined by the class diagram. */
@Slf4j
@Component
public class clsPluginRegistry {

    private final Map<String, IQuestionPlugin> attPlugins = new LinkedHashMap<>();

    /**
     * Registers an implementation by its declared name.
     * @param prmPlugin plugin to register
     */
    public void opRegisterPlugin(IQuestionPlugin prmPlugin) {
        if (prmPlugin == null || prmPlugin.opGetName() == null || prmPlugin.opGetName().isBlank()) 
            throw new IllegalArgumentException("Plugin and plugin name are required");
        
        if (attPlugins.putIfAbsent(prmPlugin.opGetName(), prmPlugin) != null) 
            throw new IllegalArgumentException("Plugin name already registered: " + prmPlugin.opGetName());
        
        log.info("Plugin registered: {}", prmPlugin.opGetName());
    }

    /**
     * Finds a plugin that supports the requested type.
     * @param prmType requested question type
     * @return supporting plugin, or null when none is registered
     */
    public IQuestionPlugin opGetPlugin(String prmType) {
        return attPlugins.values().stream()
            .filter(prmPlugin -> prmPlugin.opSupports(prmType))
            .findFirst()
            .orElse(null);
    }
}