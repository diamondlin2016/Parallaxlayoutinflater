package demo.diamond.com.parallaxlayoutinflater;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Author:    Diamond_Lin
 * Version    V1.0
 * Date:      2017/8/28 下午3:09
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/28      Diamond_Lin            1.0                    1.0
 * Why & What is modified:
 */

public class ParallaxLayoutInflater extends LayoutInflater {

    private ParallaxViewImp mParallaxView;

    protected ParallaxLayoutInflater(LayoutInflater original, Context newContext, ParallaxViewImp fragment) {
        super(original, newContext);
        this.mParallaxView = fragment;
        //重新设置布局加载器的工厂
        //工厂：创建布局文件中所有的视图
        setFactory(new ParallaxFactory(this,mParallaxView));
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new ParallaxLayoutInflater(this, newContext, mParallaxView);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class ParallaxFactory implements LayoutInflater.Factory2 {

        private ParallaxViewImp mParallaxView;
        private LayoutInflater inflater;
        private final String[] sClassPrefix = {
                "android.widget.",
                "android.view."
        };

        public ParallaxFactory(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        public ParallaxFactory(LayoutInflater parallaxLayoutInflater, ParallaxViewImp parallaxView) {
            this.inflater = parallaxLayoutInflater;
            this.mParallaxView = parallaxView;
        }

        //自定义,视图创建的过程
        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View view = createViewOrFailQuietly(name, context, attrs);

            //实例化完成
            if (view != null) {
                //获取自定义属性，通过标签关联到视图上
                setViewTag(view, context, attrs);
                mParallaxView.getParallaxViews().add(view);
            }

            return view;
        }

        private void setViewTag(View view, Context context, AttributeSet attrs) {
            //所有自定义的属性
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimationView);
            if (a != null && a.length() > 0) {
                //获取自定义属性的值
                ParallaxViewTag tag = new ParallaxViewTag();
                tag.xIn = a.getFloat(R.styleable.AnimationView_x_in, 0f);
                tag.xOut = a.getFloat(R.styleable.AnimationView_x_out, 0f);
                tag.yIn = a.getFloat(R.styleable.AnimationView_y_in, 0f);
                tag.yOut = a.getFloat(R.styleable.AnimationView_y_in, 0f);

                //index
                view.setTag(view.getId(), tag);
                a.recycle();
            }

        }

        private View createViewOrFailQuietly(String name, String prefix, Context context,
                                             AttributeSet attrs) {
            try {
                //通过系统的inflater创建视图，读取系统的属性
                return inflater.createView(name, prefix, attrs);
            } catch (Exception e) {
                return null;
            }
        }

        private View createViewOrFailQuietly(String name, Context context,
                                             AttributeSet attrs) {
            //1.自定义控件标签名称带点，所以创建时不需要前缀
            if (name.contains(".")) {
                createViewOrFailQuietly(name, null, context, attrs);
            }
            //2.系统视图需要加上前缀
            for (String prefix : sClassPrefix) {
                View view = createViewOrFailQuietly(name, prefix, context, attrs);
                if (view != null) {
                    return view;
                }
            }
            return null;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return null;
        }
    }

}