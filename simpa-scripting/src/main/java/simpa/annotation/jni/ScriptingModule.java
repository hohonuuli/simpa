package simpa.annotation.jni;

import com.google.inject.*;
import java.util.Map;
import simpa.annotation.ui.AppModule;
import simpa.annotation.ImageCaptureService;
import simpa.annotation.VideoControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the Guice module for the scripting classes. It includes the AppModule configured
 * in simpa-annotation-app as well as bindings to the ImageCaptureService and a VideoControlService
 */
public class ScriptingModule implements Module {

    public static final Logger log = LoggerFactory.getLogger(ScriptingModule.class);

    public void configure(Binder binder) {
        binder.install(new AppModule()); // Install the default application module
        binder.bind(VideoControlService.class).toInstance(new RS422VideoControlService());
        binder.bind(ImageCaptureService.class).to(QTVideoChannelImageCaptureService.class).in(Scopes.SINGLETON);
    }

    public static Injector newInjector() {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new ScriptingModule());
        // Check that all the desired interfaces are available
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        StringBuilder sb = new StringBuilder("Registered bindings: \n");
        for (Key key: bindings.keySet()) {
            sb.append("\t").append(key).append(" = ").append(bindings.get(key).getProvider().get()).append("\n");
        }
        log.info(sb.toString());
        return injector;
    }
}
 /*binder.bind(DataIngestorService.class).to(DataIngestorServiceImpl03.class);
        binder.bind(AnnotationLookupService.class).to(VARSAnnotationLookupService.class);
        binder.bind(AnnotationGeneratorService.class).to(VARSAnnotationGeneratorService.class);
        binder.bind(AnnotationPersistenceService.class).to(VARSAnnotationPersistenceService.class);
        binder.bind(UserLookupService.class).to(VARSUserLookupService.class);

        binder.bind(VarsUserPreferencesFactory.class).to(VarsUserPreferencesFactoryImpl.class);
        binder.bind(MiscDAOFactory.class).to(MiscDAOFactoryImpl.class);
        binder.bind(MiscFactory.class).to(MiscFactoryImpl.class);
        binder.bind(AnnotationDAOFactory.class).to(AnnotationDAOFactoryImpl.class);
        binder.bind(AnnotationFactory.class).to(AnnotationFactoryImpl.class);
        binder.bind(KnowledgebaseDAOFactory.class).to(KnowledgebaseDAOFactoryImpl.class);
        binder.bind(KnowledgebaseFactory.class).to(KnowledgebaseFactoryImpl.class);
        binder.bind(EAO.class).annotatedWith(Names.named("annotationEAO")).to(AnnotationEAO.class).in(Scopes.SINGLETON);
        binder.bind(EAO.class).annotatedWith(Names.named("knowledgebaseEAO")).to(KnowledgebaseEAO.class).in(Scopes.SINGLETON);
        binder.bind(EAO.class).annotatedWith(Names.named("miscEAO")).to(MiscEAO.class).in(Scopes.SINGLETON);*/