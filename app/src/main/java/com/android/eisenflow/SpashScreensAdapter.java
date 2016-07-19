package com.android.eisenflow;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sve on 7/19/16.
 */
public class SpashScreensAdapter extends PagerAdapter {
    private Context context;

    public SpashScreensAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PagerEnum pagerEnum = PagerEnum.values()[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(pagerEnum.getLayoutId(), container, false);
        container.addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return PagerEnum.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }




    public enum PagerEnum {
        ONE(R.layout.splashcreen_one),
        TWO(R.layout.splashscreen_two),
        THREE(R.layout.splashscreen_three);
        private int layoutId;

        PagerEnum(int layoutId) {
            this.layoutId = layoutId;
        }

        public int getLayoutId() {
            return layoutId;
        }
    }
}
