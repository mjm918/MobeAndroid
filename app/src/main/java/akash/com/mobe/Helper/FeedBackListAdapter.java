package akash.com.mobe.Helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import akash.com.mobe.Constructor.FeedBack;
import akash.com.mobe.R;

import static akash.com.mobe.Helper.EmojiTag.E_ANGRY;
import static akash.com.mobe.Helper.EmojiTag.E_CONF;
import static akash.com.mobe.Helper.EmojiTag.E_DISAP;
import static akash.com.mobe.Helper.EmojiTag.E_HAPPY;
import static akash.com.mobe.Helper.EmojiTag.E_RELAX;
import static akash.com.mobe.Helper.EmojiTag.E_SAD;

/**
 * Created by julfi on 27/07/2017.
 */

public class FeedBackListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedBack> feedBacks;
    private TextView tv_name,tv_date;
    private ImageView iv_emo;
    private LinearLayout linearLayout;
    private int RESOURCE;

    public FeedBackListAdapter(Activity activity,List<FeedBack>feedBacks){
        this.activity = activity;
        this.feedBacks = feedBacks;
    }

    @Override
    public int getCount() {
        return feedBacks.size();
    }

    @Override
    public Object getItem(int position) {
        return feedBacks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feedback_list, null);

        tv_name = (TextView) convertView.findViewById(R.id.tv_list_name);
        tv_date = (TextView) convertView.findViewById(R.id.tv_list_time);
        iv_emo = (ImageView) convertView.findViewById(R.id.iv_emo);
        linearLayout = (LinearLayout) convertView.findViewById(R.id.list_feedback);

        final FeedBack feedBack = feedBacks.get(position);

        final String fullname = "By  "+ feedBack.getFname() + " " + feedBack.getLname();
        final String date = "On "+feedBack.getDate();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tv_name.setText(fullname);
                tv_date.setText(date);

                switch (feedBack.getEmo()){
                    case E_ANGRY:
                        RESOURCE = R.drawable.angry;
                        linearLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.angry));
                        break;
                    case E_CONF:
                        RESOURCE = R.drawable.confident;
                        linearLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.conf));
                        break;
                    case E_DISAP:
                        RESOURCE = R.drawable.disappointed;
                        linearLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.dis));
                        break;
                    case E_HAPPY:
                        RESOURCE = R.drawable.happy;
                        linearLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.happy));
                        break;
                    case E_RELAX:
                        RESOURCE = R.drawable.relax;
                        linearLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.satisf));
                        break;
                    case E_SAD:
                        RESOURCE = R.drawable.sad;
                        linearLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.sad));
                        break;
                    default:
                        RESOURCE = 0;
                        break;
                }

                if(RESOURCE == 0){
                    iv_emo.setBackgroundResource(R.drawable.vote);
                }else{
                    iv_emo.setBackgroundResource(RESOURCE);
                }

            }
        });

        return convertView;
    }
}
