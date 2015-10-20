/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.pablog178.pdfcreator.android;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import org.w3c.tidy.Tidy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.FontFactoryImp;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.html.Tags;

@Kroll.module(name="Pdfcreator", id="com.pablog178.pdfcreator.android")
public class PdfcreatorModule extends KrollModule
{

	// Standard Debugging variables
	private static final String MODULE_NAME = "PdfcreatorModule";
	private static final String PROXY_NAME = "PDF_PROXY";

	// Private members
	private static TiApplication app;
	
	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;
	public PdfcreatorModule(){
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication myApp)
	{
		Log.d(MODULE_NAME, "inside onAppCreate");
		app = myApp;
		// put module init code that needs to run when the application is created
	}

	// Methods

	/**
	 * @method generatePDFWithHTML
	 * Generates a PDF with the given file name, based on a HTML file
	 * @param {String} filename Name of the PDF file
	 * @param {String} html String with the HTML
	 * @param {String} [author] Author for metadata
	 * Fires a "complete" event when the PDF is generated
	 * Fires a "error" event when a error is presented
	 */
	@Kroll.method(runOnUiThread=true)
	public void generatePDFWithHTML (final HashMap args) {
		Log.d(PROXY_NAME, "generatePDFWithHTML()");

		String html = "";
		String author = "";
		String filename = "";
		Boolean landscape = Boolean.FALSE;
		Boolean letter = Boolean.FALSE;
		final float marginPt = 28.35f;//1cm == 28.35pt

		try {
			if(args.containsKey("filename")){
				filename = (String) args.get("filename");
				Log.d(PROXY_NAME, "filename: " + filename);
			} else return;

			if (args.containsKey("html")) {
				html = (String) args.get("html");
				Log.d(PROXY_NAME, "html: " + html);
			} else return;

			if(args.containsKey("author")){
				author = (String) args.get("author");
				Log.d(PROXY_NAME, "author: " + author);
			}

			if (args.containsKey("landscape")){
				landscape = (Boolean) args.get("landscape");
				Log.d(PROXY_NAME, "lanscape: " + landscape);
			}

			if (args.containsKey("letter")){
				letter = (Boolean) args.get("letter");
				Log.d(PROXY_NAME, "letter: " + letter);
			}

			Rectangle size = PageSize.A4;
			if (letter){
				size = PageSize.LETTER;
			}
			if (landscape){
				size = size.rotate();
			}
			
			//create a new document
			Document document = new Document(size, marginPt, marginPt, marginPt, 0);
			TiBaseFile file = TiFileFactory.createTitaniumFile(filename, true);
			
			// Parse to XHTML
			StringWriter xhtmlWriter = new StringWriter();
			Tidy tidy = new Tidy();
			
			// tidy.setXHTML(true);
			tidy.setXmlOut(true);
			tidy.parse(new StringReader(html), xhtmlWriter);
			String xhtml = xhtmlWriter.toString();


			//get Instance of the PDFWriter
			PdfWriter pdfWriter = PdfWriter.getInstance(document, file.getOutputStream());
			
			//document header attributes
			document.addAuthor(author);
			document.addCreationDate();
			document.setPageSize(size);
			
			//open document
			document.open();

			// From Stack Overflow lol
			
			MyFontFactory fontFactory = new MyFontFactory();
			FontFactory.setFontImp(fontFactory);

			// HtmlPipelineContext htmlContext = new HtmlPipelineContext(new CssAppliersImpl(fontFactory));
			HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
			htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
			CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
			Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext, new PdfWriterPipeline(document, pdfWriter)));
			XMLWorker worker = new XMLWorker(pipeline, true);
			XMLParser p = new XMLParser(worker);
			p.parse(new StringReader(xhtml));
			
