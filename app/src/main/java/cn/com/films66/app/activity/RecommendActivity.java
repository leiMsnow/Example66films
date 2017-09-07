package cn.com.films66.app.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.byteam.superadapter.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.adapter.FilmsAdapter;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.Film;
import cn.com.films66.app.utils.Constants;

public class RecommendActivity extends AppBaseActivity {

    @Bind(R.id.rv_container)
    RecyclerView rvContainer;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    private FilmsAdapter filmsAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_recommend;
    }

    @Override
    protected void initData() {
        mSwipeContainer.setColorSchemeResources(R.color.app_main_color);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFilms();
            }
        });

        filmsAdapter = new FilmsAdapter(mContext, null, R.layout.item_main);
        rvContainer.setLayoutManager(new LinearLayoutManager(mContext));
        rvContainer.setAdapter(filmsAdapter);
        filmsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {

                Intent intent = new Intent(mContext, FilmDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_FILM_DETAIL_ID, filmsAdapter.getItem(position).id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        getFilms();
    }

    private void getFilms() {
        mSwipeContainer.setRefreshing(false);
        BaseApi.request(BaseApi.createApi(IServiceApi.class).getFilms()
                , new BaseApi.IResponseListener<List<Film>>() {
                    @Override
                    public void onSuccess(List<Film> data) {
                        filmsAdapter.replaceAll(data);
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }
}
