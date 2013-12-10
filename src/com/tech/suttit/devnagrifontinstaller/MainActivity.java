package com.tech.suttit.devnagrifontinstaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{	
	boolean finished1=true,finished2=true;
	DataOutputStream os;
	DataInputStream osRes;
	Process p;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		Button buttonOne = (Button) findViewById(R.id.button1);
		buttonOne.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	//Toast.makeText(MainActivity.this,"Button Clicked",Toast.LENGTH_LONG).show();
		    	            	;
        	    try {  
        	           p = Runtime.getRuntime().exec("su");   

        	           os = new DataOutputStream(p.getOutputStream());
        	           osRes = new DataInputStream(p.getInputStream());
        	           
        	           if (null != os && null != osRes)
        	           {
        	              // Getting the id of the current user to check if this is root
        	              os.writeBytes("id\n");
        	              os.flush();

        	              String currUid = osRes.readLine();
        	              boolean exitSu = false;
        	              if (null == currUid)
        	              {        	               
        	                 exitSu = false;
        	                 Toast.makeText(MainActivity.this,"Sorry, but can't get root access :(",Toast.LENGTH_LONG).show();
        	                 Log.d("ROOT", "Can't get root access or denied by user");
        	              }
        	              else if (true == currUid.contains("uid=0"))
        	              {        	               
        	                 exitSu = true;
        	                 Toast.makeText(MainActivity.this,"Installing files :)",Toast.LENGTH_SHORT).show();
        	                 Log.d("ROOT", "Root access granted");
        	              }
        	              else
        	              {        	                 
        	                 exitSu = true;
        	                 Toast.makeText(MainActivity.this,"Sorry, but root access rejected :(",Toast.LENGTH_LONG).show();
        	              }

        	              if (exitSu)
        	              {
        	            	  os.writeBytes("chmod 777 /system/fonts\n");
        	            	  os.flush(); 
        	            	  copyAssets("DroidHindi.ttf");
	               	          copyAssets("DroidSansFallback.ttf");
        	            	  if(finished1 && finished2){
        	            		  os.writeBytes("chmod 777 /system/fonts/DroidHindi.ttf\n");
        	            		  Toast.makeText(MainActivity.this,"Installation Finished :)",Toast.LENGTH_LONG).show();
	               	          	               	       
			               	 os.writeBytes("reboot now\n");
			 				os.writeBytes("exit\n");  
			 		        os.flush(); 
        	            	  }        	            	          	            	  
        	              }
        	           }        	                   	           
        	           p.waitFor();  

        	    }catch (InterruptedException e) {  
        	              // TODO Code to run in interrupted exception  
	        	    	Toast.makeText(MainActivity.this,"Sorry, Root access was denied",Toast.LENGTH_LONG).show();
        	           }  
        	         catch (IOException e) {  
        	           // TODO Code to run in input/output exception  
        	        	 Toast.makeText(MainActivity.this,"Sorry, Root access was denied",Toast.LENGTH_LONG).show();
        	        }                        
				
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void copyAssets(String filename)
	{
	      AssetManager assetManager = getAssets();	      
	      InputStream in = null;
	      FileWriter out = null;	      
	      try
	      {
	            in = assetManager.open(filename);
	            //File path = Environment.getRootDirectory();	            
	            //Toast.makeText(MainActivity.this,path.getAbsolutePath(),Toast.LENGTH_SHORT).show();
	            File f = new File("/system/fonts/"+filename);
	            //Toast.makeText(MainActivity.this,f.getAbsolutePath(),Toast.LENGTH_SHORT).show();
	            if(f.exists())
            	{
	            	f.delete();		            	
	            	f = new File("/system/fonts/"+filename);
            	}
	            //Toast.makeText(MainActivity.this, "I am here", Toast.LENGTH_LONG);
	            /*if(!f.createNewFile())
	            	Toast.makeText(MainActivity.this, "Failed to create file", Toast.LENGTH_SHORT).show();*/
	            //f.createNewFile();
	            out = new FileWriter(f,false);
	            copyFile(in, out);
	            in.close();
	            in = null;
	            out.flush();
	            out.close();
	            out = null;
	            //Toast.makeText(MainActivity.this, "Written File "+f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
	      }
	      catch(IOException e)
	      {
	    	  Log.e("Fuck","Failing again",e);
	    	  /*Toast.makeText(MainActivity.this, "Failed Installation :(", Toast.LENGTH_SHORT).show();
	    	  finished=false;*/
	      }      
	}
	private void copyFile(InputStream in, FileWriter out) throws IOException
	{
	      byte[] buffer = new byte[1024];
	      int read;	      
	      while((read = in.read(buffer)) != -1)
	      {
	            out.write(new String(buffer,0,read));
	      }
	}			
}
