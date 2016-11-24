package com.example.enmustafa.gas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by En.Mustafa on 21-Nov-16.
 */
public class listplaces extends Activity {


    public List<HashMap<String, String>> getPlaces() {

        return places;
    }

    public void setPlaces(List<HashMap<String, String>> places) {
        this.places = places;
    }

    //NewsArrayAdapter arrayAdapter;
      List<HashMap<String, String>> places  ;
    SimpleAdapter simpleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        Bundle b=getIntent().getExtras();
        if(b!=null)
        {
            places= (List<HashMap<String, String>>) b.getSerializable("map");
        }
        ListView listview = (ListView) findViewById(R.id.listView1);
                if (places== null)
                    Toast.makeText(this, "is empty", Toast.LENGTH_SHORT).show();
                else {
                     simpleAdapter = new SimpleAdapter(this, places, R.layout.row,
                            new String[]{"place_name", "vicinity", "latLng"},
                            new int[]{R.id.text1, R.id.text2, R.id.text3});
                    listview.setAdapter(simpleAdapter);
                }


                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                HashMap<String, String> item = (HashMap<String, String>) simpleAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                intent.putExtra("map", (Serializable) item);
                startActivity(intent);
//                Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
//                intent.putExtra("reference", reference);
//                // Starting the Place Details Activity
//                startActivity(intent);
            }
        });


    }
}
