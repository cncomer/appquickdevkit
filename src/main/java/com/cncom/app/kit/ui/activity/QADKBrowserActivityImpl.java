/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cncom.app.kit.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.library.appcompat.widget.AppCompatDialogUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

import java.lang.reflect.Field;


/**
 * An HTML-based help screen with Back and Done buttons at the bottom.
 * 
 * @author yeluosuifeng2005@gmail.com (Eric Chen)
 */
public class QADKBrowserActivityImpl extends QADKActionbarActivity {

	private static final String TAG = "BrowserActivity";
	protected WebView webView;
	protected String mUrl;
	private Toolbar mAppBar;

	private ImageView finishBtn;

	private boolean mIsDesktopSite = false;
	public static final String EXTRA_DESKTOP_SITE = "desktopsite";
	private final Button.OnClickListener backListener = new Button.OnClickListener() {
		public void onClick(View view) {
			webView.goBack();
		}
	};

	protected int getContentLayout() {
		return R.layout.activity_browser;
	}
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Log.w(TAG, "onCreate");
		if (isFinishing()) {
			return;
		}
		//This has to be called before setContentView and you must use the
        //class in com.actionbarsherlock.view and NOT android.view
        
		String title = getIntent().getStringExtra(Intents.EXTRA_NAME);
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		} else {
			setTitle(R.string.pull_to_refresh_load_label);
		}
		setContentView(getContentLayout());

		View appBarLayout = findViewById(R.id.app_bar_layout);

		if (appBarLayout != null) {
			appBarLayout.setVisibility(View.VISIBLE);
			mAppBar = (Toolbar) findViewById(R.id.app_bar);
			setSupportActionBar(mAppBar);
			mAppBar.setLogo(R.drawable.abc_ic_clear_mtrl_alpha);

			ImageView iconLogoBackground = (ImageView) findViewById(R.id.icon_logo_background);

			try {
				Field logoViewField = Toolbar.class.getDeclaredField("mLogoView");
				logoViewField.setAccessible(true);
				finishBtn = (ImageView) logoViewField.get(mAppBar);
				finishBtn.setId(finishBtn.toString().hashCode());
				finishBtn.setBackgroundDrawable(iconLogoBackground.getDrawable());
				finishBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
				finishBtn.setVisibility(View.GONE);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

//		mAppBar.setNavigationIcon(R.drawable.ic_ab_back_holo_light);
//		mAppBar.setTitleTextColor(Color.parseColor("#ffffffff"));
//		mAppBar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent upIntent = NavUtils.getParentActivityIntent(BrowserActivity.this);
//				if (upIntent == null) {
//					// If we has configurated parent Activity in bgAndroidManifest.xml, we just finish current Activity.
//					finish();
//					return;
//				}
//				if (NavUtils.shouldUpRecreateTask(BrowserActivity.this, upIntent)) {
//					// This activity is NOT part of this app's task, so create a new task
//					// when navigating up, with a synthesized back stack.
//					TaskStackBuilder.create(BrowserActivity.this)
//							// Add all of this activity's parents to the back stack
//							.addNextIntentWithParentStack(upIntent)
//									// Navigate up to the closest parent
//							.startActivities();
//				} else {
//					// This activity is part of this app's task, so simply
//					// navigate up to the logical parent activity.
//					NavUtils.navigateUpTo(BrowserActivity.this, upIntent);
//				}
//			}
//		});


//		mAppBar.inflateMenu(R.menu.app_bar_refresh);
//		mAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem menuItem) {
//				switch(menuItem.getItemId()) {
//					case R.id.menu_refresh:
//						webView.reload();
//						break;
//				}
//				return false;
//			}
//		});
		webView = (WebView) findViewById(R.id.webview);

		//清空缓存
//		webView.clearCache(true);
//		webView.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if (event.getAction() == KeyEvent.ACTION_DOWN) {
//					if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
//						webView.goBack();   //后退
//						return true;    //已处理
//					}
//				}
//				return false;
//			}
//		});
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(QADKApplication.getInstance().isInDebug()){
				webView.setWebContentsDebuggingEnabled(true);
			}
		}

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			//In some cases, you may need to display content that isn’t designed for mobile devices – for example,
//			// if you’re displaying content you don’t control. In this case, you need to force the WebView to use a desktop-size viewport:
//			if (mIsDesktopSite) {
//				webView.getSettings().setUseWideViewPort(true);
//				webView.getSettings().setLoadWithOverviewMode(true);
//
//				//If these methods are not set and no viewport is specified, the WebView will try and set the viewport width based on the content size.
//
//				//In addition to doing this, you may want to use the new layout algorithm TEXT_AUTOSIZING introduced in Android 4.4,
//				// which increases the font size to make it more readable on a mobile device
//				webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
//			}
//		}
//		webView.getSettings().setUseWideViewPort(true);
//		webView.getSettings().setLoadWithOverviewMode(true);
//		webView.setInitialScale(100);
		webView.setScrollbarFadingEnabled(true);  
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);  
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			webView.getSettings().setDisplayZoomControls(false);
		}

