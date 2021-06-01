package com.example.coviddi.ViewModels;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.coviddi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Test_activity extends AppCompatActivity {
    private FirebaseFirestore database;
    private DatabaseReference myRef;
    private List<View> allEds;
    TextView Question;
    int i;
    String collectionname;
    TestResult fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    RadioButton Answer1,Answer2,Answer3;

    ArrayList<Integer> ans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.testlayout1);
        database = FirebaseFirestore.getInstance();
        allEds = new ArrayList<>();
        SharedPreferences sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String savedText = sPref.getString("loc", "NO");
        if(savedText=="ru")
        collectionname="tests";
        else
            collectionname="tests2";
        ans=new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.e("Язык", Resources.getSystem().getConfiguration().locale.getLanguage());

        final LinearLayout linear = findViewById(R.id.linear);
        i=0;
        database.collection(collectionname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(i<10) {
                                    final View view = getLayoutInflater().inflate(R.layout.test_item, null);

                                    Question = view.findViewById(R.id.question);
                                    Answer1 = view.findViewById(R.id.answer1);
                                    Answer2 = view.findViewById(R.id.answer2);
                                    Answer3 = view.findViewById(R.id.answer3);
                                    allEds.add(view);
                                    linear.addView(view);
                                    ImageView nolImage=new ImageView(Test_activity.this);
                                    nolImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,40));
                                    linear.addView(nolImage);
                                    Question.setText(document.getString("Question"));
                                    Answer1.setText(document.getString("Answer1"));
                                    Answer2.setText(document.getString("Answer2"));
                                    if (document.getString("Answer3") != null)
                                        Answer3.setText(document.getString("Answer3"));
                                    else
                                        Answer3.setVisibility(View.INVISIBLE);
                                    i++;
                                }
                                else
                                {

                                    for(int j=1;j<11;j++)
                                    {
                                       // Log.e("Кладу в Ответы",Integer.parseInt(document.getString(j+""))+"");
                                        if(j!=10)
                                        ans.add(Integer.parseInt(document.getString(j+"")));
                                        else
                                            ans.add(Integer.parseInt(document.getString("91")));
                                    }
                                    Button check=new Button(getApplicationContext());
                                    check.setText(getResources().getText(R.string.check));
                                    check.setLayoutParams(
                                            new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT)
                                    );
                                    check.setId(R.id.check);
                                    check.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int otv=0;
                                            int score=0;
                                            Log.e("Размер",allEds.size()+"");
                                            for(int f=0;f<allEds.size();f++)
                                            {
                                                RadioGroup RadGr = ((RadioGroup) allEds.get(f).findViewById(R.id.RadGr));
                                                int ansUser = RadGr.getCheckedRadioButtonId();
                                                Log.e(ansUser+"","FID");
                                                switch(ansUser)
                                                {
                                                    case(R.id.answer1):
                                                        otv=1;
                                                        break;
                                                    case(R.id.answer2):
                                                        otv=2;
                                                        break;
                                                    case(R.id.answer3):
                                                        otv=3;
                                                        break;
                                                }
                                                Log.e("OTV",otv+"");
                                                if(otv==ans.get(f)) {
                                                    score++;

                                                }
                                                otv=0;

                                            }
                                            Log.e("Результат", score+"");
                                            RadioGroup RadGr;
                                            for(int k=0;k<allEds.size();k++)
                                            {RadGr= ((RadioGroup) allEds.get(k).findViewById(R.id.RadGr));
                                                if(RadGr != null)
                                                RadGr.clearCheck();
                                            }
                                            fragmentManager = getFragmentManager();
                                            fragmentTransaction = fragmentManager.beginTransaction();
                                            fragment =new TestResult();
                                            fragmentTransaction.add(R.id.container, fragment);
                                            Bundle bundle=new Bundle();
                                            bundle.putInt("score",score);
                                            fragment.setArguments(bundle);
                                            fragmentTransaction.commit();
                                        }
                                    });
                                    //check.setBackground(R.drawable.buttonround);
                                    check.setBackground(ContextCompat.getDrawable(Test_activity.this, R.drawable.ripple_effect));
                                    check.setTextColor(getResources().getColor(R.color.white));
                                    linear.addView(check);

                                }
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

