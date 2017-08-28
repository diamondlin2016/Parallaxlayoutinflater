package demo.diamond.com.parallaxlayoutinflater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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

public class PageFragment extends Fragment implements ParallaxViewImp{
    //此Fragment上所有的需要实现视差动画的视图
    private List<View> parallaxViews = new ArrayList<>();


    public static PageFragment newInstance(int resId) {
        Bundle bd = new Bundle();
        bd.putInt("layoutId",resId);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater original, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int layoutId = args.getInt("layoutId");
        //1.布局加载器将布局加载进来了
        //2.解析创建布局上所有的视图
        //3.自己搞定创建视图的过程
        //4.获取视图相关的自定义属性的值
        ParallaxLayoutInflater parallaxLayoutInflater = new ParallaxLayoutInflater(original, getActivity(), this);
        return parallaxLayoutInflater.inflate(layoutId, null);
    }


    public List<View> getParallaxViews() {
        return parallaxViews;
    }

}
