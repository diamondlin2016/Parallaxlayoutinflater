今天，我们来搞点事情，自定义一个 LayoutInflate，搞点有意思的东西，实现一个酷炫的动画。
首先，在自定义 LayoutInflate 之前，我们要先分析一下 LayoutInflate 的源码，了解了源码的实现方式，才能定制嘛~~~~
好了，怕你们无聊跑了，先放效果图出来镇贴


/Users/apple/Desktop/仿小红书.gif

好了，效果看完了，

那就先从LayoutInflate的源码开始吧。

##LayoutInflate
先看看官方文档吧~我英语不好，就不帮大家一句一句翻译了，反正大家也都知道这个类是干嘛的。

/Users/apple/Desktop/LayoutInflate.png

还是提取一下关键信息吧。
1.LayoutInflate 可以将 xml 文件解析成 View 对象。获取方式有两种getLayoutInflater()和getSystemService(Class)。

2.如果要创建一个新的 LayoutInflate去解析你自己的 xml，可以使用 cloneInContext，然后调用 setFactor()。

好了，我们先来回顾一下平时我们是怎么把 xml 转换成 View 的吧。

* setContentView（）

我们给 Activity 设置 布局 xml 都是调用这个方法，现在我们就来看看这个方法到底干了什么事。
	
	public void setContentView(@LayoutRes int layoutResID) {
      getWindow().setContentView(layoutResID);
      initWindowDecorActionBar();
    }
    -----以上是 Activity 的方法，调用了 Window 的 steContentView
    ----手机上的 window 都是 PhoneWindow,就不饶弯了，直接看 PhoneWindow
    ----的setContentView方法。
    public void setContentView(int layoutResID) {
        // Note: FEATURE_CONTENT_TRANSITIONS may be set in the process of installing the window
        // decor, when theme attributes and the like are crystalized. Do not check the feature
        // before this happens.
        if (mContentParent == null) {
            installDecor();
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();
        }

        if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                    getContext());
            transitionTo(newScene);
        } else {
            mLayoutInflater.inflate(layoutResID, mContentParent);
        }
        mContentParent.requestApplyInsets();
        final Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
    }
    ----在构造方法里面找到了mLayoutInflater 的赋值
    public PhoneWindow(Context context) {
        super(context);
        mLayoutInflater = LayoutInflater.from(context);
    }
    
* View.inflate()
同样是调用了LayoutInflate.inflate()方法
		
		public static View inflate(Context context, @LayoutRes int resource, ViewGroup root) {
        	LayoutInflater factory = LayoutInflater.from(context);
        	return factory.inflate(resource, root);
    	}
* LayoutInflate.from(context).inflate()
同上

我们项目中所有的 Xml 转 View 都离不开这三个方法吧，这三个方法最终调用的都还是 LayoutInflate 的 inflate 方法。

我们再来看看怎么获取到 LayoutInflate 的实例。
上面三个xml 解析成 view 的方法都是用LayoutInflate.from(context)来获取 LayoutInflate 实例的。

	public static LayoutInflater from(Context context) {
        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }
看到这个代码有木有觉得很眼熟啊，我们的 ActivityService、WindowService、NotificationService等等各种 Service 是不是都这样获取的。而我们都知道这些系统服务都是单例的，并且在应用启动的时候系统为其初始化的。好了，撤远了~~

回过头来，我们继续看 LayoutInflate 源码。

* inflate(@LayoutRes int resource, @Nullable ViewGroup root)
这个方法就是将xml 文件转换成 View 的方法，我们项目中所有的 xml 解析调用的都是这个方法。第一个参数是 xml 资源 id，第二个方法是解析后的 View 是否要添加到 root view里面去。

通过 Resources 获取 xml 解析器XmlResourceParser。

	public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot) {
        final Resources res = getContext().getResources();
        if (DEBUG) {
            Log.d(TAG, "INFLATING from resource: \"" + res.getResourceName(resource) + "\" ("
                    + Integer.toHexString(resource) + ")");
        }

        final XmlResourceParser parser = res.getLayout(resource);
        try {
            return inflate(parser, root, attachToRoot);
        } finally {
            parser.close();
        }
    }

