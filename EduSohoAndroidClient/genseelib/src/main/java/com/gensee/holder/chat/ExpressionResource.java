package com.gensee.holder.chat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.gensee.chat.gif.GifDrawalbe;
import com.gensee.chat.gif.SpanResource;
import com.gensee.rtmpresourcelib.R;
import com.gensee.view.MyTextViewEx;

public class ExpressionResource {
	
	//有app所在平台的自定义表情需要处理时，外部初始化调用
	public static void setImageGetter(ImageGetter imageGetter){
		SpanResource.setExtraImageGetter(new ImageGetter() {
			
			@Override
			public Drawable getDrawable(String source) {
				Log.d("ExpressionResource", " getDrawable " + source);
				return null;
			}
		});
		
	}
	
	public static void initExpressionResource(Context context) {
		DisplayMetrics metric = new DisplayMetrics();  
		metric = context.getResources().getDisplayMetrics();    
		GifDrawalbe.ration = metric.density;
		initTextTipList(context);
		initTextMap(context);
		initUIMap(context);
		initBrowMap(context);
		initSendRichMap(context);
	}

	/*
	 * 初始化需要文字提示的表情
	 */
	private static void initTextTipList(Context context) {

		List<String> textTipList = new ArrayList<String>();
		textTipList.add(getString(context, R.string.brow_tkl_cn));
		textTipList.add(getString(context, R.string.brow_tml_cn));
		textTipList.add(getString(context, R.string.brow_zt_cn));
		textTipList.add(getString(context, R.string.brow_fd_cn));
		textTipList.add(getString(context, R.string.brow_gz_cn));
		textTipList.add(getString(context, R.string.brow_zdsk_cn));
		SpanResource.initTextTipList(textTipList);
	}

	private static void putBrowValue(Map<String, Integer> map, Context context,
			int str, int drawble) {
		map.put(getString(context, str), drawble);
	}
	
	private static void putTextValue(Map<String, String> map, Context context,
			int str, int resId) {
		map.put(getString(context, str), getString(context, resId));
	}

