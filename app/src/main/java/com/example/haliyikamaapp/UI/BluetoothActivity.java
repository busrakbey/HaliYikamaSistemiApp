package com.example.haliyikamaapp.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements Runnable {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc, barkod_siparis_takip;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    String gelenSiparisId="0", gelenSiparisMid, gelenSubeAdi;
    String mDeviceAddress = "02:3C:6E:1A:2F:7F";

    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.bluetooth_activity);
        mScan = (Button) findViewById(R.id.Scan);

        gelenSiparisId = getIntent().getStringExtra("siparisId");
        gelenSiparisMid = getIntent().getStringExtra("siparisMid");
        gelenSubeAdi = getIntent().getStringExtra("subeAdi");

        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(BluetoothActivity.this, "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(BluetoothActivity.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
               /* try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/


                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                sleep(3000);

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();

                barkodYazici(gelenSiparisId != null ? gelenSiparisId : gelenSiparisMid, gelenSubeAdi);
            }
        });


        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });

        barkod_siparis_takip = (Button) findViewById(R.id.barkod_siparis_takip);
        barkod_siparis_takip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gelenSiparisId != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://34.91.29.223/siparisiniz/" + gelenSiparisId));
                    startActivity(browserIntent);
                }else
                    Toast.makeText(BluetoothActivity.this, "Sipariş bulunamamıştır.", Toast.LENGTH_SHORT).show();

            }
        });


    }// onCreate

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    mDeviceAddress = mExtra.getString("DeviceAddress");

                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BluetoothActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(BluetoothActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BluetoothActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    public byte[] createBarkocImage(String siparisId) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(siparisId, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            // imageView.setImageBitmap(bitmap);

            byte[] command = new byte[0];
            try {
                if (bitmap != null) {
                    command = Utils.decodeBitmap(bitmap);
                   /* outputStream.write(new byte[] { 0x1b, 'a', 0x01 });
                    printText(command);*/
                } else {
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String barkodDegerString = Base64.encodeToString(b, Base64.DEFAULT);
            return command;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void barkodYazici(String siparisId, final String subeAdi) {

        if (mBluetoothSocket == null)
            MessageBox.showAlert(getApplicationContext(), "Lütfen önce bağlantı kurmak istediğiniz cihazı seçiniz.", false);
        else {


            OutputStream os = null;
            try {
                os = mBluetoothSocket
                        .getOutputStream();
                // os.write(new byte[] { 0x1b, 'a', 0x01 });
                byte[] change = new byte[]{27, 33, 0};

                os.write(("\n   ").getBytes());
                os.write(("\n   ").getBytes());

                os.write(new byte[]{0x1B, 'a', 0x01});
                os.write((" " + trEngCevir(subeAdi).toUpperCase() + " HALI YIKAMA " + "\n").getBytes());


                os.write(createBarkocImage("http://34.91.29.223/siparisiniz/" + gelenSiparisId));

                // os.write(new byte[]{0x1b, 'a', 0x01});
                change[2] = (byte) (0x3); //small
                os.write(change);
                os.write(new byte[]{0x1B, 'a', 0x00});
                os.write(("URUN    FIYAT    MIKTAR   BIRIM   TUTAR \n").getBytes());
                os.write((" ----------------------------------------\n").getBytes());

                HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(getApplicationContext());
                List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(Long.valueOf(gelenSiparisId));
                List<SiparisDetay> siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(Long.valueOf(gelenSiparisId));

                double toplamTutar = 0.0;
                for (SiparisDetay item : siparisDetayList) {
                    if (item.getUrunId() != null)
                        os.write(db.urunDao().getUrunForId(item.getUrunId()).get(0).getUrunAdi().getBytes());
                    else
                        os.write(("     ").getBytes());

                    os.write(("   ").getBytes());
                    os.write((item.getBirimFiyat() != null ? item.getBirimFiyat().toString() : "    ").getBytes());
                    os.write(("     ").getBytes());
                    os.write((item.getMiktar() != null ? item.getMiktar().toString() : "    ").getBytes());
                    os.write(("      ").getBytes());
                    if (item.getOlcuBirimId() != null)
                        os.write(db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).get(0).getOlcuBirimi().getBytes());
                    else
                        os.write(("  ").getBytes());

                    os.write(("     ").getBytes());
                    os.write((String.valueOf((item.getMiktar() != null ? item.getMiktar() : 0) * (item.getBirimFiyat() != null ? item.getBirimFiyat() : 0))).getBytes());
                    os.write(("\n").getBytes());

                    toplamTutar = toplamTutar + (item.getMiktar() != null ? item.getMiktar() : 0) * (item.getBirimFiyat() != null ? item.getBirimFiyat() : 0);

                }


                os.write(("\nToplam Urun Sayisi: " + siparisDetayList.size()).getBytes());
                os.write(("\nToplam Tutar: " + toplamTutar + " TL").getBytes());
                os.write(("\n ----------------------------------------").getBytes());
                os.write(("\n").getBytes());
                //This is printer specific code you can comment ==== > Start


                // Setting height
                   /* int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String trEngCevir(String alinanMetin) {
        //fonksiyona gelen kelimeyi alıyoruz
        String metin = alinanMetin;
        //sonuç için vir değişken belirledik
        String sonuc = "";
        //iki tane dizi oluşturuyoruz biri türkçe karakterler için diğeri ing
        char[] ilkHarf = new char[]{'İ', 'ı', 'ü', 'Ü', 'ç', 'Ç', 'Ğ', 'ğ', 'Ş', 'ş', 'ö', 'Ö'};
        char[] yeniHarf = new char[]{'I', 'i', 'u', 'U', 'c', 'C', 'G', 'g', 'S', 's', 'o', 'O',};
        //for döngüsü açıp kelimenin harflerine tek tek bakıp
        //tr varsa replace metodu ile değiştiriyoruz.
        for (int sayac = 0; sayac < ilkHarf.length; sayac++) {
            metin = metin.replace(ilkHarf[sayac], yeniHarf[sayac]);
        }
        //burada sonuc değişkenini kullanmasınızda olur
        //direk sysout(metin) de denebilir
        sonuc = metin;
        return sonuc;
        //  System.out.println(sonuc);
    }

}
