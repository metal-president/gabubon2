
package net.gabuchan.androidrecipe.recipe065;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.gabuchan.androidrecipe.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Recipe065Activity extends Activity {
    private static final int REQUEST_CODE = 1;

    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_065);
        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // 選択された画像のUriを取得
            Uri uri = data.getData();
            // 画像を縮小して取得
            Bitmap bitmap = decodeUri(uri, mImageView.getWidth());
            // ImageViewにセット
            mImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Uriから指定されたサイズを下回らない最小のサイズのBitmapを生成します。
     * inSampleSizeが整数でしか倍率を指定できないのでぴったりにはなりません。
     * 
     * @param uri 画像のUri
     * @param width 縮小後のサイズ（ぴったりにはならない）
     * @return Bitmap画像
     */
    private Bitmap decodeUri(Uri uri, int width) {
        try {
            // 縮小する倍率を計算する
            int sampleSize = calcSampleSize(uri, width);

            BitmapFactory.Options options = new BitmapFactory.Options();
            // 縮小する倍率をセット
            options.inSampleSize = sampleSize;

            InputStream is = getContentResolver().openInputStream(uri);
            // Bitmapを生成！
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 縮小する倍率を計算します。 具体的には、指定されたサイズを下回らない最小のサイズになるような倍率を計算します。
     * 
     * @param uri 画像のUri
     * @param size 縮小後のサイズ
     * @return 縮小する倍率
     */
    private int calcSampleSize(Uri uri, int size) {
        int sampleSize = 1;
        try {
            InputStream is = getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            // Bitmapは生成せずに画像のサイズを測るだけの設定
            options.inJustDecodeBounds = true;
            // 測定！
            BitmapFactory.decodeStream(is, null, options);
            is.close();

            // 画像サイズを指定されたサイズで割る
            // int同士の除算なので自動的に小数点以下は切り捨てられる
            sampleSize = options.outWidth / size;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sampleSize;
    }
}