	private static void initTextMap(Context context) {
		HashMap<String, String> textMap = new HashMap<String, String>(18);
		putTextValue(textMap, context, R.string.brow_nh_cn,
				R.string.brow_nh_cn_text);
		putTextValue(textMap, context, R.string.brow_zj_cn,
				R.string.brow_zj_cn_text);
		putTextValue(textMap, context, R.string.brow_gx_cn,
				R.string.brow_gx_cn_text);
		putTextValue(textMap, context, R.string.brow_sx_cn,
				R.string.brow_sx_cn_text);
		putTextValue(textMap, context, R.string.brow_fn_cn,
				R.string.brow_fn_cn_text);
		putTextValue(textMap, context, R.string.brow_wl_cn,
				R.string.brow_wl_cn_text);
		putTextValue(textMap, context, R.string.brow_lh_cn,
				R.string.brow_lh_cn_text);
		putTextValue(textMap, context, R.string.brow_yw_cn,
				R.string.brow_yw_cn_text);
		putTextValue(textMap, context, R.string.brow_bs_cn,
				R.string.brow_bs_cn_text);
		putTextValue(textMap, context, R.string.brow_xh_cn,
				R.string.brow_xh_cn_text);
		putTextValue(textMap, context, R.string.brow_dx_cn,
				R.string.brow_dx_cn_text);
		putTextValue(textMap, context, R.string.brow_lw_cn,
				R.string.brow_lw_cn_text);
		putTextValue(textMap, context, R.string.brow_tkl_cn,
				R.string.brow_tkl_cn_text);
		putTextValue(textMap, context, R.string.brow_tml_cn,
				R.string.brow_tml_cn_text);
		putTextValue(textMap, context, R.string.brow_zt_cn,
				R.string.brow_zt_cn_text);
		putTextValue(textMap, context, R.string.brow_fd_cn,
				R.string.brow_fd_cn_text);
		putTextValue(textMap, context, R.string.brow_gz_cn,
				R.string.brow_gz_cn_text);
		putTextValue(textMap, context, R.string.brow_zdsk_cn,
				R.string.brow_zdsk_cn_text);
		
		putTextValue(textMap, context, R.string.emotion_bz_cn,
				R.string.emotion_bz_cn_text);
		putTextValue(textMap, context, R.string.emotion_fd_cn,
				R.string.emotion_fd_cn_text);
		putTextValue(textMap, context, R.string.emotion_gg_cn,
				R.string.emotion_gg_cn_text);
		putTextValue(textMap, context, R.string.emotion_gz_cn,
				R.string.emotion_gz_cn_text);
		putTextValue(textMap, context, R.string.emotion_hx_cn,
				R.string.emotion_hx_cn_text);
		putTextValue(textMap, context, R.string.emotion_jk_cn,
				R.string.emotion_jk_cn_text);
		putTextValue(textMap, context, R.string.emotion_jy_cn,
				R.string.emotion_jy_cn_text);
		putTextValue(textMap, context, R.string.emotion_kb_cn,
				R.string.emotion_kb_cn_text);
		putTextValue(textMap, context, R.string.emotion_kl_cn,
				R.string.emotion_kl_cn_text);
		putTextValue(textMap, context, R.string.emotion_ll_cn,
				R.string.emotion_ll_cn_text);
		putTextValue(textMap, context, R.string.emotion_qd_cn,
				R.string.emotion_qd_cn_text);
		putTextValue(textMap, context, R.string.emotion_qh_cn,
				R.string.emotion_qh_cn_text);
		putTextValue(textMap, context, R.string.emotion_qq_cn,
				R.string.emotion_qq_cn_text);
		putTextValue(textMap, context, R.string.emotion_rb_cn,
				R.string.emotion_rb_cn_text);
		putTextValue(textMap, context, R.string.emotion_se_cn,
				R.string.emotion_se_cn_text);
		putTextValue(textMap, context, R.string.emotion_tx_cn,
				R.string.emotion_tx_cn_text);
		putTextValue(textMap, context, R.string.emotion_xu_cn,
				R.string.emotion_xu_cn_text);
		putTextValue(textMap, context, R.string.emotion_yun_cn,
				R.string.emotion_yun_cn_text);
		SpanResource.initTextMap(textMap);
	}

