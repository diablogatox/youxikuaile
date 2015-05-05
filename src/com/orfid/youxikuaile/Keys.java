/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.orfid.youxikuaile;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	// 合作商户ID，用签约支付宝账号登录www.alipay.com后，在商家服务页面中获取。
	public static final String DEFAULT_PARTNER = "2088701137569957";

	// 商户收款的支付宝账号afdma2011@163.com
	public static final String DEFAULT_SELLER = "2088701137569957";

	// 商户（RSA）私钥

	public static final String PRIVATE ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMcNKdGRbm4SmO4/"+
			"9H6ATpPCzWBUx5vsPZA9e7sxzG20jxCpSHcyeMYlZzd1mXM3PT8aW4Zv5pfPp3o7"+
			"JVU/RMGOIX7uGwSpz1KSvTSrRPfBRpl1hiXir40TxAAMGlEyWdL7Txwvi8vB0TB/"+
			"EWBbpwJFWeBm35OszPcHcINlslllAgMBAAECgYBWLbbLSuG6ukaH8ByUoExk4NQ2"+
			"Pr5lHXTR/CyTsU3GuYOvPPtoz+tGLm9vrXSQU3wq0+iIXe5CD998phrVu5yyRFH6"+
			"bYfLh+Q4F//QP6g/iCIbj7Tr+tjYs5uYb8YcJS1xrw3DroD04xJirJoVN1f2rHjX"+
			"xLurNyRD6/zIPCLNiQJBAPBsT32SHXgRo8AJaF24+Fxc8DjUpya6/djIKFV54Drq"+
			"0DVHTzuJdKwkrw1yWp+4mVhKA+qbnlJ/DDjtold84nsCQQDT8qglGyCwAHwGeS/J"+
			"vnU4+igcaDNKm/IzkJEJvFIwUG9zxQvx2ycRRwrq20sVw+DdecgLa0c7c9+nHQPN"+
			"IV2fAkAwecY1TWa/tN7Nc5glfYwmI18UNlxVhNJ2CTWItoAtoPbGJ1Ckcnyh0Ouy"+
			"zGHVXoUcth/ACAyOGau/NrrK5RVNAkEAhFoqkAlRrzaOPIiew++wSeVE+QBEz1l0"+
			"tKiZOagk9tCz2Gp7HAvLaKmhcqmASNYp7IPo3OCf+ctRJY0j24vzZwJAdipAUnj8"+
			"Woz5oHANogF+suxUVGtzRTrIl692DVDJvX3GCZrtSdw0t4/U46+a0DnlEECGhaXt"+
			"egSdfvUEITz7Rg==";

	// 支付宝（RSA）公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
