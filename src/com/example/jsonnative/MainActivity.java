package com.example.jsonnative;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final String START_URL = "https://bastiaan.plaatsoft.nl/app.json";
    private JSONObject head;
    private FrameLayout root;
    private class FetchDataTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(urls[0]).openStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String str; while ((str = bufferedReader.readLine()) != null) stringBuilder.append(str).append("\n");
                bufferedReader.close();
                return stringBuilder.toString();
            } catch (Exception e) {
                return null;
            }
        }
        protected void onPostExecute(String data) {
            try {
                JSONObject page = new JSONObject(data);
                head = page.has("head") ? page.getJSONObject("head") : new JSONObject();
                render((ViewGroup)root, page.getJSONObject("body"));
            } catch (Exception e) {}
        }
    }
    private class FetchImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        public FetchImageTask(ImageView imageView) {
            this.imageView = imageView;
        }
        protected Bitmap doInBackground(String... urls) {
            try {
                return BitmapFactory.decodeStream(new URL(urls[0]).openStream());
            } catch (Exception e) {}
            return null;
        }
        protected void onPostExecute(Bitmap image) {
            imageView.setImageBitmap(image);
        }
    }
    private void render(ViewGroup root, JSONObject child) throws Exception {
        View view = null;
        String type = child.getString("type");
        if (type.equals("box")) {
            view = new View(this);
        }
        if (type.equals("hbox")) {
            view = new LinearLayout(this);
            JSONArray children = child.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) render((ViewGroup)view, children.getJSONObject(i));
        }
        if (type.equals("vbox")) {
            view = new LinearLayout(this);
            ((LinearLayout)view).setOrientation(LinearLayout.VERTICAL);
            JSONArray children = child.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) render((ViewGroup)view, children.getJSONObject(i));
        }
        if (type.equals("stack")) {
            view = new FrameLayout(this);
            JSONArray children = child.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) render((ViewGroup)view, children.getJSONObject(i));
        }
        if (type.equals("vscroll")) {
            view = new ScrollView(this);
            render((ViewGroup)view, child.getJSONObject("child"));
        }
        if (type.equals("hscroll")) {
            view = new HorizontalScrollView(this);
            render((ViewGroup)view, child.getJSONObject("child"));
        }
        if (type.equals("label")) {
            view = new TextView(this);
            ((TextView)view).setText(child.getString("text"));
        }
        if (type.equals("button")) {
            view = new Button(this);
            ((TextView)view).setText(child.getString("text"));
        }
        if (type.equals("image")) {
            view = new ImageView(this);
            new FetchImageTask((ImageView)view).execute(child.getString("url"));
        }

        JSONObject style = child.has("style") ? child.getJSONObject("style") : new JSONObject();
        if (child.has("class")) {
            String[] classes = child.getString("class").split("\\s+");
            for (int i = 0; i < classes.length; i++) {
                JSONObject style_class = head.getJSONObject("styles").getJSONObject(classes[i]);
                JSONArray keys = style_class.names();
                for (int j = 0; j < keys.length(); j++) {
                    String key = keys.getString(j);
                    if (!style.has(key)) style.put(key, style_class.getString(key));
                }
            }
        }

        int width = style.has("width") ? Integer.parseInt(style.getString("width")) : (root instanceof FrameLayout || ((LinearLayout)root).getOrientation() == LinearLayout.VERTICAL ? LinearLayout.LayoutParams.MATCH_PARENT : LinearLayout.LayoutParams.WRAP_CONTENT);
        int height = style.has("height") ? Integer.parseInt(style.getString("height")) : (root instanceof FrameLayout ? LinearLayout.LayoutParams.MATCH_PARENT : LinearLayout.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams layoutParams = root instanceof LinearLayout ? new LinearLayout.LayoutParams(width, height) : new ViewGroup.LayoutParams(width, height);

        if (root instanceof LinearLayout) {
            int marginTop = 0, marginRight = 0, marginBottom = 0, marginLeft = 0;
            if (style.has("margin")) {
                String[] margins = style.getString("margin").split("\\s+");
                if (margins.length == 1) { marginTop = marginRight = marginBottom = marginLeft = Integer.parseInt(margins[0]); }
                if (margins.length == 2) { marginTop = marginBottom = Integer.parseInt(margins[0]); marginRight = marginLeft = Integer.parseInt(margins[1]); }
                if (margins.length == 3) { marginTop = Integer.parseInt(margins[0]); marginRight = marginLeft = Integer.parseInt(margins[1]); marginBottom = Integer.parseInt(margins[2]); }
                if (margins.length == 4) { marginTop = Integer.parseInt(margins[0]); marginRight = Integer.parseInt(margins[1]); marginBottom = Integer.parseInt(margins[2]); marginLeft = Integer.parseInt(margins[3]); }
            }
            if (style.has("margin-top")) marginTop = Integer.parseInt(style.getString("margin-top"));
            if (style.has("margin-right")) marginRight = Integer.parseInt(style.getString("margin-right"));
            if (style.has("margin-bottom")) marginBottom = Integer.parseInt(style.getString("margin-bottom"));
            if (style.has("margin-left")) marginLeft = Integer.parseInt(style.getString("margin-left"));
            if (marginTop != 0 || marginRight != 0 || marginBottom != 0 || marginLeft != 0) ((LinearLayout.LayoutParams)layoutParams).setMargins(marginLeft, marginTop, marginRight, marginBottom);
        }

        int paddingTop = 0, paddingRight = 0, paddingBottom = 0, paddingLeft = 0;
        if (style.has("padding")) {
            String[] paddings = style.getString("padding").split("\\s+");
            if (paddings.length == 1) { paddingTop = paddingRight = paddingBottom = paddingLeft = Integer.parseInt(paddings[0]); }
            if (paddings.length == 2) { paddingTop = paddingBottom = Integer.parseInt(paddings[0]); paddingRight = paddingLeft = Integer.parseInt(paddings[1]); }
            if (paddings.length == 3) { paddingTop = Integer.parseInt(paddings[0]); paddingRight = paddingLeft = Integer.parseInt(paddings[1]); paddingBottom = Integer.parseInt(paddings[2]); }
            if (paddings.length == 4) { paddingTop = Integer.parseInt(paddings[0]); paddingRight = Integer.parseInt(paddings[1]); paddingBottom = Integer.parseInt(paddings[2]); paddingLeft = Integer.parseInt(paddings[3]); }
        }
        if (style.has("padding-top")) paddingTop = Integer.parseInt(style.getString("padding-top"));
        if (style.has("padding-right")) paddingRight = Integer.parseInt(style.getString("padding-right"));
        if (style.has("padding-bottom")) paddingBottom = Integer.parseInt(style.getString("padding-bottom"));
        if (style.has("padding-left")) paddingLeft = Integer.parseInt(style.getString("padding-left"));
        if (paddingTop != 0 || paddingRight != 0 || paddingBottom != 0 || paddingLeft != 0) view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        if (style.has("background-color")) view.setBackgroundColor(Color.parseColor(style.getString("background-color")));
        
        if (type.equals("label") || type.equals("button")) {
            if (style.has("color")) ((TextView)view).setTextColor(Color.parseColor(style.getString("color")));
            if (style.has("font-size")) ((TextView)view).setTextSize(Float.parseFloat(style.getString("font-size")));
            if (style.has("font-weight") && style.getString("font-weight").equals("bold")) {
                if (style.has("font-style") && style.getString("font-style").equals("italic")) {
                    ((TextView)view).setTypeface(null, Typeface.BOLD_ITALIC);
                } else {
                    ((TextView)view).setTypeface(null, Typeface.BOLD);
                }
            } else if (style.has("font-style") && style.getString("font-style").equals("italic")) {
                ((TextView)view).setTypeface(null, Typeface.ITALIC);
            }
        }
        view.setLayoutParams(layoutParams);
        root.addView(view);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(root = new FrameLayout(this));
        new FetchDataTask().execute(START_URL);
    }
}