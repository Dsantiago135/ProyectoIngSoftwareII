package com.dsantiago.proyectolab.Application.pkgMicroKernel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import com.dsantiago.proyectolab.Application.pkgPlugins.IQuestionPlugin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class clsPluginManager {

    private final clsPluginRegistry attRegistry;

    /**
     * Creates the manager and discovers plugins in the UML plugin package.
     * @param prmRegistry plugin registry
     */
    public clsPluginManager(clsPluginRegistry prmRegistry) {
        this.attRegistry = prmRegistry;
        opDiscoverPlugins("com.dsantiago.proyectolab.Application.pkgPlugins");
    }

    /**
     * Registers a question plugin.
     * @param prmPlugin plugin to register
     */
    public void opRegisterPlugin(IQuestionPlugin prmPlugin) {
        attRegistry.opRegisterPlugin(prmPlugin);
    }

    /**
     * Resolves a plugin for a requested question type.
     * @param prmType question type
     * @return supporting plugin
     * @throws IllegalArgumentException when no plugin supports the type
     */
    public IQuestionPlugin opGetPlugin(String prmType) {
        IQuestionPlugin varPlugin = attRegistry.opGetPlugin(prmType);
        if (varPlugin == null) {
            log.error("No plugin registered for question type: {}", prmType);
            throw new IllegalArgumentException("No plugin registered for question type: " + prmType);
        }
        return varPlugin;
    }

    /**
     * Discovers concrete plugin implementations in a package.
     * @param prmPackage package to scan
     */
    public void opDiscoverPlugins(String prmPackage) {
        ClassPathScanningCandidateComponentProvider varScanner =
            new ClassPathScanningCandidateComponentProvider(false);
        varScanner.addIncludeFilter(new AssignableTypeFilter(IQuestionPlugin.class));
        varScanner.findCandidateComponents(prmPackage).forEach(varCandidate -> {
            try {
                Class<?> varPluginClass = Class.forName(varCandidate.getBeanClassName());
                if (varPluginClass.isInterface() || Modifier.isAbstract(varPluginClass.getModifiers())) {
                    return;
                }
                Constructor<?> varConstructor = varPluginClass.getDeclaredConstructor();
                opRegisterPlugin((IQuestionPlugin) varConstructor.newInstance());
                log.info("Plugin discovered: {}", varPluginClass.getName());
            } catch (ReflectiveOperationException | ClassCastException prmException) {
                throw new IllegalStateException(
                    "Could not instantiate plugin: " + varCandidate.getBeanClassName(), prmException);
            }
        });
    }
}