package com.example.sorting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.Intent;
//import android.content.pm.PackageInfo; // loadAllPackages() 실행 시 필요
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
//import android.util.Log; // loadAllPackages() 실행 시 필요
import android.view.View;
import android.widget.Button;
import com.skt.Tmap.TMapTapi;

import java.util.HashMap;

//import java.util.List; // loadAllPackages() 실행 시 필요


public class TmapActivity extends AppCompatActivity {

    private Button ib_tMap;
    String packageName = "com.skt.tmap.ku";
    String APIKEY = "l7xxcdd63787be6c4e00aa5089373925bd14";

    /* private void loadAllPackages(){
         List<PackageInfo> appsInfo = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
         for(int i=0; i<appsInfo.size(); i++){
             PackageInfo packageInfo = appsInfo.get(i);
             Log.d("test", "설치된 패키지명 = " + packageInfo.packageName);
         }
     }*/
    // 설치된 패키지명 모두 출력하는 함수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TMapTapi tMapTapi = new TMapTapi(this);
        PackageManager pm = getPackageManager();
        HashMap pathInfo = new HashMap();

        tMapTapi.setSKTMapAuthentication(APIKEY);
        tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                System.out.println("APIKEY 인증 성공 !");
                //loadAllPackages();
            }

            @Override
            public void SKTMapApikeyFailed(String errorMsg) {
                System.out.println("APIKEY? 인증 실패 !");
            }
        });


        // boolean isTmapApp = tMapTapi.isTmapApplicationInstalled();
        // Tmap 설치여부 확인


        ib_tMap = findViewById(R.id.tmap);
        ib_tMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA); // 여기서 예외발생함 -> 해결!!!!!
                    pathInfo.put("rGoName", ""); //  목적지 이름 (안 넣어도 X, Y 좌표에 따라 자동 검색)
                    pathInfo.put("rGoX", "126.8911457"); // 목적지 X좌표 (경도)
                    pathInfo.put("rGoY", "37.50861147"); // 목적지 Y좌표 (위도)
                    tMapTapi.invokeRoute(pathInfo); // tmap 연동하여 출발지, 목적지, 경유지를 이용한 길안내 수행.
                    // 출발지 : 굳이 입력 안 해도 GPS 켜져 있으면 자동으로 연결됨
                    // 목적지 : 이름은 필수 사항 x, X좌표 & Y좌표 입력 필수
                    // 경유지(선택사항)

                } catch (PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                }
            }
        });
    }
}