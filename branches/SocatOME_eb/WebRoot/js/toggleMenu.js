// JavaScript Document

var aPrefix = 'pr';

function getCurrentState(mID) {
// returnvalues: 1 (closed), 2 (opened)
var state = document.cookie.substr(document.cookie.indexOf(mID+'=') + mID.length +1, 1);
return (state == 2 ? 2 : 1) ; return document.cookie.substr(document.cookie.indexOf(mID+'=') + mID.length +1, 1);
}

function initMenu(mID) {
// make the header clickable, assign an ID
var kids = document.getElementById(mID).childNodes;
for (var i = 0; i < kids.length; i++) {
if (kids[i].className == 'header') {
kids[i].onclick = toggleMenu;
kids[i].id = aPrefix + mID;
}
}
// show or hide the menu
hideMenu(mID);
}

function hideMenu(mID) {
var currentState = getCurrentState(mID);
document.getElementById(aPrefix+mID).title = (currentState == 1? 'show' : 'hide') + ' this menu';
document.getElementById(aPrefix+mID).style.cursor = 'pointer';
var kids = document.getElementById(mID).childNodes;
for (var i = 0; i < kids.length; i++) {
if (kids[i].tagName == 'LI' && kids[i].className!= 'header') {
kids[i].style.display = currentState == 1? 'none' : 'block';

}
}
}

function toggleMenu(e) {
if (window.event) e = window.event;
var mID = e.srcElement? e.srcElement.id : e.target.id;
mID = mID.substr(aPrefix.length);
// write the cookie
var oneyear = new Date(); oneyear.setFullYear(oneyear.getFullYear() + 1);
document.cookie = mID + '=' + (getCurrentState(mID) == 1? 2 : 1) + '; expires=' + oneyear.toGMTString() + '; path=/';
// show or hide the menu
hideMenu(mID);
}
// -->
