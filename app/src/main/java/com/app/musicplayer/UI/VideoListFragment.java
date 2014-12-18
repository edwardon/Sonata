package com.app.musicplayer.UI;

import android.app.Fragment;

/**
 * Created by Edward Onochie on 14/11/14.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.app.musicplayer.Custom.Swipe.BaseSwipeListViewListener;
import com.app.musicplayer.Custom.Swipe.SwipeListView;
import com.app.musicplayer.Custom.VideoListAdapter;
import com.app.musicplayer.R;
import com.app.musicplayer.Util.Auth;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;


public class VideoListFragment extends Fragment {
    private static final long NUMBER_OF_VIDEOS_RETURNED = 20;
    private static YouTube youtube;
    private VideoListAdapter videoAdapter;
    final ArrayList<com.app.musicplayer.Custom.Objects.Video> searchArray = new ArrayList<com.app.musicplayer.Custom.Objects.Video>();
    private SwipeListView swipeListView;
    private String queryTerm;
    MyActivity myActivity;
    public VideoListFragment(){
        myActivity = (MyActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.video_list_fragment, container, false);

        swipeListView = (SwipeListView) rootView.findViewById(R.id.video_list_view);
       /* ImageButton playButton = (ImageButton) rootView.findViewById(R.id.play_button);
        ImageButton pauseButton = (ImageButton) rootView.findViewById(R.id.pause_button);
        pauseButton.setEnabled(false);
        pauseButton.setVisibility(View.INVISIBLE); */
        videoAdapter = new VideoListAdapter(getActivity(),R.layout.package_row,searchArray);
        swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                mode.setTitle("Selected (" + swipeListView.getCountSelected() + ")");
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                swipeListView.unselectedChoiceStates();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }
        });

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
            }

            @Override
            public void onClickBackView(int position) {

                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    //data.remove(position);
                }
                videoAdapter.notifyDataSetChanged();
            }
        });

        swipeListView.setAdapter(videoAdapter);
        swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        if (getArguments() == null) {
            queryTerm = "thisnameisafail prototype";
        }
        else {
            queryTerm = getArguments().getString("query");
        }
        new getSearchQuery().execute();
        return rootView;
    }
    private class getSearchQuery extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            loadData();
            return null;
        }
    }
    private void loadData() {
        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            System.out.println("PREPARE TO PRIN");
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("clean-yew-765").build();

            // Prompt the user to enter a query term.
            //queryTerm = "thisnameisafail prototype";

            // Define the API request for retrieving search results.
            YouTube.Search.List search =youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = getActivity().getString(R.string.youtube_data_api_key);
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {

                prettyPrint(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {


        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                // Get the Image URL
                String imageStr = "http://img.youtube.com/vi/" + rId.getVideoId() + "/0.jpg";
                Bitmap image = null;
                // Actually load the image.
                try {
                    URL imageUrl = new URL(imageStr);
                    image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                searchArray.add(new com.app.musicplayer.Custom.Objects.Video(rId.getVideoId(),singleVideo.getSnippet().getTitle(),image));

                System.out.println(" Video Id: " + rId.getVideoId());


                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }

        getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            videoAdapter.notifyDataSetChanged();
                                            videoAdapter = new VideoListAdapter(getActivity(),R.layout.package_row,searchArray);
                                            videoAdapter.notifyDataSetChanged();
                                            swipeListView.setAdapter(videoAdapter);
                                        }
                                    });

        System.out.println("SWIPELISTVIEW");
    }

}