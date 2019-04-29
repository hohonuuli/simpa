package simpa.annotation.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import simpa.annotation.AnnotationGeneratorService;
import simpa.annotation.AnnotationLookupService;
import simpa.annotation.AnnotationPersistenceService;
import simpa.annotation.UserLookupService;
import simpa.core.DataIngestorService;

/**
 * Unit test for simple App. 
 */
public class AppTest {

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AppModule());
        
        List<Class> injectedClasses = new ArrayList<Class>() {{
            add(DataIngestorService.class);
            add(AnnotationLookupService.class);
            add(AnnotationGeneratorService.class);
            add(AnnotationPersistenceService.class);
            add(UserLookupService.class);
        }};
        
        for (Class c : injectedClasses) {
            Object svc = injector.getInstance(c);
            Assert.assertNotNull("Failed to find inject class for " + c.getName(), svc );
        }

    }
}
