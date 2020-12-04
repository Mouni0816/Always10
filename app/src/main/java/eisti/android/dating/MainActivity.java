package eisti.android.dating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

        private cards cards_data;
        private arrayAdapter arrayAdapter;
        private int i;
        private FirebaseAuth mAuth;
        private String currentUid;
        private DatabaseReference userDb;
        private String userSex;
        private String oppositeUsersex;


        ListView listView;
        List<cards> rowItems;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            checkUserSex();

            userDb=FirebaseDatabase.getInstance().getReference().child("Users");
            mAuth=FirebaseAuth.getInstance();
            currentUid=mAuth.getCurrentUser().getUid();


            rowItems = new ArrayList<cards>();


            arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );
            SwipeFlingAdapterView flingContainer=(SwipeFlingAdapterView)findViewById(R.id.frame);


            flingContainer.setAdapter(arrayAdapter);
            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    rowItems.remove(0);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    cards obj=(cards) dataObject;
                    String userId=obj.getUserId();
                    userDb.child(oppositeUsersex).child(userId).child("connections").child("nope").child(currentUid).setValue(true);
                    Toast.makeText(MainActivity.this,"Left!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    cards obj=(cards) dataObject;
                    String userId=obj.getUserId();
                    userDb.child(oppositeUsersex).child(userId).child("connections").child("yeps").child(currentUid).setValue(true);
                    isConnectionMatch(userId);
                    Toast.makeText(MainActivity.this,"Right!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                }
            });


            // Optionally add an OnItemClickListener
            flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    Toast.makeText(MainActivity.this,"click!",Toast.LENGTH_SHORT).show();
                }
            });

        }

    private void isConnectionMatch(String userId) {
            DatabaseReference currentUserConnectionsDb = userDb.child(userSex).child(currentUid).child("connections").child("yeps").child(userId);
            currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Toast.makeText(MainActivity.this,"NEW MATCH",Toast.LENGTH_LONG).show();
                        userDb.child(oppositeUsersex).child(snapshot.getKey()).child("connections").child("matches").child(currentUid).setValue(true);
                        userDb.child(userSex).child(currentUid).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public void checkUserSex(){
            final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference maleDb= FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
            maleDb.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if (snapshot.getKey().equals(user.getUid())){
                        userSex="Male";
                        oppositeUsersex="Female";
                        getOppositeUsers();
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            DatabaseReference FemaleDb= FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
            FemaleDb.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if (snapshot.getKey().equals(user.getUid())){
                        userSex="Female";
                        oppositeUsersex="Male";
                        getOppositeUsers();
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }

        public void getOppositeUsers(){
            DatabaseReference oppositesexDb= FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUsersex);
            oppositesexDb.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if (snapshot.exists()&& !snapshot.child("connections").child("nope").hasChild(currentUid) && !snapshot.child("connections").child("yeps").hasChild(currentUid)){
                        String profileImageUrl = "default";
                        if (snapshot.child("profileImageUrl").getValue()!=null){
                            if(!snapshot.child("profileImageUrl").getValue().equals("default")){
                                profileImageUrl=snapshot.child("profileImageUrl").getValue().toString();
                            }

                        }
                        cards item= new cards(snapshot.getKey(),snapshot.child("name").getValue().toString(),profileImageUrl);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }


    public void logoutUser(View view) {
            mAuth.signOut();
            Intent intent=new Intent (MainActivity.this,ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
    }

    public void goToSettings(View view) {
        Intent intent=new Intent (MainActivity.this,SettingsActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        return;

    }
}
