package com.example.sorting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class reading extends AppCompatActivity {
    String trackingNum;  //운송장번호
    String address;      //주소
    double latitude;     //위도
    double longitude;    //경도

    private RecyclerView mRv_sorting;
    private ArrayList<AddressItem> mAddressItems;
    private DBHelper mDBHelper = new DBHelper(this);
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        setInit();
    }

    private void setInit() {
        FloatingActionButton btn_add = findViewById(R.id.btn_add);
        mAddressItems = new ArrayList<>();


        loadRecentDB();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //팝업 창 띄우기
//                Dialog dialog = new Dialog(reading.this, android.R.style.Theme_Material_Light_Dialog);
//                dialog.setContentView(R.layout.dialog_edit);
//                EditText et_number = dialog.findViewById(R.id.et_number);
//                EditText et_address = dialog.findViewById(R.id.et_address);
//                Button btn_ok = dialog.findViewById(R.id.btn_ok);
//                btn_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // insert DB
//                         mDBHelper.InsertAddress(et_number.getText().toString(),et_address.getText().toString());
//
//                        // insert UI
//                        AddressItem item = new AddressItem();
//                        item.setNumber(et_number.getText().toString());
//                        item.setAddress(et_address.getText().toString());
//                        mAdapter.addItem(item);
//                        mRv_sorting.smoothScrollToPosition(0);
//                        dialog.dismiss();
//                        Toast.makeText(reading.this, "할일 목록에 추가 되었습니다 !", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                dialog.show();
                scanCode();
            }
        });
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);    //세로모드 지원하기 위
        integrator.setOrientationLocked(false); //이것도
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("코드 스캔 기기"); //스캐너 하단부에 메세지 띄움
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GeocodeUtil geocodeUtil = new GeocodeUtil(reading.this);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data); // 결과값 파싱
        StringTokenizer st;

        if (result != null) { //null 아니면 정상적으로 qr코드 스캐너가 전달한 ActivityResult 값
            if (result.getContents() != null) { //스캐너가 qr코드를 정상적으로 인식 했다
                st = new StringTokenizer(result.getContents(), "\n");
                trackingNum = st.nextToken();   //운송장번호 전달
                address = st.nextToken();        //주소 전달
                latitude = geocodeUtil.getGeoLocationListUsingAddress(address).get(0).latitude; //위도 전달
                longitude = geocodeUtil.getGeoLocationListUsingAddress(address).get(0).longitude;   //경도 달

                // insert DB
                mDBHelper.InsertAddress(trackingNum,address,latitude,longitude);
                // insert UI
                AddressItem item = new AddressItem();
                item.setNumber(trackingNum);
                item.setAddress(address);
                item.setLatitude(latitude);
                item.setLongitude(longitude);
                mAdapter.addItem(item);
                mRv_sorting.smoothScrollToPosition(0);
                Toast.makeText(reading.this, "할일 목록에 추가 되었습니다 !", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(trackingNum + "\n" + address + "\n" + latitude + "\n" + longitude);//이 쪽 만져서 토스트 메세지로 첫 번째 택배 두번째~~하면 될 듯?
                builder.setTitle("운송장 번호 확인");
                builder.setPositiveButton("스캔 다시 하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("끝내기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(this, "결과값 없음", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void loadRecentDB() {

        //저장되어 있던 DB를 가져온다.
        mAddressItems = mDBHelper.getAddressList();
        if(mAdapter == null){
            mAdapter = new CustomAdapter(mAddressItems,this);
            mRv_sorting = findViewById(R.id.rv_sorting);
            mRv_sorting.setHasFixedSize(true);
            mRv_sorting.setAdapter(mAdapter);
        }
    }
}

class GeocodeUtil { //주소 <=> 위도경도 변환
    final Geocoder geocoder;

    public static class GeoLocation { //이너클래스
        double latitude;
        double longitude;

        public GeoLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public GeocodeUtil(Context context) {   //생성자
        geocoder = new Geocoder(context);
    }

    public ArrayList<GeoLocation> getGeoLocationListUsingAddress(String address) {  //주소를 위도경도로 변환하는 메소
        ArrayList<GeoLocation> resultList = new ArrayList<>();
        try {
            List<Address> list = geocoder.getFromLocationName(address, 10);

            for (Address addr : list) {
                resultList.add(new GeoLocation(addr.getLatitude(), addr.getLongitude()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}