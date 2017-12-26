package com.example.auser.buletooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothChatService;
////import  and

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ServerMode extends AppCompatActivity {
    Button btn_SendServer,btn_ClearServer;
    EditText edt_Server;
    TextView txt_Server;
    Context context;

    private String remoteDeviceInfo;
//    private BluetoothChatService mChatService = null;
    private String remoteMacAddress;
    private String mConnectedDeviceName =null;
    private StringBuffer mOutStringBuffer;            // String buffer for outgoing messages

    private static final String TAG = "BT_Edit";
    private BluetoothAdapter btAdapter;
    private BluetoothChatService mChatService=null;
    private BluetoothDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_mode);
        findViews();
        context=this;
        edt_Server.setText("");
        txt_Server.setText("");

        btn_SendServer.setOnClickListener(new btnOnClickListener());  //監聽器
        btn_ClearServer.setOnClickListener(new btnOnClickListener());
        txt_Server.setOnEditorActionListener(textEditListener);             //txt_Server被按了enter 鍵時

        btAdapter= BluetoothAdapter.getDefaultAdapter();  //取得控制權,後才開始

        Intent intent = getIntent(); //get data from MainActivity  先判斷由那個路徑進入
        remoteDeviceInfo=intent.getStringExtra("remoteDevice");//getStringExtra取得當初給的路徑名稱

        mChatService=new BluetoothChatService(context,mHandler);
        mOutStringBuffer=new StringBuffer("");
        txt_Server.append("make bt module in server mode \n");
        mChatService.start();

        if(remoteDeviceInfo!=null) {//當有帶資料進來時,當初有帶listview資料進入
            Log.d(TAG, "Client Mode");
            String deviceMsg = remoteDeviceInfo.substring(10);
            Log.d(TAG, deviceMsg);
            txt_Server.append("Connect to remote BT device: \n" + deviceMsg + "\n");
            remoteMacAddress = remoteDeviceInfo.substring(remoteDeviceInfo.length() - 17);  //取得要連結的address名字
            Log.d(TAG, remoteMacAddress);
            device = btAdapter.getRemoteDevice(remoteMacAddress);
            mChatService.connect(device, true);


        }
    }

    private TextView.OnEditorActionListener textEditListener=new TextView.OnEditorActionListener(){  //  //txt_Server被按了enter 鍵時
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { //利用actionId來檢查按下那個鍵
            Log.d(TAG,"onEditorAction1");
            if (actionId== EditorInfo.IME_ACTION_DONE) {  //當按下enter鍵時,把字寫到textview
                Log.d(TAG,"onEditorAction2");
                String message=edt_Server.getText().toString();
                txt_Server.append(">>" +message +"\n");
                sendMessageToBT(message);
            }
            return true;//default return false;
         }
    };

    void findViews(){
        btn_SendServer=(Button)findViewById(R.id.btnSendServer);
        btn_ClearServer=(Button)findViewById(R.id.btnClearServer);
        edt_Server=(EditText)findViewById(R.id.edtServer);
        txt_Server=(TextView)findViewById(R.id.txtServer);

    }


    private class btnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnClearServer:
                    txt_Server.setText("");  //把textView清掉
                    Toast.makeText(context,"Clean display",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnSendServer:
                    txt_Server.append(">>"+ edt_Server.getText()+"\n");//自己敲出去的資料以>>開頭
                    String message=edt_Server.toString();//1.把寫的東西放在edit  2.將東西送出去bluetooth
                    sendMessageToBT(message);
                    Toast.makeText(context,"Send display",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    private void sendMessageToBT(String message){  //將資料傳到blue tooth
//        // Check that we're actually connected before trying anything
//        int mState= mChatService.getState();
//        Log.d(TAG , "mstate in sendMessage =" + valueOf(mState));
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(context,"Bluetooth device is not connected. " , Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mChatService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            sendEdit.setText(mOutStringBuffer);
//        }

    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //   dataTextView.append(">>  : " + writeMessage + "\n");   //display on TextView
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    txt_Server.append("remote : " + readMessage + "\n");   //display on TextView

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(context, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(context, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
