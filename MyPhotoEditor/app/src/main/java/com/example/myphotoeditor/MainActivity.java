package com.example.myphotoeditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//outside library used for image filters
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.geometry.Point;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ToneCurveSubFilter;

//library used for crop
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;




public class MainActivity extends AppCompatActivity {

    //load the outside library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    //the user's selected image
    ImageView pic;
    //the filter buttons
    ImageView filter1;
    ImageView filter2;
    ImageView filter3;
    ImageView filter4;
    CropImageView crop;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pic = findViewById(R.id.picture);
        filter1 = findViewById(R.id.filter1);
        filter2 = findViewById(R.id.filter2);
        filter3 = findViewById(R.id.filter3);
        filter4 = findViewById(R.id.filter4);
        crop = findViewById(R.id.cropImageView);

    }

    //when the select picture button is pressed
    public void getPhoto(View view){

        pickImage();
    }

    //when the save button is pressed
    public void savePhoto(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            //get bitmap of the picture
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();

            //save the image to phone's gallery
            MediaStore.Images.Media.insertImage
                    (getContentResolver(), convert2, "edited photo", "edited photo");

            //after user saves then clear the picture that was there
            pic.setImageBitmap(null);


            //send toast notification that the user successfully downloaded
            //the edited file
            Context context = getApplicationContext();
            CharSequence text = "Successfully saved picture!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
        else{
            System.out.println("no picture to save");
        }
    }

    //when the discard button is pressed
    public void discardPhoto(View view){

        //clear picture
        pic.setImageBitmap(null);

    }

    //when the crop button is pressed
    public void cropPhoto(View view){

        if (pic.getDrawable() != null) {
            //minimize image view
            pic.requestLayout();
            pic.getLayoutParams().height = 300;

            crop.requestLayout();
            crop.getLayoutParams().height = 700;

            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);

            //add the image to cropper screen
            crop.setImageBitmap(copy);
        }
        else{
            System.out.println("please select a photo first!");
        }

    }

    //when the crop button is pressed
    public void saveCrop(View view){

        //make sure there is an image and that crop image was pressed first
        if (pic.getDrawable() != null && crop.getCroppedImage() != null) {

            //make image view big again after the save
            pic.requestLayout();
            pic.getLayoutParams().height = 1000;

            //make crop "disappear" when user saves
            crop.requestLayout();
            crop.getLayoutParams().height = 1;

            //get cropped
            Bitmap cropped = crop.getCroppedImage();

            //set photo to new cropped
            pic.setImageBitmap(cropped);
        }
        else{
            System.out.println("please select a photo and/or select crop photo first");
        }
    }



    //has the user select an image from their library
    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //set the imageview in app with the user's selected photo
                pic.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("something went wrong");
            }

        }

        else {
            System.out.println("havent picked image");
        }
    }

    //when the filter 1 is clicked
    public void use_filter1(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {
            Filter filt1 = new Filter();
            filt1.addSubFilter(new BrightnessSubFilter(30));
            filt1.addSubFilter(new ContrastSubFilter(1.1f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt1.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 2 is clicked
    public void use_filter2(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt2 = SampleFilters.getLimeStutterFilter();

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt2.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 3 is clicked
    public void use_filter3(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt3 = SampleFilters.getAweStruckVibeFilter();

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt3.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 4 is clicked
    public void use_filter4(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt4 = SampleFilters.getNightWhisperFilter();

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt4.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 5 is clicked
    public void use_filter5(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt5 = new Filter();
            filt5.addSubFilter(new ColorOverlaySubFilter(100, .1f,.7f,.7f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt5.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 6 is clicked
    public void use_filter6(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt6 = new Filter();
            filt6.addSubFilter(new ColorOverlaySubFilter(100, .1f,.1f,.7f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt6.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 7 is clicked
    public void use_filter7(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt7 = new Filter();
            filt7.addSubFilter(new ColorOverlaySubFilter(100, .5f,.5f,.2f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt7.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 8 is clicked
    public void use_filter8(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt8 = new Filter();
            filt8.addSubFilter(new ColorOverlaySubFilter(100, .2f,.2f,.6f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt8.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 9 is clicked
    public void use_filter9(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt9 = new Filter();
            filt9.addSubFilter(new ColorOverlaySubFilter(100, .1f,.5f,.1f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt9.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }

    //when the filter 10 is clicked
    public void use_filter10(View view){

        //to prevent app from crashing when the user
        //did not select a picture but tries to save
        if (pic.getDrawable() != null) {

            Filter filt10 = new Filter();
            filt10.addSubFilter(new ColorOverlaySubFilter(100, .3f,.3f,.1f));

            //we need get the Drawable of the image view and convert/copy it in order
            //to be able to add a filter and change the bitmap.
            BitmapDrawable convert1 = (BitmapDrawable) pic.getDrawable();
            Bitmap convert2 = convert1.getBitmap();
            Bitmap copy = convert2.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap output = filt10.processFilter(copy);

            pic.setImageBitmap(output);
        }
        else{
            System.out.println("please select a photo first!");
        }
    }








}
