package book.chapter06.musicplayer;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MainActivity extends Activity implements OnClickListener {
	
    TextView title,author;//获取界面中显示歌曲标题、作者文本框
    ImageButton play,stop;//播放/暂停、停止按钮
    ActivityReceiver activityReceiver;//定义广播接收器
    public static final String CONTROL="com.example.musicplayer.control";//控制播放、暂停
    public static final String UPDATE="com.example.musicplayer.update";//更新界面显示
    int status=0x11;//定义播放状态，0x11：未播放；0x12：正在播放；0x13：暂停
    String[] titleStrs = new String[] { "老男孩", "春天里", "在路上" };//歌曲名
    String[] authorStrs= new String[]{"筷子兄弟","汪峰","刘欢"};//演唱者
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);//指定布局文件
		
		play=(ImageButton)this.findViewById(R.id.play);//获取程序界面中的两个按钮以及两个文本显示框
		stop=(ImageButton)this.findViewById(R.id.stop);
		title=(TextView)findViewById(R.id.title);
		author=(TextView)findViewById(R.id.author);
		
		//为两个按钮添加监听器
		play.setOnClickListener(this);
		stop.setOnClickListener(this);//MainActivity实现了OnClickListener接口
		
		//创建广播接收者对象
		activityReceiver=new ActivityReceiver();
		//创建IntentFilter
		IntentFilter filter=new IntentFilter(UPDATE);
		// 指定BroadcastReceiver监听的Action
		// filter.addAction(UPDATE_ACTION);
		// 注册BroadcastReceiver
		registerReceiver(activityReceiver, filter);
		Intent intent=new Intent(this,MusicService.class);
		startService(intent);//启动后台Service
	}
	
	public void onClick(View source){
		Intent intent=new Intent(CONTROL);//创建Intent
		System.out.println(source.getId());
		System.out.println(source.getId() == R.id.play);
		switch (source.getId()){
		case  R.id.play://按下“播放”/“暂停”按钮
			intent.putExtra("control", 1);
			break;
		case  R.id.stop://按下“停止”按钮
			intent.putExtra("control",2);
			break;
		}
		sendBroadcast(intent);//发送广播，将被service中的广播接收者收到
	}
	
	
	// 自定义的BroadcastReceiver，负责监听从Service传回来的广播
	public class ActivityReceiver extends BroadcastReceiver{
		public void onReceive(Context context,Intent intent){
			int update=intent.getIntExtra("update", -1);//获取Intent中的update消息
			int current =intent.getIntExtra("current",-1);//获取当前播放音乐的序号
			if(current>=0){
				title.setText(authorStrs[current]);//如果current不为-1，则显示正在播放的音乐名和演唱者
				author.setText(authorStrs[current]);

			}
			switch(update){
			case 0x11://未播放状态，显示播放按钮
				play.setImageResource(R.drawable.play);
				status=0x11;
				break;
			case 0x12://播放状态下设置使用暂停图标
				play.setImageResource(R.drawable.pause);
				status=0x12;
				break;
			case 0x13://暂停状态下设置使用播放图标                   
				play.setImageResource(R.drawable.play);
				status=0x13;
				break;
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(activityReceiver);
	}


}

