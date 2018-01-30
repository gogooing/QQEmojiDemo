package com.sanqius.loro.chatemojidemo.entity;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class EmotionList implements Serializable {

	private static final long serialVersionUID = 1946436545154L;

	public List<Emotion> mEmotionList;


	public EmotionList() {
	}

	public EmotionList(String content) {
		try{ 
			
			//创建一个新的字符串  
	        StringReader read = new StringReader(content);
	        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入  
	        InputSource source = new InputSource(read);
	        //创建一个新的SAXBuilder  
	        SAXBuilder saxbBuilder = new SAXBuilder();
	        Element root;
	        try {  
	           //通过输入源构造一个Document  
	           Document doc = saxbBuilder.build(source);
	           //取的根元素  
	           root = doc.getRootElement();  
	           updateList(root);
	        } catch (JDOMException e) {
	           e.printStackTrace();  
	        } catch (IOException e) {
	           e.printStackTrace();  
	        }  catch (Exception e) {
	           e.printStackTrace();
	        }
	       
		}catch(Exception e){
			e.printStackTrace(); 
		}
		
//		try {
//			if (content == null || content.equals("")) {
//				return;
//			}
//			JSONArray array = new JSONArray(content);
//			if (array != null) {
//				mEmotionList = new ArrayList<EmotionDyna>();
//				for (int i = 0; i < array.length(); i++) {
//					mEmotionList.add(new EmotionDyna(array.getJSONObject(i)));
//
//				}
//			}
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

	}
	public void updateList(Element element) throws IllegalAccessException {
		mEmotionList = new ArrayList<Emotion>();
		try{ 
			List<?> array = element.getChild("array").getChildren();
			for(int i=0;i<array.size();i++){
				Element subElement=(Element)array.get(i);
				mEmotionList.add(new Emotion(subElement));
			}
	       
		}catch(Exception e){
			e.printStackTrace(); 
		}
		
	}
	public Emotion GetEmotion(String key){
		if(mEmotionList !=null){
			int mlen = mEmotionList.size();
			for (int i = 0; i < mlen; i++) {
				Emotion tmp = mEmotionList.get(i);
				if(tmp.key.equals(key))
					return tmp;
			}
		}
		
		return null;
	}
	
	public String GetFileName(String key){
		if(mEmotionList !=null){
			int mlen = mEmotionList.size();
			for (int i = 0; i < mlen; i++) {
				Emotion tmp = mEmotionList.get(i);
				if(tmp.key.equals(key))
					return tmp.filename;
			}
		}
		
		return "";
	}
	public String GetKey(String filename){
		if(mEmotionList !=null){
			int mlen = mEmotionList.size();
			for (int i = 0; i < mlen; i++) {
				Emotion tmp = mEmotionList.get(i);
				if(tmp.filename.equals(filename))
					return tmp.key;
			}
		}
		
		return "";
	}
	
	public int GetPageCount(int pageSize){
		if(mEmotionList !=null){
			int count = mEmotionList.size();
			return count % pageSize == 0 ? count / pageSize
					: count /pageSize+ 1;
		}
		
		return 0;
	}
	
	public List<Emotion> GetPage(int pageCount, int page){
		if(mEmotionList !=null){
			int count = mEmotionList.size();
			List<Emotion> pageInfo = new ArrayList<Emotion>();
			pageInfo.addAll(mEmotionList.subList(page * pageCount,
					pageCount * (page + 1) > count? count :pageCount
							* (page + 1)));
			return pageInfo;
		}
		
		return new ArrayList<Emotion>();
	}
	
}