			// Finish SO c&P

			
			// Older code
			/*
			// Font Provider creation
			MyFontFactory fontProvider = new MyFontFactory();
			// fontProvider.register("/DroidSans.ttf");

			//get the XMLWorkerHelper Instance
			XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
			//convert to PDF
			worker.parseXHtml(pdfWriter, document, new ByteArrayInputStream(xhtml.getBytes("UTF-8")), null, fontProvider); //Load xhtml
			*/
			
			//close the document
			document.close();
			//close the writer
			pdfWriter.close();

			sendCompleteEvent(filename);

		}  catch (Exception e) {
			sendErrorEvent(e);
		}
	}

	/**
	 * @method generatePDFwithWebView
	 * Generates a PDF with the given webview, obtaining all of its content as an image
	 * @param {String} filename Name of the PDF file
	 * @param {TiUIView} webview Web View to obtain the html from
	 * @param {Integer} [quality] Quality of the image to generate, from 0 to 100
	 * Fires a "complete" event when the PDF is generated
	 * Fires a "error" event when a error is presented
	 */
	@Kroll.method(runOnUiThread=true)
	public void generatePDFwithWebView(final HashMap args){
		Log.d(PROXY_NAME, "generatePDF()");

		if(TiApplication.isUIThread()){
			generateiTextPDFfunction(args);
		} else {
			app.getCurrentActivity().runOnUiThread(new Runnable(){
				@Override
				public void run(){
					generateiTextPDFfunction(args);
				}
			});
		}
	}
	
	//Private functions
	
	private void generateiTextPDFfunction(HashMap args){
		Log.d(PROXY_NAME, "generateiTextPDFfunction()");
		
		String filename = "";
		TiUIView webview = null;
		int quality = 100;
		Boolean landscape = Boolean.FALSE;
		Boolean letter = Boolean.FALSE;

		try {
			if(args.containsKey("filename")){
				filename = (String) args.get("filename");
				Log.d(PROXY_NAME, "filename: " + filename);
			} else return;

			if(args.containsKey("webview")){
				TiViewProxy viewProxy = (TiViewProxy) args.get("webview");
				webview = viewProxy.getOrCreateView();
				Log.d(PROXY_NAME, "webview: " + webview.toString());
			} else return;

			if(args.containsKey("quality")){
				quality = TiConvert.toInt(args.get("quality"));
			}

			if (args.containsKey("landscape")){
				landscape = (Boolean) args.get("landscape");
				Log.d(PROXY_NAME, "lanscape: " + landscape);
			}

			if (args.containsKey("letter")){
				letter = (Boolean) args.get("letter");
				Log.d(PROXY_NAME, "letter: " + letter);
			}

			TiBaseFile file = TiFileFactory.createTitaniumFile(filename, true);
			Log.d(PROXY_NAME, "file full path: " + file.nativePath());

			Rectangle size = PageSize.A4;
			if (letter){
				size = PageSize.LETTER;
			}
			if (landscape){
				size = size.rotate();
			}

			OutputStream outputStream = file.getOutputStream();
			final int MARGIN = 0;
			final float PDF_WIDTH = size.getWidth() - MARGIN * 2; // A4: 595 //Letter: 612
			final float PDF_HEIGHT = size.getHeight() - MARGIN * 2; // A4: 842 //Letter: 792
			final int DEFAULT_VIEW_WIDTH = 980;
			final int DEFAULT_VIEW_HEIGHT = 1384;
			int viewWidth = DEFAULT_VIEW_WIDTH;
			int viewHeight = DEFAULT_VIEW_HEIGHT;
			
			Document pdfDocument = new Document(size, MARGIN, MARGIN, MARGIN, MARGIN);
			PdfWriter docWriter = PdfWriter.getInstance(pdfDocument, outputStream);

			Log.d(PROXY_NAME, "PDF_WIDTH: " + PDF_WIDTH);
			Log.d(PROXY_NAME, "PDF_HEIGHT: " + PDF_HEIGHT);


			WebView view = (WebView) webview.getNativeView();

			if (TiApplication.isUIThread()) {

				viewWidth = view.capturePicture().getWidth();
				viewHeight = view.capturePicture().getHeight();


				if(viewWidth <= 0){
					viewWidth = DEFAULT_VIEW_WIDTH;
				}

				if(viewHeight <= 0){
					viewHeight = DEFAULT_VIEW_HEIGHT;
				}


			} else {
				Log.e(PROXY_NAME, "NO UI THREAD");
				viewWidth = DEFAULT_VIEW_WIDTH;
				viewHeight = DEFAULT_VIEW_HEIGHT;
			}

			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			Log.d(PROXY_NAME, "viewWidth: " + viewWidth);
			Log.d(PROXY_NAME, "viewHeight: " + viewHeight);

			float scaleFactorWidth 	= 1 / (viewWidth  / PDF_WIDTH);
			float scaleFactorHeight = 1 / (viewHeight / PDF_HEIGHT);

			Log.d(PROXY_NAME, "scaleFactorWidth: " + scaleFactorWidth);
			Log.d(PROXY_NAME, "scaleFactorHeight: " + scaleFactorHeight);


			Bitmap viewBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
			Canvas viewCanvas = new Canvas(viewBitmap);

			// Paint paintAntialias = new Paint();
			// paintAntialias.setAntiAlias(true);
			// paintAntialias.setFilterBitmap(true);

			Drawable bgDrawable = view.getBackground();
			if (bgDrawable != null){
				bgDrawable.draw(viewCanvas);
			} else {
				viewCanvas.drawColor(Color.WHITE);
			}
			view.draw(viewCanvas);

			TiBaseFile pdfImg = createTempFile(filename);

			viewBitmap.compress(Bitmap.CompressFormat.PNG, quality, pdfImg.getOutputStream());
			
			FileInputStream pdfImgInputStream = new FileInputStream(pdfImg.getNativeFile());
			byte[] pdfImgBytes = IOUtils.toByteArray(pdfImgInputStream);
			pdfImgInputStream.close();

			pdfDocument.open();
			float yFactor = viewHeight * scaleFactorWidth;
			int pageNumber = 1;
			
			do{
				if(pageNumber > 1){
					pdfDocument.newPage();
				}
				pageNumber++;
				yFactor -= PDF_HEIGHT;
				
				Image pageImage = Image.getInstance(pdfImgBytes, true);
				// Image pageImage = Image.getInstance(buffer.array());
				pageImage.scalePercent(scaleFactorWidth * 100);
				pageImage.setAbsolutePosition(0f, -yFactor);
				pdfDocument.add(pageImage); 

				Log.d(PROXY_NAME, "yFactor: " + yFactor);
			}while(yFactor > 0);

			pdfDocument.close();

			sendCompleteEvent(filename);

		} catch (Exception exception){
			sendErrorEvent(exception);
		}
	}

	// method to invoke success callback
	private void sendCompleteEvent(String filename) {
		if (this.hasListeners("complete")) {
			KrollDict props = new KrollDict();
			props.put("filename", filename);
			this.fireEvent("complete", props);
		}
	}

	// method to invoke error callback
	private void sendErrorEvent(Exception error) {
		Log.e(PROXY_NAME, "Error", error);

		if (this.hasListeners("error")) {
			KrollDict props = new KrollDict();
			props.put("message", error.toString());
			this.fireEvent("error", props);
		}
	}

	private TiBaseFile createTempFile(String filename){
		TiBaseFile file = TiFileFactory.createTitaniumFile(filename, true);
		file.getNativeFile().deleteOnExit();

		return file;
	}

	private class MyFontFactory extends FontFactoryImp {
		@Override
		public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color, boolean cached) {
			Log.i(PROXY_NAME, "=fontname: " + fontname + " =encoding: " + encoding + " =embedded : " + embedded + " =size: " + size + " =style: " + style + " =BaseColor: " + color);
			return super.getFont("/system/fonts/Roboto.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, style, color, cached);
		}
	}
}

