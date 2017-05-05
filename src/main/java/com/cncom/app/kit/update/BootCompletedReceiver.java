package com.cncom.app.kit.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cncom.app.kit.FavorConfigBase;


public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		FavorConfigBase.getInstance().onBroadcastReceive("BootCompletedReceiver", context, intent);
	}

}
