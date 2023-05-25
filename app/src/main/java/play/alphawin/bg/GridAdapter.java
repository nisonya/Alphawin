package play.alphawin.bg;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private int ip;
    private int param;
    final int[] pics = new int[]{R.drawable.sport1, R.drawable.sport2,
            R.drawable.sport3, R.drawable.sport4,
            R.drawable.sport5, R.drawable.sport6};
    public GridAdapter(Context context, int ip, int param){
        this.context=context;
        this.ip = ip;
        this.param=param;
    }
    @Override
    public int getCount() {
        return ip;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view==null){
            imageView=new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(param,param));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else imageView =(ImageView)view;
        imageView.setImageResource(R.drawable.bg);

        return imageView;
    }
}
