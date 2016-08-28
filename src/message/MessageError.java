package message;


public enum MessageError {
	NONE,  // if no errors

	ID_MISSED,
	ID_IS_NOT_INT,

	LOGIN_MISSED,
	LOGIN_IS_NOT_STRING,

	PASSWORD_MISSED,
	PASSWORD_IS_NOT_STRING,

	INFO_MISSED,
	INFO_IS_NOT_ARRAY
}
