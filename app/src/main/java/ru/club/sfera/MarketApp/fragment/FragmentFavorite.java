package ru.club.sfera.MarketApp.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.club.sfera.MarketApp.Config;
import ru.club.sfera.R;
import ru.club.sfera.MarketApp.activities.ActivityNewsDetail;
import ru.club.sfera.MarketApp.activities.MarketMainActivity;
import ru.club.sfera.MarketApp.adapter.AdapterNews;
import ru.club.sfera.MarketApp.models.News;
import ru.club.sfera.MarketApp.utils.DbHandler;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    private List<News> data = new ArrayList<News>();
    private View root_view, parent_view;
    private RecyclerView recyclerView;
    private AdapterNews mAdapter;
    private MarketMainActivity mainActivity;
    LinearLayout lyt_root;
    DbHandler databaseHandler;

    public FragmentFavorite() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MarketMainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.market_fragment_favorite, null);
        parent_view = getActivity().findViewById(R.id.main_content);
        lyt_root = root_view.findViewById(R.id.root_layout);

        recyclerView = root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }

        return root_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

        databaseHandler = new DbHandler(getActivity());
        data = databaseHandler.getAllData();

        //set data and list adapter
        mAdapter = new AdapterNews(getActivity(), recyclerView, data);
        recyclerView.setAdapter(mAdapter);

        if (data.size() == 0) {
            showNoItemView(true);
        } else {
            showNoItemView(false);
        }

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterNews.OnItemClickListener() {
            @Override
            public void onItemClick(View v, News obj, int position) {
                ActivityNewsDetail.navigate((MarketMainActivity) getActivity(), v.findViewById(R.id.image), obj);
            }
        });

    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_later);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.market_no_favorite_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }
}
