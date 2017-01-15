package com.gerardoaugusto.myweatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GerardoAugusto on 14/1/2017.
 */

public class WeatherArrayAdapter extends ArrayAdapter<Weather>{

    private Map<String,Bitmap> bitmaps=new HashMap<>();
    private Context context;
    private int layout;
    public WeatherArrayAdapter(Context context,int l, List<Weather> forecast) {
        super(context,-1, forecast);
        this.context=context;
        layout=l;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Weather day=getItem(i);
        ViewHolder VH;
        if (convertView==null){
            VH=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(layout,parent,false);
            VH.conditionImageView= (ImageView) convertView.findViewById(R.id.conditionImageView);
            VH.dayTextView= (TextView) convertView.findViewById(R.id.dayTextView);
            VH.lowTextView= (TextView) convertView.findViewById(R.id.lowTextView);
            VH.hiTextView= (TextView) convertView.findViewById(R.id.hiTextView);
            VH.humidityTextView= (TextView) convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(VH);
        }else{
            VH= (ViewHolder) convertView.getTag();
        }

        if (bitmaps.containsKey(day.iconUrl)){
            VH.conditionImageView.setImageBitmap(bitmaps.get(day.iconUrl));
        }else
        {
            new LoadImageTask(VH.conditionImageView).execute(day.iconUrl);
        }
        VH.dayTextView.setText(context.getString(R.string.day_description,day.dayWeek,day.description));
        VH.lowTextView.setText(context.getString(R.string.low_temp,day.minTemp));
        VH.hiTextView.setText(context.getString(R.string.high_temp,day.maxTemp));
        VH.humidityTextView.setText(context.getString(R.string.humidity,day.humidity));
        return convertView;
    }

    private static class ViewHolder{
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }
    private class LoadImageTask extends AsyncTask<String,Void,Bitmap>{

        private ImageView imageView;
        public LoadImageTask(ImageView imV){
            imageView=imV;
        }
        //Tarea en segundo plano que se encarga de obtener la imagen de la url y agregarla al Map<String,Bitmap>
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=null;
            HttpURLConnection connection=null;
            try{
                URL url=new URL(params[0]);
                connection= (HttpURLConnection) url.openConnection();
                try(InputStream inputStream=connection.getInputStream()){
                    bitmap= BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0],bitmap);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            connection.disconnect();
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
