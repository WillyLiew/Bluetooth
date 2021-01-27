package com.example.bluetooh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // add android.permission.BLUETOOTH and BLUETOOTH_ADMIN in Manifest
    TextView statusView;
    RecyclerView listDevView;
    ViewAdapter viewAdapter;
    Button onOffBtn, listDevBtn;
    BluetoothAdapter myBtAdapter;
    Intent btEnableIntent;
    int btEnableRequestCode;
    boolean btBtnStatus=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusView=findViewById(R.id.statusView);
        listDevView=findViewById(R.id.recyclerView);
        listDevView.setLayoutManager(new LinearLayoutManager(this));
        onOffBtn=findViewById(R.id.onOffBtn);
        listDevBtn=findViewById(R.id.listDevBtn);
        listDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBtAdapter.isEnabled()){
                    listDevices();
                }else{
                    Toast.makeText(MainActivity.this, "Please enable bluetooth first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        myBtAdapter=BluetoothAdapter.getDefaultAdapter();
        btEnableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        btEnableRequestCode=1;
        onOffBtnMethod();

        viewAdapter = new ViewAdapter();
        listDevView.setAdapter(viewAdapter);

    }

    private void listDevices() {
        Set<BluetoothDevice>bt=myBtAdapter.getBondedDevices();
        String[] devices=new String[2+bt.size()];
        Toast.makeText(this,"Number of devices: "+String.valueOf(bt.size()),Toast.LENGTH_SHORT).show();
        devices[0]="Test Item 1";
        devices[1]="Test Item 2";
        int index=2;
        if(bt.size()>0){
            for(BluetoothDevice device:bt){
                devices[index++]=device.getName();
            }
        }
        viewAdapter.addDevices(devices);
        viewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==btEnableRequestCode){
            if(resultCode==RESULT_OK){
                statusView.setText("Bluetooth Enabled");
                onOffBtn.setText("TURN OFF");
                btBtnStatus=true;
            } else if(resultCode==RESULT_CANCELED){
                statusView.setText("Bluetooth enabling cancelled");
            }
        }
    }

    private void onOffBtnMethod() {
        onOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btBtnStatus==false){
                    if(myBtAdapter==null){
                        statusView.setText("Bluetooth is not supported on this device");
                    }else{
                        if(!myBtAdapter.isEnabled()){
                            startActivityForResult(btEnableIntent,btEnableRequestCode);
                        }else{
                            statusView.setText("Bluetooth enabled");
                            onOffBtn.setText("OFF");
                            btBtnStatus=true;
                        }
                    }
                }else{
                    if(myBtAdapter.isEnabled()){
                        myBtAdapter.disable();
                        statusView.setText("Bluetooth disabled");
                        onOffBtn.setText("ON");
                        btBtnStatus=false;
                    }
                }
            }
        });
    }

    class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder>{
        String devicelist[];

        @NonNull
        @Override
        public ViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowView=getLayoutInflater().inflate(R.layout.device_tv,parent,false);
            return new MyViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter.MyViewHolder holder, int position) {
            holder.tv.setText(String.valueOf(position)+": "+String.valueOf(devicelist[position]));
        }

        public void addDevices(String devices[]){
            devicelist=devices;
        }

        @Override
        public int getItemCount() {
            if(devicelist!=null){
                return devicelist.length;
            }else{
                return 0;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView tv;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv=itemView.findViewById(R.id.deviceTV);
            }
        }
    }


}

