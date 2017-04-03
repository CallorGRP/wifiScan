package charlie.wifiscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import charlie.wifiscan.databinding.ActivityMainBinding;

import static android.text.TextUtils.concat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityMainBinding mainBinding;
    private static final int LAYOUT = R.layout.activity_main;
    ArrayList<WifiItem> wifiItemArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    WifiManager wifiManager;
    ConnectivityManager connectivityManager;
    private List<ScanResult> scanResultList;
    private String currentWifiMac,savedWifiMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,LAYOUT);
        getWifiList();
        setRecyclerView();
        setOnClickListener();
    }

    private void getWifiList(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo.isAvailable()){
            refresh();
        }else{
            Toast.makeText(this,"no wifi",Toast.LENGTH_SHORT);
        }
    }

    private void refresh(){
        wifiItemArrayList.clear();
        scanResultList = wifiManager.getScanResults();
        for(ScanResult result : scanResultList){
            wifiItemArrayList.add(new WifiItem(result.SSID));
            Log.e("ssid",result.SSID+"..");
        }
        adapter = new WifiAdapter(wifiItemArrayList);
        mainBinding.wifiRecyclerView.setAdapter(adapter);
        mainBinding.wifiRecyclerView.invalidate();
    }

    private String getMac(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        DhcpInfo dhcpInfo = wm.getDhcpInfo();
        int serverIp = dhcpInfo.gateway;

        String ipAddress = String.format(
                "%d.%d.%d.%d",
                (serverIp & 0xff),
                (serverIp >> 8 & 0xff),
                (serverIp >> 16 & 0xff),
                (serverIp >> 24 & 0xff));

        return ipAddress;
        /*WifiInfo info = wifiManager.getConnectionInfo();
        Log.d("mac",info.getIpAddress()+"..");
        String mac = info.getIpAddress()+"("+info.getSSID()+")";
        return mac;*/
    }

    private void setRecyclerView(){
        // set layout manager
        layoutManager = new LinearLayoutManager(this);
        mainBinding.wifiRecyclerView.setLayoutManager(layoutManager);

        // set adapter
        adapter = new WifiAdapter(wifiItemArrayList);
        mainBinding.wifiRecyclerView.setAdapter(adapter);
        mainBinding.wifiRecyclerView.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.workBtn:
                currentWifiMac = getMac();
                mainBinding.currentMacTv.setText("current IP : "+currentWifiMac);
                if(currentWifiMac.equals(savedWifiMac)){
                    Toast.makeText(getApplicationContext(), "출근 성공", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "와이파이 정보를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.goHomeBtn:
                break;
            case R.id.listBtn:
                refresh();
                break;
            case R.id.saveMacBtn:
                savedWifiMac = getMac();
                if(!savedWifiMac.isEmpty()){
                    mainBinding.savedMacTv.setText("saved IP : "+savedWifiMac);
                }
                break;
        }
    }

    private void setOnClickListener(){
        mainBinding.saveMacBtn.setOnClickListener(this);
        mainBinding.workBtn.setOnClickListener(this);
        mainBinding.goHomeBtn.setOnClickListener(this);
        mainBinding.listBtn.setOnClickListener(this);

    }

    private void connectWifi(WifiItem wifiItem,int position){
        List apList = wifiManager.getScanResults();
        ScanResult ap = (ScanResult) apList.get(position);
        WifiConfiguration wfc = new WifiConfiguration();
        if(ap.capabilities.contains("OPEN")==true){
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.clear();
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }else if(ap.capabilities.contains("WEP")==true){
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.wepKeys[0] = "123456abcd";
            wfc.wepTxKeyIndex = 0;
        }else if(ap.capabilities.contains("WPA")==true||ap.capabilities.contains("WPA2")==true){
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wfc.preSharedKey = "\"".concat("123456abcd").concat("\"");
        }

    }
}
