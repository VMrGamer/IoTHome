package com.vedant.home.fragments.things;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.vedant.home.R;
import com.vedant.home.fragments.things.ThingFragment.OnListFragmentInteractionListener;
import com.vedant.home.fragments.things.Contents.Thing;
import com.vedant.home.helpers.AppData;

import java.util.List;


public class ThingRecyclerViewAdapter extends RecyclerView.Adapter<ThingRecyclerViewAdapter.ViewHolder> {

    private final List<Thing> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ThingRecyclerViewAdapter(List<Thing> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_thing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //mValues.get(position).state;

        if(mValues.get(position).state){
            //holder.mCardView.setCardBackgroundColor(Color.RED);
            if(mValues.get(position).state_change) {
                ValueAnimator colorAnimation = ValueAnimator.ofArgb(
                        ContextCompat.getColor(holder.mView.getContext(),R.color.red),
                        ContextCompat.getColor(holder.mView.getContext(),R.color.green)
                );
                colorAnimation.setDuration(300); // milliseconds
                colorAnimation.setInterpolator(new LinearInterpolator());
                colorAnimation.setRepeatCount(0);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        holder.mCardView.setCardBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
                AppData.STATE_CHANGE.put(Contents.nameToTopicMapper(mValues.get(position).content), false);
            } else {
                //holder.mCardView.setCardBackgroundColor(Color.GREEN);
                holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.green));
            }
        } else {
            //holder.mCardView.setCardBackgroundColor(Color.GREEN);
            if(mValues.get(position).state_change) {
                ValueAnimator colorAnimation = ValueAnimator.ofArgb(
                        ContextCompat.getColor(holder.mView.getContext(),R.color.green),
                        ContextCompat.getColor(holder.mView.getContext(),R.color.red)
                );
                colorAnimation.setDuration(300); // milliseconds
                colorAnimation.setInterpolator(new LinearInterpolator());
                colorAnimation.setRepeatCount(0);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        holder.mCardView.setCardBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
                AppData.STATE_CHANGE.put(Contents.nameToTopicMapper(mValues.get(position).content), false);
            } else {
                //holder.mCardView.setCardBackgroundColor(Color.RED);
                holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.red));
            }
        }
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).content);
        holder.mContentView.setText(mValues.get(position).details);
        if(mValues.get(position).content.contains("light")){
            holder.mImageView.setImageResource(R.drawable.ic_bulb);
        } else if(mValues.get(position).content.contains("fan")){
            holder.mImageView.setImageResource(R.drawable.ic_fan);
        } else if(mValues.get(position).content.contains("address")) {
            holder.mImageView.setImageResource(R.drawable.ic_location_1);
        } else if(mValues.get(position).content.contains("switch")) {
            holder.mImageView.setImageResource(R.drawable.ic_switch_part);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public final CardView mCardView;
        public Thing mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            mImageView = view.findViewById(R.id.imageView);
            mCardView = view.findViewById(R.id.cardView);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
