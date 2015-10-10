package com.kilomobi.twominutes;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by macbookpro on 09/10/2015.
 */
public class myContactAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity mActivity;
    private ArrayList mData;
    private static LayoutInflater inflater=null;
    ContactModel mContact = null;
    int i=0;
    Context mContext;

    /*************  CustomAdapter Constructor *****************/
    public myContactAdapter(Activity a, ArrayList d) {

        /********** Take passed values **********/
        mActivity = a;
        mData = d;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )mActivity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView tvContactName;
        public TextView tvContactPhone;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (vi == null) {
            vi = inflater.inflate(R.layout.cell, null);

        /****** View Holder Object to contain tabitem.xml file elements ******/

        holder = new ViewHolder();
        holder.tvContactName = (TextView) vi.findViewById(R.id.contact_name);
        holder.tvContactPhone=(TextView)vi.findViewById(R.id.contact_number);

        vi.setTag(holder); }
        else
            holder=(ViewHolder)vi.getTag();
        if(mData.size() <= 0)
        {
            holder.tvContactName.setText("Pas de favoris");
        }

        else
        {
            /***** Get each Model object from Arraylist ********/
            mContact = null;
            mContact = ( ContactModel ) mData.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.tvContactName.setText(mContact.getContactName());
            holder.tvContactPhone.setText(mContact.getContactPhone());

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            MainActivity sct = (MainActivity) mActivity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
