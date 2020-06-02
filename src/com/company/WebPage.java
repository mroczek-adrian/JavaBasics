package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WebPage {
    public WebPage() {

    }

    public String GetDataFromWebPage(String address) {
        try {
            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla");

            BufferedReader buffered = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = buffered.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
            }
            buffered.close();
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void SendDataToWebPage(String date, String address) {
        try {
            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla");

            BufferedWriter bufferedW = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
            bufferedW.write(date);
            bufferedW.flush();
            bufferedW.close();

            BufferedReader bufferedR = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            //StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedR.readLine()) != null) {
                System.out.println(line);
            }
            bufferedR.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public Elements GetAllLinks(String data) {
    	Document doc = Jsoup.parse(data);
    	Elements links = doc.select("a[href]");
    	return links;
    }
    
    public void GetLinksFromWebPage(String date) throws MalformedURLException {
        Document doc = Jsoup.parse(date);
//        System.out.println(doc.head());

        Element body = doc.body();
        Elements linki = body.select("a[href]");

        //        Elements linki = body.select("img");
//        System.out.println(linki.size());

//        for (Element e : linki) {
//            System.out.println(e.attr("href"));
//        }

        System.out.println(linki.get(5).attr("abs:href"));
        String temp = GetDataFromWebPage(linki.get(5).attr("abs:href"));
        System.out.println(temp);
        doc = Jsoup.parse(temp);
        body = doc.body();

        linki = body.select("a[href]");
        System.out.println(linki.size());
        
      
        for (Element e: linki) {
            URL tempLink = new URL(e.attr("abs:href"));
            if (!tempLink.equals("https://drive.google.com/drive/my-drive")) {
                System.out.println(tempLink.getHost());
            }
           
        }
    }

    public static void GetConnectionInfo(URLConnection connection, URL url) {
        System.out.println("Protokol:" + url.getProtocol());
        System.out.println("Info: " + url.getUserInfo());
        String host = url.getHost();
        if (host != null) {
            int atSign = host.indexOf('@');
        }
    }
    
    public void Cookie(String address) {
    	HttpURLConnection httpConn;
    	StringBuilder specifyBuilder = new StringBuilder();
    	HashMap<String,String> tempData = new HashMap<>();
    	//usuniecie wrazliwych danych
    	tempData.put("login","prosze wpisac login");
    	tempData.put("password","prosze wpisac haslo");
   
    	try {
    		StringBuilder stringB = new StringBuilder();
    		for (Map.Entry<String, String> param : tempData.entrySet()) {
    			if(stringB.length()>0) stringB.append(";");
    			stringB.append(URLEncoder.encode(param.getKey(),"UTF-8"));
    			stringB.append("=");
    			stringB.append(URLEncoder.encode(param.getValue(),"UTF-8"));
    		}
    		URL url = new URL(address);
    		httpConn = (HttpURLConnection) url.openConnection();
    		
    		httpConn.setRequestProperty("Accept", "Application");
    		httpConn.setReadTimeout(10000);
    		httpConn.setConnectTimeout(10000);
    		
    		httpConn.setRequestProperty("Cookie", stringB.toString());
    		
    		if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			InputStream in = httpConn.getInputStream();
    			BufferedReader buffor = new BufferedReader(new InputStreamReader(in,"UTF-8"));
    			
    			String line;
    			StringBuilder tempBulider = new StringBuilder();
    			
    			
    			//Wyœwietlanie calego html pliku stron
    			//budowanie address
    			while ((line = buffor.readLine()) != null) {
    			//	System.out.println(line);
    				tempBulider.append(line);
    			}
    			buffor.close();
    			in.close();
    			
    			Elements allFiles = GetAllLinks(tempBulider.toString());
    			for(Element e : allFiles) {
    				
    				
    				String ask11 = e.attr("href");
    				
    				//walidacja aby pokazywal tylko linki z plikami bez linkow folderow 
    				//biore ostatni znak href-a
    				Integer count =ask11.length() - 1;
    				
    				//Przygotowanie zmiennych do walidacji
    				String eee111=ask11.valueOf(ask11.charAt(count));
    				//System.out.println(eee111);
					String eee11=ask11.valueOf(ask11.charAt(0));
					String eeee11=ask11.valueOf(ask11.charAt(1));
    			//	System.out.println(eee11.toString()+"  "+eeee11.toString());
					
					//walidacja wyswietlanych linkow wykluczenie przypadkow nie chcianych
    				if(  !(eee11.equals("/"))  &&   !(eee11.equals("?"))   &&     !(eeee11.equals("~"))  &&     !(eee111.equals("/"))   ){
    				//		System.out.println(e.toString());
    	    				System.out.println("address= "+address+e.attr("href"));
    	    				
    				}
    	
    				
    				
    				
    				
    				
    				//Walidacja danych do wywolania w rekurencji funkcji cookie dla folderow
    				//System.out.println("e.attr= "+e.attr("href"));
    				String next_rekursion=e.attr("href");
    				//System.out.println(e.attributes());
    				
    				//Foldery koncza sie na  /
    				//System.out.println(next_rekursion.substring(next_rekursion.length() - 1));
    				
    				String next = next_rekursion.substring(next_rekursion.length() - 1).toString();
    				
    				//System.out.println(next);
    			
    				//walidacja wywolania odpowiedniej rekurencji obsluga wyjatkow
    				if(next.equals("/") ) {
    					WebPage wp = new WebPage();
    	    				String toTheNextElement = address+e.attr("href");
    	    			//	System.out.println(toTheNextElement.toString());
    	    				
    	    				
    	    				String next1 = toTheNextElement.substring(toTheNextElement.length() - 1).toString();
    	    				String next11 = toTheNextElement.substring(toTheNextElement.length() - 2).toString();
    	    				//System.out.println(next1 + next11);
    	    				String next111= next1+next11;
    	    				
    	    				
    	    				String ask1 = e.attr("href");
        					String eee1=ask1.valueOf(ask1.charAt(0));
        					String eeee1=ask1.valueOf(ask1.charAt(1));
        					
    	    				//walidacja niepoprawnego ciagu stringu aby wywolac poprawnie stworzone linki
    	    				a:{
    	    				if( next111.equals("///") &&  !(eee1.equals("/"))   ){
    	    					break a;
    	    				}else {
    	    					System.out.println("Link poprawnie zbudowany-wywolanie rekurencji");
    	    				//	System.out.println(toTheNextElement);
    	    					wp.Cookie(toTheNextElement);
    	    				}
    	    				}
    	    				
    	    		
    				}
    				
    			 
    			}
    		}
    		httpConn.disconnect();
    	}catch (MalformedURLException e) {
    		e.printStackTrace();
    	}catch (ProtocolException e) {
    		e.printStackTrace();
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	
    //	System.out.println(specifyBuilder);
    }

    public void DownoladFile(String urlFile, String saveFile) {
    	
    	HttpURLConnection httpConn;
    	try {
    		URL url = new URL(urlFile);
    		httpConn = (HttpURLConnection) url.openConnection();
    		if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			String fileName = "";
    			String disposition = httpConn.getHeaderField("Content-Dispostion");
    			System.out.println("Content"+ disposition);
    			String contentType = httpConn.getContentType();
    			System.out.println("Content-Type "+ contentType);
    			int contentLength = httpConn.getContentLength();
    			System.out.println("Content-Length " + contentLength);
    			
    			if(disposition != null) {
    				int index = disposition.indexOf("filename=");
    				if(index>0) {
    					fileName = disposition.substring(index + 10,disposition.length()-1);
    	
    				}
    			}else {
    				fileName = urlFile.substring(urlFile.lastIndexOf("/"),urlFile.length());
    				
    			}
    			String saveFilePath = saveFile + File.separator + fileName;
    			InputStream in = httpConn.getInputStream(); 
    			
    			FileOutputStream fileOut = new FileOutputStream(saveFilePath);
    			
    			int byetRead = -1;
    			byte[] buffor = new byte[2048];
    			while ((byetRead = in.read(buffor)) != -1) {
    				fileOut.write(buffor,0,byetRead);
    				
    			}
    			fileOut.close();
    			in.close();
    		
    		
    		
    		
    		
    		}else {
    			System.out.println("b³¹d" + httpConn.getResponseCode());
    			
    		}
    		
    	}catch (MalformedURLException e) {
    		e.printStackTrace();
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    
    
    
}
