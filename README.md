# RangeBar #
一个与美团酒店模块价格选择器长得极其类似的控件。

动图：
![动图](https://github.com/shunfayang/RangeBar/blob/master/img/rangeBar.gif)

## 前言 ##
项目出了新需求，有个范围选择的框框。我一看，诶这不是某团里的？不由得感叹产品和设计的活儿真容易。我秉着不重复造轮子的原则，打开了全球最大同性交友网站查找可用的 lib。果不其然，有不少。择其最接近需求效果，并比较小的项目，便开始改造轮子。但是啊，拿着轮子缝缝补补真的很糟心啊。勉强改得接近样子后，项目便上线了。

总是拿着别人的东西来用，自己的能力几乎得不到提升——这是我最在意的一点。公司项目几乎没有绚丽的效果需要实现，加上自己又懒，很多理论知识迫切需要实践。

`我发誓，我一定会回来寄几造轮子的！`

## 构思 ##
动手之前，我先自己构思了下该如何实现，画了一张图：
![构思图](https://github.com/shunfayang/RangeBar/blob/master/img/RangeBar.png)

1. 固定背景不变，与手势无关；
2. 左边滑块，决定左边选择；
3. 右边滑块，决定右边选中；
4. 选中进度条，左滑块右边开始，右滑块左边开始（为了减少无意义绘制，不使用两个滑块的中点）
5. 控件顶部还需要刻度和文字；

## 动手 ##
#### 控件所处的位置 ####
以控件距离 top 的三分之二 height 的位置绘制进度条，上方留三分之一的位置用来绘制文字很竖线。在 onSizeChanged() 中初始化所有的位置信息，滑动位置均以 heightCenter 为准：

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // init center point
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int width = measuredWidth - getPaddingLeft() - getPaddingRight();
        heightCenter = measuredHeight * 2 / 3;// 距离 top 三分之二高度

        // 计算固定背景和进度条的 top 和 bottom
        int rangeHeight = (int) (mRangeHeight / 2);
        mSolidTop = heightCenter - rangeHeight;
        mSolidBottom = heightCenter + rangeHeight;
        mSolidLeft = getPaddingLeft() + leftAndRight;
        mSolidRight = measuredWidth - getPaddingRight() - leftAndRight;
        
        // 两个滑块的位置  左滑块
        mLeftSlider.center = mSolidLeft;
        mLeftSlider.left = mLeftSlider.center - leftAndRight;
        mLeftSlider.right = mLeftSlider.center + leftAndRight;
        mLeftSlider.top = heightCenter - topAndBottom;
        mLeftSlider.bottom = heightCenter + topAndBottom;
        // 右滑块
        mRightSlider.center = mSolidRight;
        mRightSlider.left = mRightSlider.center - leftAndRight;
        mRightSlider.right = mRightSlider.center + leftAndRight;
        mRightSlider.top = heightCenter - topAndBottom;
        mRightSlider.bottom = heightCenter + topAndBottom;
        // 初始化分段
        mRange = (width - mBitmapWidth ) / (mTextArray.length - 1);
        this.mRangeArray = new int[mTextArray.length];
        for (int i = 0;i< mTextArray.length ;i ++){
            this.mRangeArray[i] = mSolidLeft + mRange * i;
        }

    }

#### 封装滑块——Slider

由于两个滑块需要根据手指触摸不断更新，便将滑块封装成 Slider 对象，将一些位置信息保存起来，供 onDraw 时使用：

    /** 滑块 */
    private class Slider {
        boolean isTouching;
        // 当前选中的位置
        int position;// 这个点是根据 字符串数组切割控件长度所得，即后面说到的自动回弹时返回的选中的点。
        float top;
        float right;
        float bottom;
        float left;
        float center;// 中心点
    }
    
#### 处理触摸事件
分别针对手指的`MotionEvent.ACTION_DOWN`  `MotionEvent.ACTION_MOVE` 以及`MotionEvent.ACTION_UP 和 MotionEvent.ACTION_CANCEL`触摸事件进行相应处理：
当手指`MotionEvent.ACTION_DOWN`，需要确定手指点击的位置是否在两个滑块中的其中一个。而滑块实际上是通过 Canvas.drawRect() 函数绘制的矩形，于是可以将手指触摸的坐标与左右滑块的 top、bottom、left 和 right 进行判断，如果触摸，将 Slider.isTouching 设置为false：
    
    /** 判断是否触摸某个 Slider **/
    private boolean isTouchSlider(Slider slider, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return x >= slider.left && x <= slider.right
                && y >= slider.top && y <= slider.bottom;
    }

当手指`MotionEvent.ACTION_DOWN 和 MotionEvent.ACTION_MOVE`，需要更新选中滑块的坐标。这里有四个判断条件：
1. x 轴坐标大于 mSolidLeft 并小于 mSolidRight ;
2. 如果是左滑块，则必须在右滑块的右边；
3. 如果是右滑块，则必须在左滑块的右边；
4. 两个滑块间隔一个单位距离（mRange);

详见 `updateTouch(Slider slider, MotionEvent event)`函数。

当手指松开，即 `MotionEvent.ACTION_UP 和 MotionEvent.ACTION_CANCEL`，需要对滑块位置进行自动滚动到特定的刻度。这个刻度也在 `onSizeChanged()`函数中通过字符串数组 `mTextArray  `进行计算得出区间刻度 mRange 。本来想通过二分法查找到此时的 x 所在区间，然而通过 mRange 计算，可以更快得出 x 接近的刻度：
通过计算除数，得到 position 大于 除数，再有大于余数的二分之一， position+1。

     /* 每次松开手指，自动滚动到区间点； */
        private void autoScroll(Slider slider) {
            float distance = slider.center - getPaddingLeft() - leftAndRight;
            int position = (int) (distance / mRange);// 除数
            float remainder = distance % mRange;// 余数
    //        Log.e("moose", "remainder=" + remainder + "  range=" + mRange);
            if (remainder <= (mRange / 2)){
                slider.center = mRangeArray[position];
                slider.position = position;
            } else {
                int index = position + 1;
                slider.position = index;
                slider.center = mRangeArray[index];
            }
            // selected listener. 选中监听。
            ……
        }

#### 绘制
前文中将左右的坐标位置都计算得到，在 onDraw 方法里只需要简单的位置即可。
1. 首先绘制固定背景；
2. 通过 mLeftSlider 和 mRightSlider 绘制 左右两个滑块；
3. 通过 mLeftSlider.right 和 mRightSlider.left 绘制选中区间矩形；
4. 绘制已 mRange 为区间刻度的 文字；根据 mLeftSlider.left 和 mRightSlider.right 决定绘制颜色；
5. 绘制文字下方的刻度线；根据 mLeftSlider.left 和 mRightSlider.right 决定绘制颜色；

## 总结
上周四神清气爽地创建 project 准备开干，却被 Gradle 的 ` Refreshing Gradle ... project `气了两小时气到水也没喝，gym 也没去，晚饭也不做，书也没看，单词也没背，就看了一部《目击者之追凶》来“放松”自己。嗯还是挺好看的那部剧，豆瓣评分8.x呢……（扯远了啊喂）

这个控件的绘制并不算难，重点在于处理触摸事件时计算滑块位置以及自动滚动到最近刻度。虽然不难，但我却花了一天半的时间去计算绘制（才不是我渣，手生而已哼）。`所以啊，要多动脑，多动手，多研究才能保持自己对技术的敏感度。`

以上。
update 2017/08/14 10:38 Monday.
