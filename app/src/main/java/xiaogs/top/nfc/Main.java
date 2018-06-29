package xiaogs.top.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    Button button;
    TextView textView;
    public static final byte[] KEY_A =
            {(byte)0x43,(byte)0x78,(byte)0xFF,(byte)0x2B,(byte)0x58,(byte)0x02};
    public static final byte[] KEY_B =
            {(byte)0xD3,(byte)0x6A,(byte)0x2D,(byte)0xC3,(byte)0xA1,(byte)0x46};
    public static byte[] VALUE={(byte)0xFF,(byte)0xFF,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x20};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        byte[] test = "1313838438000000".getBytes();
        if (nfcAdapter==null){
            //Toast.makeText(this,"设备不支持NFC功能",Toast.LENGTH_SHORT).show();
            textView.append("设备不支持NFC功能\n");
            finish();
            return;
        }else {
//            Toast.makeText(this,"设备支持",Toast.LENGTH_SHORT).show();
            textView.append("设备支持\n");
        }
        if (nfcAdapter.isEnabled()){
            //Toast.makeText(this,"设备已启用NFC功能",Toast.LENGTH_SHORT).show();
            textView.append("设备已启用NFC功能\n");
        }else {
            //Toast.makeText(this,"设备未启用NFC功能",Toast.LENGTH_SHORT).show();
            textView.append("设备未启用NFC功能\n");
            finish();
            return;
        }
    }
    @Override
    public void onResume(){
        String Istring;
        super.onResume();
        if (nfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())){
            Istring = getIntent().getAction();
            //Toast.makeText(this,Istring,Toast.LENGTH_SHORT).show();
            textView.append(Istring+'\n');
            readData(getIntent());
        }
    }
    public void readData(Intent intent) {
        Tag tag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
        for (String tech : tag.getTechList()){
            //Toast.makeText(this,tech,Toast.LENGTH_SHORT).show();
            textView.append(tech+'\n');
        }
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            if (mifareClassic.isConnected()){
                //Toast.makeText(this,"mifareClassic is connected",Toast.LENGTH_SHORT).show();
                textView.append("mifareClassic is connected\n");
            }else {
                mifareClassic.connect();
                int type = mifareClassic.getType();
                int sectorCount = mifareClassic.getSectorCount();
                int bCount,bIndex;
                String metaInfo="";
                //Toast.makeText(this,"connect成功"+sectorCount,Toast.LENGTH_SHORT).show();
                textView.append("connect成功\n");
                try {
                        if(mifareClassic.authenticateSectorWithKeyB(6,KEY_B)){
                                byte[] data = mifareClassic.readBlock(25);
                                metaInfo += "Block 24 : "
                                        + bytesToHexString(data) + "\n";

                                //Toast.makeText(this,metaInfo,Toast.LENGTH_SHORT).show();
                                data = mifareClassic.readBlock(26);
                            metaInfo += "Block 25 : "
                                    + bytesToHexString(data) + "\n";
                            //Toast.makeText(this,metaInfo,Toast.LENGTH_SHORT).show();
                            textView.append(metaInfo+"\n");

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            Toast.makeText(this,"connect失败",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            //System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
 public void buttonClick(View view){
     writeData(getIntent());
 }
    public void writeData(Intent intent){
        Tag tag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
        for (String tech : tag.getTechList()){
            //Toast.makeText(this,tech,Toast.LENGTH_SHORT).show();
            textView.append(tech);
        }
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try{
            if(mifareClassic.authenticateSectorWithKeyB(6,KEY_B)){
                mifareClassic.writeBlock(25,VALUE);
                mifareClassic.writeBlock(26,VALUE);
                mifareClassic.close();
                //Toast.makeText(this,"写入成功",Toast.LENGTH_SHORT).show();
                textView.append("写入成功\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