//		webView.getSettings().setAllowContentAccess(true);
//		webView.getSettings().setAllowFileAccess(true);
//		webView.getSettings().setAllowFileAccessFromFileURLs(true);
		// Other webview options
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小
//		String appCacheDir = this.getApplicationContext().getFilesDir().getAbsolutePath()+"/webviewAppCache";
		String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		webView.getSettings().setAppCachePath(appCacheDir);

		//设置可以使用localStorage
		webView.getSettings().setDomStorageEnabled(true);
		//应用可以有数据库
		webView.getSettings().setDatabaseEnabled(true);
		String dbPath = this.getApplicationContext().getDir( "database" , Context.MODE_PRIVATE).getPath();
		webView.getSettings().setDatabasePath(dbPath);
//		if (!ComConnectivityManager.getInstance().isConnected()) {
//			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		} else {
//			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//		}

		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//		webView.getSettings().setPluginState(WebSettings.PluginState.ON);
		webView.getSettings().setAllowFileAccess(true);
//		webView.getSettings().setAllowFileAccessFromFileURLs(true);
		webView.getSettings().setSupportZoom(true);

		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.setWebViewClient(new HelpClient());
		webView.setWebChromeClient(new WebChromeClient() {
			
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
//				BrowserActivity.this.setProgress(progress * 1000);
				//Normalize our progress along the progress bar's scale
				int myprogress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * progress;
				setAppBarProgress(progress);
				if (progress == 100) {
		                // onProgressChanged() may continue to be called after the main
		                // frame has finished loading, as any remaining sub frames
		                // continue to load. We'll only get called once though with
		                // newProgress as 100 when everything is loaded.
		                // (onPageFinished is called once when the main frame completes
		                // loading regardless of the state of any sub frames so calls
		                // to onProgressChanges may continue after onPageFinished has
		                // executed)

		                // sync cookies and cache promptly here.
		                CookieSyncManager.getInstance().sync();
				 }
			}
			
			@Override
		      public void onReceivedTitle(WebView view, String title){ 
		        super.onReceivedTitle(view, title); 
		        setTitle(title); 
		      }

//			public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
//				openFileChooser(uploadFile, acceptType, "");
//			}
//
//			//For Android 4.1
//			@JavascriptInterface
//			public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
//				mUploadHandler = UploadHandlerCompat.getUploadHandlerCompat(BrowserActivity.this);
//				mUploadHandler.openFileChooser(uploadFile, acceptType, capture);
//			}
//			// For Android < 3.0
//			@JavascriptInterface
//			public void openFileChooser( ValueCallback<Uri> uploadMsg) {
//				openFileChooser(uploadMsg, "");
//			}
//
//			// file chooser
//			public void showFileChooser(ValueCallback<Uri[]> callback, FileChooserParams params) {
//				mUploadHandler = UploadHandlerCompat.getUploadHandlerCompat(BrowserActivity.this);
//				mUploadHandler.openFileChooser(callback, params);
//			}
//			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
//											 FileChooserParams fileChooserParams) {
//				showFileChooser(filePathCallback, fileChooserParams);
//				return true;
//			}

			@Override
			@JavascriptInterface
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				AppCompatDialogUtils.createSimpleConfirmAlertDialog(view.getContext(), message);
				result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
				return true;
			}
			@Override
			@JavascriptInterface
			public boolean onJsConfirm(WebView view, String url, String message,
									   final JsResult result) {
				AppCompatDialogUtils.createSimpleConfirmAlertDialog(view.getContext(), message);
				return true;
			}
			@Deprecated
			@JavascriptInterface
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				super.onConsoleMessage(message, lineNumber, sourceID);
				DebugUtils.logD(TAG, "sourceID=" + sourceID + ", lineNumber=" + lineNumber + ", message=" + message);
			}


		});
		CookieManager.getInstance().removeSessionCookie();

