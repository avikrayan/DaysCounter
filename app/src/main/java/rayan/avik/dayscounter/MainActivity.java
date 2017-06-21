package rayan.avik.dayscounter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView itemList;
    ArrayList<ItemPojo> itemDetailsArraylist = new ArrayList<ItemPojo>();
    ItemPojo itemDetails;

    Date currentDate, eventDate;

    String DATA_URL;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemList = (ListView) findViewById(R.id.mylist);
        
        getItem();
    }

    private void getItem() {

        DATA_URL = "https://api.myjson.com/bins/hyhpx";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject reader = new JSONObject(response);
                    JSONObject responseJSON = reader.getJSONObject("response");
                    String resultSuccess = responseJSON.getString("result");
                    if (resultSuccess.equals("success")) {
                        JSONArray jsonarray = responseJSON.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject c = jsonarray.getJSONObject(i);

                            itemDetails = new ItemPojo();
                            itemDetails.setEventName(c.getString("eventname"));
                            itemDetails.setDate(c.getString("eventdate"));
                            itemDetailsArraylist.add(itemDetails);

                        }
                        progressDialog.dismiss();
                        itemList.setAdapter(new itemListAdapter());
                    }
                } catch (final JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

               /* params.put("schoolId", schoolId);
                params.put("teacherId", teacherId);
                params.put("classId", teacherClassId);
                params.put("sectionId", teacherSectionId);*/
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait...", null, true, true);
       // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.bookloadinganim));
        progressDialog.setMessage("Fetching Your Data ! Please wait...!");
        progressDialog.setCancelable(false);
    }

    private class itemListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return itemDetailsArraylist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.child_layout,parent,false);
            TextView eventName = (TextView) row.findViewById(R.id.eventname);
            TextView date = (TextView) row.findViewById(R.id.date);
            TextView dayCounter = (TextView) row.findViewById(R.id.tv_daycounter);
            Button setReminder = (Button) row.findViewById(R.id.add_reminder);
            CardView cardView = (CardView) row.findViewById(R.id.card_view);

            /////////// DayCounter //////////////////
            SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            try {
                eventDate = dfDate.parse(itemDetailsArraylist.get(position).getDate());
                currentDate = dfDate.parse(dfDate.format(cal.getTime()));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            int diffInDays = (int) ((eventDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
            dayCounter.setText(diffInDays + "");

            //cardView.setCardBackgroundColor(Color.RED);



            /*///////////Set Reminder ////////////////
            setReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar beginCal = Calendar.getInstance();
                    beginCal.set(itemDetailsArraylist.get(position).getDate());

                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                    //intent.putExtra("beginTime", cal.getTimeInMillis());
                    //intent.putExtra("allDay", true);
                    //intent.putExtra("rrule", "FREQ=YEARLY");
                    //intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                    //intent.putExtra("title", "A Test Event from android app");
                    startActivity(intent);
                }
            });*/

            eventName.setText(itemDetailsArraylist.get(position).getEventName());
            date.setText(itemDetailsArraylist.get(position).getDate());

            return row;
        }
    }
}
