/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation.vars;

import com.google.inject.Binder;
import com.google.inject.Module;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.UserLookupService;
import simpa.core.DataIngestorService;
import simpa.core.impl.DataIngestorServiceImpl03;
import vars.jpa.VarsJpaModule;

import java.util.ResourceBundle;

/**
 *
 * @author brian
 */
public class VARSModule implements Module {
    
    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;


    public VARSModule() {
        ResourceBundle bundle = ResourceBundle.getBundle("vars");
        annotationPersistenceUnit = bundle.getString("annotation.persistence.unit");
        knowledgebasePersistenceUnit = bundle.getString("knowledgebase.persistence.unit");
        miscPersistenceUnit = bundle.getString("misc.persistence.unit");
    }


    public void configure(Binder binder) {
        binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
        binder.bind(DataIngestorService.class).to(DataIngestorServiceImpl03.class);
        binder.bind(AnnotationLookupService.class).to(VARSAnnotationLookupService.class);
        binder.bind(AnnotationGeneratorService.class).to(VARSAnnotationGeneratorService.class);
        binder.bind(AnnotationPersistenceService.class).to(VARSAnnotationPersistenceService.class);
        binder.bind(UserLookupService.class).to(VARSUserLookupService.class);
    }

}
