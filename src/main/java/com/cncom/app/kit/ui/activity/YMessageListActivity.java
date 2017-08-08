package com.cncom.app.kit.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.database.AppDBHelper;
import com.cncom.app.kit.database.BjnoteContent;
import com.cncom.app.kit.utils.MyUmengNotificationClickHandler;
import com.shwy.bestjoy.utils.AsyncTaskCompat;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 消息通知  type说明
 0 是 通用消息，只显示并没有动作
 1 是订单状态消息， 增加orderno字段
 2 是 推广活动链接消息， 增加url字段  点击打开url网页
 3 是求助通知  增加conver_img 铭牌照片 component_img 零件照片， 点击消息可选择查看铭牌照片  零件照片 skuid 找到的零件id
 */
public class YMessageListActivity extends QADKActionbarActivity implements OnItemClickListener{
	private static final String TAG = "TAG";

	private YmessageCursorAdapter mYmessageCursorAdapter;
	private ListView mListView;
	private static final int[] GATEGORY_ICON = new int[]{
		R.drawable.ymeng_icon_1,
	};
	private static final int[] GATEGORY_TITLE = new int[]{
		R.string.title_ymeng_category0,
	};

	private int mGategoryId = -1;

	@Override
	protected boolean checkIntent(Intent intent) {
		mGategoryId = intent.getIntExtra(Intents.EXTRA_TYPE, -1);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		mListView = (ListView) findViewById(R.id.listview);
		View progressBar = findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		mListView.setOnItemClickListener(this);
		setTitle(R.string.menu_ymessage);
		QADKApplication.getInstance().setUnread(false);
		loadUmessagesAsync();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		AsyncTaskUtils.cancelTask(mLoadUmessageAsyncTask);
		mYmessageCursorAdapter.changeCursor(null);
		
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }
	
	private LoadUmessageAsyncTask mLoadUmessageAsyncTask;
	private void loadUmessagesAsync() {
		AsyncTaskUtils.cancelTask(mLoadUmessageAsyncTask);
		mLoadUmessageAsyncTask = new LoadUmessageAsyncTask();
		mLoadUmessageAsyncTask.execute();
	}

	class LoadUmessageAsyncTask extends AsyncTaskCompat<Void, Void, Cursor> {

		@Override
		protected Cursor doInBackground(Void... params) {
			DebugUtils.logD("LoadUmessageAsyncTask", "doInBackground");
			return mContext.getContentResolver().query(BjnoteContent.YMESSAGE.CONTENT_URI, BjnoteContent.YMESSAGE.PROJECTION, BjnoteContent.YMESSAGE.WHERE_YMESSAGE_IS_NITIFICATION, null, "" + AppDBHelper.YOUMENG_MESSAGE_SERVER_TIME + " desc");
			
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);

