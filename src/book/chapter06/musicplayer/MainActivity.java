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
	
    TextView title,author;//��ȡ��������ʾ�������⡢�����ı���
    ImageButton play,stop;//����/��ͣ��ֹͣ��ť
    ActivityReceiver activityReceiver;//����㲥������
    public static final String CONTROL="com.example.musicplayer.control";//���Ʋ��š���ͣ
    public static final String UPDATE="com.example.musicplayer.update";//���½�����ʾ
    int status=0x11;//���岥��״̬��0x11��δ���ţ�0x12�����ڲ��ţ�0x13����ͣ
    String[] titleStrs = new String[] { "���к�", "������", "��·��" };//������
    String[] authorStrs= new String[]{"�����ֵ�","����","����"};//�ݳ���
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);//ָ�������ļ�
		
		play=(ImageButton)this.findViewById(R.id.play);//��ȡ��������е�������ť�Լ������ı���ʾ��
		stop=(ImageButton)this.findViewById(R.id.stop);
		title=(TextView)findViewById(R.id.title);
		author=(TextView)findViewById(R.id.author);
		
		//Ϊ������ť��Ӽ�����
		play.setOnClickListener(this);
		stop.setOnClickListener(this);//MainActivityʵ����OnClickListener�ӿ�
		
		//�����㲥�����߶���
		activityReceiver=new ActivityReceiver();
		//����IntentFilter
		IntentFilter filter=new IntentFilter(UPDATE);
		// ָ��BroadcastReceiver������Action
		// filter.addAction(UPDATE_ACTION);
		// ע��BroadcastReceiver
		registerReceiver(activityReceiver, filter);
		Intent intent=new Intent(this,MusicService.class);
		startService(intent);//������̨Service
	}
	
	public void onClick(View source){
		Intent intent=new Intent(CONTROL);//����Intent
		System.out.println(source.getId());
		System.out.println(source.getId() == R.id.play);
		switch (source.getId()){
		case  R.id.play://���¡����š�/����ͣ����ť
			intent.putExtra("control", 1);
			break;
		case  R.id.stop://���¡�ֹͣ����ť
			intent.putExtra("control",2);
			break;
		}
		sendBroadcast(intent);//���͹㲥������service�еĹ㲥�������յ�
	}
	
	
	// �Զ����BroadcastReceiver�����������Service�������Ĺ㲥
	public class ActivityReceiver extends BroadcastReceiver{
		public void onReceive(Context context,Intent intent){
			int update=intent.getIntExtra("update", -1);//��ȡIntent�е�update��Ϣ
			int current =intent.getIntExtra("current",-1);//��ȡ��ǰ�������ֵ����
			if(current>=0){
				title.setText(authorStrs[current]);//���current��Ϊ-1������ʾ���ڲ��ŵ����������ݳ���
				author.setText(authorStrs[current]);

			}
			switch(update){
			case 0x11://δ����״̬����ʾ���Ű�ť
				play.setImageResource(R.drawable.play);
				status=0x11;
				break;
			case 0x12://����״̬������ʹ����ͣͼ��
				play.setImageResource(R.drawable.pause);
				status=0x12;
				break;
			case 0x13://��ͣ״̬������ʹ�ò���ͼ��                   
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

