package com.example.venkat.connectr1.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venkat.connectr1.activities.R;
import com.example.venkat.connectr1.adapters.Adapter_MediaRecyclerView;
import com.example.venkat.connectr1.json.BooksInfoFromJSON;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by venkat on 7/27/2015.
 */
public class Fragment_Books extends Fragment {
    BooksInfoFromJSON bookData;
    Adapter_MediaRecyclerView media_adapter;
    private OnListItemSelectedListener mediaItemSelectListner;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        bookData = new BooksInfoFromJSON();
        // here equivalent step to load data from localjson is missing.
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_media, container, false);
        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.media_cardlist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        media_adapter = new Adapter_MediaRecyclerView(bookData.getBooksList(), getActivity());
        MyJSONDownloadAsync mediaJSONDownloadAsync = new MyJSONDownloadAsync(media_adapter);
        String url = BooksInfoFromJSON.BOOKSJSON_API;
        mediaJSONDownloadAsync.execute(new String[]{url});
        recyclerView.setAdapter(media_adapter);

        media_adapter.setOnItemClickListner(new Adapter_MediaRecyclerView.OnItemClickListner() {
            @Override
            public void onItemClick(View view, int position) {
                HashMap<String, ?> media_Item = (HashMap<String, ?>) media_adapter.getItem(position);
                mediaItemSelectListner.OnListMediaItemSelected(position, media_Item);
            }
        });
        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mediaItemSelectListner = (OnListItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnFragment interaction listner");
        }
    }

    public interface OnListItemSelectedListener {
        void OnListMediaItemSelected(int position, HashMap<String, ?> movie);
    }

    public static Fragment_Books newInstance() {
        return new Fragment_Books();
    }

    private class MyJSONDownloadAsync extends AsyncTask<String, Void, BooksInfoFromJSON> {

        private final WeakReference<Adapter_MediaRecyclerView> media_Adapter;

        public MyJSONDownloadAsync(Adapter_MediaRecyclerView media_adapter) {
            this.media_Adapter = new WeakReference<Adapter_MediaRecyclerView>(media_adapter);
        }


        @Override
        protected BooksInfoFromJSON doInBackground(String... urls) {
            BooksInfoFromJSON booksDataJSON = new BooksInfoFromJSON();
            for (String url : urls) {
                booksDataJSON.dowloadBooksinfoJSON(url);

            }
            return booksDataJSON;
        }

        @Override
        protected void onPostExecute(BooksInfoFromJSON booksInfoFromJSON) {
            bookData.booksList.clear();
            for (int i = 0; i < booksInfoFromJSON.getSize(); i++) {
                bookData.booksList.add(booksInfoFromJSON.booksList.get(i));
            }
            final Adapter_MediaRecyclerView curr_Media_Adap = media_Adapter.get();
            if (curr_Media_Adap != null) {
                curr_Media_Adap.notifyDataSetChanged();
            }

        }
    }
}

