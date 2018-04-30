package com.example.jsonnative;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.text.InputType;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    public JSONObject head;

    public static String fetch(String url) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line; while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line);
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public static Bitmap fetchBitmap(String url) throws Exception {
        return BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
    }

    public void renderBox(LinearLayout root, JSONArray children) throws Exception {
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i);

            if (!child.has("style")) child.put("style", new JSONObject());
            String type = child.getString("type");
            JSONObject style = child.getJSONObject("style");

            // Class styles
            if (child.has("class")) {
                String[] classes = child.getString("class").split("\\s+");
                for (int j = 0; j < classes.length; j++) {
                    JSONObject style_class = head.getJSONObject("styles").getJSONObject(classes[j]);
                    JSONArray keys = style_class.names();
                    for (int k = 0; k < keys.length(); k++) {
                        String key = keys.getString(k);
                        if (!style.has(key)) style.put(key, style_class.getString(key));
                    }
                }
            }

            View view = null;

            // Boxes
            if (type.equals("box")) {
                view = new View(this);
            }
            if (type.equals("hbox")) {
                view = new LinearLayout(this);
                renderBox((LinearLayout)view, child.getJSONArray("children"));
            }
            if (type.equals("vbox")) {
                view = new LinearLayout(this);
                ((LinearLayout)view).setOrientation(LinearLayout.VERTICAL);
                renderBox((LinearLayout)view, child.getJSONArray("children"));
            }

            // Widgets
            if (type.equals("label")) {
                view = new TextView(this);
            }
            if (type.equals("button")) {
                view = new Button(this);
                if (!style.has("padding")) style.put("padding", "12");
            }
            if (type.equals("image")) {
                view = new ImageView(this);
                ((ImageView)view).setImageBitmap(fetchBitmap(child.getString("url")));
            }
            if (type.equals("input")) {
                view = new EditText(this);
                ((EditText)view).setInputType(child.has("password") ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                if (child.has("hint")) ((EditText)view).setHint(child.getString("hint"));
                if (!style.has("padding")) style.put("padding", "12");
            }

            // Href onclick Link
            if (child.has("href")) {
                view.setTag(child.getJSONObject("href"));
                view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            JSONObject href = (JSONObject)view.getTag();
                            String url = href.getString("url");
                            if (url.equals("back")) {
                                MainActivity.this.finish();
                            } else {
                                if (href.has("view") && href.getString("view").equals("web")) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                } else {
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    intent.putExtra("url", url);
                                    startActivity(intent);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("jn", Log.getStackTraceString(e));
                        }
                    }
                });
            }

            // Width & height
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                style.has("width") ? Integer.parseInt(style.getString("width")) :
                    (root.getOrientation() == LinearLayout.VERTICAL ? LinearLayout.LayoutParams.MATCH_PARENT : LinearLayout.LayoutParams.WRAP_CONTENT),
                style.has("height") ? Integer.parseInt(style.getString("height")) :  LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // Margin
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
            layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);

            // Padding
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
            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

            // Background color
            if (style.has("background-color")) view.setBackgroundColor(Color.parseColor(style.getString("background-color")));

            // Text/Font styles
            if (type.equals("label") || type.equals("button") || type.equals("input")) {
                if (child.has("text")) ((TextView)view).setText(child.getString("text"));
                if (style.has("color")) ((TextView)view).setTextColor(Color.parseColor(style.getString("color")));
                if (style.has("font-size")) ((TextView)view).setTextSize(Float.parseFloat(style.getString("font-size")));
                if (style.has("font-weight") && style.getString("font-weight").equals("bold")) ((TextView)view).setTypeface(null, Typeface.BOLD);
                if (style.has("font-style") && style.getString("font-style").equals("italic")) {
                    ((TextView)view).setTypeface(null, Typeface.ITALIC);
                    if (style.has("font-weight") && style.getString("font-weight").equals("bold")) ((TextView)view).setTypeface(null, Typeface.BOLD_ITALIC);
                }
            }

            view.setLayoutParams(layoutParams);
            root.addView(view);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ScrollView scroll = new ScrollView(this);
            setContentView(scroll);

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            scroll.addView(root);

            Intent intent = getIntent();
            JSONObject page = new JSONObject(fetch(intent.hasExtra("url") ? intent.getStringExtra("url") : "https://bastiaan.plaatsoft.nl/app.json"));
            head = page.has("head") ? page.getJSONObject("head") : new JSONObject();
            if (head.has("title")) setTitle(head.getString("title"));
            renderBox(root, page.getJSONArray("body"));
        } catch (Exception e) {
            Log.d("jn", Log.getStackTraceString(e));
        }
    }
}