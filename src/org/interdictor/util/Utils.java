package org.interdictor.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
public class Utils {

	public static String dump(Object o) {
		if(o == null) {
			return "null";
		}
		@SuppressWarnings("rawtypes")
		Class c = o.getClass();
		
		StringBuilder sb =  new StringBuilder();
		sb.append("{\n");
		for(Field f : c.getFields()) {
			sb.append(f.getName());
			sb.append(": ");
			try {
				sb.append(f.get(o));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append(",\n");
		}
		sb.append("}");
		return sb.toString();
	}

	public static String join(String separator, List<? extends Object> strings) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.size(); i++) {
			sb.append(strings.get(i).toString());
			if(i < strings.size() - 1 ) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public static CharSequence join(String separator, String[] items) {
		return join(separator, Arrays.asList(items));
	}
	
}
