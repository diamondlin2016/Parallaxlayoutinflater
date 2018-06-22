# Parallaxlayoutinflater

![仿小红书.gif](https://github.com/diamondlin2016/Parallaxlayoutinflater/blob/master/blog/%E4%BB%BF%E5%B0%8F%E7%BA%A2%E4%B9%A6.gif)

博客链接：[搞事情，自定义 LayoutInflate 实现酷炫引导页](http://www.jianshu.com/p/b400b3547bee)



----
public class X5WebView extends WebView {

    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };
    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            return super.onJsAlert(webView, s, s1, jsResult);
        }
    };


    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        setWebViewClient(client);
        setWebChromeClient(webChromeClient);
        initWebViewSettings();
        this.getView().setClickable(true);

    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webSetting.setDomStorageEnabled(false);//毛病 不能为true
        webSetting.setDatabaseEnabled(true);
        webSetting.setAppCacheMaxSize(8 * 1024 * 1024);
        webSetting.setAppCachePath(getContext().getCacheDir().getAbsolutePath());

        webSetting.setGeolocationEnabled(true);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

    }

    public X5WebView(Context arg0) {
        super(arg0);
        setBackgroundColor(85621);
    }

}

----

# 二分查找
二分查找又称折半查找，它是一种高效的查找方法。
二分查找要求被查找的数据和索引都是有序的，注意是数据和索引。数组是索引有序的，有序数组才满足这个条件。map是索引无序的。

时间复杂度可以口算了，n个数字，每次减少一半，Log2n



很简单的一个算法了，直接贴代码吧。

```Java

public class BinarySearchTest {
    public static void main(String[] args) {
        int[] ints = {1,3,5,6,7,8,9,11,12,12,13,15,16,18,22,25,26,27,35,37,41,42,66};
        System.out.println(binarySearch(ints,0,ints.length-1,7));
    }

    public static int binarySearch(int[] arrays, int start, int end, int des) {
        if (start > end)
            return -1;
        int mid = (start + end) >> 1;
        if (arrays[mid] == des) {
            return mid;
        } else if (arrays[mid] > mid) {
            return binarySearch(arrays, start, mid, des);
        } else {
            return binarySearch(arrays, mid, end, des);
        }

    }
}


```


# 应用场景
二分查找是一种比较基础的算法，硬要扯应用场景的话，就好比今天刚刚学习了英语音标，然后你问我怎么做一次英语演讲。同理，二分查找只是写好一个优秀程序的必备基础，带着功利性学算法很容易坚持不下去的。加油学~~

我理解的二叉查找树，就是二分查找的思想。至于其他，自己思考。



----

# 分块查找

在学习分块查找之前，我们先了解两个概念

- 顺序查找：从头到尾撸一遍，毫无效率可言
- 二分查找：刚刚我们一起学过

### 分块查找是什么

分块查找就是把 m 个有序的数据平分成 n 块，且没块保存块内最大的元素,分块满足第x块的任意数据大于x-1块。事实上就是按照顺序分块咯。
然后查找的时候，从第一个块开始比较，如果要查找的元素小于当前块最大的元素，则这个元素要么在这个块内，要么没有。

说的有点复杂，我来举个实例。

现在有一组数据a{1,2,3,4,5,6,7,8，9}先分块，变成a{{1,2,3}，{4,5,6}，{7,8,9}}，加入现在要查找元素7，则先比较第1块最大的元素3，然后再比较第2块最大的元素6，然后比较第3块最大的元素9,9>7满足条件，在第3块内查找7.

好了，分块查找讲完了，我要开喷了。。。。

这特么就是一个傻逼算法，同样对源数据要有有序，查找速度比二分慢，且还有额外的开销。基本没有什么优点。

### 分块查找的应用场景

喷完了再来聊个有意义的话题，分块查找这个概念既然被提出，就肯定是有应用场景的。

我们之前聊的二分法查找的过程中，是不支持并发的。

但是分块查找可以，上面好像漏掉了一个点没讲，因为没带入应用场景，讲了反而更显分块查找傻逼。

- 分块查找的块内元素是不要求有序的，但是必须满足第 x 块内的所有任意必须大于x-1块内的所有元素。

好了继续说并发，在高并发的场景中，分块查找是怎样支持并发的。

1.首先是分块了，不在同一快内的元素操作互不干扰。
2.如果在同一个块内，如果块内不支持并发那就同步呗。

感觉这个思想和ConcurrentHashMap有点像，感兴趣的小伙伴可以去学习一下ConCurrenthashMap.

就酱紫吧，就不写代码实现了。关键是思想。



