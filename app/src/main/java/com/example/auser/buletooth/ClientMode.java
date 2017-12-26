package com.example.auser.buletooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClientMode extends AppCompatActivity {
    Button btn_SendClient,btn_ClearClient;
    EditText edt_Client;
    TextView txt_Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_mode);
        findViews();
    }


    void findViews(){
        btn_SendClient=(Button)findViewById(R.id.btnSendClient);
        btn_ClearClient=(Button)findViewById(R.id.btnClearClient);
        edt_Client=(EditText)findViewById(R.id.edtClient);
        txt_Client=(TextView)findViewById(R.id.txtClient);

    }
}