XmlResourceParser解析 xml，并且返回 view

	public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        synchronized (mConstructorArgs) {
        		//写入跟踪信息，用于 Debug 相关，先不关心这个
            Trace.traceBegin(Trace.TRACE_TAG_VIEW, "inflate");

            final Context inflaterContext = mContext;
            //用于读取 xml 节点
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            Context lastContext = (Context) mConstructorArgs[0];
            mConstructorArgs[0] = inflaterContext;
            View result = root;
				
            try {
                // Look for the root node.
                int type;
                //空信息直接跳过
                while ((type = parser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    // Empty
                }
                //防错判断
                if (type != XmlPullParser.START_TAG) {
                    throw new InflateException(parser.getPositionDescription()
                            + ": No start tag found!");
                }
                //获取类名，比如说 TextView
                final String name = parser.getName();
                
                if (DEBUG) {
                    System.out.println("**************************");
                    System.out.println("Creating root view: "
                            + name);
                    System.out.println("**************************");
                }
                //如果标签是merge
                if (TAG_MERGE.equals(name)) {
                    if (root == null || !attachToRoot) {
                    //merge作为顶级节点的时候必须添加的 rootview
                        throw new InflateException("<merge /> can be used only with a valid "
                                + "ViewGroup root and attachToRoot=true");
                    }
							//递归方法去掉不必要的节点，为什么 merge 可以优化布局
                    rInflate(parser, root, inflaterContext, attrs, false);
                } else {
                    // Temp 是根节点
                    final View temp = createViewFromTag(root, name, inflaterContext, attrs);

                    ViewGroup.LayoutParams params = null;
							//如果不添加到 rootView 切 rootView 不等于空，则生成 LayoutParams
                    if (root != null) {
                        if (DEBUG) {
                            System.out.println("Creating params from root: " +
                                    root);
                        }
                        // Create layout params that match root, if supplied
                        params = root.generateLayoutParams(attrs);
                        if (!attachToRoot) {
                            // Set the layout params for temp if we are not
                            // attaching. (If we are, we use addView, below)
                            temp.setLayoutParams(params);
                        }
                    }

                    if (DEBUG) {
                        System.out.println("-----> start inflating children");
                    }

                    // 解析子节点
                    rInflateChildren(parser, temp, attrs, true);

                    if (DEBUG) {
                        System.out.println("-----> done inflating children");
                    }

                    // 如果要添加到 rootview。。
                    // to root. Do that now.
                    if (root != null && attachToRoot) {
                        root.addView(temp, params);
                    }

                    // Decide whether to return the root that was passed in or the
                    // top view found in xml.
                    if (root == null || !attachToRoot) {
                        result = temp;
                    }
                }

            } catch (XmlPullParserException e) {
                InflateException ex = new InflateException(e.getMessage());
                ex.initCause(e);
                throw ex;
            } catch (Exception e) {
                InflateException ex = new InflateException(
                        parser.getPositionDescription()
                                + ": " + e.getMessage());
                ex.initCause(e);
                throw ex;
            } finally {
                // Don't retain static reference on context.
                mConstructorArgs[0] = lastContext;
                mConstructorArgs[1] = null;
            }

            Trace.traceEnd(Trace.TRACE_TAG_VIEW);
            //返回解析结果
            return result;
        }
      }
      
在这个方法中，判断了是否使用 merge 优化布局，然后通过createViewFromTag解析的顶级 xml 节点的 view，并且处理了是否添加解析的布局到 rootView。调用rInflateChildren方法去解析子 View 并且添加到顶级节点 temp 里面。最后返回解析结果。

我们先来看看 createViewFromTag

	View createViewFromTag(View parent, String name, Context context, AttributeSet attrs,
            boolean ignoreThemeAttr) {
        //获取命名空间
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        // 给 view 设置主题。现在知道为什么colorPrimary等 theme 属性会影响控件颜色了吧
        if (!ignoreThemeAttr) {
            final TypedArray ta = context.obtainStyledAttributes(attrs, ATTRS_THEME);
            final int themeResId = ta.getResourceId(0, 0);
            if (themeResId != 0) {
                context = new ContextThemeWrapper(context, themeResId);
            }
            ta.recycle();
        }
			//让 view 闪烁，可以参考http://blog.csdn.net/qq_22644219/article/details/69367150
        if (name.equals(TAG_1995)) {
            // Let's party like it's 1995!
            return new BlinkLayout(context, attrs);
        }

        try {
            View view;
            优先调用了mFactory2的 oncreateView 方法，创建了 temp View
            if (mFactory2 != null) {
                view = mFactory2.onCreateView(parent, name, context, attrs);
            } else if (mFactory != null) {
                view = mFactory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }

            if (view == null && mPrivateFactory != null) {
                view = mPrivateFactory.onCreateView(parent, name, context, attrs);
            }

            if (view == null) {
                final Object lastContext = mConstructorArgs[0];
                mConstructorArgs[0] = context;
                try {
                    if (-1 == name.indexOf('.')) {
                        view = onCreateView(parent, name, attrs);
                    } else {
                        view = createView(name, null, attrs);
                    }
                } finally {
                    mConstructorArgs[0] = lastContext;
                }
            }

            return view;
        } catch (InflateException e) {
            throw e;

        } catch (ClassNotFoundException e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;

        } catch (Exception e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;
        }
    }
这里我们可以知道，mFactor 是真正用来创建 View 的，它传入了 view 的 parent、name、context、 attrs。

