package com.ecokuska.gio.denunciapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_ALL = 3;
    private static final String TAG = "GIODEBUG_Login";
    private final int code_request=1234;
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WAKE_LOCK};
    Boolean tieneDNI;
    Boolean tieneUBIGEO;
    Boolean tieneNP;
    Boolean tieneNM;
    Boolean tieneFECHA;
    String cv1;
    String cv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://portal.mpfn.gob.pe/denuncias-en-linea/verificacion";

        if(!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        //Log.d("GIODEBUG_",response);
                        tieneDNI = response.contains("name=\"dni\" id='dni'");
                        tieneUBIGEO = response.contains("name='ubigeo' id='ubigeo'");
                        tieneNP = response.contains("name='np' value='' id='np'");
                        tieneNM = response.contains("name='nm' value='' id='nm'");
                        tieneFECHA = response.contains("placeholder ='SELECCIONE FECHA'");

                        Log.d("GIODEBUG_","tieneDNI "+tieneDNI);
                        Log.d("GIODEBUG_","tieneUBIGEO "+tieneUBIGEO);
                        Log.d("GIODEBUG_","tieneNP "+tieneNP);
                        Log.d("GIODEBUG_","tieneNM "+tieneNM);
                        Log.d("GIODEBUG_","tieneFECHA "+tieneFECHA);

                        String substringCaptcha[] = response.split("name=\"dni\" id='dni'");
                        //name='captcha' id='captcha'
                        String substringCaptcha2[] = substringCaptcha[1].split("rnd=");
                        Log.d("GIODEBUG_",substringCaptcha2[1].substring(0,9));
                        setCaptcha(substringCaptcha2[1].substring(0,9));

                        String sub2[]= substringCaptcha[1].split("name=\"cv1\" value=");
                        String sub3[]= substringCaptcha[1].split("name=\"cv2\" value=");
                        cv1 = sub2[1].substring(1,2);
                        cv2 = sub3[1].substring(1,2);
                        Log.d("GIODEBUG_","_"+cv1+" , "+cv2);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.d("GIODEBUG_","ERROR!!!!");
            }

        }
        );
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void setCaptcha(String id){
        ImageView foto = (ImageView) findViewById(R.id.imagecaptcha);
        Glide.with(getBaseContext())
                .load("http://portal.mpfn.gob.pe/denuncias-en-linea/captcha?rnd="+id)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(foto);

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case code_request:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "COARSE LOCATION permitido", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "COARSE LOCATION no permitido", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void goValidar(View view){

        RequestQueue queue = Volley.newRequestQueue(this);
        //*/http://portal.mpfn.gob.pe/denuncias-en-linea/entrarvalidacion?dni=70226547&cv1=4&cv2=3&rcv1=EDGAR&rcv2=HUARANGA&captcha=2PU8
        String url ="http://portal.mpfn.gob.pe/denuncias-en-linea/entrarvalidacion?dni="+Utils.getDni();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        Log.d("GIODEBUG_",response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.d("GIODEBUG_","ERROR!!!!");
            }

        }
        )/*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                EditText editText = (EditText) findViewById(R.id.capchaET);
                final String valor = String.valueOf(editText.getText());
                Log.d("GIODEUB_C",valor+"_");
                Map<String,String> params = new HashMap<>();
                params.put("func", "ingresar");
                if (tieneDNI) params.put("dni", String.valueOf(Utils.getDni()));
                if (tieneFECHA) params.put("fn", Utils.getFecha_nacimiento());
                if (tieneUBIGEO) params.put("ubigeo", String.valueOf(Utils.getUbigeo()));
                if (tieneNP) params.put("np", Utils.getPadre());
                if (tieneNM) params.put("nm", Utils.getMadre());
                params.put("captcha", valor);
                return params;
            }
        }*/;
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

}
