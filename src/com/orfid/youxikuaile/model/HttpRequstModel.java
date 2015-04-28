package com.orfid.youxikuaile.model;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.util.HttpUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HttpRequstModel {
	// private ExecutorService executorService;
	private ExecutorService executorService1;

	public HttpRequstModel() {
		super();
		// TODO Auto-generated constructor stub
		int pool_size = 12;
		// executorService = Executors.newCachedThreadPool();
		int cpuNums = Runtime.getRuntime().availableProcessors();
		executorService1 = Executors.newFixedThreadPool(pool_size);
	}

	public boolean setRqquestHandler(Context context, final Handler handler,
			final List<NameValuePair> params, final int what, final String url) {
		boolean isOk = HttpUtils.isNetworkAvailable(context);
		if (isOk) {
			// new Thread() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// super.run();
			// JSONObject jott;
			// try {
			// Log.i("info", "url: " + url);
			// jott = HttpUtils.JSONPost(url, params);
			// Message ms = new Message();
			// ms.obj = jott;
			// ms.what = what;
			//
			// handler.sendMessage(ms);
			// } catch (ClientProtocolException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// }
			// }.start();

			// while (true) {

			executorService1.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					JSONObject jott;
					try {
						Log.i("info", "url: " + url);
						jott = HttpUtils.JSONPost(url, params);
						Message ms = new Message();
						ms.obj = jott;
						ms.what = what;

						handler.sendMessage(ms);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});// 将实例加入线程池
				// }
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.hayou_center_chongzhi_net_erro),
					Toast.LENGTH_LONG).show();
		}
		return isOk;
	}

	public void setRqquestMedia(Context context, final Handler handler,
			final List<NameValuePair> params, final int what, final String url) {
		boolean isOk = HttpUtils.isNetworkAvailable(context);
		if (isOk) {
			// new Thread() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// super.run();
			// JSONObject jott;
			// jott = HttpUtils.postUserInfo(url, params);
			// Message ms = new Message();
			// ms.obj = jott;
			// ms.what = what;
			// handler.sendMessage(ms);
			//
			// }
			// }.start();
			// while (true) {

			executorService1.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					JSONObject jott;

					jott = HttpUtils.postUserInfo(url, params);
					Message ms = new Message();
					ms.obj = jott;
					ms.what = what;
					handler.sendMessage(ms);

				}
			});// 将实例加入线程池
				// }
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.hayou_center_chongzhi_net_erro),
					Toast.LENGTH_LONG).show();
		}

	}

	public boolean setRqquestData(Context context, final Handler handler,
			final List<NameValuePair> params, final int what, final String url) {
		boolean isOk = HttpUtils.isNetworkAvailable(context);
		if (isOk) {
			// new Thread() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// super.run();
			// JSONObject jott;
			// jott = HttpUtils.postUserInfo(url, params);
			// Message ms = new Message();
			// ms.obj = jott;
			// ms.what = what;
			// handler.sendMessage(ms);
			// }
			// }.start();
			// while (true) {

			executorService1.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					JSONObject jott;
					jott = HttpUtils.postUserInfo(url, params);
					Message ms = new Message();
					ms.obj = jott;
					ms.what = what;
					handler.sendMessage(ms);

				}
			});// 将实例加入线程池
				// }
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.hayou_center_chongzhi_net_erro),
					Toast.LENGTH_LONG).show();
		}
		return isOk;
	}

	public void setRqquestImage(Context context, final Handler handler,
			final List<NameValuePair> params, final int what, final String url,
			final Activity activity) {
		boolean isOk = HttpUtils.isNetworkAvailable(context);
		if (isOk) {
			// new Thread() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// super.run();
			// JSONObject jott;
			// jott = HttpUtils.postImage(url, params, activity);
			// Message ms = new Message();
			// ms.obj = jott;
			// ms.what = what;
			// handler.sendMessage(ms);
			// }
			// }.start();
			// while (true) {

			executorService1.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					JSONObject jott;
					jott = HttpUtils.postImage(url, params, activity);
					Message ms = new Message();
					ms.obj = jott;
					ms.what = what;
					handler.sendMessage(ms);

				}
			});// 将实例加入线程池
				// }
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.hayou_center_chongzhi_net_erro),
					Toast.LENGTH_LONG).show();
		}

	}

}
