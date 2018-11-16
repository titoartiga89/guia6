package org.dev4u.hv.guia5_ejemplo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText txtArriba,txtAbajo;
    Button btnAplicar;

    RadioButton rbnBlanco,rbnNegro;


    ImageView imageView;

    final int PICK_PHOTO_FOR_AVATAR = 44;

    Resources resources;
    float scale;

    final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 23;


    Bitmap original, temporal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtArriba       = (EditText) findViewById(R.id.txtArriba);
        txtAbajo        = (EditText) findViewById(R.id.txtAbajo);
        btnAplicar      = (Button)   findViewById(R.id.btnAplicar);
        imageView       = (ImageView)findViewById(R.id.imageView);

        resources       = this.getResources();
        scale           = resources.getDisplayMetrics().density;

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararIMG();
            }
        });

        rbnBlanco       = (RadioButton)findViewById(R.id.rbnBlanco);
        rbnNegro        = (RadioButton)findViewById(R.id.rbnNegro);

        original  = ((BitmapDrawable)imageView.getDrawable()).getBitmap();





    }
    private void prepararIMG(){

        Bitmap bitmap = original;
        Bitmap resultado = aplicarTexto(
                txtArriba.getText().toString(),
                txtAbajo.getText().toString(),
                bitmap,
                scale);
        if(resultado!=null){
            imageView.setImageBitmap(resultado);
        }
    }

    private Bitmap aplicarTexto(String msg1,String msg2,Bitmap bitmap,float scale){
        try {
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();

            Log.d("Densidad : ",""+bitmap.getDensity());
            Log.d("Height   : ",""+bitmap.getHeight());
            Log.d("Width    : ",""+bitmap.getWidth());
            //para que abarque el 10% de la imagen
            //Ej:
            // 600 * 600 ->img
            // 600 -> 100%
            // 10% -> 60
            float factor = (float)(bitmap.getHeight()*0.06);

            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            if(rbnNegro.isChecked()){
                paint.setColor(Color.rgb(35,35, 35));
            }else{
                paint.setColor(Color.WHITE);
            }

            // text size in pixels
            paint.setTextSize((int) ( factor * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
            // poner el msj1
            Rect boundsMSG1 = new Rect();
            paint.getTextBounds(msg1, 0, msg1.length(), boundsMSG1);
            int xMSG1 = (bitmap.getWidth()/2) - (boundsMSG1.width()/2);//en medio
            int yMSG1 = (int)(bitmap.getHeight()*0.11); //al 12% de la altura
            canvas.drawText(msg1, xMSG1, yMSG1, paint);

            //log

            Log.d("Ancho  bmp : ",""+bitmap.getWidth());
            Log.d("Canvas     : ",""+canvas.getWidth());
            Log.d("Bounds     : ",""+boundsMSG1.width());
            Log.d("Coord X    : ",""+xMSG1);
            Log.d("xMSG*scale : ",""+(xMSG1*scale));

            int nX = (bitmap.getWidth()/2) - (boundsMSG1.width()/2);
            Log.d("Nueva X    : ",""+nX);

            // poner el msj2
            Rect boundsMSG2 = new Rect();
            paint.getTextBounds(msg2, 0, msg2.length(), boundsMSG2);
            int xMSG2 = (bitmap.getWidth()/2) - (boundsMSG2.width()/2);//en medio
            int yMSG2 = (int)(bitmap.getHeight()*0.95);
            canvas.drawText(msg2, xMSG2, yMSG2, paint);

            return bitmap;
        } catch (Exception e) {
            Log.e("ERROR",e.getMessage());
            Toast.makeText(this,"Se ha producido un error",Toast.LENGTH_SHORT).show();
            return null;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_opciones, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_abrir:
                abrirArchivo();
                break;
            case R.id.menu_guardar:
                guardarArchivo();
                break;
            case R.id.menu_cancelar:
                cancelar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // aqui va ir el temporal
    private void abrirArchivo(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }else{

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                Toast.makeText(this,"Error loading image",Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
            if(bmp!=null){
                imageView.setImageBitmap(bmp);
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    private void guardarArchivo(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //pedir permiso
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }else {
            // si ya tengo el permiso habilitado
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            String name = "IMG" + String.format("%d.png", System.currentTimeMillis());
            new SaveImage(this, "Guia5", name).execute(bitmap);
        }
    }
    private void cancelar(){
        Toast.makeText(this,"Menu Cancelar",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //si fue concedido
        }
    }
}
