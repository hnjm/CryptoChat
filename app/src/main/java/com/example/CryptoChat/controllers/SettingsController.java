package com.example.CryptoChat.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.CryptoChat.R;
import com.example.CryptoChat.common.data.exceptions.DuplicatedException;
import com.example.CryptoChat.common.data.models.User;
import com.example.CryptoChat.common.data.provider.SQLiteUserProvider;
import com.example.CryptoChat.services.AuthenticationManager;
import com.example.CryptoChat.services.FirebaseAPIs;
import com.example.CryptoChat.services.KeyValueStore;
import com.example.CryptoChat.utils.DBUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsController.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsController#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsController extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;
    private SQLiteUserProvider cp;

    public SettingsController() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsController.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsController newInstance(String param1, String param2) {
        SettingsController fragment = new SettingsController();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cp = SQLiteUserProvider.getInstance(DBUtils.getDaoSession(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings_controller, container, false);

        return view;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Settings");

        Button asAlice = (Button)getView().findViewById(R.id.debug_as_test_user_1);
        Button asBob = (Button)getView().findViewById(R.id.debug_as_test_user_2);
        Button keyEx = (Button)getView().findViewById(R.id.debug_exchange_keypair);
        Button signout = (Button)getView().findViewById(R.id.debug_signout);

        asAlice.setOnClickListener(view -> {
            User bob = new User("id_bob","Bob", "https://i.imgur.com/R3Jm1CL.png",true);
            try{
                cp.addUser(bob);
                FirebaseAPIs.writeUser(bob);
                cp.deleteUser("id_alice");
            } catch (Exception d) {
                Log.e("SettingsController", "Duplicated when adding bob");
            } finally {
                AuthenticationManager.setUid("id_alice");
                Toast.makeText(getContext(),"You are now Alice", Toast.LENGTH_SHORT).show();
            }

        });

        asBob.setOnClickListener(view -> {
            User alice = new User("id_alice","Alice", "https://i.imgur.com/R3Jm1CL.png",true);
            try{
                cp.addUser(alice);
                cp.deleteUser("id_bob");
            } catch (Exception d) {
                Log.e("SettingsController", "Duplicated when adding alice");
            } finally {
                AuthenticationManager.setUid("id_bob");
                Toast.makeText(getContext(),"You are now Bob", Toast.LENGTH_SHORT).show();
            }
        });

        keyEx.setOnClickListener(view -> {
            NFCKeyExchangeController.open(getContext());

        });

        signout.setOnClickListener(view -> {
            AuthenticationManager.setUid("-1");
            AuthenticationManager.lock();
            Login.open(getContext());
        });


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
}
