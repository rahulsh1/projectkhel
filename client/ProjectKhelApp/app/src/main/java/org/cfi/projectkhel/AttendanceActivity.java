package org.cfi.projectkhel;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

  private ListView mainList;
  private MyCustomListAdapter listAdapter;
  private List<MyCustomData> mData = new ArrayList<>();

  private static int [] IMAGES = { R.drawable.android_calendar,
                            R.drawable.android_earth,
                            R.drawable.android_friends,
                            R.drawable.android_add_contact,
                            R.drawable.aperture,
                            R.drawable.happy,
                            R.drawable.clipboard};
  // TODO move to resources instead.
  private static String [] LABELS = { "Date", "Location", "Coordinators", "Beneficiary", "Modules", "Rating", "Comments"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_attendance);

    mainList = (ListView) findViewById(R.id.listView1);
    mainList.setOnItemClickListener(this);

    addInitialListContents();
    // Set our custom adapter for the listView.
    listAdapter = new MyCustomListAdapter();
    mainList.setAdapter(listAdapter);
    listAdapter.refresh();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_attendance, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


  public void onSubmitClick(View v) {

  }

  public void onResetClick(View v) {

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  }

  /**
   * Implement a Custom Adapter for rendering the different layout for each of the Various rows
   * in this mainList.
   * Row 0 - Date
   * Row 1 - Location - SingleSelect
   * Row 2 - Coordinators - MultiSelect
   * Row 3 - Beneficiaries - MultiSelect
   * Row 4 - Modules - MultiSelect
   * Row 5 - Overall Rating - Dropdown
   * Row 6 - Comments - EditText
   */
  private class MyCustomListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    public MyCustomListAdapter() {
      mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return mData.size();
    }

    @Override
    public Object getItem(int position) {
      return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public void refresh() {
      notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      MyViewHolder viewHolder = null;
      if (convertView == null) {
        // No convertView in recycle pool yet
        convertView = mInflater.inflate(R.layout.attendance_row, parent, false);
        viewHolder = new MyViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (MyViewHolder) convertView.getTag();
        Log.d(MainActivity.TAG, "Reusing Row for " + position);
      }
      viewHolder.setValues(mData.get(position));
      return convertView;
    }

    class MyViewHolder {
      ImageView myImg;
      TextView myLabel;
      TextView  myContent;

      MyViewHolder(View v) {
        myImg =  (ImageView) v.findViewById(R.id.attIconImageView);
        myLabel = (TextView) v.findViewById(R.id.attLabelTextView);
        myContent = (TextView) v.findViewById(R.id.attContentTextView);
      }

      void setValues(MyCustomData customData) {
        myImg.setImageResource(customData.imageResource);
        myLabel.setText(customData.label);
        myContent.setText(customData.content);
      }
    }
  }

  private void addInitialListContents() {
    for (int i = 0; i < LABELS.length; i++) {
      mData.add(new MyCustomData(IMAGES[i], LABELS[i], ""));
    }
  }

  /**
   * Custom Data class to store the individual row values.
   */
  private class MyCustomData {
    int imageResource;
    String label;
    String content;

    public MyCustomData(int imageResource, String label, String content) {
      this.imageResource = imageResource;
      this.label = label;
      this.content = content;
    }

    public void setContent(String myContent) {
      content = myContent;
    }

  }
}
