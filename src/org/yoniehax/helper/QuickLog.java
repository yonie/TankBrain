package org.yoniehax.helper;

import java.text.DateFormat;
import java.util.Date;

/*
 * 
 */
public class QuickLog {

	final static int logLevel = 2;
	
	static int DEBUG = 1;
	static int INFO = 2;
	static int ERROR = 3;

	/**
	 * 
	 * 
	 * @param s
	 */
	public static void info(String s) {
		QuickLog.log(s, INFO);
	}

	/**
	 * 
	 * 
	 * @param s
	 */
	public static void debug(String s) {
		QuickLog.log(s, DEBUG);
	}

	/**
	 * 
	 * 
	 * @param s
	 */
	public static void error(String s) {
		QuickLog.log(s, ERROR);
	}

	/**
	 * 
	 * 
	 * @param s
	 * @param errorLevel
	 */
	private static void log(String s, int errorLevel) {
		if (errorLevel >= logLevel) {
			System.out.print(DateFormat.getInstance().format(new Date().getTime()) + " ");
			System.out.print((errorLevel == INFO ? "INFO" : (errorLevel == DEBUG ? "DEBUG" : "ERROR")) + ": ");
			System.out.print(s);
			System.out.println();
		}
	}

}
