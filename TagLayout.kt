package com.alex.viewlearning.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

public class TagLayout : ViewGroup {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    )
            : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 先测量所有子View
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        // 测量宽度
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)

        // 计算宽度
        var width = 0
        // 计算高度
        var height = 0

        // 当前行已使用的宽度
        var curLineWidthUsed = 0
        // 当前行最大高度
        var curLineMaxHeight = 0

        for ((index, child) in children.withIndex()) {
            // 当前子View的宽度加上已使用的宽度大于总宽度时，需要提行
            if (curLineWidthUsed + child.measuredWidth > measureWidth) {

                // 提行之后，当前行已使用的宽度就是当前子View的宽度
                curLineWidthUsed = child.measuredWidth

                // 提行之后，计算高度加一次上一行的高度
                height += curLineMaxHeight

                // 提行之后，当前行最大高度就是当前子View的高度
                curLineMaxHeight = child.measuredHeight

            } else {

                // 不需要提行

                // 当前行已使用的宽度要加上当前子View的宽度
                curLineWidthUsed += child.measuredWidth

                // 当前行最大高度要和当前子View的高度比较得出
                curLineMaxHeight = max(curLineMaxHeight, child.measuredHeight)

            }

            // 计算宽度一直保持最大
            width = max(width, curLineWidthUsed)


            // 当这个子View是最后一个的时候，计算高度要加上当前行的最大高度
            if (index == childCount - 1) {
                height += curLineMaxHeight
            }
        }

        // 过滤一下计算的宽高和父View的约束
        width = resolveSize(width, widthMeasureSpec)
        height = resolveSize(height, heightMeasureSpec)

        // 设置宽高
        setMeasuredDimension(width, height)

    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        // 当前行已使用的宽度
        var curLineWidthUsed = 0
        // 已使用的高度
        var heightUsed = 0
        // 当前行的最大高度
        var curLineMaxHeight = 0
        for (child in children) {
            // 当前子View的宽度加上已使用的宽度大于总宽度时，需要提行
            if (curLineWidthUsed + child.measuredWidth > width) {

                // 提行后，将当前行已使用的宽度设为0
                curLineWidthUsed = 0

                // 提行后，已使用的高度要加上上一行的最大高度
                heightUsed += curLineMaxHeight

                // 提行后，当前行的最大高度就是当前子View的高度
                curLineMaxHeight = child.measuredHeight

                // 布局子View，左是当前行已使用的宽度，上是已使用的总高度
                child.layout(
                    curLineWidthUsed,
                    heightUsed,
                    curLineWidthUsed + child.measuredWidth,
                    heightUsed + child.measuredHeight
                )

                // 布局完成后，当前行已使用的宽度就是当前子View的宽度
                curLineWidthUsed = child.measuredWidth

            } else {

                // 布局子View，左是当前行已使用的宽度，上是已使用的总高度
                child.layout(
                    curLineWidthUsed,
                    heightUsed,
                    curLineWidthUsed + child.measuredWidth,
                    heightUsed + child.measuredHeight
                )

                // 布局完成后，当前行的最大高度要和当前子View的高度比较得出
                curLineMaxHeight = max(curLineMaxHeight, child.measuredHeight)

                // 布局完成后，当前行已使用的宽度要加上当前子View的宽度
                curLineWidthUsed += child.measuredWidth

            }

        }
    }
}