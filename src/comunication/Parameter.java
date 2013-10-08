package comunication;

public enum Parameter {
	ID, USERNAME, PASSWORD, NEW_PASSWORD, CHANNEL_ID, USER_ID, HASH, DATE, MAIL, NEW_MAIL, ERROR, DATA;

	public static Parameter getByOrdinal(int ordinal) {

		for (Parameter p : Parameter.values()) {
			if (p.ordinal() == ordinal)
				return p;
		}
		return null;
	}

}
