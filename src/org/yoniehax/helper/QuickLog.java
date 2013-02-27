package org.yoniehax.helper;

import java.text.DateFormat;
import java.util.Date;

/**
 * The QuickLog class is used to provide basic log level functionality. 
 */
public class QuickLog {

	// current log level to display, lower is more verbose
	static int logLevel = 2;

	static int DEBUG = 1;
	static int INFO = 2;
	static int ERROR = 3;

	/**
	 * Logs text with 'info' log level.
	 * 
	 * @param text
	 *            text to log.
	 */
	public static void info(String text) {
		QuickLog.log(text, INFO);
	}

	/**
	 * Logs text with 'debug' log level.
	 * 
	 * @param text
	 *            text to log.
	 */
	public static void debug(String text) {
		QuickLog.log(text, DEBUG);
	}

	/**
	 * Logs text with 'error' log level.
	 * 
	 * @param text
	 *            text to log.
	 */
	public static void error(String text) {
		QuickLog.log(text, ERROR);
	}

	/*
	 * Logs given text with error level
	 */
	private static void log(String s, int errorLevel) {
		// only log when error level is sufficient
		if (errorLevel >= logLevel) {
			System.out.print(DateFormat.getInstance().format(new Date().getTime()) + " ");
			System.out.print((errorLevel == INFO ? "INFO" : (errorLevel == DEBUG ? "DEBUG" : "ERROR")) + ": ");
			System.out.print(s);
			System.out.println();
		}
	}

}
