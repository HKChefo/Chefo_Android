package com.example.user.chefo.chef_profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.chefo.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link photo_upload.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link photo_upload#newInstance} factory method to
 * create an instance of this fragment.
 */
public class photo_upload extends Fragment{
    final String TAG="photo_upload_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private android.hardware.Camera camera;
    private Button takingbut;
    private SurfaceView surfaceView;
    private ImageView imageView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment photo_upload.
     */
    // TODO: Rename and change types and number of parameters
    public static photo_upload newInstance(String param1, String param2) {
        photo_upload fragment = new photo_upload();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public photo_upload() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_photo_upload, container, false);
        takingbut= (Button) v.findViewById(R.id.phototakingbut);
        surfaceView= (SurfaceView) v.findViewById(R.id.surfaceView);
        imageView= (ImageView) v.findViewById(R.id.imageView);
        if(camera==null){
            try{
                camera= android.hardware.Camera.open();
                takingbut.setEnabled(true);
                Log.i(TAG,"setup the camera");

            }
            catch(Exception e){
                takingbut.setEnabled(false);
                Toast.makeText(getActivity(),"Camera not available",Toast.LENGTH_LONG).show();
                Log.i(TAG,e.getMessage());
            }
        }
        takingbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera == null) {
                    Log.i(TAG, "cannot detect camera from onClick");
                    return;
                }

                camera.takePicture(new android.hardware.Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                }, null, new android.hardware.Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                        Log.i(TAG, "entering scalephoto");
                        scalePhoto(data);
                    }
                });
            }
        });

        SurfaceHolder holder=surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    Log.i(TAG, "surface created callback");
                    if (camera != null) {
                        Log.i(TAG, "camera is not null setting camera");
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        return v;
    }

    private void scalePhoto(byte[] data){
        Log.i(TAG,"enter scalephoto");
        //resize the phto
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable=true;

        Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length,options);
        Bitmap scaledphoto=Bitmap.createScaledBitmap(photo, 200, 200 * photo.getHeight() / photo.getWidth(), false);
        Log.i(TAG,"start scaling");
        //change to portrait
        Matrix matrix=new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedscaledphoto = Bitmap.createBitmap(scaledphoto,0,0,scaledphoto.getWidth(),scaledphoto.getHeight(),matrix,true);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        rotatedscaledphoto.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] scaleddata=bos.toByteArray();
        Log.i(TAG,"finished scaling");
        imageView.setImageBitmap(rotatedscaledphoto);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
