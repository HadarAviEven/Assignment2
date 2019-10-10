package com.hadar.assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final List<Person> people = new ArrayList<>();
    private List<HashMap<String, String>> fullDictionary = new ArrayList<>();
    private Button addButton;
    private Button clearButton;
    private Person p;
    private SimpleAdapter simpleAdapter;
    private ListView lv;
    private boolean noItems;
    public static String keyForName = "KEY_FOR_NAME";
    public static String keyForGender = "KEY_FOR_GENDER";
    public static String keyForStreet = "KEY_FOR_MB_STREET";
    public static String keyForCountry = "KEY_FOR_COUNTRY";
    public static String keyForPostcode = "KEY_FOR_POSTCODE";
    public static final int REQUEST_CODE = 1;
    public static MySQLiteHelper mySQL;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noItems = true;
        context = this;
        mySQL = new MySQLiteHelper(this);
        addButton = findViewById(R.id.addBtn);
        clearButton = findViewById(R.id.clrBtn);

        add();
        clear();
    }

    public void add() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create client
                OkHttpClient client = new OkHttpClient();

                // create a request object
                String url = "https://randomuser.me/api";
                Request request = new Request.Builder().url(url).build();

                // enqueue - add item to the request queue
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    // SUCCESS!!!
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String myResponse = response.body().string();

                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();

                            WebResult wr = gson.fromJson(myResponse, WebResult.class);

                            String fullName = wr.results[0].name.first + " " +
                                    wr.results[0].name.last;

                            String fullStreet = wr.results[0].location.street.number +
                                    " " + wr.results[0].location.street.name;

                            p = new Person(
                                    fullName,
                                    wr.results[0].gender,
                                    fullStreet,
                                    wr.results[0].location.country,
                                    wr.results[0].location.postcode);

                            people.add(p);

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    HashMap<String, String> dic = new HashMap<>();

                                    dic.put(keyForName, p.getName());
                                    dic.put(keyForGender, p.getGender());
                                    dic.put(keyForStreet, p.getStreet());
                                    dic.put(keyForCountry, p.getCountry());
                                    dic.put(keyForPostcode, p.getPostcode());

                                    fullDictionary.add(dic);

                                    String[] from = {keyForName, keyForGender,
                                            keyForStreet, keyForCountry, keyForPostcode};
                                    int[] to = {R.id.name, R.id.gender,
                                            R.id.street, R.id.country, R.id.postcode};
                                    lv = findViewById(R.id.myListView);
                                    simpleAdapter = new SimpleAdapter(
                                            getBaseContext(), fullDictionary,
                                            R.layout.oneitem_layout, from, to);
                                    lv.setAdapter(simpleAdapter);

                                    addToSQL(p);
                                    noItems = false;

                                    longClick();

                                    shortClick();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void clear() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noItems) {
                    deleteAllSQL();
                    Toast.makeText(MainActivity.this, "No Data!", Toast.LENGTH_SHORT).show();
                    return;
                }
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Clear the List")
                            .setMessage("Are you sure you want to delete all?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteAllSQL();
                                    noItems = true;
                                    fullDictionary.clear();
                                    simpleAdapter.notifyDataSetChanged();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        });
    }

    public void longClick() {
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int itemPosition, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete '" + fullDictionary.get(itemPosition).get(keyForName) + "'")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeFromSQL(fullDictionary.get(itemPosition).get(keyForName));
                                fullDictionary.remove(itemPosition);
                                simpleAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();

                                if (simpleAdapter.isEmpty()) {
                                    noItems = true;
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return true;
            }
        });
    }

    public void shortClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                HashMap<String, String> hashMap = fullDictionary.get(itemPosition);

                Intent intent = new Intent(getBaseContext(), InformationActivity.class);

                intent.putExtra(keyForName, hashMap.get(keyForName));
                intent.putExtra(keyForGender, hashMap.get(keyForGender));
                intent.putExtra(keyForStreet, hashMap.get(keyForStreet));
                intent.putExtra(keyForCountry, hashMap.get(keyForCountry));
                intent.putExtra(keyForPostcode, hashMap.get(keyForPostcode));

                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String currentPerson = data.getStringExtra(InformationActivity.PERSON_NAME);
                String streetUpdate = data.getStringExtra(InformationActivity.NEW_STREET);
                String countryUpdate = data.getStringExtra(InformationActivity.NEW_COUNTRY);
                String postcodeUpdate = data.getStringExtra(InformationActivity.NEW_POSTCODE);

                for (HashMap<String, String> person : fullDictionary) {
                    if (person.get(keyForName).equals(currentPerson)) {
                        person.put(keyForStreet, streetUpdate);
                        person.put(keyForCountry, countryUpdate);
                        person.put(keyForPostcode, postcodeUpdate);
                    }
                }
                simpleAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addToSQL(Person p) {
        String correct_name = p.getName();
        String correct_gender = p.getGender();
        String correct_street = p.getStreet();
        String correct_country = p.getCountry();
        String correct_postcode = p.getPostcode();

        p = new Person(correct_name, correct_gender, correct_street, correct_country, correct_postcode);

        if (mySQL.insertData(p)) {
//            Toast.makeText(getBaseContext(), "הצלחנו להכניס P", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getBaseContext(), "לא הצלחנו להכניס P", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromSQL(String name) {
        if (mySQL.deleteData(name)) {
//            Toast.makeText(getBaseContext(), "הצלחנו למחוק "+name, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getBaseContext(), "לא הצלחנו למחוק P", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllSQL() {
        if (mySQL.deleteAllData()) {
//            Toast.makeText(getBaseContext(), "הצלחנו למחוק SQL", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getBaseContext(), "לא הצלחנו למחוק SQL", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateSQL(String name, String gender, String street, String country, String postcode) {
        if (mySQL.updateData(name, gender, street, country, postcode)) {
//            Toast.makeText(context, "הצלחנו לעדכן "+name, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "לא הצלחנו לעדכן P", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
