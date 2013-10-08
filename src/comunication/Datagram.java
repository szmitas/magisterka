
package comunication;


import java.util.HashMap;
import java.util.Map;


public class Datagram {

	public static void main(String[] args) {
	}

	public static Datagram generateAnswer(Datagram datagram, Method method,
			Map<Parameter, String> parameters) {
		Datagram result = new Datagram();
		result.datagramId = datagram.datagramId;
		result.method = method;
		result.parameters = parameters;
		result.addNotAddedParameters();
		return result;
	}

	// znak rozdzielający pola
	public static final String DELIMITER = "" + (char) 0;
	// generator id
	private static byte datagramIdGenerator = 0;
	// metoda datagramu
	private Method method;
	// nazwa parametru + jego wartość
	private Map<Parameter, String> parameters;
	// id datagramu (metoda generateAnswer() przekazuje ten sam id)
	private byte datagramId;


	private Datagram() {

	}

	public Datagram(Method method, Map<Parameter, String> parameters) {
		this.method = method;
		this.parameters = parameters;
		this.datagramId = datagramIdGenerator++;
		addNotAddedParameters();
	}


	public Datagram(String datagram) {
		this.parameters = new HashMap<Parameter, String>();
		String[] fields = datagram.split("" + DELIMITER);

		// pobieranie id datagramu
		this.datagramId = Byte.parseByte(fields[0]);

		// pobieranie metod
		method = Method.getByOrdinal(Integer.parseInt(fields[1]));

		// pobieranie parametrów
		for (int colonIndex, i = 2; i < fields.length; ++i) {
			colonIndex = fields[i].indexOf(':');
			parameters.put(Parameter.getByOrdinal(Integer.parseInt(fields[i]
					.substring(0, colonIndex))), fields[i]
					.substring(colonIndex + 1));
		}

		// uzupełnianie niewpisanych parametrów
		addNotAddedParameters();
	}

	public Method getMethod() {
		return method;
	}

	public Map<Parameter, String> getParameters() {
		return parameters;
	}

	public int getDatagramId() {
		return datagramId;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("" + datagramId + DELIMITER);
		result.append("" + method.ordinal() + DELIMITER);
		for (Parameter p : Parameter.values()) {
			String s = parameters.get(p);
			if (s != null) {
				result.append(p.ordinal() + ":" + s + DELIMITER);
			}
		}
		return result.toString();
	}

	private void addNotAddedParameters() {
		for (Parameter p : this.method.getApplicableParameters()) {

			if (!this.parameters.containsKey(p)) {

				if (p.equals(Parameter.DATA)) {
					this.parameters.put(p, "");
				} else {
					this.parameters.put(p, "-1");
				}
			}
		}
	}
}
