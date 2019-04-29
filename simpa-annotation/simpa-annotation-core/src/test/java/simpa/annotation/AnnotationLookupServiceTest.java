/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpa.annotation;

import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class AnnotationLookupServiceTest {
    
    private static final Logger log = LoggerFactory.getLogger(AnnotationLookupServiceTest.class);
    
    @Test
    public void testMockSize() {
        int maxSize = 21;
        
        for (int i = 1; i < maxSize; i++) {
            AnnotationLookupService lookup = new MockAbstractLookup(i);
            List<? extends VideoTime> tapeTimes = lookup.findAllTimecodesByDate(null, new Date(), 100000);
            Assert.assertTrue("Expected " + i + " items, found " + tapeTimes.size(), 
                tapeTimes.size() == i);
        }
    }
    
    @Test
    public void interpolationTest() {
        
        final int size = 5;
        
        AnnotationLookupService lookup = new MockAbstractLookup(size);
        List<? extends VideoTime> tapeTimes = lookup.findAllTimecodesByDate(null, new Date(), 100000);
        if (log.isDebugEnabled()) {
            StringBuilder msg = new StringBuilder("Mock Tape Times:\n");
            for (VideoTime tapeTime : tapeTimes) {
                msg.append("\t").append(tapeTime).append("\n");
            }
            log.debug(msg.toString());
        }
        
        VideoTime tapeTime = lookup.interpolateTimecodeToDate(null, new Date(), 10000, 29.97);
        
        
        log.debug("Interpolated TapeTime:\n\t" + tapeTime);
        
    }
    
}
