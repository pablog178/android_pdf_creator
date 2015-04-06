// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var pdfcreator = require('com.pablog178.pdfcreator.android');
var win = Ti.UI.createWindow({
	backgroundColor:'yellow'
});

var webView = Ti.UI.createWebView({
	width : 0,
	height : 0,
	scalesPageToFit : true,
	url : 'http://store.apple.com/us'
});

win.add(webView);
win.open();

pdfcreator.addEventListener('complete', completeEvent);
pdfcreator.addEventListener('error', errorEvent);
webView.addEventListener('load', generatePDFWithHTML);

function generatePDFWithHTML () {
	var fileName = 'myPDF.pdf';

	pdfcreator.generatePDF({
		html : webView.html,
		fileName : fileName
	});

}

function generateImage () {
	var fileName = 'myImage.png';

	pdfcreator.generateImage({
		view : webView,
		fileName : fileName
	});
}

function completeEvent(evt) {
	Ti.API.info("COMPLETED!");
	alert('PDf file created!');
}

function errorEvent (evt) {
	alert('An error ocurred!: ' + JSON.stringify(evt, null, ' '));
}