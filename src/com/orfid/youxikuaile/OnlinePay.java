package com.orfid.youxikuaile;

import java.net.URLEncoder;
import java.util.HashMap;

import com.alipay.android.app.sdk.AliPay;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class OnlinePay {
	private final String returnUrl = Constants.BASE_URL + "useraccount/recharge";
	final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
    HashMap user = dbHandler.getUserDetails();

	public void payMoney(final Context context, String money,
			final Handler onLineHandler, String no) {

		try {
			String info = getNewOrderInfo(context, money, no);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			final String orderInfo = info;

			new Thread() {
				public void run() {
					AliPay alipay = new AliPay((Activity) context,
							onLineHandler);

					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);
					Message msg = new Message();
					msg.what = Constants.RQF_PAY;
					msg.obj = result;
					onLineHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(context, R.string.remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}

	}

	public String getNewOrderInfo(Context context, String num, String number) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(String.valueOf(number) + "-"
				+ user.get("uid").toString());
		sb.append("\"&subject=\"");
		sb.append(context.getResources().getString(
				R.string.hayou_center_remote_call_alipay_tips));
		sb.append("\"&body=\"");
		sb.append(context.getResources().getString(
				R.string.hayou_center_remote_call_alipay_body_top)
				// + String.valueOf(10 * num)
				+ num
				+ context.getResources().getString(
						R.string.hayou_center_remote_call_alipay_body_bottom));
		sb.append("\"&total_fee=\"");
		sb.append(String.valueOf(num));
		// + context.getResources().getString(
		// R.string.hayou_center_money_num));
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		sb.append(URLEncoder.encode(returnUrl));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		sb.append("\"&it_b_pay=\"1m");
		// 如果show_url值为空，可不传

		sb.append("\"");
		Log.i("info", "sb: " + sb.toString());
		return new String(sb);
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
