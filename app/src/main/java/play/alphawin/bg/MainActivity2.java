package play.alphawin.bg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    private ImageView mImageView = null;
    public int level = 0;
    private boolean win = false;
    private int contPair=0;
    private int[] pairs = {3,8,10,18};
    private int[] numsCount={2,4,5,6};
    final int[] pics = new int[]{R.drawable.sport1, R.drawable.sport2,
            R.drawable.sport3, R.drawable.sport4,
            R.drawable.sport5, R.drawable.sport6};
    int[] pos ={0,1,2,0,1,2};
    int currpos = -1;
    public static Animation mAppearAnimation, mFadeAnimation;
    GridView gvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAppearAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);
        mFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        mAppearAnimation.setAnimationListener(animationAppearListener);
        mFadeAnimation.setAnimationListener(animationFadeListener);


        gvMain = (GridView)findViewById(R.id.gvMain);
        GridAdapter gridAdapter = new GridAdapter(this, (pairs[level]*2));
        gvMain.setAdapter(gridAdapter);
        gvMain.setEnabled(false);
        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currpos<0){
                    currpos=i;
                    mImageView=(ImageView)view;
                    ((ImageView)view).setImageResource(pics[pos[i]]);
                    mImageView.startAnimation(mAppearAnimation);
                    win = false;
                }
                else{
                    if(currpos==i){
                        mImageView.startAnimation(mFadeAnimation);
                    }
                    else if(pos[currpos]!=pos[i]){
                        mImageView.startAnimation(mFadeAnimation);
                        mImageView.setImageResource(pics[pos[currpos]]);
                    }
                    else{
                        win = true;
                        ((ImageView)view).setImageResource(pics[pos[i]]);
                        contPair++;
                        if(contPair==pairs[level]){
                            Dialog dialogWin = new Dialog(MainActivity2.this);
                            dialogWin.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogWin.setContentView(R.layout.win_dialog);
                            dialogWin.setCancelable(false);
                            dialogWin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogWin.show();
                            TextView text_dialog = (TextView) dialogWin.findViewById(R.id.dialog_reminder);
                            Button btn_okey= (Button) dialogWin.findViewById(R.id.ok_btn);
                            btn_okey.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogWin.dismiss();
                                }
                            });
                        }

                    }
                    currpos=-1;
                }
            }
        });
        adjustGridView();
    }


    private void adjustGridView(){
        gvMain.setNumColumns(numsCount[level]);
        //gvMain.setColumnWidth(300);
        gvMain.setVerticalSpacing(20);
        gvMain.setHorizontalSpacing(20);
        gvMain.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
    }

    Animation.AnimationListener animationAppearListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            if(win==false) mImageView.startAnimation(mFadeAnimation);
            currpos = -1;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener animationFadeListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mImageView.setImageResource(R.drawable.bg);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
    };

    public void start(View view) {
        gvMain.setEnabled(true);
    }

    public void changeLevel(View view) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView text_dialog = (TextView) dialog.findViewById(R.id.dialog_reminder);
        Button btn_1= (Button) dialog.findViewById(R.id.lvl1);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(0);
                dialog.dismiss();
            }
        });
        Button btn_2= (Button) dialog.findViewById(R.id.lvl2);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(1);
                dialog.dismiss();
            }
        });
        Button btn_3= (Button) dialog.findViewById(R.id.lvl3);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(2);
                dialog.dismiss();
            }
        });
        Button btn_4= (Button) dialog.findViewById(R.id.lvl4);
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(3);
                dialog.dismiss();
            }
        });
    }

    private void gridChange(int i){
        level=i;
        adjustGridView();
        gvMain.setColumnWidth(350-(level*10));
        GridAdapter gridAdapter = new GridAdapter(MainActivity2.this, (pairs[level]*2));
        gvMain.setAdapter(gridAdapter);
    }
}