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
    private final int mWidthPixels;
    private final int mHeightPixels;
    private final List<Picture> mPictures;

    public ProductsAdapter(
            final int layoutResourceId, final int widthPixels, final int heightPixels) {
        super();
        mLayoutResourceId = layoutResourceId;
        mWidthPixels = widthPixels;
        mHeightPixels = heightPixels;
        mPictures = getPictures();
    }

    public static List<Picture> getPictures() {
        final List<Picture> pictures = new ArrayList<Picture>();
        pictures.add(new Picture("1506905925346-21bda4d32df4", "Above the cloud line"));
        pictures.add(new Picture("1418065460487-3e41a6c84dc5", "Fog in the pines"));
        pictures.add(new Picture("1439853949127-fa647821eba0", "Glacier valley"));
        pictures.add(new Picture("1444927714506-8492d94b4e3d", "Blue ridges"));
        pictures.add(new Picture("1505765050516-f72dcac9c60e", "Peaks in cloud"));
        pictures.add(new Picture("1483728642387-6c3bdd6c93e5", "Watzmann at dusk"));
        pictures.add(new Picture("1470071459604-3b5ec3a7fe05", "Highland light"));
        pictures.add(new Picture("1433086966358-54859d0ed716", "Multnomah Falls"));
        pictures.add(new Picture("1441974231531-c6227db76b6e", "Old growth"));
        pictures.add(new Picture("1447752875215-b2761acb3c5d", "Boardwalk"));
        pictures.add(new Picture("1472214103451-9374bd1c798e", "Fairy pools"));
        pictures.add(new Picture("1470252649378-9c29740c9fa8", "Dawn on the lake"));
        pictures.add(new Picture("1501785888041-af3ef285b470", "Boathouse, Braies"));
        pictures.add(new Picture("1504893524553-b855bce32c67", "Green gorge"));
        pictures.add(new Picture("1519681393784-d120267933ba", "Peaks at night"));
        pictures.add(new Picture("1470240731273-7821a6eeb6bd", "Meadow, storm light"));
        pictures.add(new Picture("1441716844725-09cedc13a4e7", "Still water"));
        pictures.add(new Picture("1454372182658-c712e4c5a1db", "Jetty"));
        pictures.add(new Picture("1439405326854-014607f694d7", "Cold sea"));
        pictures.add(new Picture("1464822759023-fed622ff2c3b", "Alpine valley"));
        pictures.add(new Picture("1477346611705-65d1883cee1e", "Ridge at dusk"));
        return pictures;
    }

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

        final String url = PictureUrl.forSize(picture.mSlug, mWidthPixels, mHeightPixels);
        final ImageView imageView = view.findViewById(R.id.list_item_picture_image_view);
        imageView.setImageBitmap(null);
        Picasso.get().load(url).into(imageView);

        final TextView textView = (TextView) view.findViewById(R.id.list_item_picture_text_view);
        textView.setText(picture.mCaption);
        return view;
    }
}
