package com.trc.config;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.trc.service.gateway.WebserviceGateway;
import com.tscp.util.logger.DevLogger;

@Component
@Scope("singleton")
public final class Config {
	private static ClassLoader classLoader;
	private static final String configFile = "config/config.properties";
	private static final String monthsFile = "config/dates/months.properties";
	private static final String yearsFile = "config/dates/years.properties";
	private static final String statesFile = "config/geo/states.properties";
	public static String ENVIRONMENT;
	public static boolean ADMIN;
	private static int yearRange;
	public static SortedMap<String, String> states = new TreeMap<String, String>();
	public static SortedMap<String, String> months = new TreeMap<String, String>();
	public static SortedMap<Integer, String> yearsFuture = new TreeMap<Integer, String>();
	public static SortedMap<Integer, String> yearsPast = new TreeMap<Integer, String>();

	public static boolean initialized = false;

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(Config.class);

	@PostConstruct
	public static void init() {
		if (!initialized) {
			classLoader = Config.class.getClassLoader();
			try {
				loadConfig();
				loadMonths();
				loadYears();
				loadStates();
				initialized = true;
			} catch (IOException e) {
				logger.error("Failed to load properties file: " + e.getMessage());
			}
		}
	}

	private static void loadConfig() throws IOException {
		if (!WebserviceGateway.initialized) {

			Properties props = new Properties();
			props.load(classLoader.getResourceAsStream(configFile));

			ENVIRONMENT = props.getProperty("environment");
			ADMIN = props.getProperty("admin").equals("0") ? false : true;
			WebserviceGateway.serviceName = props.getProperty("serviceName");
			WebserviceGateway.namespace = props.getProperty("namespace");

			if (ENVIRONMENT.equalsIgnoreCase("production")) {
				WebserviceGateway.location = props.getProperty("wsdl_production_ip");
			} else if (ENVIRONMENT.equalsIgnoreCase("development")) {
				WebserviceGateway.location = props.getProperty("wsdl_development_ip");
			} else if (ENVIRONMENT.equalsIgnoreCase("local")) {
				WebserviceGateway.location = props.getProperty("wsdl_localhost");
			}

			DevLogger.debug("Environment: " + ENVIRONMENT);
			DevLogger.debug("Admin: " + ADMIN);
		}
	}

	private static void loadMonths() throws IOException {
		Properties properties = new Properties();
		properties.load(classLoader.getResourceAsStream(monthsFile));
		for (Entry<Object, Object> entry : properties.entrySet()) {
			months.put((String) entry.getKey(), (String) entry.getValue());
		}
	}

	private static void loadYears() throws IOException {
		Properties properties = new Properties();
		properties.load(classLoader.getResourceAsStream(yearsFile));
		yearRange = Integer.parseInt(properties.getProperty("range"));
		int currentYear = new DateTime().getYear();
		String value;
		int yearFuture;
		int yearPast;
		for (int i = 0; i < yearRange; i++) {
			yearFuture = currentYear + i;
			value = Integer.toString(yearFuture).substring(2);
			yearsFuture.put(yearFuture, value);
			yearPast = currentYear - i;
			value = Integer.toString(yearPast).substring(2);
			yearsPast.put(yearPast, value);
		}
	}

	private static void loadStates() throws IOException {
		Properties properties = new Properties();
		properties.load(classLoader.getResourceAsStream(statesFile));
		for (Entry<Object, Object> entry : properties.entrySet()) {
			states.put((String) entry.getKey(), (String) entry.getValue());
		}
	}

	public static int getYearRange() {
		return yearRange;
	}

	public static void setYearRange(
			int yearRange) {
		Config.yearRange = yearRange;
	}

	public static SortedMap<Integer, String> getYearsFuture() {
		return yearsFuture;
	}

	public static void setYearsFuture(
			SortedMap<Integer, String> yearsFuture) {
		Config.yearsFuture = yearsFuture;
	}

	public static SortedMap<Integer, String> getYearsPast() {
		return yearsPast;
	}

	public static void setYearsPast(
			SortedMap<Integer, String> yearsPast) {
		Config.yearsPast = yearsPast;
	}

	public static SortedMap<String, String> getStates() {
		return states;
	}

	public static void setStates(
			SortedMap<String, String> states) {
		Config.states = states;
	}

	public static SortedMap<String, String> getMonths() {
		return months;
	}

	public static void setMonths(
			SortedMap<String, String> months) {
		Config.months = months;
	}

}