接下来继续去看子 View 的解析rInflateChildren

	void rInflate(XmlPullParser parser, View parent, Context context,
            AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
			//获取布局层级
        final int depth = parser.getDepth();
        int type;
        //没看懂没事，我们不是来纠结 xml 解析的
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();
            //requestFocus标签，http://blog.csdn.net/ouyang_peng/article/details/46957281
            if (TAG_REQUEST_FOCUS.equals(name)) {
                parseRequestFocus(parser, parent);
            } else if (TAG_TAG.equals(name)) {
            //tag标签，只能用于 api21以上，给父view 设置一个 tag
                parseViewTag(parser, parent, attrs);
            } else if (TAG_INCLUDE.equals(name)) {
            //include 节点
                if (parser.getDepth() == 0) {
                    throw new InflateException("<include /> cannot be the root element");
                }
                parseInclude(parser, context, parent, attrs);
            } else if (TAG_MERGE.equals(name)) {
            //merge 节点
                throw new InflateException("<merge /> must be the root element");
            } else {
            	  //走了刚刚的那个方法，创建 view 设置 LayoutParams
                final View view = createViewFromTag(parent, name, context, attrs);
                final ViewGroup viewGroup = (ViewGroup) parent;
                final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                rInflateChildren(parser, view, attrs, true);
                //添加到付 view
                viewGroup.addView(view, params);
            }
        }

        if (finishInflate) {
            parent.onFinishInflate();
        }
    }

 我们来整理一下思路吧，调用步骤
 1.LayoutInflater 的静态方法 form 获取LayoutInflater实力
 2.inflate解析 xml 资源
 3.inflate 调用createViewFromTag创建了顶级view
 4.inflate 调用rInflateChildren 创建所有子 view
 5.rInflateChildren递归调用rInflate创建所有子 view。
 6.rInflate通过调用createViewFromTag真正创建一个 view。
 7.createViewFromTag通过 mFactory2、mFactory、mPrivateFactory通过parent,name,context,attrs等参数运用反射构造函数，创建出 View，

因此，我们所有的 View 的构造方法都是被 LayoutInflate 的Factory调用创建出来的。
如果要自定义 LayoutInflate 解析，只需要给调用LayoutInflate的 setFactory设置我们自定义的 Factory 即可。
但是问题来了，LayoutInflate是系统服务，而且是单例，我们直接调用LayoutInflate的 setFactory 方法，会影响后期所有 view 的创建。

所以我们需要用到LayoutInflate的cloneInContext方法clone一个新的 LayoutInflate，然后再设置自己的 Factory。至于LayoutInflate是一个抽象类，cloneInContext是一个抽象方法，我们根本不用关心，因为我们直接用系统创建好的LayoutInflate即可。 然后系统的LayoutInflate实现类我没有找到，反正在源码里面有，我不关心，感兴趣的小伙伴可以去找找看。

好了，LayoutInflate的源码分析完了，接下来我们来分析动画了。

##动画分析

源码看了很久，我们再来重新看一遍动画吧


/Users/apple/Desktop/仿小红书.gif

动画效果：
1.翻页
2.翻页的时候天上的云，地上的建筑物移动速度和翻页速度不一样
3.不同的背景物移动速度不一样，最后一页背景物上下扩散
4.翻页的过程中，人一直在走路
5.最后一页人要消失。

解决方案：

1.ViewPager
2.给 viewPage设置PageChangeListener，在滚动的时候给各种 背景物体设置setTranslation。
3.不同的背景物设置不同的setTranslation系数。
4.人物走路用帧动画即可，在viewPage滑动处于SCROLL_STATE_DRAGGING状态的时候开启帧动画。
5.这个简单，监听onPageSelected，然后再设置人为 View.GONE即可。


解决方案的问题：
粗略数了一下，6个页面大概有50个左右的背景物。如果要一个一个去获取 id，然后再根据不同的 id，设置不同的滑动速度滑动方向，可能你会疯掉。

因此，我们需要想一个办法，去解决这个问题。可能有的童鞋会说，我写一个自定义 View，设置滑动速度系数属性就行了呀。这个方法可以实现，but，你还是需要一个一个去 findViewbyid。

好了，我就不绕弯子了，直接来自定义LayoutInflater吧。





----

本次效果来源于动脑学院视频课程，很不错的一套课程，感兴趣的小伙伴可以去学学，适用于有一定基础的 android 程序员，进阶高级很有效果哦。[腾讯课堂](https://ke.qq.com/course/130901)有免费的公开课，或者去[动脑学院](https://www.dongnaoedu.com/vip-android.html)学习，官网的课程好像是收费的，当然费用对程序员来说不算高，付不起课程费用的大学生可以去腾讯课堂学习，或者某宝。。。。。。。。

