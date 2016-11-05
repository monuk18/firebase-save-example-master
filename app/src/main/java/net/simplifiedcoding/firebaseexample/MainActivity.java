package net.simplifiedcoding.firebaseexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextAddress;
    private TextView textViewPersons;
    private Button buttonSave;
    private AdView mAdView;
    private StaggeredGridLayoutManager newlayoutmanger;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-8549203901946162/9488159132");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
       // AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);



        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        textViewPersons = (TextView) findViewById(R.id.textViewPersons);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating firebase object
                Firebase ref = new Firebase(Config.FIREBASE_URL);

                //Getting values to store
                String name = editTextName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();

                if (name.equalsIgnoreCase("") || address.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Nmae or address is empty", Toast.LENGTH_LONG).show();
                    return;
                }

                //Creating Person object
                Person person = new Person();


                //Adding values
                person.setName(name);
                person.setAddress(address);

                int data = name.hashCode() + address.hashCode();

                //Storing values to firebase
                ref.child("Person" + data).setValue(person);


                //Value event listener for realtime data update
                ref.addValueEventListener(new ValueEventListener() {
                    List<Person> persons = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String string = null;
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //Getting the data from snapshot
                            Person person = postSnapshot.getValue(Person.class);

                            //Adding it to a string
                            string += "Name: " + person.getName() + "\nAddress: " + person.getAddress() + "\n\n";
                            //Displaying it on textview
                          //  textViewPersons.setText(string);

                            persons.add(person);

                        }


                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                        Recycler_View_Adapter adapter = new Recycler_View_Adapter(persons, getApplication());
                        //new layout StaggeredGridLayoutManager
                        newlayoutmanger = new StaggeredGridLayoutManager(3,1);
                        recyclerView.setLayoutManager(newlayoutmanger);
                        recyclerView.setAdapter(adapter);

                        // LayoutManager
                        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        /*recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

                            @Override
                            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                                return true;
                            }

                            @Override
                            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                                Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                            }
                        });*/


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });


            }
        });


    }

    public List<Person> fill_with_data(){

        List<Person> data = new ArrayList<>();



        return  data;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