	/*
	 * 为了在聊天表情列表中显示，有些设备直接显示gif图有问题
	 */
	private static void initUIMap(Context context) {
		long nStartTime = Calendar.getInstance().getTimeInMillis();
		HashMap<String, Integer> uiMap = new LinkedHashMap<String, Integer>(18);
		putBrowValue(uiMap, context, R.string.brow_nh_cn, R.drawable.brow_nh);
		putBrowValue(uiMap, context, R.string.brow_zj_cn, R.drawable.brow_zj);
		putBrowValue(uiMap, context, R.string.brow_gx_cn, R.drawable.brow_gx);
		putBrowValue(uiMap, context, R.string.brow_sx_cn, R.drawable.brow_sx);
		putBrowValue(uiMap, context, R.string.brow_fn_cn, R.drawable.brow_fn);
		putBrowValue(uiMap, context, R.string.brow_wl_cn, R.drawable.brow_wl);
		putBrowValue(uiMap, context, R.string.brow_lh_cn, R.drawable.brow_lh);
		putBrowValue(uiMap, context, R.string.brow_yw_cn, R.drawable.brow_yw);
		putBrowValue(uiMap, context, R.string.brow_bs_cn, R.drawable.brow_bs);
		putBrowValue(uiMap, context, R.string.brow_xh_cn, R.drawable.brow_xh);
		putBrowValue(uiMap, context, R.string.brow_dx_cn, R.drawable.brow_dx);
		putBrowValue(uiMap, context, R.string.brow_lw_cn, R.drawable.brow_lw);
		putBrowValue(uiMap, context, R.string.brow_tkl_cn, R.drawable.brow_tkl);
		putBrowValue(uiMap, context, R.string.brow_tml_cn, R.drawable.brow_tml);
		putBrowValue(uiMap, context, R.string.brow_zt_cn, R.drawable.brow_zt);
		putBrowValue(uiMap, context, R.string.brow_fd_cn, R.drawable.brow_fd);
		putBrowValue(uiMap, context, R.string.brow_gz_cn, R.drawable.brow_gz);
		putBrowValue(uiMap, context, R.string.brow_zdsk_cn,
				R.drawable.brow_zdsk);

		putBrowValue(uiMap, context, R.string.emotion_bz_cn,
				R.drawable.emotion_bz);
		putBrowValue(uiMap, context, R.string.emotion_fd_cn,
				R.drawable.emotion_fd);
		putBrowValue(uiMap, context, R.string.emotion_gg_cn,
				R.drawable.emotion_gg);
		putBrowValue(uiMap, context, R.string.emotion_gz_cn,
				R.drawable.emotion_gz);
		putBrowValue(uiMap, context, R.string.emotion_hx_cn,
				R.drawable.emotion_hx);
		putBrowValue(uiMap, context, R.string.emotion_jk_cn,
				R.drawable.emotion_jk);
		putBrowValue(uiMap, context, R.string.emotion_jy_cn,
				R.drawable.emotion_jy);
		putBrowValue(uiMap, context, R.string.emotion_kb_cn,
				R.drawable.emotion_kb);
		putBrowValue(uiMap, context, R.string.emotion_kl_cn,
				R.drawable.emotion_kl);
		putBrowValue(uiMap, context, R.string.emotion_ll_cn,
				R.drawable.emotion_ll);
		putBrowValue(uiMap, context, R.string.emotion_qd_cn,
				R.drawable.emotion_qd);
		putBrowValue(uiMap, context, R.string.emotion_qh_cn,
				R.drawable.emotion_qh);
		putBrowValue(uiMap, context, R.string.emotion_qq_cn,
				R.drawable.emotion_qq);
		putBrowValue(uiMap, context, R.string.emotion_rb_cn,
				R.drawable.emotion_rb);
		putBrowValue(uiMap, context, R.string.emotion_se_cn,
				R.drawable.emotion_se);
		putBrowValue(uiMap, context, R.string.emotion_tx_cn,
				R.drawable.emotion_tx);
		putBrowValue(uiMap, context, R.string.emotion_xu_cn,
				R.drawable.emotion_xu);
		putBrowValue(uiMap, context, R.string.emotion_yun_cn,
				R.drawable.emotion_yun);

		HashMap<String, Drawable> drawableMap = new LinkedHashMap<String, Drawable>(
				18);
		Iterator<String> iter = uiMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String tempKey = key.substring(1, key.length() - 1);
			if (tempKey.endsWith(".gif")) {
				GifDrawalbe gifDrawalbe = new GifDrawalbe(context,
						uiMap.get(key));
				gifDrawalbe.readFrames(false);
				MyTextViewEx.putGifDrawableCache(uiMap.get(key), gifDrawalbe);
				Drawable drawable = gifDrawalbe.getFrame(0);

				Drawable bitmapDrawable = zoomDrawable(context, drawable,
						(int)(drawable.getIntrinsicWidth() * GifDrawalbe.ration),
						(int)(drawable.getIntrinsicHeight() * GifDrawalbe.ration));
				drawableMap.put(key, bitmapDrawable);
			} else {
				drawableMap.put(key,
						context.getResources().getDrawable(uiMap.get(key)));
			}
		}

