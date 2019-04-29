import simpa.annotation.ui.AppModule
import org.junit.Test
import com.google.inject.Guice
import com.google.inject.Stage
import simpa.core.DataIngestorService
import simpa.annotation.ImageCaptureService
import simpa.annotation.vars.VARSModelConverter
import simpa.annotation.AnnotationLookupService
import simpa.annotation.AnnotationGeneratorService
import simpa.annotation.AnnotationPersistenceService
import simpa.annotation.UserLookupService
import org.junit.Assert
import org.slf4j.LoggerFactory
import simpa.annotation.jni.ScriptingModule
import simpa.annotation.VideoControlService

class GuiceTest {

  def log = LoggerFactory.getLogger(getClass())

  @Test
  void moduleTest() {

    /*
     * Configure SIMPA environment using GUICE
     */
    //def injector = Guice.createInjector(Stage.DEVELOPMENT, new ScriptingModule())
    def injector = ScriptingModule.newInjector()

        def injectedClasses = [
            AnnotationGeneratorService.class,
            AnnotationLookupService.class,
            AnnotationPersistenceService.class,
            DataIngestorService.class,
            ImageCaptureService.class,
            UserLookupService.class,
            VARSModelConverter.class,
            VideoControlService.class
        ]

        log.debug("Looking at ${injectedClasses}")

        for (c in injectedClasses) {
            log.debug("Fetching ${c.getName()} from Guice Injector")
            Object svc = injector.getInstance(c)
            Assert.assertNotNull("Failed to find inject class for " + c.getName(), svc )
        }
  }

}