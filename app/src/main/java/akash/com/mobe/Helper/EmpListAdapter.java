package akash.com.mobe.Helper;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import akash.com.mobe.Constructor.UserInfo;
import akash.com.mobe.R;

import static akash.com.mobe.Helper.EmojiTag.E_ANGRY;
import static akash.com.mobe.Helper.EmojiTag.E_CONF;
import static akash.com.mobe.Helper.EmojiTag.E_DISAP;
import static akash.com.mobe.Helper.EmojiTag.E_HAPPY;
import static akash.com.mobe.Helper.EmojiTag.E_RELAX;
import static akash.com.mobe.Helper.EmojiTag.E_SAD;

/**
 * Created by julfi on 26/07/2017.
 */

public class EmpListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<UserInfo> userInfos;
    private TextView tv_name,tv_email,tv_date;
    private ImageView iv_vote;
    private LinearLayout item;
    private int RESOURCE;

    public EmpListAdapter(Activity activity,List<UserInfo> userInfos){
        this.activity = activity;
        this.userInfos = userInfos;
    }

    @Override
    public int getCount() {
        return userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String DATE = df.format(c.getTime());

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item, null);

        tv_name = (TextView) convertView.findViewById(R.id.tv_emp_name);
        tv_email = (TextView) convertView.findViewById(R.id.tv_emp_email);
        tv_date = (TextView) convertView.findViewById(R.id.tv_emp_date);
        iv_vote = (ImageView) convertView.findViewById(R.id.iv_emo);
        item = (LinearLayout) convertView.findViewById(R.id.item);

        final UserInfo info = userInfos.get(position);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tv_name.setText(info.getName());
                tv_email.setText(info.getEmail());
                tv_date.setText(DATE);

                switch (info.getEmo()){
                    case E_ANGRY:
                        RESOURCE = R.drawable.angry;
                        item.setBackgroundColor(ContextCompat.getColor(activity,R.color.angry));
                        break;
                    case E_CONF:
                        RESOURCE = R.drawable.confident;
                        item.setBackgroundColor(ContextCompat.getColor(activity,R.color.conf));
                        break;
                    case E_DISAP:
                        RESOURCE = R.drawable.disappointed;
                        item.setBackgroundColor(ContextCompat.getColor(activity,R.color.dis));
                        break;
                    case E_HAPPY:
                        RESOURCE = R.drawable.happy;
                        item.setBackgroundColor(ContextCompat.getColor(activity,R.color.happy));
                        break;
                    case E_RELAX:
                        RESOURCE = R.drawable.relax;
                        item.setBackgroundColor(ContextCompat.getColor(activity,R.color.satisf));
                        break;
                    case E_SAD:
                        RESOURCE = R.drawable.sad;
                        item.setBackgroundColor(ContextCompat.getColor(activity,R.color.sad));
                        break;
                    default:
                        RESOURCE = 0;
                        break;
                }

                if(RESOURCE == 0){
                    iv_vote.setBackgroundResource(R.drawable.vote);
                }else{
                    iv_vote.setBackgroundResource(RESOURCE);
                }
            }
        });

        return convertView;
    }
}
