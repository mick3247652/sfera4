package ru.club.sfera.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import ru.club.sfera.R;
import ru.club.sfera.model.MediaItem;
import ru.club.sfera.view.TouchImageView;

public class MediaViewerAdapter extends PagerAdapter {

    private Activity act;
    private List<MediaItem> items;
    private LayoutInflater inflater;

    public MediaViewerAdapter(Activity activity, List<MediaItem> items) {

        this.act = activity;
        this.items = items;
    }

    @Override
    public int getCount() {

        return this.items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        final MediaItem item = items.get(position);

        TouchImageView mImage;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.media_viewer_item, container, false);

        mImage = (TouchImageView) viewLayout.findViewById(R.id.image);

        try {

            Glide.with(act).load(item.getImageUrl())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mImage);

        } catch (Exception e) {

            Log.e("MediaViewerAdapter", e.toString());
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
