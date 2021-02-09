package com.example.adwindow.adwindow_client;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adwindow.adwindow_client.adapter.ContentRecyclerAdapter;
import com.example.adwindow.adwindow_client.model.Content;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContentOngoing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContentOngoing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentOngoing extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private RecyclerView recyclerView;
    private ContentRecyclerAdapter contentRecyclerAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContentOngoing() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ContentOngoing.
     */
    // TODO: Rename and change types and number of parameters
    public static ContentOngoing newInstance(String param1) {
        ContentOngoing fragment = new ContentOngoing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_ongoing, container, false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            fetchContentForCurrentUser(view, mParam1, container.getContext());
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void fetchContentForCurrentUser(View view,String userUid, Context context)
    {
        DatabaseReference adReference = databaseReference.child("Advertisements").child(userUid);
        recyclerView = view.findViewById(R.id.contentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<Content> options = new FirebaseRecyclerOptions.Builder<Content>()
                .setQuery(adReference, Content.class)
                .build();

        contentRecyclerAdapter = new ContentRecyclerAdapter(context, options, (ProgressBar) view.findViewById(R.id.pgbar), (TextView) view.findViewById(R.id.emptyListText));
        recyclerView.setAdapter(contentRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        contentRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        contentRecyclerAdapter.stopListening();
    }
}
