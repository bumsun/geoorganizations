package com.partymaker.cinaitaren;

import android.text.TextUtils;
import android.util.Log;




public class L {
	public static void d(String str){
		if(Define.LOG_ENABLE){
			Log.d(Define.TAG, getLocation() + str);
		}
	}
	public static void v(String str){
		if(Define.LOG_ENABLE){
			Log.v(Define.TAG, getLocation() + str);
		}
	}
	public static void e(String str){
		if(Define.LOG_ENABLE){
			Log.e(Define.TAG, str);
		}
	}
	
	private static String getLocation() {
        final String className = L.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;
        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}
