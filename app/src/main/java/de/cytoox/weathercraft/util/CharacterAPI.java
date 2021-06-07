package de.cytoox.weathercraft.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.cytoox.weathercraft.listener.AsyncResponse;

/**
 * The type Character api.
 */
public class CharacterAPI extends AsyncTask<Void, Void, String> {
    public AsyncResponse delegate;

    public static final int READ_TIMEOUT = 7000;
    public static final int CONNECTION_TIMEOUT = 3000;
    public static final String RENDERURL = "https://skins.cytooxien.de/render.php";
    public static final String RENDERQUALITY = "&ratio=";
    public static final String HEADPARAMETER = "&vr=0&hr=0&headOnly=true";
    public static final String FORMATPARAMTETER = "&format=base64";

    private String name;
    private String cosmetic;
    private URL skinUrl;
    private URL headUrl;
    private String render;
    private int quality;
    private boolean isHead;

    /**
     * Instantiates a new Character to request a render.
     *
     * @param name    the name of the character
     * @param quality the quality/scale of the render
     * @param isHead  if true: requesting only the head of a character
     * @author Marcel Steffen
     */
    public CharacterAPI(String name, int quality, boolean isHead) {
        this.name = name;
        this.quality = quality;
        this.isHead = isHead;
        try {
            this.skinUrl = new URL(RENDERURL + "?" + "user=" + name + FORMATPARAMTETER + RENDERQUALITY + quality);
            this.headUrl = new URL(skinUrl + HEADPARAMETER);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.render = null;
    }

    /**
     * Instantiates a new Character api.
     *
     * @param name the name of the character
     */
    public CharacterAPI(String name) {
        this.name = name;
    };

    /**
     * Sets a new name and adjust the URLs.
     *
     * @param name the new name for the character
     */
    public void setName(String name) {
        this.name = name;
        try {
            this.skinUrl = new URL(RENDERURL + "?" + "user=" + name + FORMATPARAMTETER + RENDERQUALITY + quality);
            this.headUrl = new URL(skinUrl + HEADPARAMETER);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets cosmetic.
     *
     * @param cosmetic the cosmetic you want the character to wear.
     */
    public void setCosmetic(String cosmetic) {
        this.cosmetic = cosmetic;
        try {
            this.skinUrl = new URL(RENDERURL + "?" + "user=" + name + "&cosmetic=" + cosmetic + FORMATPARAMTETER + RENDERQUALITY + quality);
            this.headUrl = new URL(skinUrl + HEADPARAMETER);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRender() {
        return this.render;
    }
    public String getName() {
        return this.name;
    }
    public String getCosmetic() {
        return this.cosmetic;
    }
    public void setRender(String Render) {
        this.render = Render;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result;

        try {
            URL url;
            //Request skin render
            if (this.isHead) { //true: request head
                url = this.headUrl;
            } else { //false: request skin
                url = this.skinUrl;
            }

            HttpURLConnection skinConnection = (HttpURLConnection) url.openConnection();
            skinConnection.setRequestMethod("GET");
            skinConnection.setReadTimeout(READ_TIMEOUT);
            skinConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            Log.i("CharacterAPI", "Connecting... " + url);
            skinConnection.connect();
            Log.i("CharacterAPI", "Connected to: " + url);

            InputStreamReader skinStreamReader = new InputStreamReader(
                    skinConnection.getInputStream());
            BufferedReader skinReader = new BufferedReader(skinStreamReader);
            StringBuilder skinRenderResult = new StringBuilder();

            String line;
            while((line = skinReader.readLine()) != null){
                skinRenderResult.append(line);
            }

            skinReader.close();
            skinStreamReader.close();
            skinConnection.disconnect();

            this.render = skinRenderResult.toString(); //cache result in object
            result = skinRenderResult.toString();

        } catch(IOException e) {
            Log.e("CharacterAPI", "Skin request failed", e);
            result = null;
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.render = result;

        delegate.processFinish(result);
    }

    /**
     * Gets bitmap.
     *
     * @return the bitmap
     */
    public Bitmap getBitmap() {
        if (this.getRender() != null) {
            byte[] decodedString;
            decodedString = Base64.decode(this.getRender(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        } else {
            return null;
        }
    }
}

