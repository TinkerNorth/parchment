// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.models.Picture;
import mobi.parchment.sample.R;

public class ProductsAdapter extends BaseAdapter {

    private final int mLayoutResourceId;

    public ProductsAdapter(final int layoutResourceId) {
        super();
        mLayoutResourceId = layoutResourceId;
        mPictures = getPictures();
    }

    public static List<Picture> getPictures() {
        final List<Picture> pictures = new ArrayList<Picture>();
        pictures.add(new Picture("https://i.imgur.com/8LOZwbE.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/EfLvmlO.png", "Cheetahs"));
        pictures.add(new Picture("https://i.imgur.com/VfH1siL.png", "Cloud Break"));
        pictures.add(new Picture("https://i.imgur.com/rmU8E53.png", "Lighthouse"));
        pictures.add(new Picture("https://i.imgur.com/qZCCdFW.png", "Uluru"));
        pictures.add(new Picture("https://i.imgur.com/zkt4IEl.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/xL9BSy3.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/lYQPhnY.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/5tM1vxy.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/lUbn6U6.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/fk4l1QY.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/wk49eIK.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/qwz8VUx.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/XR8Dc2D.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/lGBUYjK.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/8AgQWCh.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/YMwlUy3.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/MyWKsZf.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/gTghrcj.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/ePH4nP4.png", "National photo contest"));
        pictures.add(new Picture("https://i.imgur.com/KVJuX7O.png", "National photo contest"));
        return pictures;
    }

    private List<Picture> mPictures = new ArrayList<Picture>();

    @Override
    public int getCount() {
        return mPictures.size();
    }

    @Override
    public Object getItem(final int position) {
        return mPictures.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private View getView(final Context context, final View convertView, final ViewGroup viewGroup) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            final View view = layoutInflater.inflate(mLayoutResourceId, viewGroup, false);
            return view;
        }
        return convertView;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        final View view = getView(context, convertView, parent);
        final Picture picture = (Picture) getItem(position);

        final ImageView imageView =
                (ImageView) view.findViewById(R.id.list_item_picture_image_view);
        imageView.setImageBitmap(null);
        Picasso.get().load(picture.mUrl).into(imageView);

        final TextView textView = (TextView) view.findViewById(R.id.list_item_picture_text_view);
        textView.setText(picture.mCaption);
        return view;
    }
}
