package com.kaspian.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Search_all extends Activity {


    private ListView lv;

    // Listview Adapter

    // Search EditText
    EditText inputSearch;
    ImageView sb;
    ImageView sd;
    boolean nav;
    boolean season;
    int w;
    int defpos;
    int rows;
    ArrayAdapter<String> adapter;

    DataBase db;
    List<String> list;
    boolean intenthappen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.mothercolor_darker));
        }

        if(!Settings_Activity.loadNightMode(this)){
            setContentView(R.layout.sreach_all);
        }else{
            setContentView(R.layout.sreach_all_dark);
        }
        intenthappen = false;
        Intent intentsss = getIntent();
        nav = intentsss.getBooleanExtra("nav",false);
        season = intentsss.getBooleanExtra("season",false);
        w = intentsss.getIntExtra("s",0);
        defpos = intentsss.getIntExtra("defpos",0);

        sb = (ImageView) findViewById(R.id.sb);
        sd = (ImageView) findViewById(R.id.sd);

        list = new ArrayList<String>();

        db = new DataBase(this);


        if (MainMenu.tabnum == 1){


            rows = db.countRows("s1");
            for(int i =1 ; i<=rows ; i++){

                String title = db.getTitle(1,i);
                list.add(title);

            }



        }else{

            for(int x = 1; x <= MainMenu.tabnum; x++) {

                String sa = "s"+x;
                rows = db.countRows(sa);
                for(int i =1 ; i<=rows ; i++){


                    // add titles
                    String title = db.getTitle(x,i);
                    list.add(title);


                }

            }

        }

        db.close();


        lv = (ListView) findViewById(R.id.list_view);
        if(App.Divider){
            ColorDrawable sage;
            if(Settings_Activity.loadNightMode(this)){
                sage = new ColorDrawable(this.getResources().getColor(R.color.night_back_lighter));
            }else{
                sage = new ColorDrawable(this.getResources().getColor(R.color.divider));
            }
            lv.setDivider(sage);
            lv.setDividerHeight(2);
        }
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setTypeface(App.sans);

        int theme = 0;
        if(!Settings_Activity.loadNightMode(this)){
            theme = R.layout.search_list_item;
        }else{
            theme = R.layout.search_list_item_dark;
        }
        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, theme , R.id.Title_of_lv_items,list);

        lv.setAdapter(adapter);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Search_all.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        // after click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                final String item = lv.getItemAtPosition(position).toString();

                for(int x = 1; x <= MainMenu.tabnum; x++) {

                    String sa = "s"+x;
                    rows = db.countRows(sa);
                    for(int i =1 ; i<=rows ; i++){

                        String title = db.getTitle(x,i);

                        if (title.equalsIgnoreCase(item)){
                            intenthappen =true;
                            Intent go = new Intent(getApplicationContext(), ShowContent.class);
                            go.putExtra("w", i);
                            go.putExtra("s", x);

                            startActivity(go);
                            finish();
                        }
                    }
                }


            }

        });



        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intenthappen =true;
                if(nav){
                    Intent ii;
                    switch (defpos) {
                        case 1:
                            ii = new Intent(Search_all.this,MainMenu.class);
                            break;
                        case 2:
                            ii = new Intent(Search_all.this,SeasonMenu.class);
                            break;
                        case 3:
                            ii = new Intent(Search_all.this,Favorites.class);
                            break;
                        case 4:
                            ii = new Intent(Search_all.this,Search_all.class);
                            break;
                        case 5:
                            ii = new Intent(Search_all.this,Settings_Activity.class);
                            break;
                        case 6:
                            ii = new Intent(Search_all.this,AboutUs.class);
                            break;

                        default:
                            ii = new Intent(Search_all.this,MainMenu.class);
                            break;
                    }

                    startActivity(ii);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else if(season){
                    Intent ii = new Intent(Search_all.this,SeasonMenu.class);

                    startActivity(ii);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else{
                    Intent ii = new Intent(Search_all.this,ContentMenu.class);
                    ii.putExtra("s",w);

                    startActivity(ii);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });

        sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearch.setText("");
            }
        });



    }


    @Override
    public void onBackPressed() {
        intenthappen =true;
        if(nav){
            Intent ii;
            switch (defpos) {
                case 1:
                    ii = new Intent(Search_all.this,MainMenu.class);
                    break;
                case 2:
                    ii = new Intent(Search_all.this,SeasonMenu.class);
                    break;
                case 3:
                    ii = new Intent(Search_all.this,Favorites.class);
                    break;
                case 4:
                    ii = new Intent(Search_all.this,Search_all.class);
                    break;
                case 5:
                    ii = new Intent(Search_all.this,Settings_Activity.class);
                    break;
                case 6:
                    ii = new Intent(Search_all.this,AboutUs.class);
                    break;

                default:
                    ii = new Intent(Search_all.this,MainMenu.class);
                    break;
            }

            startActivity(ii);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else if(season){
            Intent ii = new Intent(Search_all.this,SeasonMenu.class);

            startActivity(ii);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else{
            Intent ii = new Intent(Search_all.this,ContentMenu.class);

            ii.putExtra("s",w);
            startActivity(ii);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if(!intenthappen && !NavConstructor.intenthappennav && App.Music && Settings_Activity.loadMusic(this)){
            BackgroundSoundService.player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.Music && Settings_Activity.loadMusic(this)) {
            if (!BackgroundSoundService.player.isPlaying()) {
                BackgroundSoundService.player.start();
            }
            adapter.notifyDataSetChanged();
        }

    }

}

