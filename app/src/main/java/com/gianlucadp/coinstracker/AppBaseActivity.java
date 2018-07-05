package com.gianlucadp.coinstracker;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import java.util.Arrays;

public class AppBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddNewGroupFragment.OnGroupCreatedListener {
    public static final int RC_SIGN_IN = 1;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //User info
    private static String mUsername;
    private static String mUserId;


    //Firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference mDataBaseTransactions;
    private ChildEventListener mTransactionGroupsEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //To enable Android Iconics
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState);

        //Initialize view
        setContentView(R.layout.activity_app_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialize Firebase db
        if (mDatabase == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (savedInstanceState==null) {
                database.setPersistenceEnabled(true); //To work with offline data too
            }
            mDatabase = database.getReference();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        //Login procedure
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { //Login successful
                    onSignedInitialize(user);

                } else {    //Logout procedure
                    onSignedOutCleanup();
                    //Show login screen
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.mipmap.ic_launcher_round)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        //Show default main screen at launch
        if (savedInstanceState == null) {
            //Attach the transaction list fragment
            TransactionsListFragment mainFragment = new TransactionsListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fm_fragments_container, mainFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    private void onSignedInitialize(FirebaseUser user) {
        mUsername = user.getDisplayName();
        mUserId = user.getUid();

        // Add a reference to transaction groups and transactions
        mDataBaseTransactions = mDatabase.child("users").child(mUserId).child("transaction_groups");
        attachDatabaseReadListener();

    }


    private void onSignedOutCleanup() {
        mUsername = null;
        mUserId = null;
        detachDatabaseReadListener();

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_item_main_page) {

        } else if (id == R.id.menu_item_history) {

        } else if (id == R.id.menu_item_statistics) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attachDatabaseReadListener() {
        //Handle the listener for transactions groups
        if (mTransactionGroupsEventListener == null) {
            mTransactionGroupsEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    TransactionsListFragment transactionsListFragment = (TransactionsListFragment) getSupportFragmentManager().findFragmentById(R.id.fm_fragments_container);
                    TransactionGroup transactionGroup = dataSnapshot.getValue(TransactionGroup.class);
                    transactionGroup.setFirebaseId(dataSnapshot.getKey());
                    switch (transactionGroup.getType()) {
                        case REVENUE:
                            transactionsListFragment.getRevenueAdapter().addItem(transactionGroup);
                            break;
                        case DEPOSIT:
                            transactionsListFragment.getDepositAdapter().addItem(transactionGroup);
                            break;
                        case EXPENSE:
                            transactionsListFragment.getExpenseAdapter().addItem(transactionGroup);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
            };

            mDataBaseTransactions.addChildEventListener(mTransactionGroupsEventListener);
        }


    }

    private void detachDatabaseReadListener() {

        if (mTransactionGroupsEventListener != null) {
            mDataBaseTransactions.removeEventListener(mTransactionGroupsEventListener);
            mTransactionGroupsEventListener = null;
        }
    }


    @Override
    public void onGroupCreated(TransactionGroup transactionGroup) {
        mDatabase.child("users").child(mUserId).child("transaction_groups").push().setValue(transactionGroup);

    }


}
