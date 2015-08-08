package com.dthomson.textback;

/**
 * Created by dthomson on 8/6/2015.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<TextViewHolder> {


//    intent.putExtra("text", new TextMessage("1","Mike"));
//    Bundle data = getIntent().getExtras();
//    Student student = (TextMessage) data.getParcelable("text");

    private List<TextMessage> texts;

    public MyRecyclerAdapter(List<TextMessage> texts) {
        this.texts = new ArrayList<>();
        this.texts.addAll(texts);
    }

    public List<TextMessage> getTexts() {
        return texts;
    }

    public void addData(TextMessage textMessage, int position) {
        texts.add(position, textMessage);
        notifyItemInserted(position);
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new TextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TextViewHolder paletteViewHolder, int i) {
        TextMessage text = texts.get(i);
        paletteViewHolder.titleText.setText(text.getPerson());
        paletteViewHolder.contentText.setText(text.getConversation());
//        paletteViewHolder.card.setCardBackgroundColor(palette.getIntValue());
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    public void defaultCards() {
        clearTexts();
        ArrayList<TextMessage> texts = new ArrayList<>();
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        this.texts.addAll(texts);
        notifyItemRangeInserted(0,texts.size());
    }

    public void clearTexts() {
        int size = texts.size();
        texts.clear();
        notifyItemRangeRemoved(0,size);
    }
}