package com.stone.notificationfilter.notificationhandler.databases;

import android.content.Context;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.filestore.Crypter.DecryptionFailedException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class NotificationHandlerItemFileStorage {

  /**
   *
   */
  private static final long serialVersionUID = 6410127608267365958L;
  private static final String TAG ="HandlerItemFileStorage";
  private Context context;
  private static  String  fileName="NotificationHandler.json";
  private HashMap<String, NotificationHandlerItem> storageMap;

  private boolean autosave;

  /**
   * Creates a FileStorage. It allows you to store your serializable object in a file using a key
   * for identification and to read it somewhen later.
   *
   * @param autosave Whether every <code>store</code> operation shall automatically write the whole
   *        FileStorage to disk. If false, you will need to call the <code>save</code> method
   *        manually.
   * @param filepath The path of the file your data shall be stored in
   * @throws IOException if the file cannot be created
   * @throws IllegalArgumentException if the file is a directory
   */
  public NotificationHandlerItemFileStorage(Context context, boolean autosave)
      throws IllegalArgumentException, IOException {
    this(context);
    this.autosave = autosave;
  }

  /**
   * Creates a FileStorage. It allows you to store your serializable object in a file using a key
   * for identification and to read it somewhen later.<br>
   * All data <code>store</code>d in this FileStorage will instantly be stored in the given file.
   * This might cause many write operations on disk.
   *
   * @param  context The file your data shall be stored in
   * @throws IOException if your file cannot be created
   * @throws IllegalArgumentException if your file is a directory
   */
  public NotificationHandlerItemFileStorage(Context context) throws IOException, IllegalArgumentException {
    this.context = context;
    storageMap = new HashMap<String, NotificationHandlerItem>();
    boolean isFirstBoot = SpUtil.getBoolean(context,"appSettings","isFirstIntitHandler", true);
    if (isFirstBoot){
      save();
      SpUtil.putBoolean(context,"appSettings","isFirstIntitHandler", false);
    }
    load();
  }

  /**
   * Stores the FileStorage in the file on disk
   */
  public void save() throws IOException {

    if (storageMap==null && storageMap.size()==0){
      return ;
    }
    String json = "";
    try {
      Gson gson = new GsonBuilder().serializeNulls().create();
      json = gson.toJson(storageMap);
    }catch (JsonIOException e)
    {
      e.printStackTrace();
      return;
    }
    FileOutputStream outputStream =null;
    try{
      outputStream = context.openFileOutput(fileName,Context.MODE_PRIVATE);
      outputStream.write(json.getBytes());
      outputStream.flush();
      outputStream.close();
    }catch (FileNotFoundException e){
      e.printStackTrace();
      return ;
    }
    catch (IOException e){
      Log.e(TAG,"file");
      try{
        outputStream.close();
      } catch (IOException e2) {
        e2.printStackTrace();
      }
      return  ;
    }
    return ;
  }

  /**
   * Loads the FileStorage from the file
   */
  @SuppressWarnings("unchecked")
  private <T> void load() throws IOException {

    FileInputStream fos = null;
    BufferedReader reader = null;
    StringBuilder content = new StringBuilder();
    try {
      fos = context.openFileInput(fileName);
      reader = new BufferedReader((new InputStreamReader(fos)));
      String line = "";
      while ((line = reader.readLine())!= null){
        content.append(line);
      }
      fos.close();
    }catch (FileNotFoundException e){
      e.printStackTrace();
    }catch (IOException e) {
      Log.e(TAG,"file");
      try{
        reader.close();

      } catch (IOException e2) {
        e2.printStackTrace();
      }
    }
    if (content.length() !=0){
      storageMap = new Gson().fromJson(content.toString(), new TypeToken<HashMap<String,NotificationHandlerItem>>(){}.getType());
    }
  }

  /**
   * Stores an Object <i>o</i> using a String <i>key</i> for later identification.<br>
   * Use <code>store(String key, Object o, String password)</code> for storing your data using AES
   * encryption.
   * 
   * @param key The key as String.
   * @param o The Object.
   */
  public void store(String key, NotificationHandlerItem o) throws IOException {
    storageMap.put(key, o);
    if (autosave) {
      save();
    }
  }

  /**
   * Stores an Object <i>o</i> using a String <i>key</i> for later identification.<br>
   * Use <code>store(String key, Object o)</code> for storing your data without encryption.
   * 
   * @param key The key as String.
   * @param o The Object.
   * @param password The password.
   */
//  public void store(String key, T o, String password) throws IOException {
//    store(key, Crypter.encrypt(o, password));
//  }

  /**
   * Reads your object from the storage.<br>
   * <br>
   * Use <u>get(String key, String password)</u> for AES encrypted objects.
   * 
   * @param key The key the object is available under
   * @return your Object or null if nothing was found for <i>key</i>
   */
  public NotificationHandlerItem get(String key) {
    return storageMap.get(key);
  }

  /**
   * Reads your AES encrypted object from the storage.<br>
   * <br>
   * Use <u>get(String key)</u> instead for unencrypted objects.
   * 
   * @param key The key the object is available under
   * @param password The password to use for decryption
   * @return your object or null if nothing was found for <i>key</i> or if decryption failed (wrong
   *         password)
   * @throws DecryptionFailedException This usually happens if the password is wrong
   */
//  public T get(String key, String password) throws DecryptionFailedException {
//    if (storageMap.get(key) instanceof CryptedObject) {
//      return Crypter.decrypt((CryptedObject) get(key), password);
//    } else {
//      return get(key);
//    }
//  }

  /**
   * All stored objects in an ArrayList of Objects
   * 
   * @return all stored objects in an ArrayList of Objects
   */
  public ArrayList<NotificationHandlerItem> getAllAsArrayList() {
    ArrayList<NotificationHandlerItem> result = new ArrayList<NotificationHandlerItem>();
    for (NotificationHandlerItem c : storageMap.values()) {
      result.add(c);
    }
    return result;
  }

  /**
   * All stored objects in a HashMap of Strings and Objects
   * 
   * @return all stored objects in a HashMap of Strings and Objects
   */
  public HashMap<String, NotificationHandlerItem> getAll() {
    return storageMap;
  }

  /**
   * Prints all stored keys with corresponding objects
   */
  public void printAll() {
    System.out.println(this);
  }

  /**
   * Removes an Key-Object pair from the storage
   * 
   * @param key The key of the object
   */
  public void remove(String key) throws IOException {
    storageMap.remove(key);
    if (autosave) {
      save();
    }
  }

  /**
   * Checks whether a key is registerd
   * 
   * @param key The Key.
   * @return true if an object is available for that key
   */
  public boolean hasKey(String key) {
    return storageMap.containsKey(key);
  }

  /**
   * Checks whether an object is stored at all
   * 
   * @param o The Object.
   * @return true if the object is stored
   */
  public boolean hasObject(NotificationHandlerItem o) {
    return storageMap.containsValue(o);
  }

  /**
   * Returns the number of objects (elements) stored
   * 
   * @return The number of objects (elements) stored
   */
  public int getSize() {
    return storageMap.size();
  }

  /**
   * @return a String representation of the HashMap containing all the key-object pairs.
   */
  @Override
  public String toString() {
    String result = "FileStorage @ " + fileName + "\n";
    for (String cKey : storageMap.keySet()) {
        result += cKey + " :: " + storageMap.get(cKey) + "\n";
    }
    return result.trim();
  }

}
