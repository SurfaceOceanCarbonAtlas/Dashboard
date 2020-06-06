/**
 * clickableImage.js
 *
 * This 'unobtrusive' javascript component adds 'clickable' behavior
 * to all image tags that specifry the 'clickable' class:
 *
 *  <img class="clickableimage" src="plot_image.gif">
 *
 * To use this class a calling page must include the component:
 * 
 *  <script src="clickableImage.js" type="text/javascript"></script>
 *
 * and must then implement a top level function:
 *
 *  function clickableImage_callback(xPos,yPos) {
 *    ...
 *  }
 *
 * This code is based on the scrollImage.js example given in 
 * "DHTML Utopia: Modern Web Design" by Stuart Landridge.
 */
 
/*
 * findPos* return the positioning of an HTML element relative to
 * the browser window.
 */
// Based on findPos*, by ppk (http://www.quirksmode.org/js/findpos.html)
function findPosX(obj) {
  var curLeft = 0;
  if (obj.offsetParent) {
    do {
      curLeft += obj.offsetLeft;
    } while (obj = obj.offsetParent);
  }
  else if (obj.x) {
    curLeft += obj.x;
  }
  return curLeft;
}

function findPosY(obj) {
  var curTop = 0;
  if (obj.offsetParent) {
    do {
      curTop += obj.offsetTop;
    } while (obj = obj.offsetParent);
  }
  else if (obj.y) {
    curTop += obj.y;
  }
  return curTop;
}

// cross-browser event handling for IE5+, NS6+ and Mozilla/Gecko
// By Scott Andrew
function addEvent(obj, evType, fn, useCapture) {
  if (obj.addEventListener) {
    obj.addEventListener(evType, fn, useCapture);
    return true;
  } else if (obj.attachEvent) {
    var r = obj.attachEvent('on' + evType, fn);
    return r;
  } else {
    obj['on' + evType] = fn;
  }
}

var isIE = !window.opera && navigator.userAgent.indexOf('MSIE') != -1;

addEvent(window, 'load', scrollInit, false);

/*
 * onload initialization that attaches the clickListener() function
 * to all images specifying the class 'clickableimage'.
 */
function scrollInit() {
  if (!document.getElementsByTagName)
    return;
  var allLinks = document.getElementsByTagName('img');
  for (var i = 0; i < allLinks.length; i++) {
    var link = allLinks[i];
    if ((' ' + link.className + ' ').indexOf(' clickableimage ') != -1) {
      addEvent(link, 'click', clickListener, false);
    }
  }
}

/*
 * Event listener for mouseclicks.
 */
function clickListener(ev) {
  var e = window.event ? window.event : ev;
  var t = e.target ? e.target : e.srcElement;

  var mX, mY;
  if (e.pageX && e.pageY) {
    mX = e.pageX;
    mY = e.pageY;
  } else if (e.clientX && e.clientY) {
    mX = e.clientX;
    mY = e.clientY;
    if (isIE) {
      mX += document.body.scrollLeft;
      mY += document.body.scrollTop;
    }
  }

  // Take the absolute X and Y positions of the mouseclick and subtract the
  // X and Y positions of the element in which the click ocurred.
  //
  // This leaves us with the X and Y positions of the mouseclick relative
  // to the upper left hand corner of the image.
  var xPos = mX - findPosX(t);
  var yPos = mY - findPosY(t);

  if (t.nodeName.toLowerCase() == 'img') {
    if (clickableImage_callback) {
      clickableImage_callback(xPos,yPos);
    } else {
      alert('xPos = ' + xPos + ', yPos = ' + yPos + '.');
    }
  }
}

