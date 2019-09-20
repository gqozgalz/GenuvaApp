package com.example.genuva;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class BookActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();
    ArrayList<SeatsModel> arr = new ArrayList<>();
    TextView selectedSeats,totalPrice;
    TextView firstclassprice,secondclassprice,thirdclassprice;
    ArrayList<String>arrofselectedchair=new ArrayList<>();
    String x;
    int totalprice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        GridView gridView = findViewById(R.id.grid_View_seats);
        Bundle bundle = getIntent().getExtras();
        final String place = bundle.getString("partyPlace");
        final String partyKey = bundle.getString("partyKey");
        final GridViewAdapter gridViewAdapter = new GridViewAdapter(arr, BookActivity.this);
        gridView.setAdapter(gridViewAdapter);
        firstclassprice=findViewById(R.id.first_class_price);
        secondclassprice=findViewById(R.id.second_class_price);
        thirdclassprice=findViewById(R.id.third_class_price);
        mRef.child(place).child(partyKey).child("Seats")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        SeatsModel val = dataSnapshot.getValue(SeatsModel.class);
                        arr.add(val);
                        if(val.getId().equals("1")){
                            firstclassprice.setText(val.getTicketPrice());
                        }
                        else if(val.getId().equals("11")){
                            secondclassprice.setText(val.getTicketPrice());
                        }
                        else if(val.getId().equals("25")){
                            thirdclassprice.setText(val.getTicketPrice());
                        }
                        gridViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        SeatsModel val = dataSnapshot.getValue(SeatsModel.class);
                        arr.set(Integer.parseInt(val.getId())-1,val);
                        gridViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        selectedSeats=findViewById(R.id.selected_seats);
        totalPrice=findViewById(R.id.total_price);
        totalPrice.setText("0");
        //String x=new String();
        //x=arr.get(1).getTicketPrice();
        Toast.makeText(BookActivity.this,x,Toast.LENGTH_SHORT).show();
        firstclassprice.setText("Asdad");
        secondclassprice.setText("sad");
        thirdclassprice.setText("asdasd");

        final Boolean[] selectedArrchk = new Boolean[30];


        Arrays.fill(selectedArrchk, Boolean.FALSE);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedtxt=new String();
                if(!arr.get(i).getSeat_state()){
                    if(!selectedArrchk[i]){
                        ImageView selectedchair=view.findViewById(R.id.chair_img);
                        selectedchair.setImageResource(R.drawable.ic_chair);
                        selectedArrchk[i]=true;
                        arrofselectedchair.add(Integer.toString(i+1));
                        totalprice+=Integer.parseInt(arr.get(i).getTicketPrice());
                        totalPrice.setText(Integer.toString(totalprice));

                        for(int j=0;j<arrofselectedchair.size();j++){
                            selectedtxt+=arrofselectedchair.get(j)+";";
                        }
                        selectedSeats.setText(selectedtxt);
                    }
                    else if(selectedArrchk[i]&&!arr.get(i).getSeat_state()){
                        ImageView selectedchair=view.findViewById(R.id.chair_img);
                        selectedchair.setImageResource(R.drawable.ic_seat_green);
                        selectedArrchk[i]=false;
                        arrofselectedchair.remove(FindItemIndex(arrofselectedchair));
                        totalprice-=Integer.parseInt(arr.get(i).getTicketPrice());
                        totalPrice.setText(Integer.toString(totalprice));

                        for(int j=0;j<arrofselectedchair.size();j++){
                            selectedtxt+=arrofselectedchair.get(j)+";";
                        }
                        selectedSeats.setText(selectedtxt);
                    }
                    else{
                        ImageView selectedchair=view.findViewById(R.id.chair_img);
                        selectedchair.setImageResource(R.drawable.ic_seat_red);
                        selectedArrchk[i]=false;
                        totalPrice.setText(Integer.toString(totalprice));
                        totalprice-=Integer.parseInt(arr.get(i).getTicketPrice());
                        totalPrice.setText(Integer.toString(totalprice));

                        for(int j=0;j<arrofselectedchair.size();j++){
                            selectedtxt+=arrofselectedchair.get(j)+";";
                        }
                        selectedSeats.setText(selectedtxt);
                    }
                }
                else{
                    Toast.makeText(BookActivity.this,"This plce Has Been Taken",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button bookBtn = findViewById(R.id.book_bot);
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrofselectedchair.isEmpty()){
                    Toast.makeText(BookActivity.this,"Thier is no seleced seat",Toast.LENGTH_SHORT).show();
                }
                else{
                    for(int i=0;i<arrofselectedchair.size();i++){

                        mRef.child(place).child(partyKey).child("Seats").child(arrofselectedchair.get(i)).child("seat_state").setValue(true);
                    }
                    Toast.makeText(BookActivity.this,"Done!",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
    public int FindItemIndex(ArrayList<String>arr){
        int x=0;
        for(int i=0;i<arr.size();i++){
            if(arrofselectedchair.get(i).equals(i+1)){
                x=i;
                break;
            }

        }
        return x;
    }
}
