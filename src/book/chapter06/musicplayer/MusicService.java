package book.chapter06.musicplayer;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class MusicService extends Service {
	
	//声明广播接收者
	ServiceReceiver serviceReceiver;
	AssetManager am;//资源管理器
	String[]musics=new String[]{"oldboy.mp3","spring.mp3","way.mp3"};//定义几首歌曲
	MediaPlayer mPlayer;
	int status=0x11;//当前的状态，0x11：未播放；0x12：正在播放；0x13：暂停
	int current=0;//记录当前正在播放的音乐的序号

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		am=getAssets();//调用context里的方法
		serviceReceiver=new ServiceReceiver();//创建广播接收者对象
		IntentFilter filter=new IntentFilter(MainActivity.CONTROL);//创建IntentFilter
		registerReceiver(serviceReceiver,filter);//注册广播接收者
		mPlayer=new MediaPlayer();//创建媒体播放器
		super.onCreate();
		
		mPlayer.setOnCompletionListener(new OnCompletionListener(){
			public void onCompletion(MediaPlayer mp){
				current++;
				if(current>=3){
					current=0;//判断是否超出范围，如果超出，又从第一首开始
				}
				Intent sendIntent=new Intent(MainActivity.UPDATE);
				sendIntent.putExtra("current",current);
				sendBroadcast(sendIntent);//发送广播，将被Activity的广播接收器收到
				prepareAndPlay(musics[current]);//准备并播放音乐
			}
		});
	}
	
	private void prepareAndPlay(String music){
		try{
			AssetFileDescriptor afd=am.openFd(music);//打开指定音乐文件
			mPlayer.reset();
			mPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());//使用MediaPlayer加载指定的音乐文件
			mPlayer.prepare();//准备声音
			mPlayer.start();//播放
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public class ServiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int control=intent.getIntExtra("control", -1);
			switch(control){
			case 1://单击了“播放”或“暂停”按钮
				if(status==0x11){//原来处于没有播放状态
					prepareAndPlay(musics[current]);//准备并播放音乐
					status=0x12;
				}
				else if(status==0x12){//原来处于播放状态
					mPlayer.pause();//暂停
					status=0x13;//改变为暂停状态
				}
				else if(status==0x13){//原来处于暂停状态
					mPlayer.pause();//播放
					status=0x12;//改变状态
				}
				break;
			case 2://停止音乐
				if(status==0x12||status==0x13){//如果原来正在播放或暂停
					mPlayer.stop();//停止播放
					status=0x11;
				}
			}
			//发送广播通知Activity更改图标、文本框 

			Intent sendIntent=new Intent(MainActivity.UPDATE);
			sendIntent.putExtra("update", status);
			sendIntent.putExtra("current", current);//发送广播，将被Activity组件中的广播接收器接收到
			sendBroadcast(sendIntent);
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(serviceReceiver);
	}


}

