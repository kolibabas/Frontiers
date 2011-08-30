package com.memeworks.frontiers;

import android.util.Log;
import android.view.MotionEvent;


public class Reflect {
	   @SuppressWarnings("unchecked")
	private static Class[] cInt = new Class[] { int.class };

	   public static int getX(MotionEvent evt, int index) {
	       try {
	    	   return ((Float)evt.getClass().getMethod("getX", cInt).invoke(evt, index)).intValue();
	       } catch (Exception ex) {
	    	   Log.e("Reflected GetX", ex.toString());
	       }
	       
	       return 0;
	   }
	   
	   public static int getY(MotionEvent evt, int index) {
	       try {
	    	   return ((Float)evt.getClass().getMethod("getY", cInt).invoke(evt, index)).intValue();
	       } catch (Exception ex) {
	    	   Log.e("Reflected GetY", ex.toString());
	       }
	       
	       return 0;
	   }
	   
	   public static int getPointerCount(MotionEvent evt) {
		   try {
	    	   return ((Integer)evt.getClass().getMethod("getPointerCount").invoke(evt)).intValue();
	       } catch (Exception ex) {
	    	   Log.e("Reflected getPointerCount", ex.toString());
	       }
	       
	       return 0;
	   }
	   
	   public static int getAction(MotionEvent evt) {
		   try {
	    	   return ((Integer)evt.getClass().getMethod("getAction").invoke(evt)).intValue();
	       } catch (Exception ex) {
	    	   Log.e("Reflected getAction", ex.toString());
	       }
	       
	       return 0;
	   }
	   
	   public static int getPointerId(MotionEvent evt, int index) {
		   try {
	    	   return ((Integer)evt.getClass().getMethod("getPointerId", cInt).invoke(evt, index)).intValue();
	       } catch (Exception ex) {
	    	   Log.e("Reflected getPointerId", ex.toString());
	       }
	       
	       return 0;
	   }
}