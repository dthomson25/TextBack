package com.dthomson.textback;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TextViewHolder extends RecyclerView.ViewHolder {

    protected TextView titleText;
    protected TextView phoneNumber;
    protected TextView contentText;
    protected TextView rowIDText;
    protected CardView card;

    public TextViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.person);
        contentText = (TextView) itemView.findViewById(R.id.last_text);
        rowIDText = (TextView) itemView.findViewById(R.id.row_id);
        phoneNumber = (TextView) itemView.findViewById(R.id.phoneNumber);
        card = (CardView) itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumbers = phoneNumber.getText().toString();
                if (phoneNumbers.equals("Demo")) {
                    Uri uri = Uri.parse("smsto:6302090547");
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    v.getContext().startActivity(it);
                    return;
                }
                Uri uri = Uri.parse("smsto:" + phoneNumbers);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                v.getContext().startActivity(it);
            }
        });
    }

}
