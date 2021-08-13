package com.example.sorting;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ListMaking extends AppCompatActivity {
    private Button newList;
    private Button oldList;
    private DBHelper mDBHelper = new DBHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_listmaking);

        oldList = findViewById(R.id.btn_oldList); // btn_oldList 버튼을 눌렀을 때 기존 DB에 추가
        oldList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//                Intent intent = new Intent(ListMaking.this,reading.class);
//                startActivity(intent);
            }
        });

        newList = findViewById(R.id.btn_newList); // btn_newList 버튼을 눌렀을 때 새로운 DB 생성
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDBHelper.dbInitialize(); // db 초기화

//                Intent intent = new Intent(ListMaking.this,reading.class);
//                startActivity(intent);
            }
        });
    }
}
