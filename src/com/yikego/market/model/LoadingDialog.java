package com.yikego.market.model;
import com.yikego.market.R;

import android.app.Dialog;  
import android.content.Context;  
import android.view.Gravity;  
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;  
  
public class LoadingDialog extends Dialog {  
    public LoadingDialog(Context context) {  
        this(context, R.style.loading_dialog);  
    }  
  
    public LoadingDialog(Context context, int theme) {  
        super(context, theme);  
        this.setContentView(R.layout.loading_dialog);  
        this.getWindow().getAttributes().gravity = Gravity.CENTER;  
        ImageView spaceshipImage = (ImageView)findViewById(R.id.img);  
//        TextView tipTextView = (TextView)findViewById(R.id.tipTextView);// 提示文字  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
    }  
  
    @Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
  
        if (!hasFocus) {  
            dismiss();  
        }  
    }  
}  