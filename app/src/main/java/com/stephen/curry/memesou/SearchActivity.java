package com.stephen.curry.memesou;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stephen.curry.memesou.adapter.MyGridViewAdapter;
import com.stephen.curry.memesou.adapter.MyLvAdapter;
import com.stephen.curry.memesou.bean.RegionInfo;
import com.stephen.curry.memesou.bean.SortModel;
import com.stephen.curry.memesou.db.RegionDAO;
import com.stephen.curry.memesou.utils.CharacterParser;
import com.stephen.curry.memesou.utils.PinyinComparator;
import com.stephen.curry.memesou.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends Activity {
    private List<RegionInfo> provinceList;
    private List<RegionInfo> citysList;
    private List<String> provinces;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private MyLvAdapter adapter;
    private List<RegionInfo> mReMenCitys;//热门城市列表
    private MyGridViewAdapter gvAdapter;
    private GridView mGridView;
    private RelativeLayout iv_left;
    private LinearLayout title_ll;
    private TextView title_tv;
    private int lastFirstVisibleItem = -1;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        initData();
        initViews();
    }

    private void initData() {
        provinceList = RegionDAO.getProvencesOrCity(1);
        provinceList.addAll(RegionDAO.getProvencesOrCity(2));
        citysList = new ArrayList<RegionInfo>();
        mReMenCitys = new ArrayList<RegionInfo>();
        provinces = new ArrayList<String>();
        for (RegionInfo info : provinceList) {
            provinces.add(info.getName().trim());
        }
        mReMenCitys.add(new RegionInfo(2, R.mipmap.ic_launcher, 1, "北京"));
        mReMenCitys.add(new RegionInfo(25, R.mipmap.ic_launcher, 1, "上海"));
        mReMenCitys.add(new RegionInfo(77, R.mipmap.ic_launcher, 6, "深圳"));
        mReMenCitys.add(new RegionInfo(76, R.mipmap.ic_launcher, 6, "广州"));
        mReMenCitys.add(new RegionInfo(197, R.mipmap.ic_launcher, 14, "长沙"));
        mReMenCitys.add(new RegionInfo(343, R.mipmap.ic_launcher, 1, "天津"));
        mReMenCitys.add(new RegionInfo(76, R.mipmap.ic_launcher, 6, "成都"));
        mReMenCitys.add(new RegionInfo(197, R.mipmap.ic_launcher, 14, "重庆"));
        mReMenCitys.add(new RegionInfo(343, R.mipmap.ic_launcher, 1, "武汉"));

    }

    private void initViews() {
        View view = View.inflate(this, R.layout.head_city_list, null);
        mGridView = (GridView) view.findViewById(R.id.id_gv_remen);
        gvAdapter = new MyGridViewAdapter(this, mReMenCitys);
        mGridView.setAdapter(gvAdapter);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        title_ll = (LinearLayout) findViewById(R.id.title_layout);
        title_tv = (TextView) findViewById(R.id.title);
        sideBar.setTitleLayout(title_ll);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (s.equals("热门")) {
                    sortListView.setSelection(0);
                }
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position+1);//加上头部的headerview
                }
            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.addHeaderView(view);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position - 1)).getName(), Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("cityName", ((SortModel) adapter.getItem(position - 1)).getName());
            }
        });

//		SourceDateList = filledData(getResources().getStringArray(R.array.date));
        SourceDateList = filledData(provinceList);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new MyLvAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(SearchActivity.this, mReMenCitys.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //添加滑动监听，只有当静止的时候，才加载图片，优化列表的卡顿现象
        sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    adapter.isListViewIdle = true;
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.isListViewIdle = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) title_ll.getLayoutParams();
                    params.topMargin = 0;
                    title_ll.setLayoutParams(params);
                }

                int section = adapter.getSectionForPosition(firstVisibleItem);
                int lastSection ;
                if (firstVisibleItem==0){
                    lastSection=-1;
                    title_tv.setText("热门城市");
                }else {
                    lastSection= adapter.getSectionForPosition(firstVisibleItem - 1);
                    title_tv.setText(adapter.getHeaderText(firstVisibleItem-1));
                }
                if (lastSection != section) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = title_ll.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) title_ll
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            title_ll.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                title_ll.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(List<RegionInfo> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i).getName());
            sortModel.setSrcId(R.mipmap.ic_launcher);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }
}
