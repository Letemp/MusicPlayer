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
	
	//�����㲥������
	ServiceReceiver serviceReceiver;
	AssetManager am;//��Դ������
	String[]musics=new String[]{"oldboy.mp3","spring.mp3","way.mp3"};//���弸�׸���
	MediaPlayer mPlayer;
	int status=0x11;//��ǰ��״̬��0x11��δ���ţ�0x12�����ڲ��ţ�0x13����ͣ
	int current=0;//��¼��ǰ���ڲ��ŵ����ֵ����

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		am=getAssets();//����context��ķ���
		serviceReceiver=new ServiceReceiver();//�����㲥�����߶���
		IntentFilter filter=new IntentFilter(MainActivity.CONTROL);//����IntentFilter
		registerReceiver(serviceReceiver,filter);//ע��㲥������
		mPlayer=new MediaPlayer();//����ý�岥����
		super.onCreate();
		
		mPlayer.setOnCompletionListener(new OnCompletionListener(){
			public void onCompletion(MediaPlayer mp){
				current++;
				if(current>=3){
					current=0;//�ж��Ƿ񳬳���Χ������������ִӵ�һ�׿�ʼ
				}
				Intent sendIntent=new Intent(MainActivity.UPDATE);
				sendIntent.putExtra("current",current);
				sendBroadcast(sendIntent);//���͹㲥������Activity�Ĺ㲥�������յ�
				prepareAndPlay(musics[current]);//׼������������
			}
		});
	}
	
	private void prepareAndPlay(String music){
		try{
			AssetFileDescriptor afd=am.openFd(music);//��ָ�������ļ�
			mPlayer.reset();
			mPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());//ʹ��MediaPlayer����ָ���������ļ�
			mPlayer.prepare();//׼������
			mPlayer.start();//����
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
			case 1://�����ˡ����š�����ͣ����ť
				if(status==0x11){//ԭ������û�в���״̬
					prepareAndPlay(musics[current]);//׼������������
					status=0x12;
				}
				else if(status==0x12){//ԭ�����ڲ���״̬
					mPlayer.pause();//��ͣ
					status=0x13;//�ı�Ϊ��ͣ״̬
				}
				else if(status==0x13){//ԭ��������ͣ״̬
					mPlayer.pause();//����
					status=0x12;//�ı�״̬
				}
				break;
			case 2://ֹͣ����
				if(status==0x12||status==0x13){//���ԭ�����ڲ��Ż���ͣ
					mPlayer.stop();//ֹͣ����
					status=0x11;
				}
			}
			//���͹㲥֪ͨActivity����ͼ�ꡢ�ı��� 

			Intent sendIntent=new Intent(MainActivity.UPDATE);
			sendIntent.putExtra("update", status);
			sendIntent.putExtra("current", current);//���͹㲥������Activity����еĹ㲥���������յ�
			sendBroadcast(sendIntent);
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(serviceReceiver);
	}


}

