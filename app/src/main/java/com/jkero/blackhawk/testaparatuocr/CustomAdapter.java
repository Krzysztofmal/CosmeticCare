package com.jkero.blackhawk.testaparatuocr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<RowIngredient> rowIngredients;

    public CustomAdapter(Context context, List<RowIngredient> rowIngredients) {
        this.context = context;
        this.rowIngredients = rowIngredients;
    }

    @Override
    public int getCount() {
        return rowIngredients.size();
    }

    @Override
    public Object getItem(int i) {
        return rowIngredients.get(i);
    }

    @Override
    public long getItemId(int i) {
        return rowIngredients.indexOf(getItem(i));
    }


    // Private view holder class
    private class ViewHolder {
        ImageView imageStar;
        ImageView imageEmoticon;
        TextView ingredientName;
        TextView ingredientDesc;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {


        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.list_ingredients, null);
        }

        //Item p = getItem(position);
        RowIngredient rowIngredient = (RowIngredient) getItem(i);

        if (rowIngredient != null) {
            TextView tvIngredientName = (TextView) view.findViewById(R.id.tvName);
            TextView tvDescription = (TextView) view.findViewById(R.id.tvDesc);
            ImageView ivEmoticon = (ImageView) view.findViewById(R.id.imageEmoticon);
            ImageView ivStar = (ImageView) view.findViewById(R.id.imageStar);

            if (tvIngredientName != null) {
                tvIngredientName.setText(rowIngredient.getIngredientName());
            }

            if (tvDescription != null) {
                tvDescription.setText(rowIngredient.getIngredientDesc());
            }

            if (ivEmoticon != null) {
                ivEmoticon.setImageResource(rowIngredient.getImageEmoticon());
            }

            if (ivStar != null) {
                ivStar.setImageResource(rowIngredient.getImageStar());
            }
        }

        return view;


    }
}
