package com.jkero.blackhawk.testaparatuocr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    List<RowIngredient> rowIngredients;

    public CustomAdapter(Context context, List<RowIngredient> rowIngredients) {
        this.context = context;
        this.rowIngredients = rowIngredients;
    }

    public Object getItem(int i) {
        return rowIngredients.get(i);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.list_ingredients, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final RowIngredient rowIngredient = (RowIngredient) getItem(position);

        holder.ingredientName.setText(rowIngredient.getIngredientName());
        holder.ingredientDesc.setText(rowIngredient.getIngredientDesc());
        holder.imageEmoticon.setImageResource(rowIngredient.getImageEmoticon());
        holder.imageStar.setImageResource(rowIngredient.getImageStar());

        final RowIngredient selectedIngredient = (RowIngredient) getItem(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                builder.setTitle(rowIngredient.getIngredientName());

                builder.setMessage(rowIngredient.getIngredientDesc());
                AlertDialog alert = builder.create();

                alert.show();
            }
        });

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (context.getClass().getSimpleName().equals("ListActivity")) {
                    notifyDataSetChanged();


                    //}

                    DbHandler db;
                    ArrayList<Integer> datasList;
                    db = new DbHandler(context);
                    final Cursor datas = db.getData();
                    datasList = new ArrayList<Integer>();
                    while (datas.moveToNext()) {
                        datasList.add(datas.getInt(0));
                    }

                    boolean add = true;
                    if (datasList.contains(rowIngredient.getId())) {
                        db.deleteIngredient(rowIngredient.getId());
                        add = false;
                        //removeIngredient(position);


                        builder.setTitle(R.string.dialog_title_database);

                        builder.setMessage(R.string.dialog_removed_database);
                        AlertDialog alert = builder.create();

                        alert.show();


                    }
                    if (add) {

                        db.addIngredient(rowIngredient.getId());

                        builder.setTitle(R.string.dialog_title_database);

                        builder.setMessage(R.string.dialog_added_database);
                        AlertDialog alert = builder.create();

                        alert.show();

                         //addIngredient(rowIngredient,datasList.size());
                    }


                }
                return true;
            }

        });


    }

    private void clearIngredients() {
        rowIngredients.clear();
        notifyDataSetChanged();
    }

    private void setIngredients(List<RowIngredient> newIngredientsList) {
        clearIngredients();
        addIngredients(newIngredientsList);
    }

    private void addIngredients(List<RowIngredient> listRowIngredients) {
        rowIngredients.addAll(listRowIngredients);
        notifyDataSetChanged();
    }

    private void addLoader() {
        rowIngredients.add(null);
        notifyItemInserted(rowIngredients.size() - 1);
    }

    private void removeLoader() {
        rowIngredients.remove(rowIngredients.size() - 1);
        notifyItemRemoved(rowIngredients.size());
    }

    public void removeIngredient(int position) {
        if (position >= rowIngredients.size()) return;

        rowIngredients.remove(position);
        notifyItemRemoved(position);
    }

    public void addIngredient(RowIngredient newRowIngredient, int position)
    {
        if (position > rowIngredients.size()) return;

        rowIngredients.add(newRowIngredient);
        notifyItemInserted(position);
    }


    @Override
    public long getItemId(int i) {
        return rowIngredients.indexOf(getItem(i));
    }

    @Override
    public int getItemCount() {
        return this.rowIngredients.size();
    }


    // Private view holder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageStar;
        ImageView imageEmoticon;
        TextView ingredientName;
        TextView ingredientDesc;
        private View parentView;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.parentView = view;
            this.imageStar = (ImageView) view.findViewById(R.id.imageStar);
            this.imageEmoticon = (ImageView) view.findViewById(R.id.imageEmoticon);
            this.ingredientName = (TextView) view.findViewById(R.id.tvName);
            this.ingredientDesc = (TextView) view.findViewById(R.id.tvDesc);
        }


    }

/*
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        //View view = convertView;

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


    }*/
}
