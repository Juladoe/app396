package com.edusohoapp.plugin.video;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;

public class VideoUtl {
	
	private static int READ_SIZE = 1024 * 1000;
	private static int CACHE_SIZE = 1024 * 500;
	
	private static int CONNECTION_TIMEOUT = 10000;
	private static int SO_TIMEOUT = 10000;
	private HttpClient client;
	private long nowReadSize = 0;
	private String cacheDir;
	private Handler handler;
	private MediaPlayer mPlayer;
	private boolean isCache = true;
	private boolean isStop = false;
	private Context context;
	private SharedPreferences sp;
	
	public VideoUtl(Context _context, String _cacheDir, Handler _handler, MediaPlayer _mPlayer)
	{
		this.context = _context;
		this.mPlayer = _mPlayer;
		this.handler = _handler;
		this.cacheDir = _cacheDir;
		client = new DefaultHttpClient();
		init();
	}
	
	private void init()
	{
		File sdcard = Environment.getExternalStorageDirectory();
		if (sdcard == null) {
			isCache = false;
		}
		File cacheDirFile = new File(sdcard, cacheDir);
		if (!cacheDirFile.exists()) {
			cacheDirFile.mkdirs();
		}
		this.cacheDir = cacheDirFile.getAbsolutePath();
		this.sp = context.getSharedPreferences("edusoho", Context.MODE_PRIVATE);
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
	}
	
	public File getCacheFile(String url) throws IOException
	{
		String cachePath = MD5(url);
		if (cachePath == null) {
			return null;
		}
		
		File cacheFile = new File(cacheDir, cachePath);
		if (!cacheFile.exists()) {
			cacheFile.createNewFile();
		}
		return cacheFile;
	}
	
	public void close()
	{
		isStop = true;
	}
	
	private void setDownloadTotalSize(File cache, long size) throws Exception 
	{
		Editor edit = sp.edit();
		edit.putLong(cache.getName(), size);
		edit.commit();
	}
	
	private long getDownloadTotalSize(File cache) throws Exception
	{
		return sp.getLong(cache.getName(), 0);
	}
	
	public void downLoad(File cache, String url) throws Exception
	{
		nowReadSize = cache.length();
		long totalSize = getDownloadTotalSize(cache);
		if (nowReadSize != 0 && nowReadSize == totalSize) {
			System.out.println("return cache");
			handler.sendEmptyMessage(PlayCore.VIDEO_CACHE_READY);
			return;
		}
		
		HttpGet get = new HttpGet(url);
		get.addHeader("User-Agent", "edusoho-android");
		get.addHeader("RANGE", "bytes=" + nowReadSize + "-");
		InputStream inStream = null;
		RandomAccessFile outStream = null;
		try {		
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity.getContentLength() == -1) {
				return;
			}
			if (nowReadSize == 0) {
				setDownloadTotalSize(cache, entity.getContentLength());
			}
			outStream = new RandomAccessFile(cache, "rw");
			outStream.seek(nowReadSize);
			
			inStream = entity.getContent();
			byte[] buf = new byte[1024 * 4];
			int size = -1;
			long laterReadSize = 0;
			while ((size = inStream.read(buf)) != -1) {
				if (isStop) {
					return;
				}
				outStream.write(buf, 0, size);
				nowReadSize += size;
				if (mPlayer.isPlaying()) {
					if ( (nowReadSize - laterReadSize) > CACHE_SIZE) {
						laterReadSize = nowReadSize;
						handler.sendEmptyMessage(PlayCore.VIDEO_CACHE_UPDATE);
					}
				} else {
					if ( (nowReadSize - laterReadSize) > READ_SIZE) {
						laterReadSize = nowReadSize;
						handler.sendEmptyMessage(PlayCore.VIDEO_CACHE_READY);
					}
				}
			}
			handler.sendEmptyMessage(PlayCore.VIDEO_CACHE_END);
			System.out.println("down finished");
			cache.renameTo(new File(cache.getName() + ".mp4"));
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			outStream.close();
			inStream.close();
			get.abort();
		}
	}
	
	private String MD5(String s) 
	{
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
}
