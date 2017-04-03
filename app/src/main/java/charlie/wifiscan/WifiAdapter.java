package charlie.wifiscan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by charlie on 2017. 4. 3..
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder>{
    private ArrayList<WifiItem> mWifi;

    public WifiAdapter(ArrayList<WifiItem> wifiItems){
        mWifi = wifiItems;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView wifiName;
        ViewHolder (View view){
            super(view);
            wifiName = (TextView) view.findViewById(R.id.wifiNameTv);
        }
    }
    @Override
    public WifiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wifi,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WifiAdapter.ViewHolder holder, int position) {
            holder.wifiName.setText(mWifi.get(position).getWifiName());
    }

    @Override
    public int getItemCount() { return mWifi.size(); }
}
