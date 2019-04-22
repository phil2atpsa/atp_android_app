package za.co.atpsa.atp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import za.co.atpsa.atp.fragments.CreditsBalance;
import za.co.atpsa.atp.fragments.Donation;
import za.co.atpsa.atp.fragments.Evs;
import za.co.atpsa.atp.fragments.FeedBack;
import za.co.atpsa.atp.fragments.MiniBill;
import za.co.atpsa.atp.fragments.Dashboard;

import za.co.atpsa.atp.fragments.Report;
import za.co.atpsa.products.Products;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Access;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.pusher.pushnotifications.PushNotifications;

public class Content extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    TextView  username;
    private Handler handler = new Handler();
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseInstanceId.getInstance().getInstanceId() .addOnSuccessListener(Content.this,
                new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
               // Log.e("newToken", newToken);

            }
        });





        PushNotifications.start(getApplicationContext(), getString(R.string.pusher_key));
        PushNotifications.addDeviceInterest("atp_notifications");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }


       /* FirebaseMessaging.getInstance().subscribeToTopic("atp_notifications")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       // String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            //msg = getString(R.string.msg_subscribe_failed);
                        }
                       // Log.d("Hello", msg);
                       // Toast.makeText(Content.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.hide();

        spref = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setUpDrawerMenu(navigationView);
        View headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.username);

        new Access(this, new OnServiceResponseListener() {
            @Override
            public void completed(Object object) {
                JSONObject jsonObject = (JSONObject) object;
                username.setText("Welcome, "+jsonObject.optString("first_name")+" "+jsonObject.optString("surname"));
                JSONObject sp = jsonObject.optJSONObject("sp");
                PushNotifications.start(getApplicationContext(), getString(R.string.pusher_key));
                PushNotifications.addDeviceInterest(sp.optString("webservice_username").concat(sp.optString("webservice_password")));

                editor.putString("username", jsonObject.optString("first_name")+" "+jsonObject.optString("surname")).commit();

            }

            @Override
            public void failed(ServiceException e) {
                Log.e("Request Failed", e.getMessage());

            }
        }, false).authorize(spref.getString("access_token", ""));

        new Products(this, new OnServiceResponseListener() {
            @Override
            public void completed(Object object) {
                JSONArray array = (JSONArray)object;
                Set<String>  product_list = new HashSet<String>();
                for(int i =0; i < array.length(); i++){
                    try {
                        JSONObject jsonObject = array.getJSONObject(i);
                        int product_id = jsonObject.optInt("product_id");
                        product_list.add(String.valueOf(product_id));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                editor.putStringSet("product_list",product_list).commit();

            }

            @Override
            public void failed(ServiceException e) {

            }
        }, true)
                .list(spref.getString("access_token",""));

        onNavigationItemSelected(null);


    }


    public void navigateTo(){
        MenuItem selected_menu = null;
        if(spref.contains("selected_menu")){
           // Toast.makeText(this, ""+spref.getInt("selected_menu", 2), Toast.LENGTH_LONG).show();
            try {
                int menu_id = spref.getInt("selected_menu", 2);
                selected_menu =  navigationView.getMenu().findItem(menu_id);
              //  Toast.makeText(this, ""+selected_menu.getItemId(), Toast.LENGTH_LONG).show();
            } catch(NullPointerException e){

            }
        }

        onNavigationItemSelected(selected_menu);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item != null ? item.getItemId() : -1;
        switch(id){
            case 999:
                editor.remove("first_time_run").remove("access_token").commit();

                editor.putBoolean("first_time_run", true).commit();
              //  editor.putInt("selected_menu", 999).commit();
                startActivity(new Intent(Content.this, Login.class));
                finish();
                break;
            case 2:
                editor.putInt("selected_menu", 2).commit();
                loadFragment("evs", null);
                break;
            case 3:
                editor.putInt("selected_menu", 3).commit();
                loadFragment("mini_bill", null);
                break;
            case 5:
                editor.putInt("selected_menu", 5).commit();
                loadFragment("donation", null);
                break;
            case 555:
                editor.putInt("selected_menu", 555).commit();
                loadFragment("credit_balances", null);
                break;
            case 666:
                editor.putInt("selected_menu", 666).commit();
                loadFragment("report", null);
                break;

            case 888:
                editor.putInt("selected_menu", 888).commit();
                loadFragment("feedback", null);
                break;
            case -1 :
            default:
                loadFragment("dashboard", null);
                break;
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpDrawerMenu( NavigationView navigationView ){
        final Menu menu = navigationView.getMenu();
        menu.clear();

       // menu.Menu.NONE,0, 0, "Home"


        MenuItem dashboard = menu.add(Menu.CATEGORY_CONTAINER,-1, 0, "Dashboard");
        dashboard.setIcon(R.drawable.ic_dashboard_black_24dp);



        final SubMenu products  =  menu.addSubMenu(Menu.CATEGORY_CONTAINER,1000,1,"My Products");

        //final MenuItem menuItem = menu.add(0,0, 0, "My Products");
      //  menu.getItem(0).setIcon(R.drawable.ic_paid_black_24dp);

       // products.setHeaderIcon(R.drawable.ic_paid_black_24dp);
       // products.setHeaderTitle("My Products");


        new Products(this, new OnServiceResponseListener() {
            @Override
            public void completed(Object object) {
                JSONArray jsonArray = (JSONArray) object;
                for(int i= 0 ; i < jsonArray.length(); i++) {
                    try {
                        final JSONObject o = jsonArray.getJSONObject(i);
                        if(o.getInt("id") != 1 && o.getInt("id") != 4 ) {
                           // SubMenu sub = menuItem.getSubMenu();
                            //sub.clear();

                            products.add(Menu.NONE, o.getInt("id"), i, o.getString("product_name"));

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL url = new URL("https://atpevs.co.za/storage/app/"+ o.getString("icon"));
                                            final Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    products.setIcon(new BitmapDrawable(getResources(), image));
                                                }
                                            });

                                        } catch (IOException e) {
                                            System.out.println(e);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();



                           /* MenuItem menuItem = menu.add(Menu.NONE, o.getInt("id"), i, o.getString("product_name"));
                            switch(o.getInt("id")){
                                case 2: // Evs
                                    menuItem.setIcon(R.drawable.evs);
                                    break;
                                case 3: // Mini Bill
                                    menuItem.setIcon(R.drawable.mini_bill);
                                    break;
                                case 4: // Mini Bill
                                    menuItem.setIcon(R.drawable.repeat_collections);
                                    break;
                                case 5: // Donation
                                    menuItem.setIcon(R.drawable.donation);
                                    break;
                            }*/
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                products.setGroupCheckable(Menu.NONE,true,true);

                int index  = jsonArray.length();
                index++;
                SubMenu app_menu  =  menu.addSubMenu(Menu.CATEGORY_CONTAINER,2000,index,"Manage");


                MenuItem item =  app_menu.add(Menu.NONE, 555, 0, "Credits Balance");
                item.setIcon(R.drawable.ic_menu_manage);

                item =  app_menu.add(Menu.NONE, 666, 1, "Reports");
                item.setIcon(R.drawable.ic_view_list_black_24dp);

            //    item =  app_menu.add(Menu.NONE, 777, 2, "Settings");
          //      item.setIcon(R.drawable.ic_settings_black_24dp);

                app_menu.setGroupCheckable(Menu.NONE,true,true);


                index++;
                SubMenu  communicate  =  menu.addSubMenu(Menu.CATEGORY_CONTAINER,2000,index,"Communicate");
                item =  communicate.add(Menu.NONE, 888, 0, "Send feedback");
                item.setIcon(R.drawable.ic_menu_send);

                item =   communicate.add(Menu.NONE, 999, 0, "Logout");
                item.setIcon(R.drawable.ic_exit_to_app_red_24dp);

                navigateTo();

            }

            @Override
            public void failed(ServiceException e) {

            }
        }, false).list(spref.getString("access_token", ""));
    }

    public void loadFragment(String name, @Nullable Bundle bundle) {
        Fragment f = null;
        if (bundle != null)
            f.setArguments(bundle);

        if("dashboard".equals(name)){
            f = new Dashboard();
            setTitle("Dashboard");
        }
        if("report".equals(name)){
            f = new Report();
            setTitle("Reports");
        }
        if("evs".equals(name)){
            f = new Evs();
            setTitle("Verification");
        }
        if("mini_bill".equals(name)){
            f = new MiniBill();
            setTitle("Payment Request");
        }
        if("donation".equals(name)){
            f = new Donation();
            setTitle("Repeat Collections");
        }
        if("credit_balances".equals(name)){
            f = new CreditsBalance();
            setTitle("Credit Balances");
        }
        if("feedback".equals(name)){
            f = new FeedBack();
            setTitle("Send Feedback");
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, f)
                .commitAllowingStateLoss();
    }



}
