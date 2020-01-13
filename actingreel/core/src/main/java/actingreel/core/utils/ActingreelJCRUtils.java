package actingreel.core.utils;

import java.util.Set;
import java.util.Map.Entry;


public class ActingreelJCRUtils {

	public void debugEntrySet(Set<Entry<String, Object>> entrySet) {
		for(@SuppressWarnings("rawtypes") Entry entry : entrySet) {
			System.out.println("***** Argument in map: "+entry.getKey().toString()+" - "+entry.getValue().toString());
				System.out.println("***** Class of previous item: "+entry.getClass());
		}
	}
	
}
