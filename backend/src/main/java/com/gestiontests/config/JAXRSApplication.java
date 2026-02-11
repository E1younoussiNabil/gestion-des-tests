package com.gestiontests.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class JAXRSApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Resources REST
        resources.add(com.gestiontests.rest.CandidatResource.class);
        resources.add(com.gestiontests.rest.CreneauHoraireResource.class);
        resources.add(com.gestiontests.rest.TestResource.class);
        resources.add(com.gestiontests.rest.ResultatResource.class);
        resources.add(com.gestiontests.rest.AdministrationResource.class);
        
        // Filtres et providers
        resources.add(com.gestiontests.config.CORSFilter.class);
        resources.add(com.gestiontests.security.AdminJwtAuthFilter.class);
        resources.add(com.gestiontests.config.JacksonConfiguration.class);
        
        return resources;
    }
}
