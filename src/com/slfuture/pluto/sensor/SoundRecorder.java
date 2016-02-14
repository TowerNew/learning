package com.slfuture.pluto.sensor;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.slfuture.carrie.base.etc.Serial;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * 录音机
 */
public class SoundRecorder {
	/**
	 * 保存文件后缀名
	 */
    public static final String FILE_EXTENSION = ".amr";
    
    
	/**
	 * 媒体录制
	 */
	private MediaRecorder recorder = null;
	/**
	 * 是否正在录制
	 */
    private boolean isRecording = false;
    /**
     * 录制开始时间戳
     */
    private long startTime = 0;
    /**
     * 录制停止时间戳
     */
    private long stopTime = 0;
    /**
     * 缓存目录
     */
    private String folder = null;
    /**
     * 当前录制文件
     */
    private File currentFile = null;

    
    /**
     * 构造函数
     * 
     * @param folder 缓存文件目录
     */
    public SoundRecorder(String folder) {
    	this.folder = folder;
    	File file = new File(folder);
    	if(!file.exists()) {
    		file.mkdirs();
    	}
    }

    /**
     * 开始记录到文件
     * 
     * @param context 上下文
     * @return 执行结果
     */
    public boolean start(Context context) {
        try {
        	if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            this.currentFile = new File(folder + Serial.makeSerialString() + FILE_EXTENSION);
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioChannels(1);
            recorder.setAudioSamplingRate(8000);
            recorder.setAudioEncodingBitRate(64);
            recorder.setOutputFile(currentFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
        }
        catch (IOException e) {
            Log.e("pluto", "sound recorder start failed");
            recorder = null;
            return false;
        }
        isRecording = true;
        startTime = new Date().getTime();
        stopTime = 0;
        Log.d("pluto", "start voice recording to file:" + currentFile.getAbsolutePath());
        return true;
    }

    /**
     * 停止录音
     */
    public void discard() {
        if (null == recorder) {
        	return;
        }
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            stopTime = 0;
            if (currentFile != null && currentFile.exists() && !currentFile.isDirectory()) {
                currentFile.delete();
            }
        }
        catch (IllegalStateException e) {
        	
        }
        catch (RuntimeException e) {
        	
        }
        isRecording = false;
    }

    /**
     * 停止录音
     * 
     * @return 录音数据文件
     */
    public File stop() {
        isRecording = false;
        if(null == recorder) {
        	return null;
        }
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
        if(currentFile == null || !currentFile.exists() || !currentFile.isFile()){
            return null;
        }
        if (currentFile.length() == 0) {
            currentFile.delete();
            return null;
        }
        stopTime = new Date().getTime();
        return currentFile;
    }

    /**
     * 资源回收
     */
    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
        recorder = null;
    }

    /**
     * 判断是否在录音
     * 
     * @return 是否在录音
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * 获取当前振幅
     * 
     * @return 当前振幅
     */
    public int getAmplitude() {
    	if(null == recorder) {
    		return 0;
    	}
    	return recorder.getMaxAmplitude() * 13 / 0x7FFF;
    }

    /**
     * 获取时长
     * 
     * @return 录音时长
     */
    public long duration() {
    	if(0 == stopTime) {
    		return new Date().getTime() - startTime;
    	}
    	else {
    		return stopTime - startTime;
    	}
    }
}
