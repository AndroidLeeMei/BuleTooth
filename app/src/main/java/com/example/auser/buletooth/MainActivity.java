package com.example.auser.buletooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button btn_scan,btn_discoverable,btn_edit;
    ListView listView;
    final String TAG="BT_T";
    private Context context;
    private BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLE_BT=2;  //數值自己隨便設
    private Set<BluetoothDevice> device;
    private ArrayList<String> btDeviceList;
    private boolean receiverFlag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        btn_discoverable=(Button)findViewById(R.id.btnDiscoverable);
        btn_edit=(Button)findViewById(R.id.btnEdit);
        btn_scan=(Button)findViewById(R.id.btnScan);

        btDeviceList=new ArrayList();
        listView=(ListView)findViewById(R.id.listView);
//        先清空listView裏面的資料
        listView.setAdapter(null);
        //先清空以前註冊過的blue tooth的資料

        //監聽listView有沒有被按下去
        listView.setOnItemClickListener(new MyItemClick());
//        listView.setOnClickListener(new MyItemClick());

        //set listener
        btn_scan.setOnClickListener(new BtnOnClickListener());
        btn_discoverable.setOnClickListener(new BtnOnClickListener());
        btn_edit.setOnClickListener(new BtnOnClickListener());

        //取得控制權static class,可以直接呼叫取得
        btAdapter=BluetoothAdapter.getDefaultAdapter();
        if (btAdapter==null){
            Toast.makeText(context,"there is not buletooth",Toast.LENGTH_SHORT).show();
            finish();//此手機沒關掉手機
        }else if(!btAdapter.isEnabled()){  //有buletooth,時再檢查有沒有被關閉,若關閉,自用程式開啟,打閞isEnabled=true
            //打開buletooth
            Intent intent=new Intent((BluetoothAdapter.ACTION_REQUEST_ENABLE));
            startActivityForResult(intent,REQUEST_ENABLE_BT);
            //接收intent回來,要寫一個onActivityResult(),由REQUEST_ENABLE_BT判定是那一個request回來的結果
        }else{
            device=btAdapter.getBondedDevices();
            Log.d(TAG,"GdtBounded_devicel");
            //檢查容器裏面有沒有資料
            if(device.size()>0){
                for (BluetoothDevice device_data:device){
                    //建立一個arraylist,再將資料放入,並將整組資料傳入下一個activity
                    btDeviceList.add("Paired: " + device_data.getName()+"\n" + device_data.getAddress());
                    Log.d(TAG, device_data.getName()+"\n" + device_data.getAddress());
                }
                listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList));
                //1.型别.2.layout file xml 用android預設的 3.顯示的東西
                Log.d(TAG,"gGetBonded_device2");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_ENABLE_BT){
            if(resultCode==RESULT_CANCELED) {  //在bluetooth只有兩種結果,1開2關
                Toast.makeText(context,"Enablint BT failed", Toast.LENGTH_SHORT).show();
                finish();
            }else if (resultCode==RESULT_OK){
                device=btAdapter.getBondedDevices();
                //檢查容器裏面有沒有資料
                if(device.size()>0){
                    for (BluetoothDevice device_data:device){
                        //建立一個arraylist,再將資料放入,並將整組資料傳入下一個activity
                        btDeviceList.add("Paired: " + device_data.getName()+"\n" + device_data.getAddress());
                    }
                    listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList));
                    //1.型别.2.layout file xml 用android預設的 3.顯示的東西
                }


            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class BtnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnScan://搜尋附近的藍裝置
                    btAdapter.startDiscovery();
                    //開始做接收的動作
                    IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver,filter);
                    //要建立reveiver監聽器
                    receiverFlag=true;
                    Toast.makeText(context,"Begin to scan",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnDiscoverable:  //把自己手機打開150秒
                    Intent disIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    disIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,150);
                    startActivity(disIntent);
                    Toast.makeText(context,"Begin to discoverable",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnEdit:
                    Toast.makeText(context,"Begin to edit",Toast.LENGTH_SHORT).show();
                    Intent newIntent =new Intent(context,ServerMode.class);
                    startActivity(newIntent);
                    break;
                default:
                    Toast.makeText(context,"no button",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override//參數1.上下關係2.
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            //發現週邊設備,從intent收進來
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  //額外加進來的device
                //將資料放入arraylist,再將將資料放入arraylist放入listView
                btDeviceList.add("Found :" + device.getName() + "\n" +device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList));

            }

        }
    };

    @Override//關掉監聽器,節省電
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        btAdapter.cancelDiscovery();
        if (receiver!=null){
            if (receiverFlag){
                unregisterReceiver(receiver);
            }
        }
    }

    private class MyItemClick implements android.widget.AdapterView.OnItemClickListener {

        @Override//position代表按的位置
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            btAdapter.cancelDiscovery();
            String remoteDeviceName=parent.getItemAtPosition(position).toString();  //
            Intent newIntent=new Intent(context,ServerMode.class);  //設定連結畫面
            newIntent.putExtra("remoteDevice",remoteDeviceName);//傳入參數
            startActivity(newIntent);
        }
    }


//下面開始是網路複製來的
    String str;
    public void openScan(View target){
        BluetoothAdapter mBuletoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBuletoothAdapter==null){
            Toast.makeText(this,"沒有blue tooth", Toast.LENGTH_SHORT).show();

        }else {

            if(!mBuletoothAdapter.isEnabled()){
                mBuletoothAdapter.enable();
            }
            str="address=" +mBuletoothAdapter.getAddress()
                    + " ,name=" +mBuletoothAdapter.getAddress()
                    + " ,isDiscovering=" +mBuletoothAdapter.isDiscovering()
                    + " ,isEnabled= " +mBuletoothAdapter.isEnabled();;
            //isEnabled()判斷藍牙是否打開，已打開返回true，否則，返回false
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

            mBuletoothAdapter.startDiscovery();

            BroadcastReceiver mReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    //找到設備
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                            Toast.makeText(MainActivity.this, "find device:" + device.getName()
                                    + device.getAddress(), Toast.LENGTH_SHORT).show();

                        }
                    }//搜索完成

                    else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                            .equals(action)) {
                        Toast.makeText(MainActivity.this,"find over",Toast.LENGTH_SHORT).show();

                        setTitle("搜索完成");
//                        if (mNewDevicesAdapter.getCount() == 0) {
//                            Toast.makeText(MainActivity.this,"find over",Toast.LENGTH_SHORT).show();
//                        }


                    }
                }
            };



        }


    }

    public void openClient(View target){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,ClientMode.class);
        startActivity(intent);
    }

    public void openServer(View target){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,ServerMode.class);
        startActivity(intent);
    }



}
