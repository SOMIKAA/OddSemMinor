/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.content.Context;
import android.os.PowerManager;

/**
 * PmWakeLock - static methods to keep track of wake lock.
 * 
 * @author Clyde Zuber
 * 
 */
public class PmWakeLock {

	private static PowerManager pm;
	private static PowerManager.WakeLock wl;

	@SuppressWarnings("deprecation")
	static void acquireWakeLock(Context context) {
		pm = (PowerManager)
				context.getSystemService(Context.POWER_SERVICE);	
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | 
				PowerManager.ACQUIRE_CAUSES_WAKEUP,	"PillMinder");
		wl.acquire();
    }

    static void releaseWakeLock() {
    	try {
    		wl.release();
        } catch (Throwable thrown) {
        }
    }
    
    static boolean isWakeLock() {
    	if (wl == null) {
    		return false;
    	} else {
    		return wl.isHeld();
    	}
    }
}
