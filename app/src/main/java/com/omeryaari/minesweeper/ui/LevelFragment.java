package com.omeryaari.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;

public class LevelFragment extends Fragment {

    private String title;
    private int position;
    private GridLayout grid;
    private ImageButton gameStart;

    public static LevelFragment newInstance(String title, int position) {
        LevelFragment fragment = new LevelFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position", 0);
        title = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.level_fragment, container, false);
        TextView levelText = (TextView) view.findViewById(R.id.levelTextXML);
        levelText.setText(title);
        grid = (GridLayout) view.findViewById(R.id.gridTable);
        for(int i = 0; i < grid.getColumnCount(); i++) {
            TextView textView = new TextView(getActivity());
            switch (i) {
                case 0:
                    textView.setText(getString(R.string.highScoresNumberSign));
                    break;
                case 1:
                    textView.setText(getString(R.string.highScoresName));
                    break;
                case 2:
                    textView.setText(getString(R.string.highScoresTime));
                    break;
                default:
                    break;
            }
            textView.setTextSize(20);
            textView.setBackgroundResource(R.drawable.cell_shape);
            textView.setGravity(Gravity.CENTER);
            grid.addView(textView, i);
        }
        gameStart = (ImageButton) view.findViewById(R.id.playImageButton);
        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("key", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

}
