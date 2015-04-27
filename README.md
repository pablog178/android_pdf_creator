# Android PDF Creator
Titanium Module for generating PDF files based on HTML or a WebView content

## Before you start
* This module has been only tested with Titanium SDK 3.4.x and up
* All the PDF work is done by iText libraries: http://itextpdf.com
  * iTextG, which is a port for Android
  * XMLWorker, which is intended for parse HTML files into PDF
  * Please refer to the XMLWorker Documentation to make sure your HTML file uses all the supported tags and CSS properties: http://demo.itextsupport.com/xmlworker/itextdoc/index.html
  * iTextG is licensed under the GNU Affero General Public License version 3 and depending on the nature of your app you might need a license. Please visit [license](http://itextpdf.com/pricing/android_license) for details.
  
## Obtaining the module
* Download the zip file from the [dist](https://github.com/pablog178/android_pdf_creator/tree/master/dist) folder

## Basic Usage
1. Make sure you have the module dependency added on `tiapp.xml`
      
      `<module platform="android">com.pablog178.pdfcreator.android</module>`
      
2. Require the module where needed
3. The usage of the module is based on the events: `success` and `error`
4. The actual PDF creation can be achieved by the methods `generatePDFWithHTML()` or `generatePDFwithWebView()`
5. Once created, the success event will fire.

## Example
		var pdfCreator = require('com.pablog178.pdfcreator.android');
			
		pdfCreator.addEventListener("complete", function (_evt) {
			//Handle the PDF created
			var pdfFile = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, _evt.filename);
			//...
		});
	
		pdfCreator.addEventListener("error", function(_evt){
			//An error has ocurred
		});
		
		// Generate a PDF based on HTML
		pdfCreator.generatePDFWithHTML({
			html : '<html><body><h1>Hello World!</h1></body></html>',
			filename : 'hello.pdf'
		});
		
		//Generate a PDF based on a webview
		var webview = Ti.UI.createWebView({
			scalesPageToFit : true
		});
		
		webview.addEventListener('load', function (e) {
			pdfCreator.generatePDFwithWebView({
				filename : 'hello.pdf',
				webview : webview,
				quality : 100
			});
		});
		
		webview.url = 'www.apple.com';
