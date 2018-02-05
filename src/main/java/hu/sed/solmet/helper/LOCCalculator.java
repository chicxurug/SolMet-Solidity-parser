package hu.sed.solmet.helper;

import java.util.StringTokenizer;

public class LOCCalculator {

	private String separator;
	private int lloc;
	private int cloc;
	private int start;
	private int end;
	
	public LOCCalculator(String separator, int start, int end) {
		this.separator = separator;
		lloc = cloc = 0;
		this.start = start;
		this.end = end;
	}
	
	public void calculateLOCMetrics(String code) {
		int skip = 0;
		StringTokenizer st = new StringTokenizer(code, separator);
		while (st.hasMoreTokens()) {
			String x = st.nextToken();
			if (skip<start) {
				skip++;
				continue;
			}
			if (!x.trim().isEmpty()) {
				if (!x.trim().startsWith("/*") && !x.trim().startsWith("*") && !x.trim().startsWith("//") && !x.trim().endsWith("*/")) {
					lloc++;
					if (x.trim().contains("//")) {
						cloc++;
					}
				} else {
					cloc++;
				}
			}
			skip++;
			if (skip>end) {
				break;
			}
		}
	}
	
	public int getLLOC() {
		return lloc;
	}
	
	public int getCLOC() {
		return cloc;
	}
}
