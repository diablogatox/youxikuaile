package com.orfid.youxikuaile.util;

import java.io.BufferedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.widget.GifDecoder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class HttpUtils {
	/**
	 * 发送不包含图片在内的信息 返回布尔值表示
	 * 
	 * @param context
	 * @return
	 */
	public static JSONObject JSONPost(String url, List<NameValuePair> params)
			throws JSONException, ClientProtocolException, IOException {
		JSONObject result = new JSONObject();
		HttpPost httpPost = new HttpPost(url);
		HttpResponse httpResponse = null;
		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		httpResponse = new DefaultHttpClient().execute(httpPost);
		// System.out.println(httpResponse.getStatusLine().getStatusCode());
		Log.i("info", "statusCode: "
				+ httpResponse.getStatusLine().getStatusCode());
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			result.put("status", 0);
			JSONObject error = new JSONObject();
			error.put("info", "statusCode: "
					+ httpResponse.getStatusLine().getStatusCode());
			result.put("error", error);
			String str = EntityUtils.toString(httpResponse.getEntity());
			String start = str.substring(0, str.length() / 2);
			String end = str.substring(str.length() / 2, str.length());
			Log.i("info", start + ":" + start);
			Log.i("info", end + ":" + end);

			return result;
		} else {
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.i("info", retSrc.toString());
			result = new JSONObject(retSrc);
			return result;
		}

	}

	/**
	 * 发送包含图片在内的信息，包含头像的上传，头像不压缩 返回布尔值表示
	 * 
	 * @param context
	 * @return
	 */
	public static JSONObject postUserInfo(String url,
			List<NameValuePair> nameValuePairs) {
		JSONObject result = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int index = 0; index < nameValuePairs.size(); index++) {
				if (nameValuePairs.get(index).getName()
						.equalsIgnoreCase("file")) {
					entity.addPart(nameValuePairs.get(index).getName(),
							new FileBody(new File(nameValuePairs.get(index)
									.getValue()), "image/*"));
				} else {
					entity.addPart(
							nameValuePairs.get(index).getName(),
							new StringBody(nameValuePairs.get(index).getValue()));
				}
			}
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(httpPost, localContext);
			Log.i("info", "response-->" + response.toString());
			Log.i("info", "response code-->"
					+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(response.getEntity());
				Log.i("info", "result: " + strResult);
				result = new JSONObject(strResult);
			} else {
				result.put("result", "null");
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * http发送包含图片在内的信息，压缩图片，并且判断是否为动态图，如果是动态图 则不压缩，直接上传 返回布尔值表示
	 * 
	 * @param context
	 * @return
	 */
	public static JSONObject postImage(String url,
			List<NameValuePair> nameValuePairs, Activity activity) {
		JSONObject result = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		File file = null;

		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int index = 0; index < nameValuePairs.size(); index++) {
				if (nameValuePairs.get(index).getName()
						.equalsIgnoreCase("file")) {

					CompressImage compress = new CompressImage(activity,
							nameValuePairs.get(index).getValue());
					int orientation = compress.readPictureDegree(nameValuePairs
							.get(index).getValue());
					String s = nameValuePairs.get(index).getValue();
					String str = s.substring(s.lastIndexOf("."), s.length());
					Log.i("info", "str:" + str);
					// 获得压缩之后的图片
					Bitmap bitmap;
					try {
						InputStream is = null;
						try {
							is = new BufferedInputStream(
									new FileInputStream(s), 16 * 1024);
							is.mark(16 * 1024);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Movie gif = Movie.decodeStream(is);
						is.close();
						if (gif != null) {

							Log.i("info", "s is gif");
						} else {
							Log.i("info", "s is not gif");

						}

						if (gif == null) {
							String path = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/" + System.currentTimeMillis() + str;
							bitmap = compress.getBitmap();
							Log.i("info", "orientation:" + orientation);
							if (Math.abs(orientation) > 0) {
								bitmap = compress.rotaingImageView(orientation,
										bitmap);// 旋转图片
							}
							// byte[] bytes = compress.Bitmap2Bytes(bitmap);//
							// 生成二进制数据
							// ByteArrayInputStream baisi = new
							// ByteArrayInputStream(
							// bytes);
							// entity.addPart("file", new InputStreamBody(baisi,
							// "image/jpeg", System.currentTimeMillis() + str));

							// 将压缩之后的图片保存到本地
							FileOutputStream fileOutputStream = new FileOutputStream(
									new File(path));
							bitmap.compress(Bitmap.CompressFormat.JPEG, 80,
									fileOutputStream);
							file = new File(path);
							entity.addPart("file", new FileBody(file));
						} else {
							String path = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/" + System.currentTimeMillis() + ".gif";
							GifDecoder decoder = new GifDecoder();
							byte[] giftByte = getByteFile(s);
							int status = decoder.read(giftByte);
							if (status != GifDecoder.STATUS_OK) {
								throw new IOException("read image " + path
										+ " error!");
							}
							file = new File(path);
							if (!file.exists()) {
								file.createNewFile();

							}
							// AnimatedGifEncoder encoder = new
							// AnimatedGifEncoder();
							// FileOutputStream om = new FileOutputStream(path);
							// encoder.start(om);
							// encoder.setRepeat(decoder.getLoopCount());
							//
							// for (int i = 0; i < decoder.getFrameCount(); i++)
							// {
							// decoder.advance();
							// bitmap = decoder.getNextFrame();
							// int delay = decoder.getDelay(i);
							//
							// if (bitmap != null) {
							// bitmap = compressImage(bitmap, 10);
							// }
							// encoder.setDelay(delay);
							// encoder.addFrame(bitmap);
							// }
							// encoder.finish();

							// FileInputStream isT = new FileInputStream(path);
							// int fileLen = isT.available();
							// Log.i("info", "fileLen:" + fileLen / 1024);
							// entity.addPart("file", new FileBody(file));

							// is.close();
							is = new FileInputStream(s);
							int fileLen = is.available();
							Log.i("info", "fileLen:" + fileLen / 1024);
							if (fileLen / 1024 > 1500) {
								result.put("status", 0);
								JSONObject error = new JSONObject();
								error.put("info", "statusCode: " + "图片太大不能上传");
								result.put("error", error);
								is.close();
								return result;
							} else {
								FileOutputStream fos = new FileOutputStream(
										path);
								byte[] buffer = new byte[8192];
								int count = 0;
								while ((count = is.read(buffer)) > 0) {
									fos.write(buffer, 0, count);
								}
								fos.close();
								is.close();
								entity.addPart("file", new FileBody(file));
							}
						}

						// 添加内容

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 生成本来的文件名

				} else {
					entity.addPart(
							nameValuePairs.get(index).getName(),
							new StringBody(nameValuePairs.get(index).getValue()));
				}
			}
			httpPost.setEntity(entity);
			// httpPost.getParams().setParameter("HTTP.CONTENT_TYPE",
			// "image/png");
			HttpResponse response = httpClient.execute(httpPost, localContext);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(response.getEntity());

				Log.i("info", "result: " + strResult);
				if (file != null) {
					if (file.exists()) {
						file.delete();
					}
				}

				result = new JSONObject(strResult);
			} else {
				result.put("status", 0);
				JSONObject error = new JSONObject();
				error.put("info", "statusCode: "
						+ response.getStatusLine().getStatusCode());
				result.put("error", error);
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 判断网络环境十分正常 返回布尔值表示
	 * 
	 * @param context
	 * @return
	 */

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.i("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Log.i("NetWorkState", "Availabel");
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取本地文件字节数组
	 * 
	 * @param context
	 * @return
	 */
	public static byte[] getByteFile(String file) {
		BufferedInputStream in;
		byte[] content = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			in.close();

			content = out.toByteArray();
			return content;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

}
