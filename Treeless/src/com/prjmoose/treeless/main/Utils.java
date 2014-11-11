package com.prjmoose.treeless.main;

public class Utils {
	public static int limit(int x, int lo, int hi) {
		return Math.max(Math.min(x, hi), lo);
	}
}
