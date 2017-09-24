package com.example.my_boss.questrip;

/**
 * Created by tono on 2016/10/24.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;
import android.widget.Toast;

public class parseJsonpOfDirectionAPI {

    Instagram_connect_Activity ma;

    public Object[] parse(JSONObject jObject){
        String temp = "";
        int minute = 0;

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jsonRoutes = null;
        JSONArray jsonLegs = null;
        JSONArray jsonSteps = null;

        try {

            jsonRoutes = jObject.getJSONArray("routes");

            for(int i=0;i<jsonRoutes.length();i++){
                jsonLegs = ( (JSONObject)jsonRoutes.get(i)).getJSONArray("legs");

                //スタート地点・住所
                String s_address = (String)((JSONObject)(JSONObject)jsonLegs.get(i)).getString("start_address");

                ma.info_A = s_address;

                //到着地点・住所
                String e_address = (String)((JSONObject)(JSONObject)jsonLegs.get(i)).getString("end_address");

                ma.info_B = e_address;

                String distance_txt = (String)((JSONObject)((JSONObject)jsonLegs.get(i)).get("distance")).getString("text");

                temp += distance_txt + "<br><br>";

                String distance_val = (String)((JSONObject)((JSONObject)jsonLegs.get(i)).get("distance")).getString("value");

                temp += distance_val + "<br><br>";

                List path = new ArrayList<HashMap<String, String>>();


                for(int j=0;j<jsonLegs.length();j++){
                    jsonSteps = ( (JSONObject)jsonLegs.get(j)).getJSONArray("steps");


                    for(int k=0;k<jsonSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jsonSteps.get(k)).get("polyline")).get("points");


                        String instructions = (String)((JSONObject)(JSONObject)jsonSteps.get(k)).getString("html_instructions");
                        String duration_value = (String)((JSONObject)((JSONObject)jsonSteps.get(k)).get("duration")).getString("value");
                        String duration_txt = (String)((JSONObject)((JSONObject)jsonSteps.get(k)).get("duration")).getString("text");

                        temp += instructions + "/" + duration_value + " m /" + duration_txt + "<br><br>";

                        System.out.println(temp);

//                        System.out.println("=========================================================");
//                        System.out.println(duration_txt);

//                        時間や分のストリングを時間に変換（分）

                        if(duration_txt.indexOf("時間") != -1){
                            String[] spl = duration_txt.split("時間", 0);
//                            時間計算
                            minute += Integer.parseInt(spl[0]) * 60;
//                            分計算
                            String spl_2 = duration_txt.substring(0, spl[1].length()-1);
                            minute += Integer.parseInt(spl_2);
                        }
                        else{
                            duration_txt = duration_txt.substring(0, duration_txt.length()-1);
                            minute += Integer.parseInt(duration_txt);
                        }
//                        System.out.println("分========："+(duration_txt));
//                        System.out.println("分："+Integer.parseInt(duration_txt));
//                        時間や分のストリングを時間に変換（時間）
//                        if(duration_txt.length() >= 4){
////                            x時間yy分
//                            duration_txt = duration_txt.substring(0, duration_txt.length()-5);
//                            minute += Integer.parseInt(duration_txt) * 60;
//                            System.out.println("分1："+Integer.parseInt(duration_txt));
//                        }
//                        if(duration_txt.length() >= 3 && Integer.parseInt(duration_txt)< 10){
////                            x時間y分
//                            duration_txt = duration_txt.substring(0, duration_txt.length()-4);
//                            minute += Integer.parseInt(duration_txt) * 60;
//                            System.out.println("分2："+Integer.parseInt(duration_txt));
//                        }


//                        System.out.println(temp);
//                        System.out.println("=========================================================");

                        List<LatLng> list = decodePoly(polyline);

                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    //ルート座標
                    routes.add(path);
                }
//                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//                System.out.println(minute);
//                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

                //ルート情報
                ma.posinfo = temp;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

//        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
//        System.out.println(routes);
//        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

//        return routes;
//        ========================================================v
//        オブジェクトクラスの使用により，複数の返り値
//        http://www.kab-studio.biz/Programing/OOPinJava/08/02.html
        Object return_value[] = new Object[2];
        return_value[0] = routes;
        return_value[1] = minute;
        return return_value;
//        ========================================================^
    }

    //座標データをデコード
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}