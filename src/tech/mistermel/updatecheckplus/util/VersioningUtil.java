package tech.mistermel.updatecheckplus.util;

import java.util.ArrayList;
import java.util.List;

public class VersioningUtil {

	private VersioningUtil() {}
	
	public static enum ComparisonResult {
		INCOMPATIBLE, LATEST, NEWER, OUT_OF_DATE;
	}
	
	public static ComparisonResult compareSemantic(String current, String latest) {
		List<Integer> currentSegments = getSegments(current);
		List<Integer> latestSegments = getSegments(latest);
		
		if(currentSegments.size() == 0 || latestSegments.size() == 0)
			return ComparisonResult.INCOMPATIBLE;
		
		int length = Math.max(currentSegments.size(), latestSegments.size());
		for(int i = 0; i < length; i++) {
			int currentSegment = currentSegments.size() > i ? currentSegments.get(i) : 0;
			int latestSegment = latestSegments.size() > i ? latestSegments.get(i) : 0;
			
			if(latestSegment > currentSegment) {
				return ComparisonResult.OUT_OF_DATE;
			} else if(latestSegment < currentSegment) {
				return ComparisonResult.NEWER;
			}
		}
		
		return ComparisonResult.LATEST;
	}
	
	public static List<Integer> getSegments(String str) {
		List<Integer> segments = new ArrayList<>();
		
		for(String segment : str.split("\\.")) {
			int num = getFirstNumber(segment);
			
			if(num == -1)
				break;
			
			segments.add(num);
		}
		
		return segments;
	}
	
	public static int getFirstNumber(String string) {
		int i = 0;
		while (i < string.length() && !Character.isDigit(string.charAt(i))) i++;
		int j = i;
		while (j < string.length() && Character.isDigit(string.charAt(j))) j++;
		return Integer.parseInt(string.substring(i, j));
	}
	
}
