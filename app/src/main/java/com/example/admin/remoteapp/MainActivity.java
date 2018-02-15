package com.example.admin.remoteapp;
//ghello
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Intent remoteServiceIntent;
    int get_value;
    private static final int code = 0;
    Button bind, unbind, getvalue;
    boolean toggle;
    TextView value;
    Messenger remoteServiceRequestMesenger, remoteServiceResponseMessenger;  //we need two messenger for request and response

    //basically we need 1. intent  2. ServiceConnection class for remote service connection

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            toggle = true;
            remoteServiceRequestMesenger = new Messenger(service);  //service is the same binder that is returned from remoteService's onBind method
            remoteServiceResponseMessenger = new Messenger(new receiveRemoteServiceHandler());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            remoteServiceRequestMesenger = null;
            remoteServiceResponseMessenger = null;
            toggle = false;
        }
    };

    private class receiveRemoteServiceHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == code)
            {
                get_value = msg.arg1;
                value.setText(get_value);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bind = (Button) findViewById(R.id.button);
        unbind = (Button) findViewById(R.id.button2);
        getvalue = (Button) findViewById(R.id.button3);
        value = (TextView) findViewById(R.id.textView);

        bind.setOnClickListener(this);
        unbind.setOnClickListener(this);
        getvalue.setOnClickListener(this);

        remoteServiceIntent = new Intent();
        remoteServiceIntent.setComponent(new ComponentName("com.example.admin.practice_one","com.example.admin.practice_one.RemoteService"));

    }

    @Override
    public void onClick(View v) {

        int item = v.getId();
        if(item == R.id.button)
        {
            bindService(remoteServiceIntent, serviceConnection,BIND_AUTO_CREATE);
            Toast.makeText(this,"remoteService binded",Toast.LENGTH_SHORT);
        }
        if(item == R.id.button2)
        {
            unbindService(serviceConnection);
            Toast.makeText(this,"remoteService unbinded",Toast.LENGTH_SHORT);
        }
        if(item == R.id.button3)
        {

            if(toggle)
            {
                //prepare the request message
                Message requestMessage = Message.obtain(null,code);  //set the code
                requestMessage.replyTo = remoteServiceResponseMessenger;  //set the from address for the service to reply

                try {
                    remoteServiceRequestMesenger.send(requestMessage);   //actual request message is sent here
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
