package com.example.licl.seubdspeed.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.licl.seubdspeed.Activity.DeviceTransActivity;
import com.example.licl.seubdspeed.Activity.MainActivity;
import com.example.licl.seubdspeed.BDAPPlication;
import com.example.licl.seubdspeed.Fragment.MainFragment;
import com.example.licl.seubdspeed.Fragment.SpeedFragment;
import com.example.licl.seubdspeed.Model.Node;
import com.example.licl.seubdspeed.R;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by ekansh on 20/10/15.
 */
public class NodeAdapter extends BaseAdapter {

    private List<Node> nodeList;
    private Activity activity;
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;

    public NodeAdapter(Activity activity, List<Node> nodeList) {
        this.nodeList = nodeList;
        this.activity = activity;
    }

    public void add(List<Node> nodes){
        this.nodeList.addAll(nodes);
    }
    public void refresh(List<Node> nodes) { this.nodeList = nodes;}

    @Override
    public int getCount() {
        return nodeList.size();
    }

    @Override
    public Node getItem(int i) {
        return nodeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //view只会在第一页时为null，接下来滚动就不是null，不用再建立viewholedr可减少findviewbyid的次数，提升性能
        if(view ==null){
            layoutInflater = LayoutInflater.from(activity);
            view = layoutInflater.inflate(R.layout.list_item_person, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) view.getTag();

        final Node node = getItem(i);
        viewHolder.setValues(node);
        viewHolder.communicate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!node.isOnline()){
                    Toast.makeText(BDAPPlication.getInstance(),"该节点不在线，不能对话",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(activity, DeviceTransActivity.class);
                intent.putExtra("id",node.getId());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                BDAPPlication.getInstance().startActivity(intent);
            }
        });

        viewHolder.speed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity actvt=(MainActivity)activity;
                actvt.toSpeed(node.getId());
            }
        });

        return view;
    }


    private class ViewHolder {
        private TextView name;
        private TextView desc;
        private Button communicate;
        private Button speed;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_name);
            desc = (TextView) view.findViewById(R.id.tv_desc);
            communicate=(Button)view.findViewById(R.id.btn_communicate);
            speed=(Button)view.findViewById(R.id.btn_map);
        }

        public void setValues(Node node) {
            name.setText(node.getName());
            desc.setText(node.getDesc());
        }
    }

}
