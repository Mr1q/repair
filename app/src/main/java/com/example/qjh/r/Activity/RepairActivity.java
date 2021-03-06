package com.example.qjh.r.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.baidu.aip.imageclassify.AipImageClassify;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.qjh.r.Bean.MessageBomb;
import com.example.qjh.r.Bean.User;
import com.example.qjh.r.Control.BaseActivity;
import com.example.qjh.r.Fragment.HomeFragment;
import com.example.qjh.r.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RepairActivity extends BaseActivity implements View.OnClickListener {
    private static final int LOCATION_SUCCESS = 1;
    final String[] spinnerItem = {"请选择", "花江", "金鸡岭", "西区"};
    final String[] itemType = {"请选择", "电", "水", "五金", "教学楼直饮饮水机", "热泵"};
    private TextView tv_rpairType; //报修内容
    private Spinner spinner3;//选择框
    private Button put;
    private ImageButton speak;
    private TextView textnumber;
    public static EditText write;
    private TextView tv_getTime;//获取时间
    private EditText usernumber;//学号
    private EditText username; //姓名
    private EditText phone_number; //电话
    private Animation animation;
    private FrameLayout frameLayout;
    private TextView Msg;
    private ImageButton igb_uploadPhoto; //拍照
    private ImageView iv_photo;
    private ImageButton igb_location;
    // 照片所在的Uri地址
    private Uri imageUri;
    private static Intent intent;//接受列表传值
    public static User user = BmobUser.getCurrentUser(User.class);
    ;
    private ImageButton back_msg;
    private EditText Location;
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> adapter3;

    private Boolean Modify=false;//是否修改
    private String Id;
    private LocationClient locationClient;
    private String loactionDetail;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATION_SUCCESS:
                    Location.setText(loactionDetail);  //设置定位信息
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_repairmessage);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        intent = getIntent();
        loactionDetail = intent.getStringExtra(LocActivity.LOCATION);

        initial();


    }

    public void initial() {
        Location = (EditText) findViewById(R.id.Location);
        igb_location = (ImageButton) findViewById(R.id.igb_location);
        back_msg = (ImageButton) findViewById(R.id.back_msg);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        igb_uploadPhoto = (ImageButton) findViewById(R.id.igb_uploadPhoto);
        username = (EditText) findViewById(R.id.username);
        phone_number = (EditText) findViewById(R.id.phone_number);
        Msg = (TextView) findViewById(R.id.msg);
        frameLayout = (FrameLayout) findViewById(R.id.total_Hand);
        usernumber = (EditText) findViewById(R.id.usernumber);
        tv_getTime = (TextView) findViewById(R.id.tv_getTime);
        put = (Button) findViewById(R.id.put);
        write = (EditText) findViewById(R.id.write);
        textnumber = (TextView) findViewById(R.id.number_Text);
        speak = (ImageButton) findViewById(R.id.speak);
        tv_rpairType = (TextView) findViewById(R.id.tv_rpairType);
        spinner3 = (Spinner) findViewById(R.id.select3);
        speak.setOnClickListener(this);
        back_msg.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        igb_uploadPhoto.setOnClickListener(this);
        frameLayout.setOnClickListener(this);
        tv_getTime.setOnClickListener(this);
        put.setOnClickListener(this);
        igb_location.setOnClickListener(this);
        put.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, spinnerItem);
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, itemType);

        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter3.getItem(position).equals("请选择")) {
                    tv_rpairType.setText("请选择项目");
                } else {
                    tv_rpairType.setText(adapter3.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (loactionDetail != null) {
            Location.setText(loactionDetail);
        }

//        animation = AnimationUtils.loadAnimation(this, R.anim.spinner);
//        spinner.setOnTouchListener(new Spinner.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.startAnimation(animation);
//                return false;
//            }
//        });

        change(); //文字变化监听
        //接受活动传回来的数据
        if(intent.getStringExtra(HomeFragment.ISTRUE)!=null&&intent.getStringExtra(HomeFragment.ISTRUE).equals("-1"))
        {
            Receive();
        }

    }

    private void Receive() {
//        Intent intent = getIntent();
        if (intent != null) {
            Msg.setText(intent.getStringExtra("0"));
            for (int i = 0; i < itemType.length; i++) {
                if (itemType[i].equals(intent.getStringExtra("4"))) {
                    spinner3.setSelection(i);
                    break;
                }
            }
            Location.setText(intent.getStringExtra("3"));
            write.setText(intent.getStringExtra("5"));
            usernumber.setText(intent.getStringExtra("6"));
            username.setText(intent.getStringExtra("7"));
            phone_number.setText(intent.getStringExtra("8"));
            tv_getTime.setText(intent.getStringExtra("9"));
            Glide.with(this).load(intent.getStringExtra("10")).into(iv_photo);
            Modify = intent.getBooleanExtra("Where", false);
            Id = intent.getStringExtra("id");
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speak:
                if (ContextCompat.checkSelfPermission(RepairActivity.this, android.Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RepairActivity.this, new String[]{
                            android.Manifest.permission.RECORD_AUDIO}, 2);
                } else {
                    startRecord();
                }

                break;
            case R.id.put:
                // ArrayList<Message_Bomb> message_bombs = new ArrayList<>();
                if (Modify == false) {
                    if (!(usernumber.getText().toString().isEmpty() || phone_number.getText().toString().isEmpty() || username.getText().toString().isEmpty())) {
                        Hand_in();
                    } else {
                        Toast.makeText(RepairActivity.this, "学号、联系人、联系电话不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!(usernumber.getText().toString().isEmpty() || phone_number.getText().toString().isEmpty() || username.getText().toString().isEmpty())) {
                        Modifys();
                    } else {
                        Toast.makeText(RepairActivity.this, "学号、联系人、联系电话不能为空", Toast.LENGTH_SHORT).show();
                    }

                }
//                Intent intent1 = new Intent(this, VPM.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                Notification notification = new NotificationCompat.Builder(this).
//                        setContentTitle("First")
//                        .setContentText("第一条通知")
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL)
//                        .setPriority(NotificationCompat.PRIORITY_MAX)
//                        .build();
//                notificationManager.notify(1, notification);
                break;
            case R.id.total_Hand:
                final Intent intent = new Intent(RepairActivity.this, MessageActivity.class);
                intent.putExtra("datas", Msg.getText());
                startActivityForResult(intent, 10);
                break;

            //获取时间
            case R.id.tv_getTime:
                Gettime();
                break;

            case R.id.igb_uploadPhoto:
                takePhoto();
                break;

            case R.id.back_msg:
                finish();
                break;
            case R.id.igb_location:
                requestPermission();
                locationClient = new LocationClient(getApplicationContext());
                LocationClientOption locationClientOption = new LocationClientOption();
                locationClientOption.setOpenGps(true);
                //  locationClientOption.setScanSpan(2000);
                locationClientOption.setIgnoreKillProcess(true);
                locationClientOption.setIsNeedAddress(true);
                locationClientOption.setIsNeedLocationDescribe(true);
                locationClientOption.setOpenAutoNotifyMode();
                locationClientOption.setCoorType("bd09ll");
                locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                locationClient.setLocOption(locationClientOption);
                MyLocationLister myLocationLister = new MyLocationLister();
                locationClient.registerLocationListener(myLocationLister);
                locationClient.start();

                break;

            case R.id.iv_photo:
                new XPopup.Builder(this)
                        .asImageViewer(iv_photo, imageUri, true, -1,
                                -1, 50, false, new ImageLoader())
                        .show();
                break;

        }
    }

    public class MyLocationLister extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            loactionDetail = bdLocation.getLocationDescribe();
            handler.sendEmptyMessage(LOCATION_SUCCESS);
        }

    }

    public static class ImageLoader implements XPopupImageLoader {
        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
            Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_round).
                    override(Target.SIZE_ORIGINAL)).into(imageView);
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return Glide.with(context).downloadOnly().load(uri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void takePhoto() {
        final CustomPopupWindow popupWindow = new CustomPopupWindow.Builder()
                .setContext(this) //设置 context
                .setContentView(R.layout.head_image) //设置布局文件
                .setwidth(LinearLayout.LayoutParams.WRAP_CONTENT) //设置宽度，由于我已经在布局写好，这里就用 wrap_content就好了
                .setheight(LinearLayout.LayoutParams.WRAP_CONTENT) //设置高度
                .setFouse(true)  //设置popupwindow 是否可以获取焦点
                .setOutSideCancel(true) //设置点击外部取消
                .setBackGroudAlpha(RepairActivity.this, 0.7f) //是否设置背景色，原理为调节 apha
                .builder()
                .showAtLocation(R.layout.common_activity_repairmessage, Gravity.BOTTOM, 0, 0); //设置popupwindow居中显示
        //添加点击事件
        popupWindow.setOnClickListener(R.id.pop_pic, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        Intent photo = new Intent(Intent.ACTION_PICK);
//                        photo.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // photo.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                Intent photo = new Intent();
                if (Build.VERSION.SDK_INT < 19) {
                    photo.setAction(Intent.ACTION_GET_CONTENT);
                    photo.setType("image/*");
                } else {
                    photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photo.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                }
                startActivityForResult(photo, 1);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnClickListener(R.id.pop_camera, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File image = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                try {
                    if (image.exists()) {
                        image.delete();
                    }
                    image.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(RepairActivity.this, "com.example.qjh.r.fileprovider", image);
                    Log.d("image_class", "onClick: " + imageUri);
                } else {
                    imageUri = Uri.fromFile(image);
                }

                Intent intent2 = new Intent("android.media.action.IMAGE_CAPTURE");
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent2, 2);
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnClickListener(R.id.pop_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void startRecord() {
        Voice voice = new Voice(this);
        voice.initSpeech(RepairActivity.this);
    }


    private void Modifys() {
        if (imageUri == null) {
            final MessageBomb message_bomb = new MessageBomb();
            final ProgressDialog proess = new ProgressDialog(this);
            proess.setTitle("提示");
            proess.setMessage("上传中...");
            proess.setCancelable(false);
            proess.show();
            message_bomb.setValue("title", Msg.getText().toString());
            message_bomb.setValue("Msg", write.getText().toString());
            message_bomb.setValue("obj_Name", tv_rpairType.getText().toString());
            message_bomb.setValue("Location", Location.getText().toString());
            message_bomb.setValue("name", username.getText().toString());
            message_bomb.setValue("number", usernumber.getText().toString());
            message_bomb.setValue("phone", phone_number.getText().toString());
            message_bomb.setValue("time", tv_getTime.getText().toString());
            message_bomb.update(Id, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        proess.dismiss();
                        Toast.makeText(RepairActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        } else {
            final BmobFile bmobFile = new BmobFile(uriToFile(imageUri, this));
            final ProgressDialog proess = new ProgressDialog(this);
            proess.setTitle("提示");
            proess.setMessage("上传中...");
            proess.setCancelable(false);
            proess.show();
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        final MessageBomb message_bomb = new MessageBomb();
                        message_bomb.setValue("picture", bmobFile);
                        message_bomb.setValue("title", Msg.getText().toString());
                        message_bomb.setValue("Msg", write.getText().toString());
                        message_bomb.setValue("obj_Name", tv_rpairType.getText().toString());
                        message_bomb.setValue("Location", Location.getText().toString());
                        message_bomb.setValue("name", username.getText().toString());
                        message_bomb.setValue("number", usernumber.getText().toString());
                        message_bomb.setValue("phone", phone_number.getText().toString());
                        message_bomb.setValue("time", tv_getTime.getText().toString());
                        message_bomb.setValue("isFinish", false);
                        message_bomb.update(Id, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    proess.dismiss();
                                    Toast.makeText(RepairActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    private void Hand_in() {
        if (imageUri == null) {
            Toast.makeText(RepairActivity.this, "未上传图片", Toast.LENGTH_SHORT).show();
        } else {
            final BmobFile bmobFile = new BmobFile(uriToFile(imageUri, this));
            final ProgressDialog proess = new ProgressDialog(this);
            proess.setTitle("提示");
            proess.setMessage("上传中...");
            proess.setCancelable(false);
            proess.show();
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        final MessageBomb message_bomb = new MessageBomb();
                        message_bomb.setPicture(bmobFile);
                        message_bomb.setTitle(Msg.getText().toString());
                        message_bomb.setMsg(write.getText().toString());
                        message_bomb.setObj_Name(tv_rpairType.getText().toString());
                        message_bomb.setLocation(Location.getText().toString());
                        message_bomb.setName(username.getText().toString());
                        message_bomb.setNumber(usernumber.getText().toString());
                        message_bomb.setPhone(phone_number.getText().toString());
                        message_bomb.setTime(tv_getTime.getText().toString());
                        message_bomb.setFinish(false);
                        message_bomb.setIdd(user.getObjectId());
                        message_bomb.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    MessageBomb message_bomb1 = new MessageBomb(Msg.getText().toString(), write.getText().toString(), Location.getText().toString(), usernumber.getText().toString(), username.getText().toString(), tv_getTime.getText().toString());
                                    HomeFragment.message_bombs_list.add(message_bomb);
                                    HomeFragment.fruit_adapter.notifyDataSetChanged();
                                    Toast.makeText(RepairActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    proess.dismiss();
                                    finish();
                                }
                            }
                        });

                    }
                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    try {
                        //该uri是上一个Activity返回的
                        imageUri = data.getData();
                        Glide.with(this).load(imageUri).into(iv_photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        //    imageUri = data.getData();
                        Glide.with(this).load(imageUri).into(iv_photo);
                        final File file = uriToFile(imageUri, this);
                        Log.d("onActivityResult_class",
                                "onActivityResult: /" + file.getAbsolutePath());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Text(file.getPath());
                            }
                        }).start();

                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

//                        pane.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 10:
                    Msg.setText(data.getStringExtra("data"));
                    break;
            }
        }

    }

    public void Gettime() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(RepairActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                stringBuilder.append(hourOfDay + ":" + minute);
                tv_getTime.setText(stringBuilder);
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();

        new DatePickerDialog(RepairActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String text = "你选择了" + year + "年" + (month + 1) + "月" + dayOfMonth + "日";  //月份从0开始计数
                Toast.makeText(RepairActivity.this, text, Toast.LENGTH_SHORT).show();

                stringBuilder.append(year + "-" + (month + 1) + "-" + dayOfMonth + " ");
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    //设置APPID/AK/SK
    public static final String APP_ID = "17897100";
    public static final String API_KEY = "GtXSVM1L5w3pHfbWhXcaK8NL";
    public static final String SECRET_KEY = "1ODdlpq6w5UcQdxSHkFYdz85GuTPZ4y1";

    private void Text(String path) {
        // 初始化一个AipImageClassify
        AipImageClassify client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        ///  client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        // client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 调用接口
        //String path = "test.jpg";
        JSONObject res = client.advancedGeneral(path, new HashMap<String, String>());
        try {
            System.out.println(res.toString());

            System.out.println(res.toString(2));
            Log.d("image_text", "Text: " + res.toString(2));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void change() {
        usernumber.addTextChangedListener(new TextWatcher() {
            private int editstar;
            private int editend;
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                editstar = usernumber.getSelectionStart();
                editend = usernumber.getSelectionEnd();
                if (temp.length() > 12) {
                    Toast.makeText(RepairActivity.this, "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                    s.delete(editstar - 1, editend);
                    int tempSelection = editstar;
                    usernumber.setText(s);
                    usernumber.setSelection(tempSelection);
                }
            }
        });


        write.addTextChangedListener(new TextWatcher() {
            private int editstar;
            private int editend;
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editstar = write.getSelectionStart();
                editend = write.getSelectionEnd();
                textnumber.setText(String.valueOf(200 - temp.length()));
                if (temp.length() > 200) {
                    Toast.makeText(RepairActivity.this, "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                    s.delete(editstar - 1, editend);
                    int tempSelection = editstar;
                    write.setText(s);
                    write.setSelection(tempSelection);
                }

            }
        });


        username.addTextChangedListener(new TextWatcher() {
            private int editstar;
            private int editend;
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editstar = username.getSelectionStart();
                editend = username.getSelectionEnd();
                if (temp.length() > 5) {
                    Toast.makeText(RepairActivity.this, "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                    s.delete(editstar - 1, editend);
                    int tempSelection = editstar;
                    username.setText(s);
                    username.setSelection(tempSelection);
                }
            }
        });

        phone_number.addTextChangedListener(new TextWatcher() {
            private int editstar;
            private int editend;
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editstar = phone_number.getSelectionStart();
                editend = phone_number.getSelectionEnd();
                if (temp.length() > 12) {
                    Toast.makeText(RepairActivity.this, "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                    s.delete(editstar - 1, editend);
                    int tempSelection = editstar;
                    phone_number.setText(s);
                    phone_number.setSelection(tempSelection);
                }
            }
        });
    }

    public static File uriToFile(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            return GetFIlePath_From_Uri.GetFilePath_Min_sdkversion(uri, context);
        } else if ("content".equals(uri.getScheme())) {
            return new File(GetFIlePath_From_Uri.getFilePathFromURI(context, uri));//新的方式
        }
        return null;
    }


    /**
     * 请求权限
     */
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {// 没有权限，申请权限。
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        BAIDU_READ_PHONE_STATE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecord();
        } else {
            Toast.makeText(this, "用户拒绝了权限", Toast.LENGTH_SHORT).show();
        }
        switch (requestCode) {
            case BAIDU_READ_PHONE_STATE://刚才的识别码
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                    //  startLocaion();//开始定位
                } else {
                    //用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    Toast.makeText(this, "未开启定位权限,请手动到设置去开启权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
