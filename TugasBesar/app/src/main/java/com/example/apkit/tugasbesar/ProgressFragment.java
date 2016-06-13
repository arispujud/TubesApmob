package com.example.apkit.tugasbesar;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgressFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ListView listView;
    AdapterProgress adapterProgress;
    private ProgressDialog progressDialog;
    SessionManager sessionManager = new SessionManager();
    String webPage=null;
    String postValue=null;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        listView = (ListView) view.findViewById(R.id.list_view_progress);
        adapterProgress = new AdapterProgress(getActivity(), R.layout.list_progress);
        listView.setAdapter(adapterProgress);
        postValue="api_key="+sessionManager.getPreferences(getContext(),"ACCESS_TOKEN");
        prosesGet(UrlConfig.GET_PROGRESS_URL, postValue);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
        void onMessageFragmentInteraction(String string);
    }

    private void prosesGet(String strUrl, final String param){
        final String url = strUrl;
        progressDialog = progressDialog.show(getActivity(),"","Connencting!!");
        new Thread(){
            public void run(){
                InputStream in = null;
                Message msg = Message.obtain();
                msg.what=1;
                try{
                    in = openHttpConnection(url,param);
                    if(in==null){
                        Bundle b = new Bundle();
                        b.putString("str", "Timeout\n");
                        msg.setData(b);
                    }else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder str = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            str.append(line + "\n");
                        }
                        webPage = str.toString();
                        Bundle b = new Bundle();
                        b.putString("str", webPage);
                        msg.setData(b);
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageHandler.sendMessage(msg);
            }
        }.start();
    }

    private InputStream openHttpConnection(String strUrl, String param){
        InputStream in = null;
        int resCode = -1;
        try{
            URL url = new URL(strUrl+"?"+param);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            //httpConn.setDoOutput(true);
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("GET");
            //httpConn.setFixedLengthStreamingMode(param.getBytes().length);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            //out.print(param);
            //out.close();
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
            else{
                in=null;
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    private Handler messageHandler = new Handler(){
        public  void handleMessage(Message msg){
            super.handleMessage(msg);
            final String strData = (String)(msg.getData().getString("str"));
            try{

                JSONArray jsonArray= new JSONObject(strData).getJSONArray("data");
                for(int i=0; i<jsonArray.length(); i++){
                    DetailListOrder detail = new DetailListOrder();
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    detail.setIdOrder(jsonObject.getString("id_order"));
                    detail.setSubject(jsonObject.getString("subject"));
                    detail.setStatus(jsonObject.getString("status"));
                    detail.setProgres(jsonObject.getString("progres"));
                    detail.setHarga(jsonObject.getString("harga"));
                    detail.setTanggal(jsonObject.getString("tgl"));
                    detail.setJam(jsonObject.getString("jam"));
                    detail.setIntProgress(jsonObject.getString("int_progress"));
                    adapterProgress.add(detail);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    };
}
