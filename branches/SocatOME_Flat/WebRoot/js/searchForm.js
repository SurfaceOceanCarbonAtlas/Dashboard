// JavaScript Document
//the following javascript functions perform some basic validations
function checkSearchForm(){
  var start=0; var end=0; var s='';
  var responses=new Array; var w=new Array;
  var f=document.searchForm;
  if(isEmpty(f.term2.value)){ alert("Nothing to search for!"); return; }
  
  f.submit();
}
var whitespace = " \t\n\r";
function isEmpty(s){
  if((s == null) || (s.length == 0)) return true;
  var i;
  for(i=0;i<s.length;i++){
   var c = s.charAt(i);
   if(whitespace.indexOf(c) == -1) return false;
  }
  return true;
} 
function help(w){

  var win=window.open(pt_23102.makeAbsoluteURL(w),"Help_Window",
    "height=350,width=550,resizable,scrollbars"
  );
  win.window.focus();
}