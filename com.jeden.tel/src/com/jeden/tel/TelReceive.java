package com.jeden.tel;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;

public class TelReceive extends BroadcastReceiver {

	private static boolean incomingFlag = false;
  
	private static String incoming_number = null;
  
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//����ǲ���绰
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
        {                        
                incomingFlag = false;
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER); 
                Tool.BfLog("���˵绰��"+phoneNumber);
    	}
        else
    	{                        
            //���������
            TelephonyManager tm = 
                (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                        
            
            switch (tm.getCallState()) 
            {
	            case TelephonyManager.CALL_STATE_RINGING:
	                    incomingFlag = true;//��ʶ��ǰ������
	                    incoming_number = intent.getStringExtra("incoming_number");
	                    Tool.BfLog("������˵绰��"+incoming_number);
	                    SharedPreferences sp = context.getSharedPreferences("jeden_tel",
				    			MainActivity.MODE_PRIVATE);
	                    String tels = sp.getString("tels", null);
	                    if(tels != null)
	                    {
	                    	String telstemp[] = tels.split(",");
	                    	for(String temp:telstemp)
	                    	{
			                    if(temp.contains(incoming_number))  
			                    {
		                            stop(context, incoming_number);//��������
		                            String hestory = sp.getString("hestory", null);
		                            if(hestory == null)
		                            {
		                            	hestory = "";
		                            }
		                            
		                            hestory = hestory + "\n ���ص��˵绰��" + incoming_number;
		                            sp.edit().putString("hestory", hestory).commit();
		                            //abortBroadcast();//�ضϹ㲥
			                    }
	                    		
	                    	}
	                    }
	                    break;
	            case TelephonyManager.CALL_STATE_OFFHOOK:                                
	                    if(incomingFlag)
	                    {
	                    	
	                    }
	                    break;
	            
	            case TelephonyManager.CALL_STATE_IDLE:                                
	                    if(incomingFlag)
	                    {       
	                    	
	                    }
	                    break;
            } 
        }
	}
	public void stop(Context context ,String incoming_number) 
	{ 
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//��������
        ITelephony iTelephony = getITelephony(context); //��ȡ�绰�ӿ�
        try 
        {
        	iTelephony.endCall();//�����绰                    
        } catch (RemoteException e) 
        {                                
        	e.printStackTrace();
        }                    
        //�ٻָ���������    
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);      
	}
	private static ITelephony getITelephony(Context context) 
	{
		ITelephony iTelephony = null;
		TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try 
		{
			Method getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
		    getITelephonyMethod.setAccessible(true);
		    iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyMgr, (Object[]) null);
		} catch (Exception e) 
		{
		      e.printStackTrace();
	    }
	    return iTelephony;
	}
}