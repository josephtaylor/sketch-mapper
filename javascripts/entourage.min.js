/*!
 * Entourage 1.1.2 - Automatic Download Tracking for Asynchronous Google Analytics
 *
 * Copyright (c) 2011 by Tian Valdemar Davis (http://techoctave.com/c7)
 * Licensed under the MIT (http://en.wikipedia.org/wiki/MIT_License) license.
 *
 * Learn More: http://techoctave.com/c7/posts/58-entourage-js-automatic-download-tracking-for-asynchronous-google-analytics
 */
(function(){var c=new function(){var c=/\.pdf$|\.zip$|\.od*|\.doc*|\.xls*|\.ppt*|\.exe$|\.dmg$|\.mov$|\.avi$|\.mp3$/i,e=function(){var a=this.pathname,a=a.substring(0,-1===a.indexOf("#")?a.length:a.indexOf("#")),a=a.substring(0,-1===a.indexOf("?")?a.length:a.indexOf("?")),a=a.substring(a.lastIndexOf("/")+1,a.length);_gaq.push(["_trackPageview","/download/"+a])};return{version:"1.1.2",initialize:function(){for(var a=document.links,b=0,f=a.length;b<f;b++){var d=a[b].pathname.match(c);"undefined"!==
typeof d&&null!==d&&(a[b].onclick=e)}}}};window.entourage=c;window.onload=c.initialize})();