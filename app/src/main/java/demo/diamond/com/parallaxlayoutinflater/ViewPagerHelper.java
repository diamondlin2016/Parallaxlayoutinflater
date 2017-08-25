package demo.diamond.com.parallaxlayoutinflater;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;

/**
 * Author:    Diamond_Lin
 * Version    V1.0
 * Date:      2017/8/25 下午6:12
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/25      Diamond_Lin            1.0                    1.0
 * Why & What is modified:
 */

public class ViewPagerHelper implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private ImageView ivWomen;


    private ViewPagerHelper(){}

    public static ViewPagerHelper newInstance() {
        return new ViewPagerHelper();
    }

    public void startListener(ViewPager viewPager, ImageView ivWomen) {
        this.viewPager = viewPager;
        this.ivWomen = ivWomen;
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
