package demo.diamond.com.parallaxlayoutinflater;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ImageView mIvWomen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
    }

    private void setUpView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIvWomen = (ImageView) findViewById(R.id.iv_woman);
//        mViewPager.setAdapter();
        ViewPagerHelper.newInstance().startListener(mViewPager,mIvWomen);
    }
}
