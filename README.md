# jsonnative
A native JSON renderer!!! FOR ANDROID!!!

Write a JSON file and it renders in a native Android app!

## How to build your own
Change the url at the START_URL constant in `MainActivity.java`

## Web version included!
See the web.html file!!!

This version is very minimal...

## Syntax
The syntax of JSON Native is very simple.

The simplest page you can make is:
```
{
    "body": { "type":"vscroll", "child":
        { "type": "vbox", "children": [
            { "type": "label", "text": "Hello World!" },
            { "type": "button", "text": "Look I'm a simple button!" }
        ]}
    }
}
```
For a more deeper example goto `app.json`.

This renders only a simple label (TextView) on the standard vertical scrollview with a vertical LinearLayout.

## Widgets
So as you can see contains the body contains a array of widgets / objects, this is the table of all the widgets:

| Type name     | Child(ren)? | Android class | Description  |
| ------------- |------------ | ------------- | ------------ |
| box           | -           | View          | A simple box for decorative purpose: like a horizontal border in a list |
| vbox          | children    | (Vertical) LinearLayout | A vertical box for children widgets |
| hbox          | children    | LinearLayout  | A horizontal box for children widgets |
| stack         | children    | FrameLayout   | A stack layout for child widget |
| vscroll       | child       | ScrollView    | A vertical scroll box for a child widget |
| hscroll       | child       | HorizontalScrollView | A horizontal scroll box for child widget |
| label         | -           | TextView      | A text label |
| button        | -           | Button        | A native button |
| image         | -           | ImageView     | A image |

## For more info
Visit a very similair project called Jasonette:
- http://jasonette.com/
- http://docs.jasonette.com/
- https://github.com/Jasonette/JASONETTE-Android

## A look at this cute icon!!!
![Logo](web_hi_res_512.png)