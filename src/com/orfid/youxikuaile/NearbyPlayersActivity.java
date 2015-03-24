package com.orfid.youxikuaile;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.parser.RecommendItemsParser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NearbyPlayersActivity extends Activity {

    private ListView nearbyPlayersLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_nearby_players);
        nearbyPlayersLv = (ListView) findViewById(R.id.lv_nearby_players);
        initView();
        setListener();
        obtainData();
	}

	private void initView() {
		
	}

	private void setListener() {
		
	}

	private void obtainData() {
      try {
			doFindUsersByDistanceAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void doFindUsersByDistanceAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("mod", 0);
        HttpRestClient.post("user/findByDistance", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	
                    } else if (status == 0) {
                        Toast.makeText(NearbyPlayersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
