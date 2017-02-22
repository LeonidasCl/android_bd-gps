package com.example.licl.seubdspeed.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.licl.seubdspeed.Adapters.NodeAdapter;
import com.example.licl.seubdspeed.Model.Node;
import com.example.licl.seubdspeed.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面 一级页面
 * Created by 李嘉文 on 2017/2/19.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    private SwipeRefreshLayout refreshLayout;
    private NodeAdapter nodeAdapter;
    private ListView listView;
    private int bootCounter=0;
    private int maxRecords = 25;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        nodeAdapter = new NodeAdapter(getActivity(),bootData());
        listView = (ListView) view.findViewById(R.id.person_list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        listView.setAdapter(nodeAdapter);

        onScrollListener();
        onRefreshListener();


        return view;
    }

    private void onRefreshListener(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                refreshLayout.setRefreshing(true);
                bootCounter=0;
                nodeAdapter.refresh(bootData());
                nodeAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void onScrollListener(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount > totalItemCount - 2 && totalItemCount < maxRecords) {
                    if (bootCounter>15){
                        Toast.makeText(getActivity(),"没有更多的节点了",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    nodeAdapter.add(bootData());
                    nodeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private List<Node> bootData(){
        List<Node> nodes = new ArrayList<Node>();
        for(int i=bootCounter;i<bootCounter+10;i++){
            Node node = new Node();
            node.setName("节点" + i);
            node.setId(String.valueOf(i));
            if (i==0){
                node.setDesc("节点" + i + " （在线）");
                node.setId("D1426");
                node.setOnline(true);
            }
            if (i>0&&i<3){
                node.setDesc("节点" + i + " （离线）");
                node.setOnline(false);
            }
            if (i>2){
                node.setDesc("模拟节点" + i);
                node.setOnline(false);
            }
            nodes.add(node);
        }
        bootCounter+=5;
        return nodes;
    }
}
