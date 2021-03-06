/*
 * Android Event Injector 
 *
 * Copyright (c) 2013 by Radu Motisan , radu.motisan@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * For more information on the GPL, please go to:
 * http://www.gnu.org/copyleft/gpl.html
 *
 */

package com.phoenix.eventinjector;

import android.util.Log;

import java.util.ArrayList;


public class Events
{
	
	private final static String TAG = "Events";
	
	public class InputDevice {
		
		private int mId;
		private String mPath, mName;
		private boolean mOpen;
		
		InputDevice(int id, String path) {
			mId = id; mPath = path;
		}
		
		public int InjectEvent() {
			return 0;
		}
		
		public int getPollingEvent() {
			return PollDev(mId);
		}

		public int getSuccessfulPollingType() {
			return getType();
		}
		public int getSuccessfulPollingCode() {
			return getCode();
		}
		public int getSuccessfulPollingValue() {
			return getValue();
		}
		
		public boolean getOpen() {
			return mOpen;
		}
		public int getId() {
			return mId;
		}
		public String getPath() {
			return mPath;
		}
		public String getName() {
			return mName;
		}
		
		public void Close() {
			mOpen = false;
			if(mOpen) {
				RemoveDev(mId);
			}
		}

		final int SYNC_REPORT = 0x00;

		final int EV_SYNC = 0x00,
				EV_KEY = 0x01,
				EV_REL = 0x02,
				EV_ABS = 0x03,
				REL_X = 0x00,
				REL_Y = 0x01,
				REL_Z = 0x02,
				BTN_TOUCH = 0x14a;// 330

		final int DOWN = 0x01,
				UP = 0x00;

		final int ABS_X = 0x35,
		          ABS_Y = 0x36;

		public int SendTouchDownAbs(int x, int y ) {
			intSendEvent(mId, EV_ABS, ABS_X, x); //set x coord
			intSendEvent(mId, EV_ABS, ABS_Y, y); //set y coord
			intSendEvent(mId, EV_KEY, BTN_TOUCH, DOWN); // touch down
			intSendEvent(mId, EV_SYNC, SYNC_REPORT, SYNC_REPORT);
			intSendEvent(mId, EV_ABS, ABS_X,x);
			intSendEvent(mId, EV_ABS, ABS_Y,y);
			intSendEvent(mId, EV_SYNC, SYNC_REPORT, SYNC_REPORT);
//			try {
//				Thread.sleep(3000);
//			} catch (Exception e){
//
//			}
			intSendEvent(mId, EV_KEY, BTN_TOUCH,UP); //touch up
			intSendEvent(mId, EV_SYNC, SYNC_REPORT, SYNC_REPORT);
			return 1;
		}

		public int SendMoveAbs(int sX, int sY, int eX, int eY) {
			int deltaX, deltaY, currX, currY;
			final int delta = 2;

			deltaX = (eX - sX) / 2;
			deltaY = (eY - sY) / 2;

			currX = sX; currY = sY;

			//touch down
			intSendEvent(mId, EV_ABS, ABS_X, sX); //set x coord
			intSendEvent(mId, EV_ABS, ABS_Y, sY); //set y coord
			intSendEvent(mId, EV_KEY, BTN_TOUCH, DOWN); // touch down
			intSendEvent(mId, EV_SYNC, SYNC_REPORT, SYNC_REPORT);

			int targetX,targetY;
			//start move
			do {
				intSendEvent(mId, EV_ABS, ABS_X, currX);
				intSendEvent(mId, EV_ABS, ABS_Y, currY);
				intSendEvent(mId, EV_SYNC, SYNC_REPORT, SYNC_REPORT);

				currX += deltaX;
				currY += deltaY;
				targetX = Math.abs(currX - eX);
				targetY = Math.abs(currY - eY);

				try {
					Thread.sleep(300);
				} catch (Exception e){

				}
			} while(targetX > 4 || targetY > 4);

			intSendEvent(mId, EV_KEY, BTN_TOUCH,UP); //touch up
			intSendEvent(mId, EV_SYNC, SYNC_REPORT, SYNC_REPORT);

			return 1;
		}

		public boolean Open(boolean forceOpen) {
			int res = OpenDev(mId);
	   		// if opening fails, we might not have the correct permissions, try changing 660 to 666
	   		if (res != 0) {
	   			// possible only if we have root
	   			if(forceOpen && Shell.isSuAvailable()) { 
	   				// set new permissions
	   				Shell.runCommand("chmod 666 "+ mPath);
	   				// reopen
	   			    res = OpenDev(mId);
	   			}
	   		}
	   		mName = getDevName(mId);
	   		mOpen = (res == 0);
	   		// debug
	   		Log.d(TAG,  "Open:"+ mPath +" Name:"+ mName +" Result:"+ mOpen);
	   		// done, return
	   		return mOpen;
	   	}
	}
	
	// top level structures
	public ArrayList<InputDevice> m_Devs = new ArrayList<InputDevice>(); 
	
	
	public int Init() {
		m_Devs.clear();
		int n = ScanFiles(); // return number of devs
	   	
		for (int i=0;i < n;i++) 
			m_Devs.add(new InputDevice(i, getDevPath(i)));
	   	return n;
	}
	
	public void Release() {
		for (InputDevice idev: m_Devs)
			idev.Close();
	}
	   	 
	// JNI native code interface
	public native static void intEnableDebug(int enable);

	private native static int ScanFiles(); // return number of devs
	private native static int OpenDev(int devid);
	private native static int RemoveDev(int devid);
	private native static String getDevPath(int devid);
	private native static String getDevName(int devid);
	private native static int PollDev(int devid);
	private native static int getType();
	private native static int getCode();
	private native static int getValue();
	// injector:
	private native static int intSendEvent(int devid, int type, int code, int value);
    
    static {
        System.loadLibrary("EventInjector");
    }

}


