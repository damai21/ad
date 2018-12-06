package io.freestar.ssp.server.common.core.system.job;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.atg.openssp.common.core.system.loader.GlobalContextLoader;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogArchiveTransferJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(LogArchiveTransferJob.class);
    private static final File baseDirRemote = new File(".");
    private static final File baseDirLocal = new File("./tomcat");
    private static final File logfilesDirRemote = new File(baseDirRemote, "logfiles");
    private static final File logfilesDirLocal = new File(baseDirLocal, "logfiles");
    private static final File statusDirRemote = new File(logfilesDirRemote, "status");
    private static final File statusDirLocal = new File(logfilesDirLocal, "status");
    private static final File archiveDirRemote = new File(logfilesDirRemote, "ssp");
    private static final File archiveDirLocal = new File(logfilesDirLocal, "ssp");
    private final String env;
    private final File statusDir;
    private final File archiveDir;
    private final boolean local;
    private String hostname;

    public LogArchiveTransferJob() {
        env = GlobalContextLoader.resolveEnvironment()+"_";
        if (env != null && !"".equals(env) && !"_".equals(env)) {
            statusDir = statusDirRemote;
            archiveDir = archiveDirRemote;
            local = false;
        } else {
            statusDir = statusDirLocal;
            archiveDir = archiveDirLocal;
            local = true;
        }
        log.info(getClass().getSimpleName() + " (status="+statusDir+", archive="+archiveDir+")");
        if (!statusDir.exists()) {
            statusDir.mkdirs();
        }
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
            log.error("could not get hostname", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(getClass().getSimpleName() + " fired ... next fire time: " + context.getNextFireTime());
        ArrayList<File> archives = new ArrayList<>();
        findArchives(archiveDir, archives);
        if (!local) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            generateVMSTAT(archives, sdf.format(now));
            generateFREE(archives, sdf.format(now));
            generateDU(archives, sdf.format(now));
        }
        log.info(getClass().getSimpleName() + " fired: " + archives);
        copyArchives(archives);
    }

    private void copyArchives(List<File> archives) {
        for (File f : archives) {
            String name = f.getName();
            String newName = constructNewName(env, hostname, f, new Date());
            try {
                GoogleCloudHandler.getInstance().uploadFile(f.getPath(), newName);
            } catch (Exception e) {
                log.error("saving file: "+f.getPath(), e);
            }
        }
        removeArchives(archives);
    }

    public static String constructNewName(String env, String hostname, File f, Date date) {
        String name = f.getName();
        int index = name.indexOf(".");
        String suffix = name.substring(index+1);
        name = name.substring(0, index);
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        String dir = sdf.format(date);
        String newName;
        if (env != null && !"".equals(env) && !"_".equals(env)) {
            newName = env.replaceAll("_","")+dir+name+"-"+hostname+"."+suffix;
        } else {
            newName = "dev"+dir+name+"-"+hostname+"."+suffix;
        }
        return newName;
    }

    private void removeArchives(List<File> archives) {
        for (File f : archives) {
            f.delete();
        }
    }

    private void findArchives(File dir, List<File> archives) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    findArchives(f, archives);
                } else {
                    if (f.getName().endsWith(".gz")) {
                        archives.add(f);
                    }
                }
            }
        }
    }

    private void generateDU(List<File> archives, String timeTag) {
        File file = new File(statusDir, "du_ah-"+timeTag+".out");
        CommandTool.execute("DU", "/usr/bin/du -ah", file);
        archives.add(file);
    }

    private void generateVMSTAT(List<File> archives, String timeTag) {
        File file = new File(statusDir, "vmstat.out-"+timeTag+".out");
        CommandTool.execute("VMSTAT", "/usr/bin/vmstat", file);
        archives.add(file);
    }

    private void generateFREE(List<File> archives, String timeTag) {
        File file = new File(statusDir, "free_h.out-"+timeTag+".out");
        CommandTool.execute("FREE", "/usr/bin/free -h", file);
        archives.add(file);
    }
}
