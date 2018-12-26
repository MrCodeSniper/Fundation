package com.hrz.common.aop;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import com.hrz.common.util.HrzUtil;


public class ViewClickInject extends View.AccessibilityDelegate {


    public static final int FRAGMENT_TAG_KEY= 0xFFFFFF;
    private Activity mActivity;

    private Activity activity;

    /**
     * 设置Activity页面中View的事件监听
     * @param activity
     */
    public void setActivityTracker(Activity activity) {
        this.mActivity=activity;
        View contentView = activity.findViewById(android.R.id.content);
        if (contentView != null) {
            setViewClickedTracker(contentView, null);
        }
    }


    private void setViewClickedTracker(View view, Fragment fragment) {
        if (needTracker(view)) {
            if (fragment != null) {
                view.setTag(FRAGMENT_TAG_KEY, fragment);
            }
            view.setAccessibilityDelegate(this);
        }
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                setViewClickedTracker(((ViewGroup) view).getChildAt(i), fragment);
            }
        }
    }

    @Override
    public void sendAccessibilityEvent(View host, int eventType) {
        super.sendAccessibilityEvent(host, eventType);
        if (AccessibilityEvent.TYPE_VIEW_CLICKED == eventType && host != null) {
            HrzUtil.LogDHrz("点击事件的ID:"+mActivity.getPackageName()
                    +"&&"+mActivity.getLocalClassName()
                    +"&&"+host.getId()
                    +"&&"+host.getClass().getCanonicalName());
        }
    }

    private boolean needTracker(View view){
        return true;
    }











}
