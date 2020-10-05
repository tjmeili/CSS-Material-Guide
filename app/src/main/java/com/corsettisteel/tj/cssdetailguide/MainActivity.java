package com.corsettisteel.tj.cssdetailguide;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SPINNER_POSITION = "spinner position", TAG_LIST_POSITION = "list position";
    private static boolean appStarted = false, loadingNewData = false, orientationChanged = false;
    private static int prevItem = 0, saveStateItemPos = -1;
    private ArrayList<Beam> beams;
    private ArrayList<Angle> angles;
    private ArrayList<Channel> channels;
    private ArrayList<Pipe> pipes;
    private ArrayList<Tube> recTubes;
    private ArrayList<Tube> cylTubes;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private CustomAdapter adapter;
    private RelativeLayout relativeLayout, relativeLayoutSpinner;
    private Spinner spinner;
    private ListView listView;
    private TextView tvDetail1, tvDetail2, tvDetail3, tvDetail4, tv1, tv2, tv3, tv4;
    private ImageView ivDesign;
    private ImageButton ibCalculator;
    private MediaPlayer player;
    private View selector;
    private int itemsToShow = 11, cellHeight, middleCell, firstVisibleItem, selectedList = -1;
    private Bundle savedInstanceState = null;

    private String currentSelectedItemWeight = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState;
        }

        tvDetail1 = (TextView) findViewById(R.id.tvDepth);
        tvDetail2 = (TextView) findViewById(R.id.tvWeb);
        tvDetail3 = (TextView) findViewById(R.id.tvWidth);
        tvDetail4 = (TextView) findViewById(R.id.tvFlange);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        spinner = (Spinner) findViewById(R.id.spinner);
        ivDesign = (ImageView) findViewById(R.id.ivDesign);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayoutSpinner = (RelativeLayout) findViewById(R.id.relativeLayoutSpinner);

        beams = new ArrayList<>();
        channels = new ArrayList<>();
        angles = new ArrayList<>();
        recTubes = new ArrayList<>();
        cylTubes = new ArrayList<>();
        pipes = new ArrayList<>();

        ibCalculator = (ImageButton) findViewById(R.id.ibCalculator);
        ibCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyWeightToClipboard();
                launchCalculator();
            }
        });

        populateData();
        initializePicker();
        initializeSpinner();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_SPINNER_POSITION, spinner.getSelectedItemPosition());
        outState.putInt(TAG_LIST_POSITION, listView.getFirstVisiblePosition());
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int height = listView.getHeight();
        cellHeight = height / itemsToShow;
        middleCell = itemsToShow / 2;

        adapter.setCellHeight(cellHeight);

        if (selector.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) selector.getLayoutParams();
            p.height = cellHeight;
            p.setMargins(0, cellHeight * middleCell, 0, 0);
            selector.requestLayout();
        }

        if (savedInstanceState != null) {
            spinner.setSelection(savedInstanceState.getInt(TAG_SPINNER_POSITION));
            listView.setSelection(savedInstanceState.getInt(TAG_LIST_POSITION));
        }

        /*if(selectedList != -1 && saveStateItemPos != -1){
            spinner.setSelection(selectedList);
            listView.setSelectionFromTop(saveStateItemPos, 0);
            saveStateItemPos = -1;
            selectedList = -1;
        } else if(!appStarted){
            listView.setSelection(adapter.getCount() / 2);
            firstVisibleItem = adapter.getCount() / 2;
            addEmpties();
            adapter.notifyDataSetChanged();
            appStarted = true;
        }*/

        /*if(hasFocus){
            adapter.setCellHeight(cellHeight);

            if(!appStarted){
                listView.setAdapter(adapter);

            }

            appStarted = true;
        }*/


    }

    private void initializePicker() {
        getLayoutInflater().inflate(R.layout.custom_picker, relativeLayout, true);
        listView = (ListView) relativeLayout.findViewById(R.id.listview);
        selector = (View) relativeLayout.findViewById(R.id.chooser);
        adapter = new CustomAdapter(this, R.layout.list_item, new ArrayList<Component>());
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE) {

                    View child = view.getChildAt(0);

                    if (child != null) {
                        firstVisibleItem = listView.getFirstVisiblePosition();
                        Rect r = new Rect(0, 0, child.getWidth(), child.getHeight());
                        double height = child.getHeight() * 1.0;

                        view.getChildVisibleRect(child, r, null);
                        if (Math.abs(r.height()) < (int) height / 2) {
                            firstVisibleItem++;
                            listView.setSelection(firstVisibleItem);


                        } else {
                            listView.setSelection(firstVisibleItem);
                            view.playSoundEffect(SoundEffectConstants.CLICK);
                        }
                        prevItem = firstVisibleItem;
                        int mid = (firstVisibleItem + listView.getLastVisiblePosition()) / 2;
                        //setDetails(mid + "", "", "", "");
                        displayItemDataAtPosition(mid);

                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != prevItem) {
                    int mi = firstVisibleItem + (itemsToShow / 2);
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                    //player.start();
                    if (!loadingNewData) {
                        displayItemDataAtPosition(mi);
                    }

                    //setDetails(mi + "", "", "", "");
                }
                prevItem = firstVisibleItem;

            }
        });

    }

    private void initializeSpinner() {
        spinnerAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_text, getResources().getStringArray(R.array.spinner_options));
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadingNewData = true;
                adapter.clear();
                switch (i) {
                    case 0:
                        adapter.addAll(angles);
                        showAngles();
                        break;
                    case 1:
                        adapter.addAll(beams);
                        showBeams();
                        break;
                    case 2:
                        adapter.addAll(channels);
                        showChannels();
                        break;
                    case 3:
                        adapter.addAll(pipes);
                        showPipes();
                        break;
                    case 4:
                        adapter.addAll(recTubes);
                        showRecTubes();
                        break;
                    case 5:
                        adapter.addAll(cylTubes);
                        showCylTubes();
                        break;
                }
                //setDetails("0", "0", "0", "0");


                adapter.notifyDataSetChanged();
                /*if(appStarted){
                    listView.setSelection(adapter.getCount() / 2);
                    firstVisibleItem = adapter.getCount() / 2;
                    addEmpties();
                }*/

                listView.setSelection(adapter.getCount() / 2);
                firstVisibleItem = adapter.getCount() / 2;
                addEmpties();

                loadingNewData = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void launchCalculator() {
        ArrayList<HashMap<String, Object>> items = new ArrayList<>();

        final PackageManager pm = getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for(PackageInfo pi : packs) {
            if(pi.packageName.toString().toLowerCase().contains("calcul")) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }

        if(items.size() >= 1) {
            String packageName = (String) items.get(0).get("packageName");
            Intent i = pm.getLaunchIntentForPackage(packageName);
            if(i != null) {
                startActivity(i);
            } else {
                Log.d("CALCULATOR", "launchCalculator: failed");
            }
        }
    }

    private void copyWeightToClipboard() {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard != null)
                clipboard.setText(currentSelectedItemWeight);
        } else {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("weight", currentSelectedItemWeight);
            if(clipboard != null)
                clipboard.setPrimaryClip(clip);
        }
    }



    private void addEmpties() {
        adapter.addEmpties(itemsToShow / 2);

    }

    private void displayItemDataAtPosition(int position) {
        int pos = position - (itemsToShow / 2);
        int selected = spinner.getSelectedItemPosition();
        switch (selected) {
            case 0:
                String wpf = angles.get(pos).getWpf();
                setDetails(wpf, "", "", "");
                currentSelectedItemWeight = wpf;
                break;
            case 1:
                Beam b = beams.get(pos);
                setDetails(b.getDepth(), b.getWeb(), b.getWidth(), b.getFlange());
                break;
            case 2:
                Channel c = channels.get(pos);
                setDetails(c.getDepth(), c.getWeb(), c.getWidth(), c.getFlange());
                break;
            case 3:
                Pipe p = pipes.get(pos);
                setDetails(p.getOd(), p.getThickness(), p.getWpf(), "");
                currentSelectedItemWeight = p.getWpf();
                break;
            case 4:
                Tube rt = recTubes.get(pos);
                setDetails(rt.getWpf(), "", "", "");
                currentSelectedItemWeight = rt.getWpf();
                break;
            case 5:
                Tube ct = cylTubes.get(pos);
                setDetails(ct.getWpf(), "", "", "");
                currentSelectedItemWeight = ct.getWpf();
                break;
            default:
                setDetails("0", "0", "0", "0");
        }
    }

    private void showAngles() {
        //show design image and details
        ivDesign.setImageResource(R.drawable.l);
        showWpfOnly();
    }

    private void showBeams() {
        //show design image and details
        ivDesign.setImageResource(R.drawable.w);
        showBeamDetails();
    }

    private void showChannels() {
        //show design image and details
        ivDesign.setImageResource(R.drawable.c);
        showBeamDetails();
    }

    private void showPipes() {
        //show design image and details
        ivDesign.setImageResource(R.drawable.pipe);
        showPipeDetails();
    }

    private void showRecTubes() {
        ivDesign.setImageResource(R.drawable.hss);
        showWpfOnly();
    }

    private void showCylTubes() {
        ivDesign.setImageResource(R.drawable.pipe);
        showWpfOnly();
    }

    private void setDetails(String s1, String s2, String s3, String s4) {
        tvDetail1.setText(s1);
        tvDetail2.setText(s2);
        tvDetail3.setText(s3);
        tvDetail4.setText(s4);
    }

    private void showWpfOnly() {
        tv1.setText("Weight/ft.:");
        tv2.setVisibility(View.INVISIBLE);
        tv3.setVisibility(View.INVISIBLE);
        tv4.setVisibility(View.INVISIBLE);
        tvDetail2.setVisibility(View.INVISIBLE);
        tvDetail3.setVisibility(View.INVISIBLE);
        tvDetail4.setVisibility(View.INVISIBLE);
    }

    private void showBeamDetails() {
        tv1.setText("Depth:");
        tv2.setText("Web:");
        tv3.setText("Width:");

        tv2.setVisibility(View.VISIBLE);
        tv3.setVisibility(View.VISIBLE);
        tv4.setVisibility(View.VISIBLE);
        tvDetail2.setVisibility(View.VISIBLE);
        tvDetail3.setVisibility(View.VISIBLE);
        tvDetail4.setVisibility(View.VISIBLE);
    }

    private void showPipeDetails() {
        tv1.setText("O.D.:");
        tv2.setText("Thickness:");
        tv3.setText("Weight/ft.:");

        tv2.setVisibility(View.VISIBLE);
        tv3.setVisibility(View.VISIBLE);
        tv4.setVisibility(View.INVISIBLE);
        tvDetail2.setVisibility(View.VISIBLE);
        tvDetail3.setVisibility(View.VISIBLE);
        tvDetail4.setVisibility(View.INVISIBLE);
    }

    private void populateData() {
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        dbHandler.createDataBase();
        SQLiteDatabase db = dbHandler.getDatabase();
        String query = "SELECT * FROM Beams";
        Cursor data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Beam b = new Beam();
            b.setShape(data.getString(0));
            b.setDepth(data.getString(1));
            b.setWeb(data.getString(2));
            b.setWidth(data.getString(3));
            b.setFlange(data.getString(4));
            beams.add(b);
        }

        query = "SELECT * FROM S_Beams";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Beam b = new Beam();
            b.setShape(data.getString(0));
            b.setDepth(data.getString(1));
            b.setWeb(data.getString(2));
            b.setWidth(data.getString(3));
            b.setFlange(data.getString(4));
            beams.add(b);
        }

        query = "SELECT * FROM Junior_Beams";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Beam b = new Beam();
            b.setShape(data.getString(0));
            b.setDepth(data.getString(1));
            b.setWeb(data.getString(2));
            b.setWidth(data.getString(3));
            b.setFlange(data.getString(4));
            beams.add(b);
        }

        query = "SELECT * FROM Channels";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Channel c = new Channel();
            c.setShape(data.getString(0));
            c.setDepth(data.getString(1));
            c.setWeb(data.getString(2));
            c.setWidth(data.getString(3));
            c.setFlange(data.getString(4));
            channels.add(c);
        }

        query = "SELECT * FROM Misc_Channels";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Channel c = new Channel();
            c.setShape(data.getString(0));
            c.setDepth(data.getString(1));
            c.setWeb(data.getString(2));
            c.setWidth(data.getString(3));
            c.setFlange(data.getString(4));
            channels.add(c);
        }

        query = "SELECT * FROM Angles";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Angle a = new Angle();
            a.setShape(data.getString(0));
            a.setWpf(data.getString(1));
            angles.add(a);
        }

        query = "SELECT * FROM Pipes";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Pipe p = new Pipe();
            p.setShape(data.getString(0));
            p.setOd(data.getString(1));
            p.setThickness(data.getString(2));
            p.setWpf(data.getString(3));
            pipes.add(p);
        }

        query = "SELECT * FROM Rect_Tubes";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Tube rt = new Tube();
            rt.setShape(data.getString(0));
            rt.setWpf(data.getString(1));
            recTubes.add(rt);
        }

        query = "SELECT * FROM Cylindrical_Tubes";
        data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Tube ct = new Tube();
            ct.setShape(data.getString(0));
            ct.setWpf(data.getString(1));
            cylTubes.add(ct);
        }

        db.close();

    }

}
