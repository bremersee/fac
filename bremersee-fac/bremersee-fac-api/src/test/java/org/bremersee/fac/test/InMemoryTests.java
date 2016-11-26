package org.bremersee.fac.test;

import junit.framework.TestCase;
import org.bremersee.comparator.ObjectComparatorFactory;
import org.bremersee.fac.FailedAccessCounterImpl;
import org.bremersee.fac.domain.mem.FailedAccessInMemoryDao;
import org.bremersee.fac.model.AccessResultDto;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.pagebuilder.PageBuilderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;

/**
 * @author Christian Bremer
 */
public class InMemoryTests {

    private static final long removeFailedAccessEntriesAfterMillis = 1000L;

    private static final long removeFailedEntriesInterval = Duration.ofSeconds(2L).toMillis();

    private FailedAccessInMemoryDao dao;

    private FailedAccessCounterImpl fac;

    @Before
    public void start() {

        ObjectComparatorFactory objectComparatorFactory = ObjectComparatorFactory.newInstance();

        dao = new FailedAccessInMemoryDao();
        dao.setObjectComparatorFactory(objectComparatorFactory);

        fac = new FailedAccessCounterImpl();
        fac.setFailedAccessCounterThreshold(3);
        fac.setFailedAccessDao(dao);
        fac.setPageBuilder(new PageBuilderImpl());
        fac.setRemoveFailedAccessEntriesAfterMillis(removeFailedAccessEntriesAfterMillis);
        fac.setRemoveFailedEntriesInterval(removeFailedEntriesInterval);
        fac.start();
    }

    @Test
    public void testFailedAccessCounter() {

        final int threshold = fac.getFailedAccessCounterThreshold();
        final String remoteHost = "localhost";
        final String resourceId_01 = "01";

        AccessResultDto accessResult_01;
        for (int i = 0; i <= threshold; i++) {

            accessResult_01 = fac.accessFailed(resourceId_01, remoteHost, System.currentTimeMillis());
            if (i < threshold) {
                System.out.println(i + " < " + threshold + ": Is access granted? " + accessResult_01.isAccessGranted());
                TestCase.assertEquals(true, accessResult_01.isAccessGranted());
            } else {
                System.out.println(i + " == " + threshold + ": Is access granted? " + accessResult_01.isAccessGranted());
                TestCase.assertEquals(false, accessResult_01.isAccessGranted());
            }
        }

        FailedAccess failedAccess_01 = dao.getByResourceIdAndRemoteHost(resourceId_01, remoteHost);
        System.out.println(String.format("Entity is not null? %s", failedAccess_01 != null));
        TestCase.assertNotNull(failedAccess_01);

        final long start = System.currentTimeMillis();
        final long maxTime = start + removeFailedAccessEntriesAfterMillis + removeFailedEntriesInterval;

        while (dao.getByResourceIdAndRemoteHost(resourceId_01, remoteHost) != null
                && System.currentTimeMillis() <= maxTime) {

            System.out.println("Waiting for unlocking the resource ...");
            try {
                Thread.sleep(800L);
            } catch (InterruptedException e) {
                return;
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("Start: " + new Date(start));
        System.out.println("  End: " + new Date(end));

        final boolean isGrantedAgain = fac.isAccessGranted(resourceId_01, remoteHost).isAccessGranted();
        System.out.println("Is access granted? " + isGrantedAgain);
        TestCase.assertEquals(true, isGrantedAgain);
    }

    @After
    public void stop() {
        fac.stop();
    }

}
