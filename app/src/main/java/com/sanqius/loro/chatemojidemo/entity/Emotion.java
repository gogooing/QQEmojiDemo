package com.sanqius.loro.chatemojidemo.entity;


import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Emotion implements Serializable {

	private static final long serialVersionUID = -146445435454L;

	public String key;// id
	public String filename;// text

	public Emotion() {

	}

	public Emotion(JSONObject json) {
		try {
			key = json.getString("key");
			filename = json.getString("filename");

			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public Emotion(Element element) {
		try {
			key = element.getChild("key").getValue();
			filename = element.getChild("string").getValue();

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String GetFilePath() {
		return "Qface/png/"+filename+"@2x.png";
	}
	
	public String GetFileGif() {
		return "Qface/gif/"+filename+"@2x.gif";
	}
}
