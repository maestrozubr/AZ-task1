package filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * Logging filter entity.
 *
 * @author Anton
 */
public class LoggerFilter implements Filter {

	private ArrayList<Level> levels = new ArrayList<>();

	/**
	 * Set levels to accept
	 *
	 * @param levels: list of levels to accept
	 */
	public LoggerFilter(Level... levels) {
		Collections.addAll(this.levels, levels);
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		return levels.contains(record.getLevel());
	}
}
