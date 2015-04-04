package progsoul.opendata.leccebybike.views;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import progsoul.opendata.leccebybike.R;

/**
 * Created by ProgSoul on 26/03/2015.
 */
public class RosetteProgressDialog extends Dialog {
    private ImageView rosetteImageView;
    private Context context;

    public RosetteProgressDialog(Context context) {
        super(context, android.R.style.Theme_Holo_NoActionBar_TranslucentDecor);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rosette_progress_dialog);

        rosetteImageView = (ImageView) findViewById(R.id.rosette_image_view);

        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();

        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_indefinitely);
        rosetteImageView.setAnimation(rotateAnimation);
        rosetteImageView.startAnimation(rotateAnimation);
    }
}