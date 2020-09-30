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
    private class ViewHolder{
        ImageView imageStar;
        ImageView imageEmoticon;
        TextView ingredientName;
        TextView ingredientDesc;
    }




    @Override
    public View getView(int i, View convertView, ViewGroup parent) {


        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.list_ingredients, null);
        }

        //Item p = getItem(position);
        RowIngredient p = (RowIngredient) getItem(i);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.tvName);
            TextView tt2 = (TextView) v.findViewById(R.id.tvDesc);
            ImageView tt3 = (ImageView) v.findViewById(R.id.imageEmoticon);
            ImageView tt4 = (ImageView) v.findViewById(R.id.imageStar);

            if (tt1 != null) {
                tt1.setText(p.getIngredientName());
            }

            if (tt2 != null) {
                tt2.setText(p.getIngredientDesc());
            }

            if (tt3 != null) {
                tt3.setImageResource(p.getImageEmoticon());
            }

            if (tt4 != null) {
                tt4.setImageResource(p.getImageStar());
            }
        }

        return v;






    }
}
