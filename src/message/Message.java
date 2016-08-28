package message;

import checker.Checker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.logging.Logger;

/**
 * Message entity
 *
 * @author Anton
 */
public class Message {

	private Logger logger = Checker.getLogger();
	private JsonObject jsonMessage = new JsonObject();
	private MessageStatus status = MessageStatus.NONE;
	private MessageError error = MessageError.NONE;

	/**
	 * Try to parse java.java.checker.java.message and check status if success
	 *
	 * @param input: java.java.checker.java.message to parse
	 */
	public Message(String input) {
		try {
			JsonElement element = new JsonParser().parse(input);
			if (element.isJsonObject()) {
				jsonMessage = element.getAsJsonObject();
				checkStatus();
			} else {
				logger.warning("Message is not JSON");
			}
		} catch (JsonParseException jpEx) {
			logger.severe("Message isn't in JSON format");
			logger.severe(input);
			System.exit(1);
		}
	}

	public MessageStatus getStatus() {
		return status;
	}

	public MessageError getError() {
		return error;
	}

	/**
	 * Check status by relevant.
	 * id       - the most important
	 * login    - second
	 * password - third
	 * info     - the least
	 *
	 * password enough to OK status, maybe changed or deleted from here
	 */
	private void checkStatus() {
		if (checkId()) {
			status = MessageStatus.ID;
			if (checkLogin()) {
				status = MessageStatus.LOGIN;
				if (checkPassword()) {
					status = MessageStatus.PASSWORD;
					if (checkInfo()) {
						status = MessageStatus.INFO;
					}
				}
			}
		}

		// maybe changed or deleted
		if (status.compareTo(MessageStatus.PASSWORD) >= 0) status = MessageStatus.OK;

		if (!status.equals(MessageStatus.OK)) {
			logger.warning("Message checking error: " + error);
		}
	}

	/**
	 * id must be integer
	 */
	private boolean checkId() {
		if (jsonMessage.has("id")) {
			if (jsonMessage.get("id").isJsonPrimitive()) {
				if (jsonMessage.getAsJsonPrimitive("id").isNumber()) {
					String stringId = jsonMessage.getAsJsonPrimitive("id").getAsString();
					if (!stringId.contains(".")) return true;
					else error = MessageError.ID_IS_NOT_INT;
				} else error = MessageError.ID_IS_NOT_INT;
			} else error = MessageError.ID_IS_NOT_INT;
		} else error = MessageError.ID_MISSED;

		return false;
	}

	/**
	 * login must be string
	 */
	private boolean checkLogin() {
		if (jsonMessage.has("login")) {
			if (jsonMessage.get("login").isJsonPrimitive()) {
				if (jsonMessage.getAsJsonPrimitive("login").isString()) {
					return true;
				} else error = MessageError.LOGIN_IS_NOT_STRING;
			} else error = MessageError.LOGIN_IS_NOT_STRING;
		} else error = MessageError.LOGIN_MISSED;

		return false;
	}

	/**
	 * password must be string
	 */
	private boolean checkPassword() {
		if (jsonMessage.has("password")) {
			if (jsonMessage.get("password").isJsonPrimitive()) {
				if (jsonMessage.getAsJsonPrimitive("password").isString()) {
					return true;
				} else error = MessageError.PASSWORD_IS_NOT_STRING;
			} else error = MessageError.PASSWORD_IS_NOT_STRING;
		} else error = MessageError.PASSWORD_MISSED;

		return false;
	}

	/**
	 * info must be array
	 */
	private boolean checkInfo() {
		if (jsonMessage.has("info")) {
			if (jsonMessage.get("info").isJsonArray()) {
				return true;
			} else error = MessageError.INFO_IS_NOT_ARRAY;
		} else error = MessageError.INFO_MISSED;

		return false;
	}
}
