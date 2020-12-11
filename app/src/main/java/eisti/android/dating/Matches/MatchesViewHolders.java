package eisti.android.dating.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eisti.android.dating.R;
import eisti.android.dating.chat.ChatActivity;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public TextView mMatchId,mMatchName;
    public ImageView mMatchImage;

    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchName=(TextView)itemView.findViewById(R.id.Matchname);

        mMatchId=(TextView)itemView.findViewById(R.id.Matchid);
        mMatchImage=(ImageView)itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId",mMatchId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }
}
