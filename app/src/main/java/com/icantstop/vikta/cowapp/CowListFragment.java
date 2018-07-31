package com.icantstop.vikta.cowapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 *Класс, отображающий список коров
 */
public class CowListFragment extends Fragment {

    private RecyclerView mCowRecyclerView;
    private CowAdapter mAdapter;
    private Button mButton;
    private TextView mTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cow_list, container, false);


        mCowRecyclerView = view.findViewById(R.id.cow_recycler_view);
        mCowRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mButton = view.findViewById(R.id.cow_empty_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cow cow = new Cow();
                CowLab.get(getActivity()).addCow(cow);
                Intent intent = CowPagerActivity.newIntent(getActivity(), cow.getId());
                startActivity(intent);
            }
        });

        mTextView = view.findViewById(R.id.cow_empty_textView);

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_cow_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_cow:
                Cow cow = new Cow();
                CowLab.get(getActivity()).addCow(cow);
                Intent intent = CowPagerActivity.newIntent(getActivity(), cow.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *Обновление пользовательского интерфейса
     */
    private void updateUI() {
        CowLab cowLab = CowLab.get(getActivity());
        List<Cow> cows = cowLab.getCows();

        checkVisibilty(mButton, null);
        checkVisibilty(null, mTextView);

        if (mAdapter == null) {
            mAdapter = new CowAdapter(cows);
            mCowRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCows(cows);
            mAdapter.notifyDataSetChanged();
        }

    }

    private class CowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTagTextView;
        public TextView mBreedTextView;
        public TextView mColorTextView;
        public TextView mAgeTextView;

        private Cow mCow;

        public CowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTagTextView = itemView.findViewById(R.id.cow_tag_textView);
            mBreedTextView = itemView.findViewById(R.id.cow_breed_textView);
            mColorTextView = itemView.findViewById(R.id.cow_color_textView);
            mAgeTextView = itemView.findViewById(R.id.cow_age_textView);
        }

        public void bindCow(Cow cow) {
            mCow = cow;
            mTagTextView.setText(Integer.toString(mCow.getTagNumber()));
            mBreedTextView.setText(getResources().getString(R.string.cow_breed)+" "+mCow.getBreed());
            mColorTextView.setText(getResources().getString(R.string.cow_color)+" "+mCow.getColor());
            mAgeTextView.setText(getResources().getString(R.string.cow_age)+" "+mCow.getAge());
        }

        @Override
        public void onClick(View view) {
            Intent intent = CowPagerActivity.newIntent(getActivity(), mCow.getId());
            startActivity(intent);
        }
    }

    private class CowAdapter extends RecyclerView.Adapter<CowHolder> {

        private List<Cow> mCows;

        public CowAdapter(List<Cow> cows) {
            mCows = cows;
        }

        @Override
        public CowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_cow, parent, false);
            return new CowHolder(view);
        }

        @Override
        public void onBindViewHolder(CowHolder holder, int position) {
            Cow cow = mCows.get(position);
            holder.bindCow(cow);
        }

        @Override
        public int getItemCount() {
            return mCows.size();
        }

        public void setCows(List<Cow> cows) {
            mCows = cows;
        }
    }

    private void checkVisibilty(Button mButton, TextView mText) {
        int visibilty = isCowsEmpty(CowLab.get(getActivity()));
        if (mButton != null) {
            mButton.setVisibility(visibilty);
        } else {
            if (mText != null) {
                mText.setVisibility(visibilty);
            }
        }
    }

    private int isCowsEmpty(CowLab cowLab) {

        if (cowLab.getCows().size() == 0) {
            return View.VISIBLE;
        }
        return View.GONE;
    }

}
