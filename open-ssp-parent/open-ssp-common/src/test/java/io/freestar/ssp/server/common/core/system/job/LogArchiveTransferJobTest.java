package io.freestar.ssp.server.common.core.system.job;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.*;

public class LogArchiveTransferJobTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    //  dev/2018/08/21/ssp-server-59ff865b5b-8btms-auction-20180821-1719.log.gz
    //  env+dir+hostname+-name
    //  dev/2018/08/21/auction-20180821-1719-ssp-server-59ff865b5b-8btms.log.gz
    //  env+dir+
    @Test
    public void execute() {
        String env = "dev";
        String hostname = "ssp-server-59ff865b5b-8btms";
        File file = new File("auction-20180822-2139.log.gz");
        Date date = new Date(1534965838604L);
        System.out.println(System.currentTimeMillis());

        assertEquals("dev/2018/08/22/auction-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(env, hostname, file, date));
        assertEquals("dev/2018/08/22/auction-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(null, hostname, file, date));
        file = new File("auction-win-20180822-2139.log.gz");
        assertEquals("dev/2018/08/22/auction-win-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(env, hostname, file, date));
        assertEquals("dev/2018/08/22/auction-win-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(null, hostname, file, date));

        file = new File("auction-20180822-2139.log.gz");
        env = "";
        assertEquals("dev/2018/08/22/auction-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(env, hostname, file, date));
        assertEquals("dev/2018/08/22/auction-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(null, hostname, file, date));
        file = new File("auction-win-20180822-2139.log.gz");
        assertEquals("dev/2018/08/22/auction-win-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(env, hostname, file, date));
        assertEquals("dev/2018/08/22/auction-win-20180822-2139-ssp-server-59ff865b5b-8btms.log.gz", LogArchiveTransferJob.constructNewName(null, hostname, file, date));
    }
}