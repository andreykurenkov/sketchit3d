package edu.gatech.sketchit;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class BottomOverlay extends RelativeLayout{
	private ArrayList<ImageButton> b = new ArrayList<ImageButton>();
	private int width;
	private int incr;
	
	public BottomOverlay(Context context) {
		super(context);
		this.width = this.getWidth();
		float alpha = 0.5f;
		b.add(new ImageButton(context)); // Extrude
		b.get(0).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(0).setAlpha(alpha);
		b.add(new ImageButton(context)); // Line
		b.get(1).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(1).setAlpha(alpha);
		b.add(new ImageButton(context)); // Rectangle
		b.get(2).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(2).setAlpha(alpha);
		b.add(new ImageButton(context)); // Circle
		b.get(3).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(3).setAlpha(alpha);
		b.add(new ImageButton(context)); // Free
		b.get(4).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(4).setAlpha(alpha);
		b.add(new ImageButton(context)); // Zoom
		b.get(5).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(5).setAlpha(alpha);
		b.add(new ImageButton(context)); // Pan
		b.get(6).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.generic_button));
		b.get(6).setAlpha(alpha);
		this.incr = width / b.size();
		setup();
	}
	public BottomOverlay(Context context, ArrayList<ImageButton> b){
		super(context);
		this.width = this.getWidth();
		this.b = b;		
		this.incr = width / b.size();
		setup();
	}
	
	public void setup(){
		setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200));
		setBackgroundColor(Color.GREEN);
		setPadding(20, 10, 20, 30);
		
		ArrayList<RelativeLayout.LayoutParams> layout_params = new ArrayList<RelativeLayout.LayoutParams>();
		for(int i = 0; i < b.size(); i++){
			b.get(i).setId(i+200);
			layout_params.add(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));			
			if(i == 0){
				layout_params.get(i).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layout_params.get(i).setMargins(0, 0, 0, 0);
				b.get(i).setLayoutParams(layout_params.get(i));
			}else {
				layout_params.get(i).addRule(RelativeLayout.LEFT_OF, b.get(i-1).getId());
				layout_params.get(i).setMargins(0, 0, 0, 0);
				b.get(i).setLayoutParams(layout_params.get(i));
			}
			addView(b.get(i));
		}
		
		//Listeners
		b.get(0).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Extrude");
            }
        });
		b.get(1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Line");
            }
        });
		b.get(2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Rectangle");
            }
        });
		b.get(3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Circle");
            }
        });
		b.get(4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Free Form");
            }
        });
		b.get(5).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Zoom");
            }
        });
		b.get(6).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: home_open onclicklistener
            	Log.d("BottomOverlay", "Pan");
            }
        });
		
		
	}
	
}
