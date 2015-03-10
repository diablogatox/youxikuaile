package com.orfid.youxikuaile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import com.orfid.youxikuaile.pojo.FeedItem;
import com.orfid.youxikuaile.pojo.UserItem;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.pojo.FeedAttachmentImgItem;
import com.orfid.youxikuaile.widget.StaggeredGridView;

/**
 * Created by Administrator on 2015/3/3.
 */
public class NewsFeedPublishActivity extends Activity implements View.OnClickListener {

    private ImageButton backBtn;
    private Button saveBtn;
    private EditText contentEt;
    private TextView mCount;
    private StaggeredGridView feedImgAttachmentGv;
    private ImageView feedOptionsImgIv;
    private MyAdapter adapter;
    ArrayList imageItems = new ArrayList();
//    List<InputStream> fileList = new ArrayList<InputStream>();
    List<Integer> fileIds = new ArrayList<Integer>();
    List<FeedAttachmentImgItem> fileItems = new ArrayList<FeedAttachmentImgItem>();
    ProgressDialog pDialog;

    private static final int PHOTO_PICKER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_publish);
        init();
    }

    private void init() {
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        contentEt = (EditText) findViewById(R.id.et_content);
        mCount = (TextView) findViewById(R.id.newsfeedpublish_count);
        saveBtn = (Button) findViewById(R.id.btn_save);
        feedImgAttachmentGv = (StaggeredGridView) findViewById(R.id.gv_feed_attachment_img);
        feedOptionsImgIv = (ImageView) findViewById(R.id.newsfeedpublish_img);

        backBtn.setOnClickListener(this);
        feedOptionsImgIv.setOnClickListener(this);

        adapter = new MyAdapter(this, R.layout.gridview_item,
                imageItems);
        feedImgAttachmentGv.setAdapter(adapter);

        contentEt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = s.length();
                mCount.setText(String.valueOf(number));
                selectionStart = contentEt.getSelectionStart();
                selectionEnd = mCount.getSelectionEnd();
                if (temp.length() > 140) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    contentEt.setText(s);
                    contentEt.setSelection(tempSelection);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentEt.getText().toString().trim().length() == 0) {
                    Toast.makeText(NewsFeedPublishActivity.this, "内容不能为空", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    try {
                        doFeedPublishAction();
//                        setResult(RESULT_OK, null);// TODO
//                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (contentEt.getText().toString().trim().length() > 0) {
                    backDialog();
                } else {
                    finish();
                }
                break;
            case R.id.newsfeedpublish_img:
                Intent i1 = new Intent(this, PhotoPickerActivity.class);
                startActivityForResult(i1, PHOTO_PICKER);
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PHOTO_PICKER:
                // add to gridview
                Bitmap photo = data.getExtras().getParcelable("data");
                InputStream photoInputStream = Util.bitmap2InputStream(photo, 100);
//                fileList.add(photoInputStream);
                imageItems.add(new FeedAttachmentImgItem(photo));
                adapter.notifyDataSetChanged();
                // upload this photo
                doUploadImageAction(photoInputStream);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (contentEt.getText().toString().trim().length() > 0) {
                backDialog();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("您编辑的内容将被清空");
        builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder .create().show();
    }

//    private void doFeedPublishAction() throws JSONException {
//
//    	if (fileList.size() > 0) { // 有照片时先上传
//    		Log.d("fileList size > 0 ====> ", "true");
//    		final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
//    		HashMap user = dbHandler.getUserDetails();
//    		RequestParams params = new RequestParams();
//    		params.put("token", user.get("token").toString());
//            for (int i=0; i< fileList.size(); i++) {
//                params.put("files["+i+"]", fileList.get(i), "image_"+System.currentTimeMillis()+".png");
//            }
//            pDialog = new ProgressDialog(this);
//    		HttpRestClient.post("media/upload", params, new JsonHttpResponseHandler() {
//				@Override
//				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//				      Log.d("response=======>", response.toString());
//				      try {
//				    	  int status = response.getInt("status");
//				    	  if (status == 1) { // success
//                              JSONObject data = response.getJSONObject("data");
//                              JSONArray fileArr = data.getJSONArray("files");
//                              if (fileArr.length() > 0) {
//                                  for (int i=0; i<fileArr.length(); i++) {
//                                      JSONObject file = fileArr.getJSONObject(i);
//                                      fileIds.add(file.getInt("id"));
//                                  }
//                              }
//                              // 发布动态
//                              doRealPublishAction();
//				    	  } else if (status == 0) {
//				    		  Toast.makeText(NewsFeedPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
//				    	  }
//			          } catch (JSONException e) {
//			              e.printStackTrace();
//			          }
//				}
//
//                @Override
//                public void onStart() {
//                    if (pDialog.isShowing() == false) {
//                        pDialog.setTitle("请稍等...");
//                        pDialog.show();
//                    }
//                }
//
//            });
//
//	    } else {
//            // 发布动态
//            doRealPublishAction();
//	    }
//    }

    private void doFeedPublishAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("pid", 0);
        params.put("type", 0);
        params.put("text", contentEt.getText().toString().trim());
        params.put("files", fileIds);
        pDialog = new ProgressDialog(this);
        HttpRestClient.post("feed/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        JSONObject data = response.getJSONObject("data");
                        JSONObject user = data.getJSONObject("user");
                        JSONArray files = data.getJSONArray("files");
                        if (files.length() > 0) {
                            for (int i=0; i<files.length(); i++) {
                                JSONObject jFile = files.getJSONObject(i);
                                fileItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
                            }
                        }
                        de.greenrobot.event.EventBus.getDefault().postSticky(new FeedItem(data.getInt("feedid"),
                                new UserItem(user.getString("uid"), null, null, user.getString("username"), user.getString("photo"), null),
                                data.getString("text"), data.getInt("commentcount"), data.getInt("forwardcount"),
                                data.getInt("praisecount"), data.getString("publishtime"), data.getInt("type"), fileItems));
                        setResult(RESULT_OK);
                        finish();
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                if (pDialog.isShowing() == false) {
                    pDialog.setTitle("请稍等...");
                    pDialog.show();
                }
            }

            @Override
            public void onFinish() {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }

        });
    }

    private void doUploadImageAction(InputStream photoInputStream) {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("files[]", photoInputStream, "image_"+System.currentTimeMillis()+".png");
        HttpRestClient.post("media/upload", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        JSONObject data = response.getJSONObject("data");
                        JSONArray fileArr = data.getJSONArray("files");
                        if (fileArr.length() > 0) {
                            for (int i=0; i<fileArr.length(); i++) {
                                JSONObject file = fileArr.getJSONObject(i);
                                fileIds.add(file.getInt("id"));
                            }
                        }
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class MyAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList data = new ArrayList();

        public MyAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) row.findViewById(R.id.iv_attachment);
                holder.delete = (ImageView) row.findViewById(R.id.iv_delete);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            final FeedAttachmentImgItem item = (FeedAttachmentImgItem) data.get(position);
            holder.image.setImageBitmap(item.getImage());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageItems.remove(item);
                    adapter.notifyDataSetChanged();
                }
            });
            return row;
        }

        class ViewHolder {
            ImageView image;
            ImageView delete;
        }
    }

}