		long nEndTime = Calendar.getInstance().getTimeInMillis();
		Log.i("initUIMap", "initUIMap time long = " + (nEndTime - nStartTime));
		SpanResource.initUiMap(drawableMap);
	}

	private static Bitmap drawableToBitmap(Context context, Drawable drawable) // drawable 转换成bitmap
	{
		int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取drawable的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把drawable内容画到画布中
		return bitmap;
	}

	private static Drawable zoomDrawable(Context context, Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(context, drawable); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = (float)GifDrawalbe.ration;//((float) w / width); // 计算缩放比例
		float scaleHeight = (float)GifDrawalbe.ration;//((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		newbmp.getWidth();
		newbmp.getHeight();
		return new BitmapDrawable(context.getResources(), newbmp); // 把bitmap转换成drawable并返回
	}


	/*
	 * 用于接收聊天消息，展示对应的表情
	 */
	private static void initBrowMap(Context context) {
		LinkedHashMap<String, Integer> browMap = new LinkedHashMap<String, Integer>(
				18);
		putBrowValue(browMap, context, R.string.brow_nh_cn, R.drawable.brow_nh);
		putBrowValue(browMap, context, R.string.brow_zj_cn, R.drawable.brow_zj);
		putBrowValue(browMap, context, R.string.brow_gx_cn, R.drawable.brow_gx);
		putBrowValue(browMap, context, R.string.brow_sx_cn, R.drawable.brow_sx);
		putBrowValue(browMap, context, R.string.brow_fn_cn, R.drawable.brow_fn);
		putBrowValue(browMap, context, R.string.brow_wl_cn, R.drawable.brow_wl);
		putBrowValue(browMap, context, R.string.brow_lh_cn, R.drawable.brow_lh);
		putBrowValue(browMap, context, R.string.brow_yw_cn, R.drawable.brow_yw);
		putBrowValue(browMap, context, R.string.brow_bs_cn, R.drawable.brow_bs);
		putBrowValue(browMap, context, R.string.brow_xh_cn, R.drawable.brow_xh);
		putBrowValue(browMap, context, R.string.brow_dx_cn, R.drawable.brow_dx);
		putBrowValue(browMap, context, R.string.brow_lw_cn, R.drawable.brow_lw);
		putBrowValue(browMap, context, R.string.brow_tkl_cn,
				R.drawable.brow_tkl);
		putBrowValue(browMap, context, R.string.brow_tml_cn,
				R.drawable.brow_tml);
		putBrowValue(browMap, context, R.string.brow_zt_cn, R.drawable.brow_zt);
		putBrowValue(browMap, context, R.string.brow_fd_cn, R.drawable.brow_fd);
		putBrowValue(browMap, context, R.string.brow_gz_cn, R.drawable.brow_gz);
		putBrowValue(browMap, context, R.string.brow_zdsk_cn,
				R.drawable.brow_zdsk);
		
		putBrowValue(browMap, context, R.string.emotion_bz_cn,
				R.drawable.emotion_bz);
		putBrowValue(browMap, context, R.string.emotion_fd_cn,
				R.drawable.emotion_fd);
		putBrowValue(browMap, context, R.string.emotion_gg_cn,
				R.drawable.emotion_gg);
		putBrowValue(browMap, context, R.string.emotion_gz_cn,
				R.drawable.emotion_gz);
		putBrowValue(browMap, context, R.string.emotion_hx_cn,
				R.drawable.emotion_hx);
		putBrowValue(browMap, context, R.string.emotion_jk_cn,
				R.drawable.emotion_jk);
		putBrowValue(browMap, context, R.string.emotion_jy_cn,
				R.drawable.emotion_jy);
		putBrowValue(browMap, context, R.string.emotion_kb_cn,
				R.drawable.emotion_kb);
		putBrowValue(browMap, context, R.string.emotion_kl_cn,
				R.drawable.emotion_kl);
		putBrowValue(browMap, context, R.string.emotion_ll_cn,
				R.drawable.emotion_ll);
		putBrowValue(browMap, context, R.string.emotion_qd_cn,
				R.drawable.emotion_qd);
		putBrowValue(browMap, context, R.string.emotion_qh_cn,
				R.drawable.emotion_qh);
		putBrowValue(browMap, context, R.string.emotion_qq_cn,
				R.drawable.emotion_qq);
		putBrowValue(browMap, context, R.string.emotion_rb_cn,
				R.drawable.emotion_rb);
		putBrowValue(browMap, context, R.string.emotion_se_cn,
				R.drawable.emotion_se);
		putBrowValue(browMap, context, R.string.emotion_tx_cn,
				R.drawable.emotion_tx);
		putBrowValue(browMap, context, R.string.emotion_xu_cn,
				R.drawable.emotion_xu);
		putBrowValue(browMap, context, R.string.emotion_yun_cn,
				R.drawable.emotion_yun);
		SpanResource.initBrowSource(browMap);
	}

	/*
	 * 用于发送聊天信息的表情替换
	 */
	private static void initSendRichMap(Context context) {
		HashMap<String, String> richSendMap = new HashMap<String, String>(18);
		richSendMap.put(getString(context, R.string.brow_nh_cn),
				"emotion.smile.gif");
		richSendMap.put(getString(context, R.string.brow_zj_cn),
				"emotion.goodbye.gif");
		richSendMap.put(getString(context, R.string.brow_gx_cn),
				"emotion.laugh.gif");
		richSendMap.put(getString(context, R.string.brow_sx_cn),
				"emotion.cry.gif");
		richSendMap.put(getString(context, R.string.brow_fn_cn),
				"emotion.angerly.gif");
		richSendMap.put(getString(context, R.string.brow_wl_cn),
				"emotion.nod.gif");
		richSendMap.put(getString(context, R.string.brow_lh_cn),
				"emotion.lh.gif");
		richSendMap.put(getString(context, R.string.brow_yw_cn),
				"emotion.question.gif");
		richSendMap.put(getString(context, R.string.brow_bs_cn),
				"emotion.bs.gif");
		richSendMap.put(getString(context, R.string.brow_xh_cn), "rose.up.png");
		richSendMap.put(getString(context, R.string.brow_dx_cn),
				"rose.down.png");
		richSendMap.put(getString(context, R.string.brow_lw_cn),
				"chat.gift.png");
		richSendMap.put(getString(context, R.string.brow_tkl_cn),
				"feedback.quickly.png");
		richSendMap.put(getString(context, R.string.brow_tml_cn),
				"feedback.slowly.png");
		richSendMap.put(getString(context, R.string.brow_zt_cn),
				"feedback.agreed.png");
		richSendMap.put(getString(context, R.string.brow_fd_cn),
				"feedback.against.gif");
		richSendMap.put(getString(context, R.string.brow_gz_cn),
				"feedback.applaud.png");
		richSendMap.put(getString(context, R.string.brow_zdsk_cn),
				"feedback.think.png");
		
		richSendMap.put(getString(context, R.string.emotion_bz_cn),
				"emotion.bz.gif");
		richSendMap.put(getString(context, R.string.emotion_fd_cn),
				"emotion.fd.gif");
		richSendMap.put(getString(context, R.string.emotion_gg_cn),
				"emotion.gg.gif");
		richSendMap.put(getString(context, R.string.emotion_gz_cn),
				"emotion.gz.gif");
		richSendMap.put(getString(context, R.string.emotion_hx_cn),
				"emotion.hx.gif");
		richSendMap.put(getString(context, R.string.emotion_jk_cn),
				"emotion.jk.gif");
		richSendMap.put(getString(context, R.string.emotion_jy_cn),
				"emotion.jy.gif");
		richSendMap.put(getString(context, R.string.emotion_kb_cn),
				"emotion.kb.gif");
		richSendMap.put(getString(context, R.string.emotion_kl_cn),
				"emotion.kl.gif");
		richSendMap.put(getString(context, R.string.emotion_ll_cn),
				"emotion.ll.gif");
		richSendMap.put(getString(context, R.string.emotion_qd_cn),
				"emotion.qd.gif");
		richSendMap.put(getString(context, R.string.emotion_qh_cn),
				"emotion.qh.gif");
		richSendMap.put(getString(context, R.string.emotion_qq_cn),
				"emotion.qq.gif");
		richSendMap.put(getString(context, R.string.emotion_rb_cn),
				"emotion.rb.gif");
		richSendMap.put(getString(context, R.string.emotion_se_cn),
				"emotion.se.gif");
		richSendMap.put(getString(context, R.string.emotion_tx_cn),
				"emotion.tx.gif");
		richSendMap.put(getString(context, R.string.emotion_xu_cn),
				"emotion.xu.gif");
		richSendMap.put(getString(context, R.string.emotion_yun_cn),
				"emotion.yun.gif");
		SpanResource.initSendRichMap(richSendMap);
	}

	private static String getString(Context context, int resId) {
		return context.getString(resId);
	}
}
