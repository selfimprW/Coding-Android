package net.coding.program.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.coding.program.R;
import net.coding.program.common.Global;

/**
 * Created by Carlos2015 on 2015/8/19.
 * 输入控件(common_enter_emoji)支持切换动画的父容器,必须保证它只有两个子控件,并且输入控件是第二个
 */
public class EnterLayoutAnimSupportContainer extends FrameLayout {
    private ViewGroup mContent;
    private FrameLayout mEnter;
    private boolean isFirstLayout = true;
    private boolean isAdjustResize = true;
    private int mEnterLayoutBottomMargin;
    private int mContentHeight;
    private int mEnterHeight;
    private int lastEnterLayoutBottomMargin;
    private LayoutParams lp_enter, lp_content;
    private EditText comment;
    private int minCommentHeight;
    private int minEnterHeight;
    private int minVoiceLayoutTop, minEmojikeyboardLayoutTop;
    private int minEnterLayoutBottomMargin;
    private ViewGroup voiceLayout, emojiKeyboardLayout,mInputBox;
    private FrameLayout.LayoutParams lp_voice, lp_emoji;
    private OnEnterLayoutBottomMarginChanagedCallBack mOnEnterLayoutBottomMarginChanagedCallBack;
    private Editable tempData;
    private Handler mHandler;
    private boolean isEnterHeightChanaged;
    private int inputboxHeight;
    /**
     * mEnter关闭时的绝对y坐标
     */
    public int closeY;
    /**
     * mEnter打开时的绝对y坐标
     */
    public int openY;
    /**
     * mEnter关闭且输入法弹出将mEnter顶上去时的绝对y坐标
     */
    public int softkeyboardOpenY;
    public int orignalHeight;

    private int mY;

    public EnterLayoutAnimSupportContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EnterLayoutAnimSupportContainer);
        isAdjustResize = a.getBoolean(R.styleable.EnterLayoutAnimSupportContainer_isAdjustResize, false);
        a.recycle();
    }

    public void setEnterLayoutBottomMargin(int mEnterLayoutBottomMargin) {
        this.mEnterLayoutBottomMargin = mEnterLayoutBottomMargin;
        isEnterHeightChanaged = false;
        requestLayout();
    }
