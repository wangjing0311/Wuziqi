package com.ylw.wuziqi.net;

public class AliKey {

	private static String s = "504b0304140008080800636be94800000000000000000000000001000000"
			+ "30b30ca8ccb3c82a74350b370d754e0af3a8c90df3cf362f370ff18c0a2bcbf4280d8"
			+ "ff00f4b73750b8d282d49cb0db50000504b0708f820808a310000002f000000";

	public static String ak() {
		try {
			String str = ZipStringUtils.unzip(s);
			return str.split("\\|")[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String sk() {
		try {
			String str = ZipStringUtils.unzip(s);
			return str.split("\\|")[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
