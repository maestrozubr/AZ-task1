package checker;

import message.Message;
import message.MessageStatus;
import filters.LoggerFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.stream.Collectors;

/**
 * checker.Checker is the main class of the bundle.
 * It reads data from console or from file, checks it and write logs
 *
 * @author Anton
 */
public class Checker {

	private static final String LOG_DIR = "log";
	private static final String DEFAULT_INPUT_FILE = "input.txt";

	private static Logger logger;

	private static List<String> results;

	public static void main(String[] args) {
		logger = getLogger();
		logger.config("Logger set up");

		Path inputFilePath;
		if (args.length > 0) inputFilePath = Paths.get(args[0]);
		else inputFilePath = Paths.get(DEFAULT_INPUT_FILE);

		if (Files.exists(inputFilePath)) results = processFile(inputFilePath);
		else results = processStdin();
	}

	static List<String> getResults() {
		return results;
	}

	/**
	 * Read all lines from file and check each line as single java.java.checker.java.message
	 */
	private static List<String> processFile(Path filePath) {
		logger.info("Read from " + filePath.toString());
		List<String> results = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
			results.addAll(lines.stream().map(Checker::processMessage).collect(Collectors.toList()));
		} catch (IOException ioEx) {
			logger.warning("Can't read input file. Probably rights problem.");
		}
		return results;
	}

	/**
	 * Read line from stdin and check it as single java.java.checker.java.message
	 * while not receive Ctrl-D
	 */
	private static List<String> processStdin() {
		logger.info("Read from stdin");
		Scanner scanner = new Scanner(System.in);
		List<String> results = new ArrayList<>();
		while (true) {
			try {
				synchronized (Checker.class) {
					Checker.class.wait(150);
				}
				System.out.print("Input: ");  // Input prompt
				String line = scanner.nextLine();
				results.add(processMessage(line));
			} catch (NoSuchElementException nseEx) {
				break;
			} catch (InterruptedException interEx) {
				logger.severe("Can't wait main process");
				break;
			}
		}
		return results;
	}

	/**
	 * Create new java.java.checker.java.message entity and write log
	 */
	private static String processMessage(String line) {
		Message message = new Message(line);
		if (message.getStatus().equals(MessageStatus.OK)) {
			logger.fine(line);
		} else {
			logger.warning(line);
		}
		return message.getError().name();
	}

	/**
	 * Trying to set up logger and exit if couldn't
	 * Logger can be created only once.
	 *
	 * @return own logger
	 */
	public static Logger getLogger() {
		Logger mainLogger = Logger.getGlobal();
		try {
			if (logger == null) {
				mainLogger = setupLogger();
			} else {
				mainLogger = logger;
			}
		} catch (IOException ioEx) {
			Logger.getGlobal().severe("Logger setup error: " + ioEx.getMessage());
			System.exit(1);
		}
		return mainLogger;
	}

	/**
	 * Create and config main logger
	 * File handlers:
	 *     access:  logs only correct messages
	 *     error:   logs errors and incorrect messages
	 *     java.java.checker.java.checker: logs config and other system info
	 * Console handlers:
	 *     console: logs only warnings and errors
	 *
	 * @return own logger
	 */
	private static Logger setupLogger() throws IOException {
		checkDir();

		Logger logger = Logger.getLogger(Checker.class.getSimpleName());
		logger.setLevel(Level.ALL);

		Formatter formatter = getFormatter();

		FileHandler accessFileHandler = getAccessFileHandler(formatter);
		FileHandler checkerFileHandler = getCheckerFileHandler(formatter);
		FileHandler errorFileHandler = getErrorFileHandler(formatter);
		ConsoleHandler consoleHandler = getConsoleHandler(formatter);

		logger.addHandler(accessFileHandler);
		logger.addHandler(checkerFileHandler);
		logger.addHandler(errorFileHandler);
		logger.addHandler(consoleHandler);

		// Disable default parent handler (Global handler)
		logger.setUseParentHandlers(false);

		return logger;
	}

	/**
	 * Checks if log directory is exist and create it not
	 */
	private static void checkDir() {
		Path path = Paths.get(LOG_DIR);
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException ioEx) {
				Logger.getGlobal().severe("Can't create log dir. Probably rights problem.");
			}
		}
	}

	/**
	 * Sets up format of log messages
	 *
	 * @return formatter with own config
	 */
	private static Formatter getFormatter() {
		return new Formatter() {
			@Override
			public String format(LogRecord record) {

				// Date settings
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(record.getMillis());
				SimpleDateFormat format = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss.SSS]");

				// Level name settings
				int levelNameLen = record.getLevel().toString().length();
				int levelSpacesCount = Level.WARNING.getName().length() - levelNameLen; // Max length - current length
				String levelSpaces = new String(new char[levelSpacesCount]).replace('\0', ' '); // string with spaces

				// Class name settings
				int classNameLen = record.getSourceClassName().length();
				int classSpacesCount = 15 - classNameLen; // Max class name is java.java.checker.Message (15) symbols
				String classSpaces = new String(new char[classSpacesCount]).replace('\0', ' ');

				// Final format
				return format.format(calendar.getTime()) + " - " +
						levelSpaces + record.getLevel() + " - " +            // right justified level name
						classSpaces + record.getSourceClassName() + " - " +  // right justified class name
						record.getMessage() + "\n";
			}
		};
	}

	/**
	 * Create access file handler, set level and add filter
	 *
	 * @return access file handler
	 */
	private static FileHandler getAccessFileHandler(Formatter formatter) throws IOException {
		FileHandler accessFileHandler = new FileHandler(LOG_DIR + File.separatorChar + "access.log", true);
		accessFileHandler.setLevel(Level.FINEST);
		LoggerFilter accessFileFilter = new LoggerFilter(Level.FINEST, Level.FINER, Level.FINE);
		accessFileHandler.setFilter(accessFileFilter);
		accessFileHandler.setFormatter(formatter);

		return accessFileHandler;
	}

	/**
	 * Create java.java.checker.java.checker file handler, set level and add filter
	 *
	 * @return java.java.checker.java.checker file handler
	 */
	private static FileHandler getCheckerFileHandler(Formatter formatter) throws IOException {
		FileHandler checkerFileHandler = new FileHandler(LOG_DIR + File.separatorChar + "checker.log", true);
		checkerFileHandler.setLevel(Level.CONFIG);
		LoggerFilter checkerFileFilter = new LoggerFilter(Level.CONFIG, Level.INFO);
		checkerFileHandler.setFilter(checkerFileFilter);
		checkerFileHandler.setFormatter(formatter);

		return checkerFileHandler;
	}

	/**
	 * Create error file handler, set level and add filter
	 *
	 * @return error file handler
	 */
	private static FileHandler getErrorFileHandler(Formatter formatter) throws IOException {
		FileHandler errorFileHandler = new FileHandler(LOG_DIR + File.separatorChar + "error.log", true);
		errorFileHandler.setLevel(Level.WARNING);
		LoggerFilter errorFileFilter = new LoggerFilter(Level.WARNING, Level.SEVERE);
		errorFileHandler.setFilter(errorFileFilter);
		errorFileHandler.setFormatter(formatter);

		return errorFileHandler;
	}

	/**
	 * Create console handler, set level and add filter
	 *
	 * @return console handler
	 */
	private static ConsoleHandler getConsoleHandler(Formatter formatter) {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.WARNING);
		consoleHandler.setFormatter(formatter);

		return consoleHandler;
	}
}