//
//    public void hideEnterPanel(){
//        lp_enter.height = inputboxHeight;
//        lp_enter.bottomMargin = 0;
//        isEnterHeightChanaged = true;
//        requestLayout();
//    }
//
//    public void showEnterPanel(){
//        lp_enter.height = mEnterHeight;
//        lp_enter.bottomMargin = 0;
//        isEnterHeightChanaged = true;
//        requestLayout();
//    }


    /**
     * 判断输入法弹出时会不会拉伸布局
     *
     * @return
     */
    public boolean isAdjustResize() {
        return isAdjustResize;
    }

    public void setOnEnterLayoutBottomMarginChanagedCallBack(OnEnterLayoutBottomMarginChanagedCallBack callBack) {
        this.mOnEnterLayoutBottomMarginChanagedCallBack = callBack;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (isFirstLayout && getVisibility() != View.GONE) {
            mContent = (ViewGroup) getChildAt(0);
            mEnter = (FrameLayout) getChildAt(1);
            lp_content = (LayoutParams) mContent.getLayoutParams();
            lp_enter = (LayoutParams) mEnter.getLayoutParams();
            mEnterLayoutBottomMargin = lp_enter.bottomMargin;
            minEnterLayoutBottomMargin = mEnterLayoutBottomMargin;
            lp_enter.gravity = Gravity.BOTTOM | Gravity.LEFT;
            comment = (EditText) mEnter.findViewById(R.id.comment);
            mInputBox = (ViewGroup) mEnter.findViewById(R.id.mInputBox);
            voiceLayout = (ViewGroup) mEnter.findViewById(R.id.voiceLayout);
            emojiKeyboardLayout = (ViewGroup) mEnter.findViewById(R.id.emojiKeyboardLayout);
            lp_voice = (LayoutParams) voiceLayout.getLayoutParams();
            lp_emoji = (LayoutParams) emojiKeyboardLayout.getLayoutParams();
            isFirstLayout = false;
            orignalHeight = bottom - top;
            inputboxHeight = mInputBox.getMeasuredHeight();
            int[] location = new int[2];
            getLocationInWindow(location);
            mY = location[1];
            lastEnterLayoutBottomMargin = mEnterLayoutBottomMargin;
            if (isAdjustResize) {
                lp_content.height = -1;
            }
            //当输入框中文本内容大于1行时，此时获取不到输入框的最小高度，因此将文本保存起来，清空输入框，通过
            //第一个延迟消息得到单行输入框的高度(最小高度),第二个延迟消息将暂时保存的文本放入输入框，此时整个输入控件的
            //显示就会正常

            if(isAdjustResize && mEnter.getVisibility() != View.GONE && comment.getLineCount()>1){
                tempData = comment.getText();
                comment.setText("");
                final int status = mEnter.getVisibility();
                mEnter.setVisibility(View.GONE);
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case 1:
                                if(comment.getLineCount()<=1){
                                    if(!TextUtils.isEmpty(comment.getText())){
                                        tempData = comment.getText();
                                    }
                                    mEnter.setVisibility(status);
                                    mHandler.sendEmptyMessageDelayed(2,100);
                                }else{
                                    tempData = comment.getText();
                                    comment.setText("");
                                    mEnter.setVisibility(View.GONE);
                                    mHandler.sendEmptyMessageDelayed(1,100);
                                }
                                break;
                            case 2:
                                comment.setText(tempData);
                                tempData = null;
                                mHandler = null;
                                break;
                        }
                    }
                };
                mHandler.sendEmptyMessageDelayed(1,200);
            }else{
                if (mEnter.getVisibility() != View.GONE) {
                    //comment最小高度，当行数发生变化时，它的高度会随之改变

                    minEnterHeight = mEnter.getMeasuredHeight();
                    minCommentHeight = comment.getMeasuredHeight();
                    minVoiceLayoutTop = lp_voice.topMargin;
                    minEmojikeyboardLayoutTop = lp_emoji.topMargin;
                }
            }


        }

        if (minEnterHeight == 0 && mEnter.getVisibility() != View.GONE) {
            //comment最小高度，当行数发生变化时，它的高度会随之改变
            minEnterHeight = mEnter.getMeasuredHeight();
            minCommentHeight = comment.getMeasuredHeight();
            minVoiceLayoutTop = lp_voice.topMargin;
            minEmojikeyboardLayoutTop = lp_emoji.topMargin;
            inputboxHeight = mInputBox.getMeasuredHeight();
        }
        if(!isEnterHeightChanaged){
            if (mEnter.getVisibility() != View.GONE) {
                int commentHeight = comment.getMeasuredHeight();
                int dH = commentHeight - minCommentHeight;
                mEnterHeight = minEnterHeight + dH;
                if (dH > 0) {
                    lp_enter.height = mEnterHeight;
                    lp_voice.topMargin = minVoiceLayoutTop + dH;
                    lp_emoji.topMargin = minEmojikeyboardLayoutTop + dH;
                } else {
                    lp_voice.topMargin = minVoiceLayoutTop;
                    lp_emoji.topMargin = minEmojikeyboardLayoutTop;
                }

                closeY = mY + orignalHeight - (mEnterHeight + mEnterLayoutBottomMargin);
                openY = closeY + mEnterLayoutBottomMargin;

            }

            int content_bottom = bottom - top - (mEnterHeight + mEnterLayoutBottomMargin);
            int cL = 0, cR = 0, cT = 0, cB = 0;
            int eL = 0, eR = 0, eT = 0, eB = 0;
            if (isAdjustResize) {
                // lp_enter.bottomMargin = mEnterLayoutBottomMargin;

                //加上这一句防止mContent的高度有时显示不正确
                if (lp_content.height == -1) {
                    lp_content.height = content_bottom - getPaddingTop() - lp_content.topMargin;
                }
                cL = getPaddingLeft() + lp_content.leftMargin;
                cT = getPaddingTop() + lp_content.topMargin;
                cR = cL + mContent.getMeasuredWidth();
                cB = content_bottom;

                eL = lp_enter.leftMargin + getPaddingLeft();
                eT = content_bottom;
                eR = eL + mEnter.getMeasuredWidth();
                eB = mEnterHeight + content_bottom;


            } else {

                cL = getPaddingLeft() + lp_content.leftMargin;
                cT = getPaddingTop() + lp_content.topMargin;
                cR = cL + mContent.getMeasuredWidth();
                cB = cT + mContent.getMeasuredHeight();

                eL = lp_enter.leftMargin + getPaddingLeft();
                eT = content_bottom;
                eR = eL + mEnter.getMeasuredWidth();
                eB = mEnterHeight + content_bottom;

            }

            mContent.layout(cL, cT, cR, cB);
            mEnter.layout(eL, eT, eR, eB);
            lp_enter.bottomMargin = mEnterLayoutBottomMargin;
            if (mOnEnterLayoutBottomMarginChanagedCallBack != null) {
                mOnEnterLayoutBottomMarginChanagedCallBack.onChanage(mEnterLayoutBottomMargin, lastEnterLayoutBottomMargin);
            }
            lastEnterLayoutBottomMargin = mEnterLayoutBottomMargin;
        }else{
            super.onLayout(changed,left,top,right,bottom);
        }

       // super.onLayout(changed,left,top,right,bottom);
    }

    public interface OnEnterLayoutBottomMarginChanagedCallBack {
        void onChanage(int lastBottomMargin, int newBottomMargin);
    }
}
