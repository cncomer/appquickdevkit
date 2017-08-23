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

package com.cncom.app.kit.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.ui.activity.FragmentHostActivity;
import com.cncom.app.library.appcompat.widget.AppCompatDialogUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

import java.lang.reflect.Field;

import static com.cncom.app.kit.R.id.webview;


/**
 * An HTML-based help screen with Back and Done buttons at the bottom.
 * 
 * @author yeluosuifeng2005@gmail.com (Eric Chen)
 */
public class QADKBrowserFragment extends QADKFragment implements ActionMenuView.OnMenuItemClickListener{

	private static final String TAG = "QADKBrowserFragment";
	protected WebView webView;
	protected ActionMenuView actionMenuView;
	protected String mUrl;
	protected String mStartUrl;
	protected Toolbar mAppBar;

	protected ImageView finishBtn;

	private boolean mIsDesktopSite = false;
	public static final String EXTRA_DESKTOP_SITE = "desktopsite";

	private ProgressBar mProgressBar;

	protected Drawable homeAsUpIndicator;

	protected final Button.OnClickListener backListener = new Button.OnClickListener() {
		public void onClick(View view) {
			webView.goBack();
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		CookieManager.getInstance().removeSessionCookie();
		initArgument();
	}

	protected void initArgument() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			mUrl = bundle.getString(Intents.EXTRA_URI);
			mUrl = mUrl.trim();
			mIsDesktopSite = bundle.getBoolean(EXTRA_DESKTOP_SITE, false);
			mStartUrl = mUrl;
		}
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_browser, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initToolBar(view);
		webView = (WebView) view.findViewById(webview);
		initWebView(webView);
	}




	/**
	 * 初始化webview
	 * @param webView
	 */
	protected void initWebView(WebView webView) {


		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(QADKApplication.getInstance().isInDebug()){
				webView.setWebContentsDebuggingEnabled(true);
			}
		}


		webView.setScrollbarFadingEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			webView.getSettings().setDisplayZoomControls(false);
		}


		// Other webview options
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小
//		String appCacheDir = this.getApplicationContext().getFilesDir().getAbsolutePath()+"/webviewAppCache";
		String appCacheDir = this.getActivity().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		webView.getSettings().setAppCachePath(appCacheDir);

		//设置可以使用localStorage
		webView.getSettings().setDomStorageEnabled(true);
		//应用可以有数据库
		webView.getSettings().setDatabaseEnabled(true);
		String dbPath = this.getActivity().getApplicationContext().getDir( "database" , Context.MODE_PRIVATE).getPath();
		webView.getSettings().setDatabasePath(dbPath);
//		if (!ComConnectivityManager.getInstance().isConnected()) {
//			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		} else {
//			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//		}

		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//		webView.getSettings().setPluginState(WebSettings.PluginState.ON);
		webView.getSettings().setAllowFileAccess(true);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//			webView.getSettings().setAllowFileAccessFromFileURLs(true);
