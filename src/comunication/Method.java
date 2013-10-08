package comunication;


public enum Method {

	//request
	LOGIN(Parameter.ID, Parameter.USERNAME, Parameter.PASSWORD),
	LOGOUT(Parameter.ID, Parameter.HASH),
	CHANNEL_NEWS(Parameter.HASH, Parameter.CHANNEL_ID),
	USER_NEWS(Parameter.HASH),
	DAY_NEWS(Parameter.HASH, Parameter.DATE),
	SUBSCRIBE(Parameter.HASH,Parameter.DATA),
	UNSUBSCRIBE(Parameter.HASH,Parameter.CHANNEL_ID),
	REGISTER(Parameter.ID, Parameter.USERNAME, Parameter.PASSWORD, Parameter.MAIL),
	NEW_PASS(Parameter.ID,  Parameter.HASH, Parameter.NEW_PASSWORD ,Parameter.PASSWORD),
	NEW_MAIL(Parameter.ID,  Parameter.HASH, Parameter.NEW_MAIL ,Parameter.MAIL),
	TOP_CHANNELS(Parameter.HASH),
	SUBSYN(Parameter.HASH),
	//response
	LOGIN_RESPONSE(Parameter.HASH),
	LOGOUT_RESPONSE(),
	NEWPASS_RESPONSE(),
	REGISTER_RESPONSE(),
	SUBSCRIBE_RESPONSE(Parameter.ID, Parameter.DATA),
	NEWMAIL_RESPONSE(),
	UNSUBSCRIBE_RESPONSE(),
	TOPCHANNELS_RESPONSE(Parameter.DATA),
	CHANNELNEWS_RESPONSE(Parameter.DATA),
	USERNEWS_RESPONSE(Parameter.DATA),
	SUBSYN_RESPONSE(Parameter.DATA),
	//errors
	ERROR(Parameter.DATA);

	public static Method getByOrdinal(int ordinal) {
		for (Method m : Method.values()) {
			if (m.ordinal() == ordinal)
				return m;
		}
		return null;
	}

	private Parameter[] applicableParameters;


	private Method(Parameter... applicableParameters) {
		this.applicableParameters = applicableParameters;
	}

	public Parameter[] getApplicableParameters() {
		return applicableParameters;
	}
}
