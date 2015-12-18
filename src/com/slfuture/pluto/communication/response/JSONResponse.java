package com.slfuture.pluto.communication.response;

import android.content.Context;
import android.widget.Toast;

import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.response.core.IResponse;

/**
 * JSON数据反馈类
 */
public abstract class JSONResponse extends CommonResponse<String> {
	/**
	 * 上下文
	 */
	private Context context = null;
	
	
	/**
	 * 构造函数
	 */
	public JSONResponse(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 构造函数
	 * 
	 * @param tag 附属信息
	 */
	public JSONResponse(Context context, Object tag) {
		super(tag);
		this.context = context;
	}
	
	/**
	 * 结束回调
	 * 
	 * @param content 回执内容
	 */
	@Override
	public void onFinished(String content) {
		if(IResponse.CODE_SUCCESS != code()) {
			Toast.makeText(context, "网络错误", Toast.LENGTH_LONG).show();
			onFinished((JSONVisitor) null);
		}
		else if(null == content) {
			Toast.makeText(context, "数据错误", Toast.LENGTH_LONG).show();
			onFinished((JSONVisitor) null);
		}
		JSONVisitor visitor = new JSONVisitor(JSONObject.convert(content));
		if(null != visitor.getString("msg")) {
			Toast.makeText(context, visitor.getString("msg"), Toast.LENGTH_LONG).show();
		}
		onFinished(visitor);
	}

	/**
	 * 结束回调
	 * 
	 * @param content 回执内容
	 */
	public abstract void onFinished(JSONVisitor content);
}
