package com.orfid.youxikuaile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.model.Attach;
import com.orfid.youxikuaile.model.Expressions;

public class StringUtil {

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String StringFilter(String str) throws PatternSyntaxException {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static boolean isPicture(String img) {
		if (img.equals("") || img == null) {
			return false;
		}
		// 获得文件后缀名
		String tmpName = img.substring(img.lastIndexOf(".") + 1, img.length());
		// 声明图片后缀名数组
		String imgeArray[][] = { { "bmp", "0" }, { "dib", "1" },
				{ "gif", "2" }, { "jfif", "3" }, { "jpe", "4" },
				{ "jpeg", "5" }, { "jpg", "6" }, { "png", "7" },
				{ "tif", "8" }, { "tiff", "9" }, { "ico", "10" } };
		// 遍历名称数组
		for (int i = 0; i < imgeArray.length; i++) {
			// 判断单个类型文件的场合
			if (imgeArray[i][0].equals(tmpName.toLowerCase())) {
				return true;
			}
			// 判断符合全部类型的场合
			if (imgeArray[i][0].equals(tmpName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static int isExpression(String str) {
		if (str.equals("") || str == null) {
			return -1;
		}
		for (int i = 0; i < Expressions.ImgNames.length; i++) {
			if (str.equals(Expressions.ImgNames[i])) {
				return i;
			}
		}

		return -1;

	}

	public static String getThumbUploadPath(String oldPath, int bitmapMaxWidth)
			throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(oldPath, options);
		int height = options.outHeight;
		int width = options.outWidth;
		int reqHeight = 0;
		int reqWidth = bitmapMaxWidth;
		reqHeight = (reqWidth * height) / width;
		// 在内存中创建bitmap对象，这个对象按照缩放大小创建的
		options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth,
				reqHeight);
		// System.out.println("calculateInSampleSize(options, 480, 800);==="
		// + calculateInSampleSize(options, 480, 800));
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
		// Log.e("asdasdas", "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);
		Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap,
				bitmapMaxWidth, reqHeight, false));
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		// .format(new Date());
		return saveImg(bbb, oldPath);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			options -= 10;// 每次都减少10
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static String saveImg(Bitmap b, String name) throws Exception {
		// String path = Environment.getExternalStorageDirectory().getPath()
		// + File.separator + "test/headImg/";
		// File mediaFile = new File(path + File.separator + name + ".jpg");
		// if (mediaFile.exists()) {
		// mediaFile.delete();
		//
		// }
		// if (!new File(path).exists()) {
		// new File(path).mkdirs();
		// }
		// mediaFile.createNewFile();
		File mediaFile = new File(name);
		FileOutputStream fos = new FileOutputStream(mediaFile);
		b.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.flush();
		fos.close();
		b.recycle();
		b = null;
		System.gc();
		return mediaFile.getPath();
	}

	

	public static void changeContent(Context context, final Handler handler,
			TextView view, String content, final int what) {
		if (content == null || content.equals("")) {
			return;
		}
		int APIVersion;
		APIVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		int wd, ht;
		if (APIVersion >= 11) {
			wd = context.getResources().getDimensionPixelSize(
					R.dimen.hayou_tiezi_express_width);
			ht = context.getResources().getDimensionPixelSize(
					R.dimen.hayou_tiezi_express_width);
		} else {
			wd = 2 * context.getResources().getDimensionPixelSize(
					R.dimen.hayou_luntan_padding_letf_right);
			ht = 2 * context.getResources().getDimensionPixelSize(
					R.dimen.hayou_luntan_padding_letf_right);
		}
		Bitmap bitmap = null;
		view.setText("");
		// Log.i("info", "indexOf(@): " + content.indexOf("@"));
		if (content.indexOf("@") >= 0) {
			String[] str = content.split("@");
			if (str.length > 0) {
				if (content.indexOf("@") == 0) {
					view.append("@");
				} else {
					view.append(str[0]);
				}

				for (int i = 1; i < str.length; i++) {
					int index = str[i].indexOf(" ");
					// Log.i("info", "index: " + index);
					String sb, eb;
					if (index > 0) {
						sb = str[i].substring(0, index);
						eb = str[i].substring(index, str[i].length());
						SpannableString ss = new SpannableString("@" + sb);
						ss.setSpan(new ForegroundColorSpan(0x0a8cd2), 0,
								index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						final String name = sb;
						ss.setSpan(new ClickableSpan() {

							@Override
							public void onClick(View widget) {
								// TODO Auto-generated method stub

								Message mes = new Message();
								mes.what = what;
								mes.obj = name;

								handler.sendMessage(mes);
							}
						}, 0, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						view.append(ss);
						if (eb.indexOf("[") >= 0 && eb.indexOf("]") >= 0) {
							String[] sst = eb.split("\\[");
							if (sst.length > 0) {
								if (eb.indexOf("[") == 0) {
									view.append("[");
								} else {
									view.append(sst[0]);
								}

								for (int j = 1; j < sst.length; j++) {
									int in = sst[j].indexOf("]");

									String sn, sr;
									if (in > 0) {
										sn = sst[j].substring(0, in);
										// Log.i("info", "sn::" + sn);
										List<String> tempList = Arrays
												.asList(Expressions.expressionImgName);
										if (tempList.contains(sn)) {

											for (int arg = 0; arg < Expressions.expressionImgName.length; arg++) {
												String s = Expressions.expressionImgName[arg];
												if (s.equals(sn)) {

													bitmap = BitmapFactory
															.decodeResource(
																	context.getResources(),
																	Expressions.expressionImgs1[arg]);
													bitmap = ThumbnailUtils
															.extractThumbnail(
																	bitmap, wd,
																	ht);
													ImageSpan imageSpan = new ImageSpan(
															context, bitmap);

													SpannableString spannableString = new SpannableString(
															Expressions.expressionImgName[arg]);
													spannableString
															.setSpan(
																	imageSpan,
																	0,
																	Expressions.expressionImgName[arg]
																			.length(),
																	Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
													view.append(spannableString);
													bitmap = null;
													imageSpan = null;
													break;
												}
											}
										} else {
											view.append("[" + sn + "]");
										}
										sr = sst[j].substring(in + 1,
												sst[j].length());
										view.append(sr);
									}
								}
								if (eb.lastIndexOf("[") == eb.length() - 1) {
									view.append("[");
								}
							} else {
								view.append(eb);
							}
						} else {
							view.append(eb);
						}

					} else {
						view.append(str[i]);
					}

				}
				if (content.lastIndexOf("@") == content.length() - 1) {
					view.append("@");
				}
				view.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				view.append(content);
			}

		} else {
			// Log.i("info", "indexOf([): " + content.indexOf("["));
			// Log.i("info", "indexOf(]): " + content.indexOf("]"));
			if (content.indexOf("[") >= 0 && content.indexOf("]") >= 0) {

				String[] sst = content.split("\\[");
				if (sst.length > 0) {
					if (content.indexOf("[") == 0) {
						view.append("[");
					} else {
						view.append(sst[0]);
					}

					for (int j = 1; j < sst.length; j++) {
						int in = sst[j].indexOf("]");

						String sn, sr;
						if (in > 0) {
							sn = sst[j].substring(0, in);
							List<String> tempList = Arrays
									.asList(Expressions.expressionImgName);
							if (tempList.contains(sn)) {
								for (int arg = 0; arg < Expressions.expressionImgName.length; arg++) {
									String s = Expressions.expressionImgName[arg];
									if (s.equals(sn)) {

										bitmap = BitmapFactory
												.decodeResource(
														context.getResources(),
														Expressions.expressionImgs1[arg]);
										bitmap = ThumbnailUtils
												.extractThumbnail(bitmap, wd,
														ht);
										ImageSpan imageSpan = new ImageSpan(
												context, bitmap);

										SpannableString spannableString = new SpannableString(
												Expressions.expressionImgName[arg]);
										spannableString
												.setSpan(
														imageSpan,
														0,
														Expressions.expressionImgName[arg]
																.length(),
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
										view.append(spannableString);
										bitmap = null;
										break;
									}
								}
							} else {
								view.append("[" + sn + "]");
							}
							sr = sst[j].substring(in + 1, sst[j].length());
							view.append(sr);
						}
					}
					if (content.lastIndexOf("[") == content.length() - 1) {
						view.append("[");
					}
				} else {
					view.setText(content);
				}
			} else {
				// Log.i("info", "content: " + content);
				view.setText(content);
			}
		}
		bitmap = null;
	}

	public static void setContentText(Context context, TextView tv,
			String content) {
		if (content.indexOf("[") >= 0 && content.indexOf("]") >= 0) {
			String[] sst = content.split("\\[");
			if (sst.length > 0) {
				if (content.indexOf("[") == 0) {
					tv.append("[");
				} else {
					tv.append(sst[0]);
				}

				for (int j = 1; j < sst.length; j++) {
					int in = sst[j].indexOf("]");
					String sn, sr;
					if (in > 0) {
						sn = sst[j].substring(0, in);
						List<String> tempList = Arrays
								.asList(Expressions.expressionImgName);
						if (tempList.contains(sn)) {
							for (int arg = 0; arg < Expressions.expressionImgName.length; arg++) {
								String s = Expressions.expressionImgName[arg];
								if (s.equals(sn)) {
									Bitmap bitmap = null;
									bitmap = BitmapFactory.decodeResource(
											context.getResources(),
											Expressions.expressionImgs1[arg]);
									bitmap = ThumbnailUtils
											.extractThumbnail(
													bitmap,
													context.getResources()
															.getDimensionPixelSize(
																	R.dimen.hayou_tiezi_express_width),
													context.getResources()
															.getDimensionPixelSize(
																	R.dimen.hayou_tiezi_express_width));
									ImageSpan imageSpan = new ImageSpan(
											context, bitmap);

									SpannableString spannableString = new SpannableString(
											Expressions.expressionImgName[arg]);
									spannableString.setSpan(imageSpan, 0,
											Expressions.expressionImgName[arg]
													.length(),
											Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									tv.append(spannableString);
									bitmap = null;
									break;
								}
							}
						} else {
							tv.append("[" + sn + "]");
						}
						sr = sst[j].substring(in + 1, sst[j].length());
						tv.append(sr);
					}
				}
				if (content.lastIndexOf("[") == content.length() - 1) {
					tv.append("[");
				}
			} else {
				tv.append(content);
			}
		} else {
			tv.append(content);
		}
	}

	public static void setContenteEdit(Context context, EditText tv,
			String content) {
		if (content.indexOf("[") >= 0 && content.indexOf("]") >= 0) {
			String[] sst = content.split("\\[");
			if (sst.length > 0) {
				if (content.indexOf("[") == 0) {
					tv.append("[");
				} else {
					tv.append(sst[0]);
				}

				for (int j = 1; j < sst.length; j++) {
					int in = sst[j].indexOf("]");
					String sn, sr;
					if (in > 0) {
						sn = sst[j].substring(0, in);
						List<String> tempList = Arrays
								.asList(Expressions.expressionImgName);
						if (tempList.contains(sn)) {
							for (int arg = 0; arg < Expressions.expressionImgName.length; arg++) {
								String s = Expressions.expressionImgName[arg];
								if (s.equals(sn)) {
									Bitmap bitmap = null;
									bitmap = BitmapFactory.decodeResource(
											context.getResources(),
											Expressions.expressionImgs1[arg]);
									bitmap = ThumbnailUtils
											.extractThumbnail(
													bitmap,
													context.getResources()
															.getDimensionPixelSize(
																	R.dimen.hayou_tiezi_express_width),
													context.getResources()
															.getDimensionPixelSize(
																	R.dimen.hayou_tiezi_express_width));
									ImageSpan imageSpan = new ImageSpan(
											context, bitmap);

									SpannableString spannableString = new SpannableString(
											Expressions.expressionImgName[arg]);
									spannableString.setSpan(imageSpan, 0,
											Expressions.expressionImgName[arg]
													.length(),
											Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									tv.append(spannableString);
									bitmap = null;
									break;
								}
							}
						} else {
							tv.append("[" + sn + "]");
						}
						sr = sst[j].substring(in + 1, sst[j].length());
						tv.append(sr);
					}
				}
				if (content.lastIndexOf("[") == content.length() - 1) {
					tv.append("[");
				}
			} else {
				tv.append(content);
			}
		} else {
			tv.append(content);
		}
	}

	public static void playMusic(String name) {
		MediaPlayer mMediaPlayer = new MediaPlayer();

		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();

			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void changeContentData(Context context, TextView view,
			String content) {
		if (content == null || content.equals("")) {
			return;
		}
		view.setText("");
		// Log.i("info", "indexOf(@): " + content.indexOf("@"));
		if (content.indexOf("@") >= 0) {
			String[] str = content.split("@");
			if (str.length > 0) {
				if (content.indexOf("@") == 0) {
					view.append("@");
				} else {
					view.append(str[0]);
				}

				for (int i = 1; i < str.length; i++) {
					int index = str[i].indexOf(" ");
					// Log.i("info", "index: " + index);
					String sb, eb;
					if (index > 0) {
						sb = str[i].substring(0, index);
						eb = str[i].substring(index, str[i].length());
						// SpannableString ss = new SpannableString("@" + sb);
						// ss.setSpan(new ForegroundColorSpan(0x0a8cd2), 0,
						// index +
						// 1,
						// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						// final String name = sb;
						// ss.setSpan(new ClickableSpan() {
						//
						// @Override
						// public void onClick(View widget) {
						// // TODO Auto-generated method stub
						// // Log.i("info", "click " + name);
						// // Log.i("info", "click " + what);
						// // if (handler == null) {
						// // Log.i("info", "handler==null");
						// // }
						// Message mes = new Message();
						// mes.what = what;
						// mes.obj = name;
						//
						// handler.sendMessage(mes);
						// }
						// }, 0, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						view.append(sb);
						if (eb.indexOf("[") >= 0 && eb.indexOf("]") >= 0) {
							String[] sst = eb.split("\\[");
							if (sst.length > 0) {
								if (eb.indexOf("[") == 0) {
									view.append("[");
								} else {
									view.append(sst[0]);
								}

								for (int j = 1; j < sst.length; j++) {
									int in = sst[j].indexOf("]");
									String sn, sr;
									if (in > 0) {
										sn = sst[j].substring(0, in);
										// Log.i("info", "sn::" + sn);
										List<String> tempList = Arrays
												.asList(Expressions.expressionImgName);
										if (tempList.contains(sn)) {
											for (int arg = 0; arg < Expressions.expressionImgName.length; arg++) {
												String s = Expressions.expressionImgName[arg];
												if (s.equals(sn)) {
													Bitmap bitmap = null;
													bitmap = BitmapFactory
															.decodeResource(
																	context.getResources(),
																	Expressions.expressionImgs1[arg]);
													bitmap = ThumbnailUtils
															.extractThumbnail(
																	bitmap,
																	context.getResources()
																			.getDimensionPixelSize(
																					R.dimen.hayou_tiezi_express_width),
																	context.getResources()
																			.getDimensionPixelSize(
																					R.dimen.hayou_tiezi_express_width));
													ImageSpan imageSpan = new ImageSpan(
															context, bitmap);

													SpannableString spannableString = new SpannableString(
															Expressions.expressionImgName[arg]);
													spannableString
															.setSpan(
																	imageSpan,
																	0,
																	Expressions.expressionImgName[arg]
																			.length(),
																	Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
													view.append(spannableString);
													bitmap = null;
													break;
												}
											}
										} else {
											view.append("[" + sn + "]");
										}
										sr = sst[j].substring(in + 1,
												sst[j].length());
										view.append(sr);
									}
								}
								if (eb.lastIndexOf("[") == eb.length() - 1) {
									view.append("[");
								}
							} else {
								view.append(eb);
							}
						} else {
							view.append(eb);
						}

					} else {
						view.append(str[i]);
					}

				}
				if (content.lastIndexOf("@") == content.length() - 1) {
					view.append("@");
				}
				view.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				view.setText(content);
			}

		} else {
			// Log.i("info", "indexOf([): " + content.indexOf("["));
			// Log.i("info", "indexOf(]): " + content.indexOf("]"));
			if (content.indexOf("[") >= 0 && content.indexOf("]") >= 0) {
				String[] sst = content.split("\\[");
				if (sst.length > 0) {
					if (content.indexOf("[") == 0) {
						view.append("[");
					} else {
						view.append(sst[0]);
					}

					for (int j = 1; j < sst.length; j++) {
						int in = sst[j].indexOf("]");
						String sn, sr;
						if (in > 0) {
							sn = sst[j].substring(0, in);
							List<String> tempList = Arrays
									.asList(Expressions.expressionImgName);
							if (tempList.contains(sn)) {
								for (int arg = 0; arg < Expressions.expressionImgName.length; arg++) {
									String s = Expressions.expressionImgName[arg];
									if (s.equals(sn)) {
										Bitmap bitmap = null;
										bitmap = BitmapFactory
												.decodeResource(
														context.getResources(),
														Expressions.expressionImgs1[arg]);
										bitmap = ThumbnailUtils
												.extractThumbnail(
														bitmap,
														context.getResources()
																.getDimensionPixelSize(
																		R.dimen.hayou_tiezi_express_width),
														context.getResources()
																.getDimensionPixelSize(
																		R.dimen.hayou_tiezi_express_width));
										ImageSpan imageSpan = new ImageSpan(
												context, bitmap);

										SpannableString spannableString = new SpannableString(
												Expressions.expressionImgName[arg]);
										spannableString
												.setSpan(
														imageSpan,
														0,
														Expressions.expressionImgName[arg]
																.length(),
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
										view.append(spannableString);
										bitmap = null;
										break;
									}
								}
							} else {
								view.append("[" + sn + "]");
							}
							sr = sst[j].substring(in + 1, sst[j].length());
							view.append(sr);
						}
					}
					if (content.indexOf("[") == content.length() - 1) {
						view.append("[");
					}
				} else {
					view.setText(content);
				}
			} else {
				// Log.i("info", "content: " + content);
				view.setText(content);
			}
		}
	}

}