			mYmessageCursorAdapter = new YmessageCursorAdapter(YMessageListActivity.this, result, true);
			mListView.setAdapter(mYmessageCursorAdapter);
			findViewById(R.id.progressBar).setVisibility(View.GONE);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			findViewById(R.id.progressBar).setVisibility(View.GONE);
		}
		
	}
	
	private class YmessageCursorAdapter extends CursorAdapter {

		private int _curCategoryId = 0;
		private static final int VIEW_TYPE_CATEGORY = 0;
		private static final int VIEW_TYPE_SUB = 1;
		public YmessageCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}
		
		protected void onContentChanged() {
			super.onContentChanged();
	    }

		private void setCurrentCategoryId(int categoryId) {
			_curCategoryId = categoryId;
			DebugUtils.logD("YmessageCursorAdapter", "setCurrentCategoryId categoryId=" + _curCategoryId);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder = new ViewHolder();
			int viewType = getItemViewType(cursor.getPosition());
			if (viewType == VIEW_TYPE_CATEGORY) {
				view = LayoutInflater.from(context).inflate(R.layout.ymeng_gategory_list_item, parent, false);
				viewHolder._icon = (ImageView) view.findViewById(R.id.icon);
			} else if (viewType == VIEW_TYPE_SUB){
				view = LayoutInflater.from(context).inflate(R.layout.ymeng_list_item, parent, false);
			}
			
			
			viewHolder._title = (TextView) view.findViewById(R.id.title);
			viewHolder._text = (TextView) view.findViewById(R.id.content);
//			viewHolder._text.setAutoLinkMask(Linkify.ALL);
			viewHolder._date = (TextView) view.findViewById(R.id.time);
			view.setTag(viewHolder);
			return view;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public int getItemViewType(int position) {
//			if (_curCategoryId == -1) {
//				return VIEW_TYPE_CATEGORY;
//			} else {
//				return VIEW_TYPE_SUB;
//			}
			return VIEW_TYPE_CATEGORY;
		}


		public UMessage getUMessageFromCursor(Cursor c) {
			try {
				UMessage mesasge = new UMessage(new JSONObject(c.getString(BjnoteContent.YMESSAGE.INDEX_MESSAGE_RAW)));
				return mesasge;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			UMessage message = getUMessageFromCursor(cursor);
			if (message != null) {
				viewHolder._text.setText(message.text);
				viewHolder._title.setText(message.title);
			} else {
				viewHolder._title.setText(cursor.getString(BjnoteContent.YMESSAGE.INDEX_TITLE));
				viewHolder._text.setText(cursor.getString(BjnoteContent.YMESSAGE.INDEX_TEXT));
			}
			viewHolder._date.setText(DateUtils.TOPIC_SUBJECT_DATE_TIME_FORMAT.format(new Date(cursor.getLong(BjnoteContent.YMESSAGE.INDEX_DATE))));
			int viewType = getItemViewType(cursor.getPosition());

			viewHolder._categoryId = MyUmengNotificationClickHandler.TYPE_SYSTEM;
			if (message.extra != null) {
				String type = message.extra.get("type");
				if (!TextUtils.isEmpty(type)) {
					viewHolder._categoryId = Integer.valueOf(type);
				}
			}

			if (viewType == VIEW_TYPE_CATEGORY) {
				if (viewHolder._icon != null) {
					viewHolder._icon.setImageResource(GATEGORY_ICON[viewHolder._categoryId]);
					viewHolder._icon.setTag(viewHolder);
				}
			} else if (viewType == VIEW_TYPE_SUB){

			}
			
			viewHolder._UMessage = message;
		}
		
	}
	
	private class ViewHolder {
		private TextView _date, _title, _text;
		private ImageView _icon;
		private int _categoryId = -1;
		private UMessage _UMessage;
	}
	
	public static void startActivity(Context context, Bundle bundle) {
		Intent intent = new Intent(context, YMessageListActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (context instanceof Activity) {

		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}
	public static void startActivity(Context context, int category) {
		Intent intent = new Intent(context, YMessageListActivity.class);
		intent.putExtra(Intents.EXTRA_TYPE, category);
		if (context instanceof Activity) {

		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
//		if (mGategoryId == -1) {
//			setCategory(viewHolder._categoryId);
//		} else if (mGategoryId > -1) {
//			//内容项，我们需要特殊处理维修类型的通知，跳转到订单详细
//			if (viewHolder._categoryId == YouMengMessageHelper.TYPE_ASK_FOR_SKU) {
//				shwoSkuOperationDialog(viewHolder._UMessage);
//			}
//		}

		//内容项，我们需要特殊处理维修类型的通知，跳转到订单详细
//		if (viewHolder._categoryId == YouMengMessageHelper.TYPE_ASK_FOR_SKU) {
//			shwoSkuOperationDialog(viewHolder._UMessage);
//		} else {
//			YouMengMessageHelper.onUmessageClick(mContext, viewHolder._UMessage);
//		}
//		MyUmengNotificationClickHandler.onUmessageClick(mContext, viewHolder._UMessage);
	}
	
//	@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        // Respond to the action bar's Up/Home button
//        case android.R.id.home:
//     	   if (mGategoryId > -1) {
//			   setCategory(-1);
//     		   return true;
//     	   } else if (mReturnBackOnFinish){
//			   finish();
//			   return true;
//		   }
//     	   default:
//     		  return super.onOptionsItemSelected(item);
//        }
//
//    }

//	@Override
//	public void onBackPressed() {
//		if (mGategoryId > -1) {
//			setCategory(-1);
//		} else {
//			super.onBackPressed();
//		}
//	}


//	private void shwoSkuOperationDialog(final UMessage uMessage) {
//		if (uMessage.extra != null) {
//			final String skuId = uMessage.extra.get("skuid");
//			final String conver_img = uMessage.extra.get("conver_img");
//			final String component_img = uMessage.extra.get("component_img");
//			if (!TextUtils.isEmpty(skuId)
//					&& !TextUtils.isEmpty(conver_img)
//					&& !TextUtils.isEmpty(component_img)) {
////				new AlertDialog.Builder(mContext)
////						.setItems(R.array.ask_for_sku_with_skuid, new DialogInterface.OnClickListener() {
////							@Override
////							public void onClick(DialogInterface dialog, int which) {
////								if (which == 0) {
////									YouMengMessageHelper.onUmessageClick(mContext, uMessage);
////								} else if (which == 1) {
////									SupportUrlSingleTouchImageViewActivity.startActivity(mContext, conver_img);
////								} else if (which == 2) {
////									SupportUrlSingleTouchImageViewActivity.startActivity(mContext, component_img);
////								}
////							}
////						})
////						.setNegativeButton(android.R.string.cancel, null)
////						.show();
//				YouMengMessageHelper.onUmessageClick(mContext, uMessage);
//			} else {
//				AskJwbForSkuRecordListFragment.startFragment(mContext, null);
//			}
//		} else {
//			DebugUtils.logE(TAG, "shwoSkuOperationDialog extra is null");
//		}
//
//	}

}
