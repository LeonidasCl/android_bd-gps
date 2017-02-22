package com.example.licl.seubdspeed.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

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
    private int maxRecords = 400;

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
            public void onRefresh() {
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
                    nodeAdapter.add(bootData());
                    nodeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private List<Node> bootData(){
        List<Node> nodes = new ArrayList<Node>();
        for(int i=bootCounter;i<bootCounter+5;i++){
            Node node = new Node();
            node.setName("节点" + i);
            node.setId(i);
            node.setDesc("这是测试节点" + i);
            nodes.add(node);
        }
        return nodes;
    }
}
