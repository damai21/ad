package io.freestar.ssp.server.common.core.system.job;

import com.atg.openssp.common.core.system.loader.GlobalContextLoader;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeoIp2TransferJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(GeoIp2TransferJob.class);
    public static final String DB_NAME = "GeoLite2-City.mmdb";
    public static final String VERSION_FILE = "GeoLite2-City-Version.txt";
    public static final String VERSION_TYPE = "text/plain";
    public static final File WORKING_DIR = new File("/opt/GeoLite2_data");
    public static final File DATABASE = new File(WORKING_DIR, DB_NAME);
    public static final File TEMP = new File(WORKING_DIR, DB_NAME+"_temp");
    public static final File VERSION = new File(WORKING_DIR, VERSION_FILE);
    private final String env;

    public GeoIp2TransferJob() {
        env = GlobalContextLoader.resolveEnvironment()+"_";
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(getClass().getSimpleName() + " fired ... next fire time: " + context.getNextFireTime());
        execute();
    }

    private void execute() {
        if (!WORKING_DIR.exists()) {
            WORKING_DIR.mkdirs();
        }
//        if (GoogleCloudHandler.getInstance().exists(env+VERSION_FILE)) {
//            try {
//                String myVersion = getLocalVersion(VERSION);
//                String remoteVersion = GoogleCloudHandler.getInstance().extractStringFromFile(env+VERSION_FILE);
//                if (myVersion == null || !myVersion.equals(remoteVersion)) {
//                    grabRemote(TEMP);
//                    promoteToFinal(TEMP, DATABASE);
//                    touchVersion(VERSION, remoteVersion);
//                }
//            } catch (IOException e) {
//                log.error("grabbing remote version", e);
//            }
//        }
    }

    private void touchVersion(File version, String v) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(version));
        pw.println(v);
        pw.close();
    }

    private String getLocalVersion(File versionFile) throws IOException {
        if (versionFile != null && versionFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(versionFile));
                return br.readLine();
            } catch (FileNotFoundException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void grabRemote(File database) throws IOException {
        GoogleCloudHandler.getInstance().downloadFile(database,env+DB_NAME);
    }

    static void promoteToFinal(File tempDb, File db) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        File newFile = new File(db.getParentFile(), db.getName()+"-"+sdf.format(now));
        db.renameTo(newFile);
        tempDb.renameTo(db);
    }

    public static void initDatabase() {
        GeoIp2TransferJob job = new GeoIp2TransferJob();
     //   job.execute();
    }
}
