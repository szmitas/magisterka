package comunication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	
	public static List<Integer> stringToIntList(String input, String delimiter) {
		List<Integer> result = new ArrayList<Integer>();
		List<String> inputs = new ArrayList<String>(Arrays.asList(input.split(delimiter)));
		for (String s : inputs) {
			result.add(Integer.parseInt(s));
		}
		return result;
	}
	
	public static String intListToString(List<Integer> inputs, String delimiter) {
		String result = "";
		if (inputs != null && !inputs.isEmpty()) {
			result += inputs.get(0).toString();
			if (inputs.size() > 1) {
				for (int i = 1; i < inputs.size(); ++i) {
					result += delimiter + inputs.get(i).toString();
				}
			}
		}
		return result;
	}
}
