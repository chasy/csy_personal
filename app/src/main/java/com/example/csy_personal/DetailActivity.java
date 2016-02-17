package com.example.csy_personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by 차승용 on 2016-02-17.
 * 일정 상세 페이지
 */
public class DetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.detail);

        Intent intent = getIntent();

        String year= intent.getStringExtra("year");
        String month = intent.getStringExtra("month");
        String days = intent.getStringExtra("days");

        TextView textView = (TextView)findViewById(R.id.lblNowDay);
        textView.setText(year + "-" + month + "-" + days);
        ReadFile();
    }

    public void btnBack(View v){
        finish();
    }

    public void btnSave(View v){
        EditText title = (EditText)findViewById(R.id.txtTitle);
        EditText content = (EditText)findViewById(R.id.txtContent);
        TextView nowday = (TextView)findViewById(R.id.lblNowDay);

        final String fileName = nowday.getText().toString();

        try{
            FileOutputStream fos = openFileOutput(fileName,Context.MODE_PRIVATE);
            String str = content.getText().toString();
            fos.write(str.getBytes());
            fos.close();
            Toast.makeText(this,"저장되었습니다.",Toast.LENGTH_SHORT).show();
            finish();;
        }
        catch(Exception e){
            Toast.makeText(this,"에러!!",Toast.LENGTH_SHORT).show();
        }
        finally {

        }

    }

    public void ReadFile(){
        try{
            TextView nowday = (TextView)findViewById(R.id.lblNowDay);

            final String fileName = nowday.getText().toString();

            /*File files = new File(fileName);

            if(files.exists()==true) {*/

                FileInputStream fis = openFileInput(fileName);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                String str = new String(buffer);

                EditText content = (EditText) findViewById(R.id.txtContent);
                content.setText(str);
                fis.close();
          /*  }
            else{
                Toast.makeText(this,"저장된 내용이 없습니다.",Toast.LENGTH_SHORT).show();
            }*/
        }
        catch(Exception e){
            //Toast.makeText(this,"에러!!",Toast.LENGTH_SHORT).show();
        }
    }
}
