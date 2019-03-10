package com.yasoft.smsar;



import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity {


    TextView userNameEdit, passwordEdit;
    TextView _signUp;
    Button _login;
    Intent intent;
    ImageView imageView;
    TextView mError;
    Smsar mFrag = new Smsar();
    SharedPreferences pref;
    private DBHelper mDBHelper;

    String email,name,username,password,pn;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _login =(Button)findViewById(R.id.logIn);
        _signUp=(TextView)findViewById(R.id.sginUp);


        //_signUp.setText(Html.fromHtml(String.format(getString(R.string.sign_up))));


   String text = "New ? Start Now By  <font color='blue'>"+String.format(getString(R.string.sign_up))+"</font>.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _signUp.setText(Html.fromHtml(text,  Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        } else {
            _signUp.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }
        pref = getSharedPreferences("user_details", MODE_PRIVATE);
            if(pref.contains("username")&&pref.contains("password"))
                 success();




       FragmentManager fm=getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.mainView, mFrag);
        ft.commit();

        imageView=(ImageView)findViewById(R.id.logo);

        mDBHelper =new DBHelper(this);


         mError=(TextView)findViewById(R.id.mError);
        //Define the variables
        userNameEdit=(EditText)findViewById(R.id.userName);
        passwordEdit=(EditText)findViewById(R.id.password);


        userNameEdit.setFocusableInTouchMode(true);
        passwordEdit.setFocusableInTouchMode(true);
        //End



        _signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               intent=new Intent(MainActivity.this,Signup.class);
               startActivity(intent);

            }
        });



    }


    public void logIN(View view)throws SQLException{

        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        prepareToInsert();
        if (validationVariable()) {
            try {

                Cursor rs = mDBHelper.getData(username);
                rs.moveToFirst();
                if (rs.getCount() > 0) {
                    String nam = rs.getString(rs.getColumnIndex(DBHelper.SMSAR_COLUMN_USERNAME));
                    String pass = rs.getString(rs.getColumnIndex(DBHelper.SMSAR_COLUMN_PASSWORD));
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("username", nam);
                    editor.putString("password",pass);
                    editor.apply();
                   // editor.commit();
                    if (password.equals(pass)) {
                     success();

                    } else
                        Toast.makeText(this, "Wrong Password", Toast.LENGTH_LONG).show();
                    if (!rs.isClosed()) {
                        rs.close();
                    }

                }
                else
                    Toast.makeText(this, "Wrong Username", Toast.LENGTH_LONG).show();
            }
                catch(SQLException e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

                }
            }
        else{
            mError.setText("Empty Fields");
            mError.setVisibility(View.VISIBLE);
        }

    }
    public boolean validationVariable () {
        if (username.isEmpty()||password.isEmpty())
            return false;

        return true;

    }

    private void success(){
        intent = new Intent(MainActivity.this, SmsarMainActivity.class);
        startActivity(intent);
        finish();
        this.finish();
    }

    public void closeSmsar(){
        Intent intent=new Intent(this,UserMainActivity.class);
        startActivity(intent);


    }
    public void prepareToInsert(){

        username=userNameEdit.getText().toString();
        if(!username.isEmpty()){
            password= passwordEdit.getText().toString();
            String space=username.charAt(username.length()-1)+"";
            username=username.toLowerCase();
            username=username.replaceAll(" ","");
        }


    }
}
