package com.example.carbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT =2 ;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Button btn;
    private ArrayAdapter mArrayAdapter;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        btn=(Button) this.findViewById(R.id.button11);
        final TextView tv=this.findViewById(R.id.textView);
        btn.setOnClickListener((View.OnClickListener) this);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //创建蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //搜索蓝牙
        mBluetoothAdapter.startDiscovery();

        //创建listView的适配器
        mArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        //意图过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND); //蓝牙搜索
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //开始搜索
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //结束搜索


        //创建广播接收器
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //获得intent的行动
                String action = intent.getAction();
                /*
                三组蓝牙广播状态分别是：
                BluetoothAdapter.ACTION_DISCOVERY_STARTED  开始蓝牙搜索
                BluetoothDevice.ACTION_FOUND  蓝牙搜索中
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED 蓝牙搜索完毕
                 */
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    //创建蓝牙设备，我们可以从BluetoothDevice 里获得各种信息 名称、地址 等等
//                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    // 将设备名称和地址放入array adapter，以便在ListView中显示
//                    mArrayAdapter.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                    Toast.makeText(this,"开始搜索", Toast.LENGTH_SHORT).show();
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                    Toast.makeText(this,"搜索完毕",Toast.LENGTH_SHORT).show();
//                }
            }
        };

        registerReceiver(broadcastReceiver,intentFilter);//添加广播
    }


    public void DiscovryBlueTooth() {
        if (mBluetoothAdapter.isDiscovering()) {
            //判断蓝牙是否正在扫描，如果是调用取消扫描方法；如果不是，则开始扫描
            mBluetoothAdapter.cancelDiscovery();
        } else
            mBluetoothAdapter.startDiscovery();

    }

    public void OpenBlueTooth() {
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null) {
        Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            return;
        }
        //设备支持蓝牙功能
        if (!mBluetoothAdapter.isEnabled()) {
            //用于启动蓝牙功能的 Intent
            Intent enableBtIntent=new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, RESULT_OK);
        }
        // Log.i("widgetDemo", "button1 被用户点击了。");
    }

    public void CloseBlueTooth(){
            BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.disable();
            }
    }

    public void settingsOpen(){
        Intent intent=new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }

    public void BlueToothVisible(){
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter.getScanMode()!=BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(i);
        }
    }
    public void SendMessage(){

    }

    public void ConnectBlueTooth(){}

    public void ReconnectBlueTooth(){}

    /**
     * 初始化蓝牙状态广播监听
     */
    private void initBtState(){
        BtTemperatureReceiver mBtTemperatureReceiver = new BtTemperatureReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBtTemperatureReceiver,intentFilter);
    }

    /**
     * 蓝牙状态广播回调
     */
    class BtTemperatureReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                //注意!这里是先拿action 等于 BluetoothAdapter.ACTION_STATE_CHANGED 在解析intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                case BluetoothAdapter.ACTION_STATE_CHANGED:
//                    Log.e("触发蓝牙状态");
                    int blState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blState){
                        case BluetoothAdapter.STATE_TURNING_ON:
//                            Log.e("蓝牙正在开启");
                            break;
                        case BluetoothAdapter.STATE_ON:
//                            Log.e("蓝牙已经开启");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
//                            Log.e("蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
//                            Log.e("蓝牙已经关闭");
                            break;
                        case BluetoothAdapter.ERROR:
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

        }
    }

}
