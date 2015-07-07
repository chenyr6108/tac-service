package com.brick.base.util;

import java.io.File;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.springframework.core.io.Resource;

public class BirtReportEngine {
	
	Logger logger = Logger.getLogger(BirtReportEngine.class);
	
	private Resource engineHome;
	private String outputPath;
	private Resource reportDesigenPath;
	private String reportDesigenPathStr;
	private String financeleaseJar;
	
	public String getFinanceleaseJar() {
		return financeleaseJar;
	}

	public void setFinanceleaseJar(String financeleaseJar) {
		this.financeleaseJar = financeleaseJar;
	}

	public String getReportDesigenPathStr() {
		return reportDesigenPathStr;
	}

	public void setReportDesigenPathStr(String reportDesigenPathStr) {
		this.reportDesigenPathStr = reportDesigenPathStr;
	}

	public Resource getEngineHome() {
		return engineHome;
	}

	public void setEngineHome(Resource engineHome) {
		this.engineHome = engineHome;
	}

	public Resource getReportDesigenPath() {
		return reportDesigenPath;
	}

	public void setReportDesigenPath(Resource reportDesigenPath) {
		this.reportDesigenPath = reportDesigenPath;
	}

	public String getOutputPath() {
		return "\\\\"+ LeaseUtil.getIPAddress()+ outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	private IReportEngine engine = null;
	private EngineConfig config = null;
	
	/**
	 * Configure the Engine and start the Platform
	 * @throws BirtException
	 */
	public void startUp() throws BirtException{
		config = new EngineConfig();
		config.setEngineHome(engineHome.getFilename());
		config.setLogConfig(null, Level.FINE);
		Platform.startup(config);
	}
	
	/**
	 * 制作投保单
	 * @param reportDesign
	 * @param outputFileName
	 * @param paramMap
	 * @throws Exception
	 */
	public void executeReport(String reportDesign, String outputFileName, Map<String, Object> paramMap) throws Exception {
		IRunAndRenderTask task = null;
		IReportRunnable design = null;
		try {
			IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			// Open the report design
			engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);
			String entryPath = reportDesigenPathStr + "/" + reportDesign;
			logger.info("=======================>>" + entryPath);
			if (reportDesigenPath.exists()) {
				logger.info("本地:===============>>");
				//File designFile = new File(reportDesigenPath.getFile(), reportDesign);
				//design = engine.openReportDesign(new FileInputStream(designFile));
				///tac-service-1.0.jar
				File webJar = new File(reportDesigenPath.getFile(), "tac-service-1.0.jar");
				if (!webJar.exists()) {
					webJar = new File(reportDesigenPath.getFile(), "tac-service.jar");
				}
				if (webJar.exists()) {
					JarFile thisJar = new JarFile(webJar);
					logger.info(thisJar.getName());
					JarEntry entry = thisJar.getJarEntry(entryPath);
					logger.info(entry);
					if (entry == null) {
						throw new Exception("找不到Desigen文件");
					}
					design = engine.openReportDesign(thisJar.getInputStream(entry));
				}
			} else {
				logger.info("Jar包:===============>>");
				JarFile thisJar = new JarFile(financeleaseJar);
				logger.info(thisJar.getName());
				JarEntry entry = thisJar.getJarEntry(entryPath);
				logger.info(entry);
				if (entry == null) {
					throw new Exception("找不到Desigen文件");
				}
				design = engine.openReportDesign(thisJar.getInputStream(entry));
			}
			task = engine.createRunAndRenderTask(design);
			// Set rendering options - such as file or stream output,
			// output format, whether it is embeddable, etc
			HTMLRenderOption options = new HTMLRenderOption();
			// Set parameter
			if (paramMap != null) {
				task.setParameterValues(paramMap);
				task.validateParameters();
			}
			// Set output file.
			options.setOutputFileName(getOutputPath() + File.separator + outputFileName);
			//Set output format 
			String[] s = outputFileName.split("\\.");
			options.setOutputFormat(s[s.length - 1]);
			task.setRenderOption(options);
			// run the report and destroy the engine
			// Note - If the program stays resident do not shutdown the Platform or
			// the Engine
			
			// logger.info("Finished");
			// System.exit(0);
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				task.run();
				task.close();
				engine.shutdown();
				//Platform.shutdown();
			} catch (Exception e) {
				throw e;
			}
		}
	}
}
