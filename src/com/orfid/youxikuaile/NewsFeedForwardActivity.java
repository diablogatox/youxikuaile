package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.adapter.FaceGVAdapter;
import com.orfid.youxikuaile.adapter.FaceVPAdapter;

public class NewsFeedForwardActivity extends Activity implements OnClickListener {

	private TextView originalContentTv;
	private ImageButton backBtn;
	private ImageView photoIv, faceIv;
	private ProgressDialog pDialog;
	private EditText contentEt;
	private Button saveBtn;
	private View faceWrapperView;
	private ViewPager faceViewPager;
	private long feedId;
	private int position;
	private String photo;
	private List<String> staticFacesList;
	private List<View> views = new ArrayList<View>();
	private LinearLayout mDotsLayout;
	private int columns = 6;
    private int rows = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed_forward);
		initView();
		initStaticFaces();
		setListener();
		obtainData();
	}

	private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void obtainData() {
		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		position = intent.getIntExtra("position", 0);
		feedId = intent.getLongExtra("feedId", 0);
		photo = intent.getStringExtra("photo");
		
		SpannableStringBuilder sb = Utils.handlerFaceInContent(this, originalContentTv,
                content);
		originalContentTv.setText(sb);
		if (photo != null) {
			ImageLoader.getInstance().displayImage(photo, photoIv);
		}
		
		initViewPager();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		faceIv.setOnClickListener(this);
	}

	private void initView() {
		originalContentTv = (TextView) findViewById(R.id.original_content_tv);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		contentEt = (EditText) findViewById(R.id.content_et);
		saveBtn = (Button) findViewById(R.id.save_btn);
		photoIv = (ImageView) findViewById(R.id.photo_iv);
		faceIv = (ImageView) findViewById(R.id.newsfeedpublish_face);
		faceWrapperView = findViewById(R.id.rl_expression);
		faceViewPager = (ViewPager) findViewById(R.id.face_viewpager);
		mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.save_btn:
				if (contentEt.getText().toString().trim().equals("")) {
					Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
				} else {
					try {
						doFeedPublishAction();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case R.id.newsfeedpublish_face:
				if (!faceWrapperView.isShown()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) this.getApplicationContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(contentEt.getWindowToken(), 0);
                    faceWrapperView.setVisibility(View.VISIBLE);
                } else {
                    faceWrapperView.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager =(InputMethodManager) this.getApplicationContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
		        
				break;
		}
	}
	
	private void doFeedPublishAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("pid", feedId);
        params.put("type", 1);
        params.put("text", contentEt.getText().toString().trim());
        pDialog = new ProgressDialog(this);
        HttpRestClient.post("feed/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	Intent intent = new Intent();
                    	intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedForwardActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
	
	private void initViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        faceViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }
	
	private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }
	
	private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }
	
	private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }
	
	private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }
	
	 private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((contentEt.getText()));
        int iCursorEnd = Selection.getSelectionEnd((contentEt.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) contentEt.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((contentEt.getText()));
        ((Editable) contentEt.getText()).insert(iCursor, text);
    }
	 
	 private void delete() {
        if (contentEt.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(contentEt.getText());
            int iCursorStart = Selection.getSelectionStart(contentEt.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable) contentEt.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) contentEt.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) contentEt.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }
	 
	 private boolean isDeletePng(int cursor) {
	        String st = "#[face/png/f_static_000.png]#";
	        String content = contentEt.getText().toString().substring(0, cursor);
	        if (content.length() >= st.length()) {
	            String checkStr = content.substring(content.length() - st.length(),
	                    content.length());
	            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
	            Pattern p = Pattern.compile(regex);
	            Matcher m = p.matcher(checkStr);
	            return m.matches();
	        }
	        return false;
	    }

}
