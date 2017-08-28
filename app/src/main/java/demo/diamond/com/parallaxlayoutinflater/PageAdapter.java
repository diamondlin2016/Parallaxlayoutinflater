package demo.diamond.com.parallaxlayoutinflater;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Author:    Diamond_Lin
 * Version    V1.0
 * Date:      2017/8/25 下午6:20
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/25      Diamond_Lin            1.0                    1.0
 * Why & What is modified:
 */

public class PageAdapter extends FragmentPagerAdapter {

    private List<PageFragment> fragments;

    public PageAdapter(FragmentManager fm,List<PageFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