//		webView.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if (event.getAction() == KeyEvent.ACTION_DOWN) {
//					if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//						webView.goBack();
//						return true;
//					}
//				}
//				return false;
//			}
//		});
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		loadUrl(mUrl);
	}

	protected void loadUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			if (url.startsWith("http://")
					|| url.startsWith("https://")
					|| url.startsWith("www.")) {
				webView.loadUrl(url);
			} else {
				webView.loadDataWithBaseURL("about:blank", url, "text/html", "utf-8",null);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		webView.saveState(state);
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (webView.canGoBack()) {
//				webView.goBack();
//				return true;
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	private final class HelpClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			overridePageFinished(view, url);
			if (webView.canGoBack()) {
				if (finishBtn != null) {
					finishBtn.setVisibility(View.VISIBLE);
				}

			} else {
				if (finishBtn != null) {
					finishBtn.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			 // reset sync timer to avoid sync starts during loading a page
            CookieSyncManager.getInstance().resetSync();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (overrideUrlLoading(view, url)) {
				return true;
			} else {
				//webView.loadUrl(url);
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			overrideReceivedError(view, errorCode, description, failingUrl);
		}

//		@Override
//		@JavascriptInterface
//		public void onFormResubmission(WebView view, Message dontResend, Message resend) {
//			resend.sendToTarget();
//		}
	}

	protected void overrideReceivedError(WebView view, int errorCode, String description, String failingUrl) {

	}

	/**
	 * 网页加载完成
     */
	public void overridePageFinished(WebView view, String url) {

	}

	protected boolean overrideUrlLoading(WebView view, String url) {
		DebugUtils.logD(TAG, "overridePageFinished " + url);
		boolean handled = false;
		if(!url.startsWith("http:")
				&& !url.startsWith("https:")) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			handled = true;
		} else if (url.endsWith(".apk")) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			handled = true;
		}
		return handled;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuItem menuItem = menu.add(1, R.id.button_back, 0, R.string.button_webview_goback);
//
//		MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//
//		menuItem = menu.add(1, R.id.menu_refresh, 1, R.string.menu_refresh);
//		MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.button_back);
		if (menuItem != null) {
			menuItem.setVisible(webView.canGoBack());
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();
		if (i == android.R.id.home) {
			if (webView.canGoBack()) {
				webView.goBack();
				return true;
			}

			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finish();
			}
			return true;
		} else if (i == R.id.button_back) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finish();
			}
			return true;
		} else if (i == R.id.menu_refresh) {//			if (!TextUtils.isEmpty(mUrl)) {
//				//WebView cookies清理
////					CookieSyncManager.createInstance(mContext);
////					CookieSyncManager.getInstance().startSync();
////					CookieManager.getInstance().removeSessionCookie();
//				webView.clearCache(true);
//				webView.loadUrl(mUrl);
//			}
			webView.clearCache(true);
			webView.reload();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected boolean checkIntent(Intent intent) {
		mUrl = intent.getStringExtra(Intents.EXTRA_URI);
		if (TextUtils.isEmpty(mUrl)) {
			mUrl = intent.getStringExtra(Intents.EXTRA_ADDRESS);
		}
		if (TextUtils.isEmpty(mUrl)) {
			Uri uri = intent.getData();
			if (uri != null && (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https"))) {
				mUrl = intent.getDataString();
			}
		}
		mIsDesktopSite = intent.getBooleanExtra(EXTRA_DESKTOP_SITE, false);
		return true;
	}
	
	public static void startActivity(Context context, String url, String title) {
		DebugUtils.logD(TAG, "startActivity url " + url);
		Intent intent = new Intent(context, QADKBrowserActivityImpl.class);
		intent.putExtra(Intents.EXTRA_URI, url);
		if (!TextUtils.isEmpty(title)) {
			intent.putExtra(Intents.EXTRA_NAME, title);
		}
		context.startActivity(intent);
	}

	public static Intent createIntent(Context context, String url, String title) {
		Intent intent = new Intent(context, QADKBrowserActivityImpl.class);
		intent.putExtra(Intents.EXTRA_URI, url);
		if (!TextUtils.isEmpty(title)) {
			intent.putExtra(Intents.EXTRA_NAME, title);
		}
		return intent;
	}
	public static void startActivity(Context context, String url, String title, boolean desktoSites) {
		DebugUtils.logD(TAG, "startActivity url " + url);
		Intent intent = new Intent(context, QADKBrowserActivityImpl.class);
		intent.putExtra(Intents.EXTRA_URI, url);
		if (!TextUtils.isEmpty(title)) {
			intent.putExtra(Intents.EXTRA_NAME, title);
		}
		intent.putExtra(EXTRA_DESKTOP_SITE, desktoSites);
		context.startActivity(intent);
	}
	private ProgressBar mProgressBar;
	private void setAppBarProgress(int progress) {
		DebugUtils.logD(TAG, "setAppBarProgress " + progress);
		if (progress >= 100) {
			if (mProgressBar != null) {
				DebugUtils.logD(TAG, "setAppBarProgress point 2");
				mProgressBar.setProgress(progress*100);
				mProgressBar.setVisibility(View.GONE);
			}

		} else {

			if (mProgressBar == null) {
				DebugUtils.logD(TAG, "setAppBarProgress point 1");
				mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
				mProgressBar.setMax(10000);
				mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.abs_progress_horizontal_holo_light));
			}

			int newProgress = progress*100;
			if (mProgressBar.getProgress() != newProgress) {
				DebugUtils.logD(TAG, "setAppBarProgress point 3");
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(progress*100);
			}

		}

	}

	@Override
	public void finish() {
		//我们对页面进行销毁的时候，其中webview持有的HTML页面还会继续存在，加入我们在HTML页面中做了一些监听手机晃动、声音…… 以及使用了js定时任务的情况下。
		//单纯的销毁我们的native页面并不能达到让页面中这些内容停止执行, 所以在小会native页面之前，将webview的页面设置问空页面即可.
		// 当我们对Activity进行finish的时候，webview持有的页面并不会立即释放，如果页面中有在执行js等其他操作，仅仅进行finish是完全不够的。
		webView.loadUrl("about:blank");
		super.finish();
	}

}
