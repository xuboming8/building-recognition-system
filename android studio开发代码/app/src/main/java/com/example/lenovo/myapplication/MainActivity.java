package com.example.lenovo.myapplication;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.*;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {
    //Load the tensorflow inference library
    static {
        System.loadLibrary("tensorflow_inference");
    }
    //PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
    private String MODEL_PATH = "file:///android_asset/my_modelB.pb";
    private String INPUT_NAME = "input_1";
    private String OUTPUT_NAME = "dense_2/Softmax";
    private TensorFlowInferenceInterface tf;

    //ARRAY TO HOLD THE PREDICTIONS AND FLOAT VALUES TO HOLD THE IMAGE DATA
    float[] PREDICTIONS = new float[50];
    private float[] floatValues;
    private int[] INPUT_SIZE = {224,224,3};

    ImageView imageView;
    ImageView ic1,ic2;
    TextView resultView;

    private static int REQ_1 = 1;
    private static int REQ_2 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initialize tensorflow with the AssetManager and the Model
        tf = new TensorFlowInferenceInterface(getAssets(),MODEL_PATH);
        imageView = (ImageView) findViewById(R.id.imageview);
        resultView = (TextView) findViewById(R.id.results);
        ic1 = (ImageView) findViewById(R.id.icon1);
        ic2 = (ImageView) findViewById(R.id.icon2);
        try {
            InputStream imageStream1 = getAssets().open("icon2.png");
            Bitmap bitmap1 = BitmapFactory.decodeStream(imageStream1);
            ic1.setImageBitmap(bitmap1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream imageStream2 = getAssets().open("icon1.png");
            Bitmap bitmap2 = BitmapFactory.decodeStream(imageStream2);
            ic2.setImageBitmap(bitmap2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final FloatingActionButton predict1 = (FloatingActionButton) findViewById(R.id.predict1);
        final FloatingActionButton predict2 = (FloatingActionButton) findViewById(R.id.predict2);
        predict1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
//                    InputStream imageStream = getAssets().open("blue.png");
//                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//                    imageView.setImageBitmap(bitmap);
//                    predict(bitmap);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQ_1);
                }
                catch (Exception e){
                }
            }
        });
        predict2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_2);
                }
                catch (Exception e){
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_1){
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
                predict(bitmap);
            }
            if(requestCode==REQ_2){
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                    imageView.setImageBitmap(bitmap);
                    predict(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int class_index = -1;
        if(item.getItemId() == R.id.b_0 || item.getItemId() == R.id.title_1 || item.getItemId() == R.id.title_2){
            imageView.setImageDrawable(null);
        }
        if(item.getItemId() == R.id.b_1){
            Toast.makeText(this,"美国国会大厦",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("A.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 0;
        }
        if(item.getItemId() == R.id.b_2){
            Toast.makeText(this,"埃菲尔铁塔",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("aifo.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 1;
        }
        if(item.getItemId() == R.id.b_3){
            Toast.makeText(this,"巴黎圣母院",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("bali.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 2;
        }
        if(item.getItemId() == R.id.b_4){
            Toast.makeText(this,"布达拉宫",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("bdlg.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 3;
        }
        if(item.getItemId() == R.id.b_5){
            Toast.makeText(this,"大本钟",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("benzhong.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 4;
        }
        if(item.getItemId() == R.id.b_6){
            Toast.makeText(this,"比萨斜塔",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("bisa.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 5;
        }
        if(item.getItemId() == R.id.b_7){
            Toast.makeText(this,"北京大裤衩",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("bjdkc.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 6;
        }
        if(item.getItemId() == R.id.b_8){
            Toast.makeText(this,"蓝色清真寺",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("blue.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 7;
        }
        if(item.getItemId() == R.id.b_9){
            Toast.makeText(this,"东方明珠",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("dfmz.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 8;
        }
        if(item.getItemId() == R.id.b_10){
            Toast.makeText(this,"迪拜塔",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("dibai.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 9;
        }
        if(item.getItemId() == R.id.b_11){
            Toast.makeText(this,"双子塔",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("double.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 10;
        }
        if(item.getItemId() == R.id.b_12){
            Toast.makeText(this,"自由女神像",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("freedom.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 11;
        }
        if(item.getItemId() == R.id.b_13){
            Toast.makeText(this,"故宫",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("gg.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 12;
        }
        if(item.getItemId() == R.id.b_14){
            Toast.makeText(this,"广州塔",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("gzt.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 13;
        }
        if(item.getItemId() == R.id.b_15){
            Toast.makeText(this,"哈尔滨大剧院",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("hebdjy.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 14;
        }
        if(item.getItemId() == R.id.b_16){
            Toast.makeText(this,"湖州喜来登酒店",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("hzxldjd.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 15;
        }
        if(item.getItemId() == R.id.b_17){
            Toast.makeText(this,"角斗场",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("jiaodou.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 16;
        }
        if(item.getItemId() == R.id.b_18){
            Toast.makeText(this,"卢浮宫",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("lufu.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 17;
        }
        if(item.getItemId() == R.id.b_19){
            Toast.makeText(this,"玛丽莲梦露大厦",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("mali.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 18;
        }
        if(item.getItemId() == R.id.b_20){
            Toast.makeText(this,"鸟巢",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("nc.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 19;
        }
        if(item.getItemId() == R.id.b_21){
            Toast.makeText(this,"圣索菲亚大教堂",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("shengsuo.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 20;
        }
        if(item.getItemId() == R.id.b_22){
            Toast.makeText(this,"北京银河soho",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("soho.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 21;
        }
        if(item.getItemId() == R.id.b_23){
            Toast.makeText(this,"泰姬陵",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("taiji.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 22;
        }
        if(item.getItemId() == R.id.b_24){
            Toast.makeText(this,"吴哥窟",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("wuge.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 23;
        }
        if(item.getItemId() == R.id.b_25){
            Toast.makeText(this,"悉尼歌剧院",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("xini.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 24;
        }
        if(item.getItemId() == R.id.b_26){
            Toast.makeText(this,"亚琛大教堂",Toast.LENGTH_SHORT).show();
            InputStream imageStream = null;
            try {
                imageStream = getAssets().open("yasheng.png");
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            class_index = 25;
        }
        try{
            final String label = ImageUtils.getLabel(getAssets().open("labels2.json"),class_index);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultView.setText(label);
                }
            });
        }
        catch (Exception e){

        }
        return true;
    }

    //FUNCTION TO COMPUTE THE MAXIMUM PREDICTION AND ITS CONFIDENCE
    public static Object[] argmax(float[] array){
        int best = -1;
        float best_confidence = 0.0f;
        for(int i = 0;i < array.length;i++){
            float value = array[i];
            if (value > best_confidence){
                best_confidence = value;
                best = i;
            }
        }
        return new Object[]{best,best_confidence};
    }
    public void predict(final Bitmap bitmap){
        //Runs inference in background thread
        new AsyncTask<Integer,Integer,Integer>(){
            @Override
            protected Integer doInBackground(Integer ...params){
                //Resize the image into 224 x 224
                Bitmap resized_image = ImageUtils.processBitmap(bitmap,224);
                //Normalize the pixels
                floatValues = ImageUtils.normalizeBitmap(resized_image,224,0,255.0f);
                //Pass input into the tensorflow
                tf.feed(INPUT_NAME,floatValues,1,224,224,3);
                //compute predictions
                tf.run(new String[]{OUTPUT_NAME});
                //copy the output into the PREDICTIONS array
                tf.fetch(OUTPUT_NAME,PREDICTIONS);
                //Obtained highest prediction
                Object[] results = argmax(PREDICTIONS);
                int class_index = (Integer) results[0];
                //float confidence = (Float) results[1];
                try{
                    //final String conf = String.valueOf(confidence * 100).substring(0,5);
                    //Convert predicted class index into actual label name
                    final String label = "预测结果为：" + ImageUtils.getLabel(getAssets().open("labels2.json"),class_index);
                    //Display result on UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultView.setText(label);
                        }
                    });
                }
                catch (Exception e){

                }
                return 0;
            }
        }.execute(0);

    }


}