//			webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//		}

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
				overrideReceivedTitle(view, title);

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
	}


	protected void initToolBar(View view) {
		View appBarLayout = view.findViewById(R.id.app_bar_layout);

		actionMenuView = (ActionMenuView) appBarLayout.findViewById(R.id.action_menu_view);
		actionMenuView.setOnMenuItemClickListener(this);
		TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(new int[] {
				R.attr.homeAsUpIndicator,
		});
		final int resourceId = typedArray.getResourceId(0, 0);
		if (resourceId != 0) {
			homeAsUpIndicator = AppCompatResources.getDrawable(getActivity(), resourceId);
		}
//		homeAsUpIndicator = typedArray.getDrawable(0);

		if (appBarLayout != null) {
			mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
			appBarLayout.setVisibility(View.VISIBLE);
			mAppBar = (Toolbar) view.findViewById(R.id.app_bar);
			mAppBar.setLogo(R.drawable.abc_ic_clear_mtrl_alpha);
			mAppBar.setNavigationIcon(null);
			mAppBar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (webView.canGoBack()) {
						webView.goBack();
					} else {
						mAppBar.setNavigationIcon(null);
					}
				}
			});
			ImageView iconLogoBackground = (ImageView) view.findViewById(R.id.icon_logo_background);

			try {
				Field logoViewField = Toolbar.class.getDeclaredField("mLogoView");
				logoViewField.setAccessible(true);
				finishBtn = (ImageView) logoViewField.get(mAppBar);
				finishBtn.setId(R.id.button_finish);
				finishBtn.setBackgroundDrawable(iconLogoBackground.getDrawable());
				finishBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getActivity().finish();
					}
				});
				finishBtn.setVisibility(View.GONE);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

    protected void prepareData() {
        loadUrl(mUrl);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		prepareData();
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

	private final class HelpClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			overridePageFinished(view, url);
			if (webView.canGoBack()) {
				if (finishBtn != null) {
					finishBtn.setVisibility(View.VISIBLE);
				}

				if (homeAsUpIndicator != null) {
					mAppBar.setNavigationIcon(homeAsUpIndicator);
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


		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				WebResourceResponse webResourceResponse = QADKBrowserFragment.this.shouldInterceptRequest(view, url);
				if (webResourceResponse != null) {
					DebugUtils.logW(TAG, "shouldInterceptRequest local find " + url);
					return webResourceResponse;
				}
			}
			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

				WebResourceResponse webResourceResponse = QADKBrowserFragment.this.shouldInterceptRequest(view, request.getUrl().toString());
				if (webResourceResponse != null) {
					return webResourceResponse;
				}
			}
			return super.shouldInterceptRequest(view, request);
		}
	}

	protected void overrideReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		DebugUtils.logD(TAG, "overrideReceivedError errorCode=" + errorCode+",description=" + description+",failingUrl="+failingUrl);
	}


	/**
	 * 接收到网页的标题
	 * @param view
	 * @param title
	 */
	public void overrideReceivedTitle(WebView view, String title) {
		if (mAppBar != null) {
			mAppBar.setTitle(title);
		} else {
			getActivity().setTitle(title);
		}
	}


	/**
	 * 是否使用本地的资源
	 * @param view
	 * @param resouceUrl
	 * @return
	 */
	protected WebResourceResponse shouldInterceptRequest(WebView view, String resouceUrl) {
		DebugUtils.logD(TAG, "shouldInterceptRequest resouceUrl=" + resouceUrl);
		return null;
	}

	/**
	 * 网页加载完成
     */
	public void overridePageFinished(WebView view, String url) {
		DebugUtils.logD(TAG, "overridePageFinished " + url);
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


	protected void reload() {
		webView.clearCache(true);
		webView.reload();
	}


	public static void startFragment(Context context, String url, String title) {
		startFragment(context, url, title, false);
	}

	public static void startFragment(Context context, String url, String title, boolean desktoSites) {
		DebugUtils.logD(TAG, "startFragment url " + url);
		FragmentHostActivity.startActivity(context, buildArgument(url, title, desktoSites));
	}

	public static Bundle buildArgument(String url, String title, boolean desktoSites) {
		Bundle bundle = new Bundle();
		if (!TextUtils.isEmpty(title)) {
			bundle.putString(Intents.EXTRA_NAME, title);
		}
		bundle.putBoolean(EXTRA_DESKTOP_SITE, desktoSites);
		bundle.putString(Intents.EXTRA_URI, url);

		return bundle;
	}

	private void setAppBarProgress(int progress) {
		if (mProgressBar == null) {
			return;
		}
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
	public void onDestroy() {
		super.onDestroy();
		//我们对页面进行销毁的时候，其中webview持有的HTML页面还会继续存在，加入我们在HTML页面中做了一些监听手机晃动、声音…… 以及使用了js定时任务的情况下。
		//单纯的销毁我们的native页面并不能达到让页面中这些内容停止执行, 所以在小会native页面之前，将webview的页面设置问空页面即可.
		// 当我们对Activity进行finish的时候，webview持有的页面并不会立即释放，如果页面中有在执行js等其他操作，仅仅进行finish是完全不够的。
		webView.loadUrl("about:blank");
		webView.clearCache(true);
	}

}
