package akash.com.mobe.Helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import akash.com.mobe.Constructor.NewsFeed;
import akash.com.mobe.R;

import static akash.com.mobe.Helper.EmojiTag.E_ANGRY;
import static akash.com.mobe.Helper.EmojiTag.E_CONF;
import static akash.com.mobe.Helper.EmojiTag.E_DISAP;
import static akash.com.mobe.Helper.EmojiTag.E_HAPPY;
import static akash.com.mobe.Helper.EmojiTag.E_RELAX;
import static akash.com.mobe.Helper.EmojiTag.E_SAD;

/**
 * Created by julfi on 28/07/2017.
 */

public class NewsFeedListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private TextView tv_name,tv_email,tv_time;
    private ImageView iv_emo;
    private List<NewsFeed> newsFeeds;
    private int RESOURCE;

    public NewsFeedListAdapter(Activity activity,List<NewsFeed>newsFeeds){
        this.activity = activity;
        this.newsFeeds = newsFeeds;
    }

    @Override
    public int getCount() {
        return newsFeeds.size();
    }

    @Override
    public Object getItem(int position) {
        return newsFeeds.get(position);
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
            convertView = inflater.inflate(R.layout.newsfeed_list, null);

        tv_name = (TextView) convertView.findViewById(R.id.nf_name);
        tv_email = (TextView) convertView.findViewById(R.id.nf_email);
        tv_time = (TextView) convertView.findViewById(R.id.nf_time);
        iv_emo = (ImageView) convertView.findViewById(R.id.nf_image);

        final NewsFeed newsFeed = newsFeeds.get(position);

        final String name = newsFeed.getFname()+ " " + newsFeed.getLname();
        final String date = "Posted on "+ newsFeed.getDay() + " at " + newsFeed.getTime();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tv_name.setText(name);
                tv_email.setText(newsFeed.getEmail());
                tv_time.setText(date);

                switch (newsFeed.getEmo()){
                    case E_ANGRY:
                        RESOURCE = R.drawable.angry;
                        break;
                    case E_CONF:
                        RESOURCE = R.drawable.confident;
                        break;
                    case E_DISAP:
                        RESOURCE = R.drawable.disappointed;
                        break;
                    case E_HAPPY:
                        RESOURCE = R.drawable.happy;
                        break;
                    case E_RELAX:
                        RESOURCE = R.drawable.relax;
                        break;
                    case E_SAD:
                        RESOURCE = R.drawable.sad;
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
