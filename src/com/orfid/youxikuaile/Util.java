package com.orfid.youxikuaile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

public class Util {

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}


	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}


	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}


	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static String showGender(String sex) {
        String gender = null;
        switch (Integer.parseInt(sex)) {
            case 0:
                break;
            case 1:
                gender = "男";
                break;
            case 2:
                gender = "女";
                break;
            default:
                break;
        }
        return gender;
    }

    public static int getAgeByTimestamp (long timestamp) {
        if (timestamp == 0) return 0;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int _year = cal.get(Calendar.YEAR);
        int _month = cal.get(Calendar.MONTH);
        int _day = cal.get(Calendar.DAY_OF_MONTH);

        GregorianCalendar cal2 = new GregorianCalendar();
        int y, m, d, a;

        y = cal2.get(Calendar.YEAR);
        m = cal2.get(Calendar.MONTH);
        d = cal2.get(Calendar.DAY_OF_MONTH);
        cal2.set(_year, _month, _day);
        a = y - cal2.get(Calendar.YEAR);
        if ((m < cal2.get(Calendar.MONTH))
                || ((m == cal2.get(Calendar.MONTH)) && (d < cal2
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            a = 0;
//                throw new IllegalArgumentException("Age < 0");
        return a;
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String covertTimestampToDate(long timeStamp){

        String dateStr = null;
        try{
            DateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
            Date netDate = (new Date(timeStamp));
            dateStr = sdf.format(netDate);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return dateStr;
    }

    public static SpannableStringBuilder handlerFaceInContent(Context context, final TextView gifTextView,String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring("#[face/png/f_static_".length(), tempText.length()- ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = context.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,new AnimatedGifDrawable.UpdateListener() {
                            @Override
                            public void update() {
                                gifTextView.postInvalidate();
                            }
                        })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(),tempText.length() - "]#".length());
                try {
                    sb.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open(png))), m.start(), m.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }
    
    public static String changeImgWidth(String htmlContent){
        Document doc_Dis = Jsoup.parse(htmlContent);
        Elements ele_Img = doc_Dis.getElementsByTag("img");
        if (ele_Img.size() != 0){
            for (Element e_Img : ele_Img) {
                e_Img.attr("style", "width:100%");
            }
        }
        String newHtmlContent=doc_Dis.toString();
        return newHtmlContent;
    }
}
