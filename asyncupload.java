public class asyncUpload extends AsyncTask<Integer, Integer, Integer> {  
    int maxBufferSize = 1*64*1024;
  	int headerSize=89;
  	String urlString = "http://example.com/upload.php";
  	int fileSize=0;
  	int infoSize=0;
  	int percentage=10;
      	
    protected Integer doInBackground(Integer... file) {
      	Integer ret=0;
      	
      	HttpURLConnection conn = null;
      	DataOutputStream dos = null;
      	DataInputStream inStream = null;
      
      	String file_name="example.jpg";
      	String file_path = "/demo/example.jpg";
      
      	String lineEnd = "\r\n";
      	String twoHyphens = "--";
      	String boundary =  "*****";
      
      	int bytesRead, bytesAvailable, bufferSize;
      	byte[] buffer;    		
      	
      	try
      	{
        		Log.i("example","Start uploading file: " + file_name);
        
        		FileInputStream fileInputStream = new FileInputStream(new File(file_path));
        
        		// open a URL connection to the Servlet 
        		URL url = new URL(urlString);
        
        		//Open a HTTP connection to the URL
        		conn = (HttpURLConnection) url.openConnection();
        
        		// Allow Inputs
        		conn.setDoInput(true);
        
        		// Allow Outputs
        		conn.setDoOutput(true);
        
        		// Don't use a cached copy.
        		conn.setUseCaches(false);
        
        		//Timeout 60secs
        		conn.setConnectTimeout(60000);
        		
        		// Use a post method.
        		fileSize=fileInputStream.available();
        		infoSize=fileSize + headerSize + file_name.length();
        		conn.setFixedLengthStreamingMode(infoSize);
        		conn.setRequestMethod("POST");
        
        		conn.setRequestProperty("Connection", "Keep-Alive");
        		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
        
        		dos = new DataOutputStream( conn.getOutputStream() );
        
        		dos.writeBytes(twoHyphens + boundary + lineEnd);
        		dos.writeBytes("Content-Disposition: form-data; name='uploadedfile';filename='" + file_name + "'" + lineEnd);
        		dos.writeBytes(lineEnd);
        
        		// create a buffer of maximum size
        		bytesAvailable = fileInputStream.available();
        		bufferSize = Math.min(bytesAvailable, maxBufferSize);
        		buffer = new byte[bufferSize];
        
        		// read file and write it into form...
        		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        		int bytesSent=0;
        		while (bytesRead > 0)
        		{
        				if (bytesSent > 0){
          					int pg=(bytesSent*100)/infoSize;
          					publishProgress(pg);
        				}
        				bytesSent+=bufferSize;
        				dos.write(buffer, 0, bufferSize);
        				bytesAvailable = fileInputStream.available();
        				bufferSize = Math.min(bytesAvailable, maxBufferSize);
        				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
      			}
    
      			// send multipart form data necesssary after file data...
      			dos.writeBytes(lineEnd);
      			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    
      			//close streams
      			fileInputStream.close();
      			dos.flush();
      			dos.close();
    		}
    		catch (MalformedURLException ex)
    		{
      			Log.e("example", "error: " + ex.getMessage(), ex);
      			ret=1;
    		}
    		catch (IOException ioe)
    		{
      			Log.e("example", "error: " + ioe.getMessage(), ioe);
      			ret=1;
    		}
    		if (ret==0){
        		try {
          			inStream = new DataInputStream ( conn.getInputStream() );
          			String str;
          			   
          			while (( str = inStream.readLine()) != null)
          			{
          			   Log.i("example","Server Response: "+str);
          			}
          			inStream.close();
        		}
        		catch (IOException ioex){
          			Log.e("example", "error: " + ioex.getMessage(), ioex);
          			ret=1;
        		}
    		}
    		return ret;
  	}    
  	protected void onProgressUpdate(Integer... progress) {
    		Log.i("PROGRESS",String.valueOf(progress[0])+"%");
    		int i=progress[0]/percentage;
    		int background=i*percentage;
    		
        //Do anything in UI
  	}
  	protected void onPostExecute(Integer result) {
    		Log.i("PROGRESS","100%");

    		//Do anything in UI
  	} 
}