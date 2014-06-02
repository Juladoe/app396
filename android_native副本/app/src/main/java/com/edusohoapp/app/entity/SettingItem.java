package com.edusohoapp.app.entity;

public class SettingItem {

	public int logo;
	public String title;
	public int direction;
	public int type;
	public ItemCheckListener listener;
	
	public void checkBtn()
	{
		if (listener != null) {
			listener.check();
		}
	}
	
	public static abstract class ItemCheckListener
	{
		public abstract void check();
	}
}
