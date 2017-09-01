package com.cncom.app.kit.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.baidu.panosdk.plugin.indoor.util.ScreenUtils;
import com.cncom.app.kit.R;

import java.util.List;

/**
 * Created by bestjoy on 2017/8/16.
 */

public abstract class QADKMenuPopWindow extends PopupWindow {

    protected LayoutInflater layoutInflater;
    protected GridView contentGridView;
    protected Context context;
    private int numColumns = 3;

    private AdapterView.OnItemClickListener onItemClickListener;

    public QADKMenuPopWindow(Context context) {
        super(context);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.customer_pop_menu_gridview, null);
        setContentView(view);
        contentGridView = (GridView) view.findViewById(R.id.gridview);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        contentGridView.setBackgroundDrawable(background);
    }

    public void setMenus(List<AppMenuItem> menus) {
        MenuAdapter menuAdapter = new MenuAdapter(menus);
        contentGridView.setAdapter(menuAdapter);
        contentGridView.setOnItemClickListener(menuAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        contentGridView.setNumColumns(numColumns);
    }

    public int getNumColumns() {
        return this.numColumns;
    }

//    public void setGridViewBackgroundDrawable(Drawable drawable) {
//        setBackgroundDrawable(drawable);
//    }

    @Override
    public void showAtLocation(View anchor, int gravity, int xoff, int yoff) {
        if (contentGridView.getAdapter() != null && contentGridView.getAdapter().getCount() > 0) {
            super.showAtLocation(anchor, gravity, xoff, yoff);
        }
    }

    public void showAtRightLocation(View anchor) {

    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = ScreenUtils.getSreenHeight(anchorView.getContext());
        final int screenWidth = ScreenUtils.getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }


    private class MenuAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
        private List<AppMenuItem> menus;
        public MenuAdapter(List<AppMenuItem> menus) {
            this.menus = menus;
        }

        @Override
        public int getCount() {
            return menus.size();
        }

        @Override
        public AppMenuItem getItem(int position) {
            return menus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getItemId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = QADKMenuPopWindow.this.getView(position, parent);
            }
            bindView(getItem(position), convertView);

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(parent, view, position, id);
            }
            dismiss();
        }
    }

    protected abstract View getView(int position, ViewGroup parent);
    protected abstract void bindView(AppMenuItem appMenuItem, View view);

}
