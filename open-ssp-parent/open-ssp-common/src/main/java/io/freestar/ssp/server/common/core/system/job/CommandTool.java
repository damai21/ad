package io.freestar.ssp.server.common.core.system.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.PrintWriter;

public final class CommandTool {
	private static final Logger LOG = LoggerFactory.getLogger(LogArchiveTransferJob.class);

	private CommandTool() {
	}

	public static void execute(String info, String command) throws IOException {
		Runtime run = Runtime.getRuntime();
		try {
			//LOG.info("execute", "calling for " + info);
			//LOG.info("execute", command);
			Process proc = run.exec(command);
			//LOG.debug("execute", "starting for " + command);
			proc.waitFor();
			//LOG.debug("execute", "ending for " + command);
			BufferedReader is = new BufferedReader(new InputStreamReader((proc.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			//LOG.debug("execute", sb);
			is.close();
			is = new BufferedReader(new InputStreamReader((proc.getErrorStream())));
			sb.setLength(0);
			while ((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			System.out.println(sb);
			//LOG.error("execute", "E: " + sb);
			is.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.info("execute", e);
		}
	}

	public static void execute(String info, String command, File outFile) {
		Runtime run = Runtime.getRuntime();
		try {
			//LOG.info("execute", "calling for " + info);
			//LOG.info("execute", command);
			Process proc = run.exec(command);
			//LOG.debug("execute", "starting for " + command);
			proc.waitFor();
			//LOG.debug("execute", "ending for " + command);
			BufferedReader is = new BufferedReader(new InputStreamReader((proc.getInputStream())));
			PrintWriter fw  = new PrintWriter(new FileWriter(outFile));
			String line;
			while ((line = is.readLine()) != null) {
				fw.println(line);
			}
			//LOG.debug("execute", sb);
			fw.close();
			is.close();
			is = new BufferedReader(new InputStreamReader((proc.getErrorStream())));
			StringBuilder sb = new StringBuilder();
			while ((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			System.out.println(sb);
			//LOG.error("execute", "E: " + sb);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("execute", e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.info("execute", e);
		}
	}

	/*
	public static void execute(String info, String[] command) {
		Runtime run = Runtime.getRuntime();
		try {
			LOG.info("execute", "calling for " + info);
			// LOG.info("execute", command);
			Process proc = run.exec(command);
			LOG.debug("execute", "starting for " + command);
			proc.waitFor();
			LOG.debug("execute", "ending for " + command);
			BufferedReader is = new BufferedReader(new InputStreamReader((proc.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			LOG.debug("execute", sb);
			is.close();
			is = new BufferedReader(new InputStreamReader((proc.getErrorStream())));
			sb.setLength(0);
			while ((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			System.out.println(sb);
			LOG.error("execute", "E: " + sb);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("execute", e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.info("execute", e);
		}
	}
	*/

}
