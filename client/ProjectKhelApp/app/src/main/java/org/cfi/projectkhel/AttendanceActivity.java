package org.cfi.projectkhel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import org.cfi.projectkhel.model.Attendance;
import org.cfi.projectkhel.data.DataManager;
import org.cfi.projectkhel.data.DataUtils;
import org.cfi.projectkhel.model.Entry;

import static org.cfi.projectkhel.AttendanceConstants.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Attendance Activity
 *
 * TODO:
 * 1. Move all hardcoded strings into resource file.
 * 2. Generalize Dialog handling based on type (single-select, multi-select)
 * 3. Optimize DataManager calls - Done
 */
public class AttendanceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

  private ListView mainList;
  private Dialog ratingDialog;
  private final int SHORTEN_LEN = 15;

  private MyCustomListAdapter listAdapter;
  private List<MyCustomData> mData = new ArrayList<>();
  private int mYear;
  private int mMonthOfYear;
  private int mDayOfMonth;

  private Attendance attendance;

  private static int [] IMAGES = { R.drawable.android_calendar,
                            R.drawable.android_earth,
                            R.drawable.android_friends,
                            R.drawable.android_add_contact,
                            R.drawable.aperture,
                            R.drawable.happy,
                            R.drawable.clipboard};

  private static String [] ATTENDANCE_ELEM_LABELS;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_attendance);
    ATTENDANCE_ELEM_LABELS = getResources().getStringArray(R.array.attendance_elems);

    mainList = (ListView) findViewById(R.id.listView1);
    mainList.setOnItemClickListener(this);

    initialize();

    // Set our custom adapter for the listView.
    listAdapter = new MyCustomListAdapter();
    mainList.setAdapter(listAdapter);
    listAdapter.notifyDataSetChanged();
  }

  private void initialize() {
    // TODO: Fetch the logged in user.
    attendance = new Attendance("TODO");

    populateListContents();

    final Calendar c = Calendar.getInstance();
    mYear = c.get(Calendar.YEAR);
    mMonthOfYear = c.get(Calendar.MONTH);
    mDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
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
    Log.d(TAG, "Attendance: " +  attendance);
    // TODO - Verify if all data is filled up, Ask for confirmation first.
    AlertDialog show = new AlertDialog.Builder(this)
        .setTitle(getString(R.string.submit_attendance))
        .setMessage(getString(R.string.submit_confirm))
        .setIcon(android.R.drawable.ic_menu_compass)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialog, int whichButton) {
            Toast.makeText(AttendanceActivity.this, getString(R.string.submit_inform), Toast.LENGTH_SHORT).show();
            ((KhelApplication) getApplication()).getDataFetcher().pushAttendanceData(attendance);
            finish();
          }
        })
        .setNegativeButton(android.R.string.no, null).show();

  }

  public void onResetClick(View v) {
    new AlertDialog.Builder(this)
        .setTitle(getString(R.string.reset_attendance))
        .setMessage(getString(R.string.reset_confirm))
        .setIcon(android.R.drawable.ic_menu_compass)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialog, int whichButton) {
            for (MyCustomData data : mData) {
              data.setContent("");
            }
            attendance = attendance.clone();
            listAdapter.notifyDataSetChanged();
          }})
        .setNegativeButton(android.R.string.no, null).show();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Log.d(TAG, "Clicked on position: " + position + " id = " + id);
    final MyCustomData dataRow = mData.get(position);
    switch(position) {
      case ROW_DATE:
        handleDateDialog(dataRow);
        break;
      case ROW_RATING:
        handleRatingDialog(dataRow);
        break;
      case ROW_LOCATION:
        handleLocationDialog(dataRow);
        break;
      case ROW_BENEFICIARIES:
        handleBeneficiariesDialog(dataRow);
        break;
      case ROW_MODULES:
        handleModulesDialog(dataRow);
        break;
      case ROW_COORDINATORS:
        handleCoordinatorsDialog(dataRow);
        break;
      case ROW_COMMENTS:
        handleCommentsDialog(dataRow);
        break;
      default:
        break;
    }
  }

  private void handleDateDialog(final MyCustomData dataRow) {
    final DatePickerDialog dpd = new DatePickerDialog(this,
        new DatePickerDialog.OnDateSetListener() {

          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dataRow.setContent(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            mYear = year;
            mMonthOfYear = monthOfYear;
            mDayOfMonth = dayOfMonth;
            attendance.setDate(year, monthOfYear, dayOfMonth);
            listAdapter.notifyDataSetChanged();
          }
        }, mYear, mMonthOfYear, mDayOfMonth);
    dpd.show();
  }

  private void handleLocationDialog(final MyCustomData dataRow) {
    final List<Entry> locations =  DataManager.getInstance().getLocations();
    final int checkItem = DataUtils.getSelectedItemFromId(attendance.getLocation(), locations);

    // List where we track the selected item
    final Integer selectedItem[] = new Integer[1];
    selectedItem[0] = checkItem;
    final CharSequence[] locItems = DataUtils.getEntryNames(locations);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.location_dialog_title))
        . setSingleChoiceItems(locItems, checkItem,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = which;
              }
            })
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            dataRow.setContent(locItems[selectedItem[0]].toString());
            attendance.setLocation(DataUtils.getIdFromSelectedItem(selectedItem[0], locations));

            // Select all beneficiaries by default for this location.
            final List<Entry> beneficiaries =
                DataManager.getInstance().getBeneficiariesForLocation(attendance.getLocation());
            attendance.addBeneficiaries(DataUtils.getAllEntryIds(beneficiaries));

            listAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            // do nothing
          }
        });

    builder.show();
  }

  private void handleCoordinatorsDialog(final MyCustomData dataRow) {
    final List<Entry> coordinators = DataManager.getInstance().getCoordinators();
    final CharSequence coordinatorNames[] = DataUtils.getEntryNames(coordinators);

    boolean[] checkItems = DataUtils.getSelectedItemsFromIds(attendance.getCoordinators(), coordinators);
    // List where we track the selected items
    final List<Integer> selectedItems = DataUtils.getSelectedItemsFromBoolList(checkItems);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.coor_dialog_title))
        .setMultiChoiceItems(coordinatorNames, checkItems,
            new DialogInterface.OnMultiChoiceClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                  // If the user checked the item, add it to the selected items
                  selectedItems.add(which);
                } else if (selectedItems.contains(which)) {
                  // Else, if the item is already in the array, remove it
                  selectedItems.remove(Integer.valueOf(which));
                }
              }
            })
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            dataRow.setContent(" [ " + selectedItems.size() + " ] ");
            attendance.addCoordinators(DataUtils.getIdsFromSelectedItems(selectedItems, coordinators));
            listAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            // do nothing
          }
        });

    builder.show();
  }

  private void handleModulesDialog(final MyCustomData dataRow) {
    final List<Entry> modules = DataManager.getInstance().getModules();
    final boolean[] checkItems = DataUtils.getSelectedItemsFromIds(attendance.getModules(), modules);

    // List where we track the selected items
    final List<Integer> selectedItems = DataUtils.getSelectedItemsFromBoolList(checkItems);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.mod_dialog_title))
        .setMultiChoiceItems(DataUtils.getEntryNames(modules), checkItems,
            new DialogInterface.OnMultiChoiceClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                  // If the user checked the item, add it to the selected items
                  selectedItems.add(which);
                } else if (selectedItems.contains(which)) {
                  // Else, if the item is already in the array, remove it
                  selectedItems.remove(Integer.valueOf(which));
                }
              }
            })
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            dataRow.setContent(" [ " + selectedItems.size() + " ] ");
            attendance.addModules(DataUtils.getIdsFromSelectedItems(selectedItems, modules));
            listAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            // do nothing
          }
        });

    builder.show();
  }

  private void handleBeneficiariesDialog(final MyCustomData dataRow) {
    if (attendance.getLocation() <= 0) {
      Toast.makeText(this, getString(R.string.benef_location_select), Toast.LENGTH_SHORT).show();
      return;
    }
    final List<Entry> beneficiaries = DataManager.getInstance().getBeneficiariesForLocation(attendance.getLocation());
    final CharSequence beneficiaryNames[] = DataUtils.getEntryNames(beneficiaries);
    boolean[] checkItems = DataUtils.getSelectedItemsFromIds(attendance.getBeneficiaries(), beneficiaries);

    // List where we track the selected items
    final List<Integer> selectedItems = DataUtils.getSelectedItemsFromBoolList(checkItems);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.benef_dialog_title))
        .setMultiChoiceItems(beneficiaryNames, checkItems,
            new DialogInterface.OnMultiChoiceClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                  // If the user checked the item, add it to the selected items
                  selectedItems.add(which);
                } else if (selectedItems.contains(which)) {
                  // Else, if the item is already in the array, remove it
                  selectedItems.remove(Integer.valueOf(which));
                }
              }
            })
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            dataRow.setContent(" [ " + selectedItems.size() + " ] ");
            attendance.addBeneficiaries(DataUtils.getIdsFromSelectedItems(selectedItems, beneficiaries));
            listAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            // do nothing
          }
        });

    builder.show();
  }


  private void handleRatingDialog(final MyCustomData dataRow) {
    if (ratingDialog == null) {
      ratingDialog = new Dialog(AttendanceActivity.this, R.style.NoTitleDialog);
      ratingDialog.setContentView(R.layout.rating_dialog);
      ratingDialog.setCancelable(true);
    }

    final RatingBar ratingBar = (RatingBar)ratingDialog.findViewById(R.id.dialog_ratingbar);
    ratingBar.setRating(attendance.getRating() * 1.0f);

    final Button updateButton = (Button) ratingDialog.findViewById(R.id.rank_dialog_button);
    updateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final int userRankValue = (int) ratingBar.getRating();
        attendance.setRating(userRankValue);
        dataRow.setContent(Integer.toString(userRankValue));
        listAdapter.notifyDataSetChanged();
        ratingDialog.dismiss();
      }
    });
    //now that the dialog is set up, it's time to show it
    ratingDialog.show();
  }

  private void handleCommentsDialog(final MyCustomData dataRow) {
    final EditText commentsTxt = new EditText(this);
    commentsTxt.setHint(getString(R.string.comments_hint));
    commentsTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

    new AlertDialog.Builder(this)
        .setTitle(getString(R.string.comments_title))
        .setView(commentsTxt)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            final String comment = commentsTxt.getText().toString();
            dataRow.setContent(shortenIt(comment));
            attendance.setComments(comment);
            listAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
          }
        })
        .show();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final MyViewHolder viewHolder;
      if (convertView == null) {
        // No convertView in recycle pool yet
        convertView = mInflater.inflate(R.layout.attendance_row, parent, false);
        viewHolder = new MyViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (MyViewHolder) convertView.getTag();
        //Log.d(TAG, "Reusing Row for " + position);
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

  private void populateListContents() {
    for (int i = 0; i < ATTENDANCE_ELEM_LABELS.length; i++) {
      mData.add(new MyCustomData(IMAGES[i], ATTENDANCE_ELEM_LABELS[i], ""));
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

  private String shortenIt(String data) {
    return data.substring(0, data.length() > SHORTEN_LEN ? SHORTEN_LEN : data.length()) + "...";
  }

}
