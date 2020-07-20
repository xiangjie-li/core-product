import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ScriptGenerator {

//	public static void main(String[] args) {
//		
//
//	}

	 
	
	ArrayList<LinkedHashMap> arr;
	public ScriptGenerator(ArrayList<LinkedHashMap> _arr) {
		arr = _arr;
	}
	
	
	
	
	public LinkedHashMap generate() {
		System.out.println("start to generate");
		
		LinkedHashMap config = new LinkedHashMap();
		for (LinkedHashMap m : arr) {
			String[] targetPathArr = ((String) m.get("target")).split("\\.");
		    String[] sourcePathArr = ((String) m.get("source")).split("\\.");
		    int maxDepth = Math.max(targetPathArr.length, sourcePathArr.length) - 1;
		    generateMapping(config, targetPathArr, sourcePathArr, maxDepth, 0, 0);
		}
		return config;
	}
	

	
	public void generateMapping(LinkedHashMap m, String[] targetPathArr, String[] sourcePathArr, int maxDepth, int curTargetDepth, int curSourceDepth) {
//		int curTargetDepth = curDepth > targetPathArr.length - 1 ? targetPathArr.length - 1 : curDepth;
//		int curSourceDepth = curDepth > sourcePathArr.length - 1 ? sourcePathArr.length - 1 : curDepth;
		int nextTargetDepth = curTargetDepth + 1 > targetPathArr.length - 1 ? targetPathArr.length - 1 : curTargetDepth + 1;
		int nextSourceDepth = curSourceDepth + 1 > sourcePathArr.length - 1 ? sourcePathArr.length - 1 : curSourceDepth + 1;
		
		
		String curTargetPath = targetPathArr[curTargetDepth];
		String curSourcePath = sourcePathArr[curSourceDepth];
		
		String curType = getMappingType(curTargetPath, curSourcePath, curTargetDepth, targetPathArr.length - 1, curSourceDepth, sourcePathArr.length - 1);
		
		// if the path is array which contains '*', remove the '*' in config json
		if (curTargetPath.charAt(0) == '*') curTargetPath = curTargetPath.substring(1);
		if (curSourcePath.charAt(0) == '*') curSourcePath = curSourcePath.substring(1);
		
		// if the path is content which is surrounded by parenthesis, remove the parenthesis in config json
		if (curSourcePath.charAt(0) == '(') curSourcePath = curSourcePath.substring(1, curSourcePath.length() - 1);
		
		
	    // if type is "arrayItem_to_something", specify the itemIndex as an element in the config object
	    if (curType == "arrayItem_to_field" || curType == "arrayItem_to_object") {
	    	int indexOfOpenSquareBracket = curSourcePath.indexOf('[');
	    	String itemIndexWithBracket = curSourcePath.substring(indexOfOpenSquareBracket);
	    	int itemIndex = Integer.parseInt(itemIndexWithBracket.substring(1, itemIndexWithBracket.length() - 1));
	    	m.put("itemIndex", itemIndex);
	    	curSourcePath = curSourcePath.substring(0, indexOfOpenSquareBracket);
	    }
	    
	    
	    // if the curTargetPath is the field name and contains data type coercion, (eg. price:number)
	    // then add the targetDataType as an element in mapping.
	    int indexOfColon = curTargetPath.indexOf(':');
	    if (indexOfColon > 0) {
	    	m.put("targetDataType", curTargetPath.substring(indexOfColon + 1));
	    	curTargetPath = curTargetPath.substring(0, indexOfColon);
	    }
	    
	    
		
	    m.put("target", curTargetPath);
	    m.put("source", curSourcePath);
	    m.put("type", curType);
	    
	    
	    if (curTargetDepth < targetPathArr.length - 1 || curSourceDepth < sourcePathArr.length - 1) {
//	    	ArrayList<LinkedHashMap> mappings = new ArrayList<>();
	    	int mappingIdx = 0;
	    	
	    	if (m.containsKey("mappings")) {
	    		
	    
	    		LinkedHashMap[] mappings = (LinkedHashMap[]) m.get("mappings"); 
    			String nextTargetPath = targetPathArr[nextTargetDepth].charAt(0) == '*' ? targetPathArr[nextTargetDepth].substring(1) : targetPathArr[nextTargetDepth];
    			
    			// try
    			String nextSourcePath = sourcePathArr[nextSourceDepth].charAt(0) == '*' ? sourcePathArr[nextSourceDepth].substring(1) : sourcePathArr[nextSourceDepth];
    					
    					
	    		int[] offsets = new int[2];
	    		offsets[0] = 0;    // offsets[0] is the offset of the targetPathDepth
	    		offsets[1] = 0;    // offsets[1] is the offset of the sourcePathDepth
	    		int idx = mappingContainsTarget(mappings, nextTargetPath, nextSourcePath, offsets);
    			while (idx >= 0) {
    				m = mappings[idx];
    				mappings = (LinkedHashMap[])mappings[idx].get("mappings");
    				offsets[0]++;
    				nextTargetPath = targetPathArr[nextTargetDepth + offsets[0]].charAt(0) == '*' ? targetPathArr[nextTargetDepth + offsets[0]].substring(1) : targetPathArr[nextTargetDepth + offsets[0]];
    				
    				// try
    				nextSourcePath = sourcePathArr[nextSourceDepth + offsets[1]].charAt(0) == '*' ? sourcePathArr[nextSourceDepth + offsets[1]].substring(1) : sourcePathArr[nextSourceDepth + offsets[1]];
    				
    				idx = mappingContainsTarget(mappings, nextTargetPath, nextSourcePath, offsets);
    			}
    			m.put("mappings", growMappingsArray(mappings));
    			mappingIdx = ((LinkedHashMap[]) m.get("mappings")).length - 1;
    			generateMapping(((LinkedHashMap[]) m.get("mappings"))[mappingIdx], targetPathArr, sourcePathArr, maxDepth, nextTargetDepth + offsets[0], nextSourceDepth + offsets[1]);
    			
	    	}
	    	
	    	else {
	    	    LinkedHashMap[] mappings = new LinkedHashMap[1];
	    	    mappings[0] = new LinkedHashMap();
	    	    m.put("mappings",mappings);
	    	    generateMapping(((LinkedHashMap[]) m.get("mappings"))[mappingIdx], targetPathArr, sourcePathArr, maxDepth, nextTargetDepth, nextSourceDepth);
	    	}
	    	
	    }
	}
	
	
	public int mappingContainsTarget(LinkedHashMap[] mappings, String nextTargetPath, String nextSourcePath, int[] offsets) {
		for (int i = 0; i < mappings.length; i++) {
			if (mappings[i].get("target").equals(nextTargetPath)) {
				if (mappings[i].get("type").equals("field_to_object") || (mappings[i].get("type").equals("object_to_object") && !mappings[i].get("source").equals(nextSourcePath))) {
					mappings[i].put("source", "multiple");
				}
				else {
					offsets[1]++;
				}
				return i;
			}
		}
		return -1;
	}
	
	
	
	
	public LinkedHashMap[] growMappingsArray(LinkedHashMap[] mappings) {
		int prevLen = mappings.length;
		LinkedHashMap[] newMappings = new LinkedHashMap[prevLen + 1];
		for (int i = 0; i < prevLen; i++) {
			newMappings[i] = mappings[i];
		}
		newMappings[prevLen] = new LinkedHashMap();
		return newMappings;
	}
	
	
	
	
	
	
	public String getMappingType(String curTargetPath, String curSourcePath, int curTargetDepth, int targetDepth, int curSourceDepth, int sourceDepth) {
		if (curTargetPath.charAt(0) == '*' && curSourcePath.charAt(0) == '*') {
			return "array_to_array";
		} 
		else if (curSourcePath.charAt(0) == '*' && curSourcePath.charAt(curSourcePath.length() - 1) == ']') {
		    if (curTargetDepth == targetDepth) {
			    return "arrayItem_to_field";
			}
			else {
				return "arrayItem_to_object";
			}
			
		}
		
		else if (curTargetDepth < targetDepth && curSourceDepth < sourceDepth) {
			return "object_to_object";
		}
		else if (curTargetDepth < targetDepth && curSourceDepth == sourceDepth) {
			return "field_to_object";
		}
		else if (curTargetDepth == targetDepth && curSourceDepth < sourceDepth) {
			return "object_to_field";
		}
		else if ((curTargetDepth == targetDepth && curSourceDepth == sourceDepth) && curSourcePath.equals("_index")) {
			return "index_to_field";
		}
		else if (curSourceDepth == sourceDepth && curSourcePath.charAt(0) == '(') {
			return "content_to_field";
		}
		else {
			return "field_to_field";
		}
	}
}
