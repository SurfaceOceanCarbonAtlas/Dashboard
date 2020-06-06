/**
 * @private
 * @fileoverview 
 * The DateWidget object manages a collection of Year-Month-Day-Time
 * Selectors for user selection of a single Date-Time or a pair of start and 
 * end Date-Times.
 * <p>
 * The DateWidget object is initialized with a valid range (DateLo <--> DateHi)
 * and allows the user to select dates Date1 and Date2 within this range
 * (DateLo <= Date1 <= Date2 <= DateHi).
 * <p>
 * It also handles the number of days in each month as well as leap years  
 * so that the user can never select an invalid date (<i>e.g.</i> "Sept 31").
 * <p>
 * Climatologies are handled by excluding 'Y' from the menu_sets passed to the
 * render() method.
 * <p>
 * The Date1 and Date2 Year-Month-Day-Time selectors each show all
 * possible dates in the valid range.  Setting Date1 to a value
 * higher than the current Date2 causes Date2 to reset to the
 * same value as Date1, satisfying (Date1 <= Date2).
 * <p>
 * <b>Note:</b>  Safari's implementation of the javascript Date object has 
 * a greatly restricted range:  1901-12-13 to 2038-01-19.  The DateWidget
 * will not work properly on Safari with dates outside that range.  To see
 * the range of valid dates for your browser, please point it to:
 *
 * http://www.merlyn.demon.co.uk/js-datex.htm#DRF
 *
 * @author Jonathan Callahan
 * @version $Revision: 1137 $
 * jli: Fix a bug in selectDay(DayMenu) that fails to set hh:mm  (2008-4-9) 
 */

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Define the DateWidget object                                               //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * Constructs a new DateWidget object.
 * @constructor
 * <p>
 * The 'lo' and 'hi' parameters must be of the form "YYYY-MM-DD [HH:mm:SS]'
 * or the string 'TODAY' or 'NOW' (see the parseDate() method).
 * <p>
 * The 'deltaMinutes' and 'offsetMinutes' parameters can be used to 
 * support, for example, 6 hourly forecats that appear 15 minutes after 
 * the hour.
 * <p>
 * @param {string} lo end of the domain of valid times of the form 'YYYY-MM-DD [HH:mm:SS]'
 * @param {string} hi end of the domain of valid times of the form 'YYYY-MM-DD [HH:mm:SS]'
 * @param {int} deltaMintues interval in the time selector 
 * @param {int} offsetMinutes offset from 00:00:00 in the time selector
 * @param {int} add_sub_1 minutes by which to adjust Date1
 * @param {int} add_sub_2 minutes by which to adjust Date2
 * @return new DateWidget object
 */
function DateWidget(lo,hi,deltaMinutes,offsetMinutes,add_sub_1,add_sub_2) {

  // Public methods that make up the the ImageSlideSorter Widget API

  this.setCallback = DateWidget_setCallback;
  this.render = DateWidget_render;
  this.renderToNode = DateWidget_renderToNode;
  this.disable = DateWidget_disable;
  this.enable = DateWidget_enable;
  this.show = DateWidget_show;
  this.hide = DateWidget_hide;
  this.getValue = DateWidget_getValue;
  this.setValue = DateWidget_setValue;
/*
 * TODO:  Implelement this one as part of the LAS 'Widget Interface'
  this.setValueByIndex = DateWidget_setValueByIndex;
 */

  // Public methods

  this.initializeYearMenu = DateWidget_initializeYearMenu;

  this.setDateRange = DateWidget_setDateRange;
  this.setDate1 = DateWidget_setDate1;
  this.setDate2 = DateWidget_setDate2;
/*
  this.nextDate1 = DateWidget_nextDate1;
  this.nextDate2 = DateWidget_nextDate2;
*/
  this.resetDateRange = DateWidget_resetDateRange;
  this.resetDate1 = DateWidget_resetDate1;
  this.resetDate2 = DateWidget_resetDate2;
  this.toString = DateWidget_toString;
  this.getDateLo = DateWidget_getDateLo;
  this.getDateHi = DateWidget_getDateHi;
  this.getDate1 = DateWidget_getDate1;
  this.getDate2 = DateWidget_getDate2;
  this.alert = DateWidget_alert;
  this.setDeltaMinutes = DateWidget_setDeltaMinutes;
  this.setOffsetMinutes = DateWidget_setOffsetMinutes;

  // LAS/Ferret specific methods

  this.getDate1_Ferret = DateWidget_getDate1_Ferret;
  this.getDate2_Ferret = DateWidget_getDate2_Ferret;

  // Private methods

  this.parseDate = DateWidget_parseDate;
  this.febDays = DateWidget_febDays;
  this.twoDigit = DateWidget_twoDigit;
  this.fourDigit = DateWidget_fourDigit;
  this.correctOrder = DateWidget_correctOrder;
  this.addEvent = DateWidget_addEvent;
  this.flash = DateWidget_flash;

  // Private methods that are called by the event handlers

  this.selectYear = DateWidget_selectYear;
  this.selectMonth = DateWidget_selectMonth;
  this.selectDay = DateWidget_selectDay;
  this.selectTime = DateWidget_selectTime;

  // Event handlers      

/*
 * See optoinClick method below
  this.optionClick = DateWidget_optionClick;
 */
  this.selectChange = DateWidget_selectChange;

  // Initialization

/**
 * The SlideSorter requires that each widget must have a type attribute identifying it.
 */
  this.widgetType = 'DateWidget';
/**
 * @ignore
 */
  this.climatology = 0;
/**
 * @ignore
 */
  this.disabled = 0;
/**
 * @ignore
 */
  this.visible = 0;

/**
 * The number of minutes between options in the Time selector.
 * <p>
 * For example, setting deltaMinutes to 60 will result in the
 * Time selector presenting an option for every hour, on the hour.
 * <p>
 * The default setting of 1440 (= 24 hours) shows only a single value of
 * "00:00:00".
 */
  this.deltaMinutes = (deltaMinutes) ? deltaMinutes : 1440;

/**
 * The number of minutes by which to offset the Time selector from "00:00:00".
 * <p>
 * For example, setting deltaMinutes to 360 and offsetMinutes to 30
 * will cause the Time selector to show the following values:
 * <ul>
 * <li>"00:30:00"</li>
 * <li>"06:30:00"</li>
 * <li>"12:30:00"</li>
 * <li>"18:30:00"</li>
 * </ul>
 * <p>
 * This may be useful for forecast data that is available at offsetMinutes
 * after some regular interval.
 */
  this.offsetMinutes = (offsetMinutes) ? offsetMinutes : 0;

  // Attempt to parse the incoming date strings

  var date1, date2, error_msg;
  try {
    date1 = this.parseDate(lo,add_sub_1);
  } catch(error) {
    error_msg = "DateWidget ERROR: new:  error parsing \"" + lo + "\"\n" + error;
    alert(error_msg);
  }
  try {
    date2 = this.parseDate(hi,add_sub_2);
  } catch(error) {
    error_msg = "DateWidget ERROR: new:  error parsing \"" + hi + "\"\n" + error;
    alert(error_msg);
  }

  // Set the time domain

/**
 * The Year associated with the earliest valid date.
 */
  this.yearLo = date1[0];
/**
 * The Month associated with the earliest valid date.
 */
  this.monthLo = date1[1];
/**
 * The Day associated with the earliest valid date.
 */
  this.dayLo = date1[2];
/**
 * The Hour associated with the earliest valid date.
 */
  this.hourLo = date1[3];
/**
 * The Minute associated with the earliest valid date.
 */
  this.minuteLo = date1[4];
/**
 * The Second associated with the earliest valid date.
 */
  this.secondLo = date1[5];
/**
 * The Year associated with the latest valid date.
 */
  this.yearHi = date2[0];
/**
 * The Month associated with the latest valid date.
 */
  this.monthHi = date2[1];
/**
 * The Day associated with the latest valid date.
 */
  this.dayHi = date2[2];
/**
 * The Hour associated with the latest valid date.
 */
  this.hourHi = date2[3];
/**
 * The Minute associated with the latest valid date.
 */
  this.minuteHi = date2[4];
/**
 * The Second associated with the latest valid date.
 */
  this.secondHi = date2[5];

  // On initialization, set the time range to the full domain

/**
 * The currently selected low Year
 */
  this.year1 = this.yearLo;
/**
 * The currently selected low Month
 */
  this.month1 = this.monthLo;
/**
 * The currently selected low Day
 */
  this.day1 = this.dayLo;
/**
 * The currently selected low Hour
 */
  this.hour1 = this.hourLo;
/**
 * The currently selected low Minute
 */
  this.minute1 = this.minuteLo;
/**
 * The currently selected low Second
 */
  this.second1 = this.secondLo;
/**
 * The currently selected high Year
 */
  this.year2 = this.yearHi;
/**
 * The currently selected high Month
 */
  this.month2 = this.monthHi;
/**
 * The currently selected high Day
 */
  this.day2 = this.dayHi;
/**
 * The currently selected high Hour
 */
  this.hour2 = this.hourHi;
/**
 * The currently selected high Minute
 */
  this.minute2 = this.minuteHi;
/**
 * The currently selected high Second
 */
  this.second2 = this.secondHi;

}

/**
 * Creates the table of menus associated with the DateWidget.
 * <p>
 * Any children of element_id will be removed and replaced with a set of menus from the
 * following collection:  [Y]ear, [M]onth, [D]ay, [T]ime.  The order in which these are
 * specified determiens the order in which they appear in the interface.
 * <p>
 * If menu_set_2 is omitted, only a single set of menus will appear.
 * @param {string} element_id 'id' attribute of the element into which the menus are inserted.
 * @param {string} menu_set_1 one or more of the characters 'YMDT', in any order, specifying
 *                 which menus will be shown for Date1
 * @param {string} menu_set_1 one or more of the characters 'YMDT', in any order, specifying
 *                 which menus will be shown for Date2
 */
function DateWidget_renderToNode(node,menu_set_1,menu_set_2) {
  this.node = node;
  this.menu_set_1 = menu_set_1;
  this.menu_set_2 = menu_set_2;
  if(node.childNodes)
        var children = node.childNodes;
  else
        var children = [];

  var num_children = children.length;

  // Remove any children of this widget
  // NOTE:  Start removing children from the end.  Otherwise, what was
  // NOTE:  children[1]  becomes children[0] when children[0] is removed.
  for (var i=num_children-1; i>=0; i--) {
    var child = children[i];
    if (child) {
      node.removeChild(child);
    }
  }

  // Create up to 8 Select objects
  var Text1, Year1, Month1, Day1, Time1;
  var Text2, Year2, Month2, Day2, Time2;
  var climatology_1 = 0;
  var climatology_2 = 0;

  // Test the 'menu_set_1/2' strings for required widgets
  // Create required Select objects with the appropriate 'id' 
  // Give each Select object a reference to the DateWidget
  // Give the DateWidget a reference to each Select object

  if (!menu_set_1) {
    error_msg = "DateWidget ERROR:  render: menu_set_1 must be defined and  must contain only characters from \"YMDT\"";
    throw(error_msg);
  }

  // TODO:  Create the 'id' attribute from this.element_id + "_Year1" etc. 
  // TODO:  instead of just "DW_Year1" etc.

  // The Year, Month, Day and Time widgets are always created so that initialization
  // along the path: "Year -> Month -> Day" can procede as usual.  The formatting
  // specified inthe menu_set only determines whether they are visible or not.
  Year1 = document.createElement('select');
  Year1.setAttribute('id','DW_Year1'); 
  Year1.widget = this;
  this.Year1 = Year1;
  if (menu_set_1.indexOf('Y') < 0) {
    this.Year1.style.display = 'none';
    climatology_1 = 1;
    this.climatology = 1;

    text1 = document.createTextNode('Climatology:  ');
    Text1 = document.createElement('span');
    Text1.setAttribute('id','DW_Text1'); 
    Text1.appendChild(text1);
    Text1.widget = this;
    this.Text1 = Text1;
  }

  Month1 = document.createElement('select');
  Month1.setAttribute('id','DW_Month1'); 
  Month1.widget = this;
  this.Month1 = Month1;
  if (menu_set_1.indexOf('M') < 0) {
    this.Month1.style.display = 'none';
  }

  Day1 = document.createElement('select');
  Day1.setAttribute('id','DW_Day1'); 
  Day1.widget = this;
  this.Day1 = Day1;
  if (menu_set_1.indexOf('D') < 0) {
    this.Day1.style.display = 'none';
  }

  Time1 = document.createElement('select');
  Time1.setAttribute('id','DW_Time1'); 
  Time1.widget = this;
  this.Time1 = Time1;
  if (menu_set_1.indexOf('T') < 0) {
    this.Time1.style.display = 'none';
  }

  if (menu_set_2) {
    Year2 = document.createElement('select');
    Year2.setAttribute('id','DW_Year2'); 
    Year2.widget = this;
    this.Year2 = Year2;
    if (menu_set_2.indexOf('Y') < 0) {
      this.Year2.style.display = 'none';
      climatology_2 = 1;

      text2 = document.createTextNode('Climatology:  ');
      Text2 = document.createElement('span');
      Text2.setAttribute('id','DW_Text2'); 
      Text2.appendChild(text2);
      Text2.widget = this;
      this.Text2 = Text2;
    }

    Month2 = document.createElement('select');
    Month2.setAttribute('id','DW_Month2'); 
    Month2.widget = this;
    this.Month2 = Month2;
    if (menu_set_2.indexOf('M') < 0) {
      this.Month2.style.display = 'none';
    }

    Day2 = document.createElement('select');
    Day2.setAttribute('id','DW_Day2'); 
    Day2.widget = this;
    this.Day2 = Day2;
    if (menu_set_2.indexOf('D') < 0) {
      this.Day2.style.display = 'none';
    }

    Time2 = document.createElement('select');
    Time2.setAttribute('id','DW_Time2'); 
    Time2.widget = this;
    this.Time2 = Time2;
    if (menu_set_2.indexOf('T') < 0) {
      this.Time2.style.display = 'none';
    }

    if (climatology_1 != climatology_2) {
      error_msg = "DateWidget ERROR:  render: menu_set_1 \"" + menu_set_1 + "\" and menu_set_2 \"" + menu_set_2 +
                  "\" must both include or both exclude the \"Y\" flag in the menu_set" ;
      throw(error_msg);
    }
  }

  // Create the table that holds the menus

  var DW_table, DW_tr1, DW_td1, DW_tr2, DW_td2;

  DW_table = document.createElement('table');
  DW_table.setAttribute('id', 'DW_table');
  DW_tbody = document.createElement('tbody');
  DW_tbody.setAttribute('id', 'DW_tbody');
  DW_tr1 = document.createElement('tr');
  DW_tr1.setAttribute('id', 'DW_tr1');
  DW_td1 = document.createElement('td');
  DW_td1.setAttribute('id', 'DW_td1');
  DW_table.appendChild(DW_tbody);
  DW_tbody.appendChild(DW_tr1);
  DW_tr1.appendChild(DW_td1);

  // Create the first DateWidget
  // Add the hidden menus first, then order the visible ones as per formatting instructions
  if (Year1.style.display == 'none') {
    DW_td1.appendChild(Year1);
    DW_td1.appendChild(Text1);
  }
  if (Month1.style.display == 'none') {
    DW_td1.appendChild(Month1);
  }
  if (Day1.style.display == 'none') {
    DW_td1.appendChild(Day1);
  }
  if (Time1.style.display == 'none') {
    DW_td1.appendChild(Time1);
  }
  for (i=0; i<menu_set_1.length; i++) {
    switch (menu_set_1.charAt(i)) {
      case 'Y':
        DW_td1.appendChild(Year1);
        break;
      case 'M':
        DW_td1.appendChild(Month1);
        break;
      case 'D':
        DW_td1.appendChild(Day1);
        break;
      case 'T':
        DW_td1.appendChild(Time1);
        break;
      default:
        error_msg = "DateWidget ERROR:  render: menu_set \"" + menu_set_1 + "\" must contain only characters from \"YMDT\"";
        throw(error_msg);
        break;
    }
  }

  if (menu_set_2) {
    // Add a second row to the table
    DW_tr2 = document.createElement('tr');
    DW_tr2.setAttribute('id', 'DW_tr2');
    DW_td2 = document.createElement('td');
    DW_td2.setAttribute('id', 'DW_td2');
    DW_tbody.appendChild(DW_tr2);
    DW_tr2.appendChild(DW_td2);

    // Add the hidden menus first, then order the visible ones as per formatting instructions
    if (Year2.style.display == 'none') {
      DW_td2.appendChild(Year2);
      DW_td2.appendChild(Text2);
    }
    if (Month2.style.display == 'none') {
      DW_td2.appendChild(Month2);
    }
    if (Day2.style.display == 'none') {
      DW_td2.appendChild(Day2);
    }
    if (Time2.style.display == 'none') {
      DW_td2.appendChild(Time2);
    }
    // Create the second DateWidget
    for (i=0; i<menu_set_2.length; i++) {
      switch (menu_set_2.charAt(i)) {
        case 'Y':
          DW_td2.appendChild(Year2);
          break;
        case 'M':
          DW_td2.appendChild(Month2);
          break;
        case 'D':
          DW_td2.appendChild(Day2);
          break;
        case 'T':
          DW_td2.appendChild(Time2);
          break;
        default:
          error_msg = "DateWidget ERROR:  render: menu_set \"" + menu_set_2 + "\" must contain only characters from \"YMDT\"";
          throw(error_msg);
          break;
      }
    }
  }

  node.appendChild(DW_table);

  this.initializeYearMenu(Year1);
  if (menu_set_2) {
    this.initializeYearMenu(Year2);
  }
 
  this.visible = 1;

}
/**
 * Creates the table of menus associated with the DateWidget.
 * <p>
 * Any children of element_id will be removed and replaced with a set of menus from the
 * following collection:  [Y]ear, [M]onth, [D]ay, [T]ime.  The order in which these are
 * specified determiens the order in which they appear in the interface.
 * <p>
 * If menu_set_2 is omitted, only a single set of menus will appear.
 * @param {string} element_id 'id' attribute of the element into which the menus are inserted.
 * @param {string} menu_set_1 one or more of the characters 'YMDT', in any order, specifying
 *                 which menus will be shown for Date1
 * @param {string} menu_set_1 one or more of the characters 'YMDT', in any order, specifying
 *                 which menus will be shown for Date2
 */
function DateWidget_render(element_id,menu_set_1,menu_set_2) {

  this.element_id = element_id;
  this.menu_set_1 = menu_set_1;
  this.menu_set_2 = menu_set_2;

  var node = document.getElementById(this.element_id);
  if(node.childNodes)
  	var children = node.childNodes;
  else
	var children = [];
  var num_children = children.length;

  // Remove any children of this widget
  // NOTE:  Start removing children from the end.  Otherwise, what was
  // NOTE:  children[1]  becomes children[0] when children[0] is removed.
  for (var i=num_children-1; i>=0; i--) {
    var child = children[i];
    if (child) {
      node.removeChild(child);
    }
  }

  // Create up to 8 Select objects
  var Text1, Year1, Month1, Day1, Time1;
  var Text2, Year2, Month2, Day2, Time2;
  var climatology_1 = 0;
  var climatology_2 = 0;

  // Test the 'menu_set_1/2' strings for required widgets
  // Create required Select objects with the appropriate 'id' 
  // Give each Select object a reference to the DateWidget
  // Give the DateWidget a reference to each Select object

  if (!menu_set_1) {
    error_msg = "DateWidget ERROR:  render: menu_set_1 must be defined and  must contain only characters from \"YMDT\"";
    throw(error_msg);
  }

  // TODO:  Create the 'id' attribute from this.element_id + "_Year1" etc. 
  // TODO:  instead of just "DW_Year1" etc.

  // The Year, Month, Day and Time widgets are always created so that initialization
  // along the path: "Year -> Month -> Day" can procede as usual.  The formatting
  // specified inthe menu_set only determines whether they are visible or not.
  Year1 = document.createElement('select');
  Year1.setAttribute('id','DW_Year1'); 
  Year1.widget = this;
  this.Year1 = Year1;
  if (menu_set_1.indexOf('Y') < 0) {
    this.Year1.style.display = 'none';
    climatology_1 = 1;
    this.climatology = 1;

    text1 = document.createTextNode('Climatology:  ');
    Text1 = document.createElement('span');
    Text1.setAttribute('id','DW_Text1'); 
    Text1.appendChild(text1);
    Text1.widget = this;
    this.Text1 = Text1;
  }

  Month1 = document.createElement('select');
  Month1.setAttribute('id','DW_Month1'); 
  Month1.widget = this;
  this.Month1 = Month1;
  if (menu_set_1.indexOf('M') < 0) {
    this.Month1.style.display = 'none';
  }

  Day1 = document.createElement('select');
  Day1.setAttribute('id','DW_Day1'); 
  Day1.widget = this;
  this.Day1 = Day1;
  if (menu_set_1.indexOf('D') < 0) {
    this.Day1.style.display = 'none';
  }

  Time1 = document.createElement('select');
  Time1.setAttribute('id','DW_Time1'); 
  Time1.widget = this;
  this.Time1 = Time1;
  if (menu_set_1.indexOf('T') < 0) {
    this.Time1.style.display = 'none';
  }

  if (menu_set_2) {
    Year2 = document.createElement('select');
    Year2.setAttribute('id','DW_Year2'); 
    Year2.widget = this;
    this.Year2 = Year2;
    if (menu_set_2.indexOf('Y') < 0) {
      this.Year2.style.display = 'none';
      climatology_2 = 1;

      text2 = document.createTextNode('Climatology:  ');
      Text2 = document.createElement('span');
      Text2.setAttribute('id','DW_Text2'); 
      Text2.appendChild(text2);
      Text2.widget = this;
      this.Text2 = Text2;
    }

    Month2 = document.createElement('select');
    Month2.setAttribute('id','DW_Month2'); 
    Month2.widget = this;
    this.Month2 = Month2;
    if (menu_set_2.indexOf('M') < 0) {
      this.Month2.style.display = 'none';
    }

    Day2 = document.createElement('select');
    Day2.setAttribute('id','DW_Day2'); 
    Day2.widget = this;
    this.Day2 = Day2;
    if (menu_set_2.indexOf('D') < 0) {
      this.Day2.style.display = 'none';
    }

    Time2 = document.createElement('select');
    Time2.setAttribute('id','DW_Time2'); 
    Time2.widget = this;
    this.Time2 = Time2;
    if (menu_set_2.indexOf('T') < 0) {
      this.Time2.style.display = 'none';
    }

    if (climatology_1 != climatology_2) {
      error_msg = "DateWidget ERROR:  render: menu_set_1 \"" + menu_set_1 + "\" and menu_set_2 \"" + menu_set_2 +
                  "\" must both include or both exclude the \"Y\" flag in the menu_set" ;
      throw(error_msg);
    }
  }

  // Create the table that holds the menus

  var DW_table, DW_tr1, DW_td1, DW_tr2, DW_td2;

  DW_table = document.createElement('table');
  DW_table.setAttribute('id', 'DW_table');
  DW_tbody = document.createElement('tbody');
  DW_tbody.setAttribute('id', 'DW_tbody');
  DW_tr1 = document.createElement('tr');
  DW_tr1.setAttribute('id', 'DW_tr1');
  DW_td1 = document.createElement('td');
  DW_td1.setAttribute('id', 'DW_td1');
  DW_table.appendChild(DW_tbody);
  DW_tbody.appendChild(DW_tr1);
  DW_tr1.appendChild(DW_td1);

  // Create the first DateWidget
  // Add the hidden menus first, then order the visible ones as per formatting instructions
  if (Year1.style.display == 'none') {
    DW_td1.appendChild(Year1);
    DW_td1.appendChild(Text1);
  }
  if (Month1.style.display == 'none') {
    DW_td1.appendChild(Month1);
  }
  if (Day1.style.display == 'none') {
    DW_td1.appendChild(Day1);
  }
  if (Time1.style.display == 'none') {
    DW_td1.appendChild(Time1);
  }
  for (i=0; i<menu_set_1.length; i++) {
    switch (menu_set_1.charAt(i)) {
      case 'Y':
        DW_td1.appendChild(Year1);
        break;
      case 'M':
        DW_td1.appendChild(Month1);
        break;
      case 'D':
        DW_td1.appendChild(Day1);
        break;
      case 'T':
        DW_td1.appendChild(Time1);
        break;
      default:
        error_msg = "DateWidget ERROR:  render: menu_set \"" + menu_set_1 + "\" must contain only characters from \"YMDT\"";
        throw(error_msg);
        break;
    }
  }

  if (menu_set_2) {
    // Add a second row to the table
    DW_tr2 = document.createElement('tr');
    DW_tr2.setAttribute('id', 'DW_tr2');
    DW_td2 = document.createElement('td');
    DW_td2.setAttribute('id', 'DW_td2');
    DW_tbody.appendChild(DW_tr2);
    DW_tr2.appendChild(DW_td2);

    // Add the hidden menus first, then order the visible ones as per formatting instructions
    if (Year2.style.display == 'none') {
      DW_td2.appendChild(Year2);
      DW_td2.appendChild(Text2);
    }
    if (Month2.style.display == 'none') {
      DW_td2.appendChild(Month2);
    }
    if (Day2.style.display == 'none') {
      DW_td2.appendChild(Day2);
    }
    if (Time2.style.display == 'none') {
      DW_td2.appendChild(Time2);
    }
    // Create the second DateWidget
    for (i=0; i<menu_set_2.length; i++) {
      switch (menu_set_2.charAt(i)) {
        case 'Y':
          DW_td2.appendChild(Year2);
          break;
        case 'M':
          DW_td2.appendChild(Month2);
          break;
        case 'D':
          DW_td2.appendChild(Day2);
          break;
        case 'T':
          DW_td2.appendChild(Time2);
          break;
        default:
          error_msg = "DateWidget ERROR:  render: menu_set \"" + menu_set_2 + "\" must contain only characters from \"YMDT\"";
          throw(error_msg);
          break;
      }
    }
  }

  node.appendChild(DW_table);

  this.initializeYearMenu(Year1);
  if (menu_set_2) {
    this.initializeYearMenu(Year2);
  }
 
  this.visible = 1;
}

/**
 * Creates the year selector and re-initializes the month and day selectors.
 * @private
 * @param YearMenu document select object for year
 */
function DateWidget_initializeYearMenu(YearMenu) {

// Recreate the Year menu
//   If widget #1, range = yearLo:yearHi
//   If widget #2, range = yearLo:yearHi

  var loYear;
  var hiYear;
  var currentYear;

// Get the appropriate loYear, hiYear and currentYear

  // TODO:  Use matching instead of '==' when 'DW_Year1' is replaced by
  // TODO:  this.entry_id + "_Year1"
  if (YearMenu.getAttribute('id') == 'DW_Year1') {
    loYear = this.yearLo;
    hiYear = this.yearHi;
    if (this.year1 > this.year2) {
      currentYear = this.year2;
      this.flash(YearMenu);
    } else {
      currentYear = this.year1;
    }
  } else {
    loYear = this.yearLo;
    hiYear = this.yearHi;
    if (this.year1 > this.year2) {
      currentYear = this.year1;
      this.flash(YearMenu);
    } else {
      currentYear = this.year2;
    }
  }

// Create a new set of options and then select
// a year: current selection or nearest available unless initializing.

// NOTE:  See Quirksmode for many hints on cross-browser scripting (http://www.quirksmode.org/)
// NOTE:  
// NOTE:  1) Qurksmode recommends usig the traditional model for event registering
// NOTE:
// NOTE:  2) Safari 1.3 doesn't support events on Options
// NOTE:     Instead, attach an onchange event to the Select object

  var n = hiYear - loYear + 1
  var y = loYear;
  with (YearMenu) {
    options.length=0;
    for (i=0; i<n; i++) {
      options[i]=new Option(y,y);
      //this.addEvent(options[i], 'click', this.optionClick, false);
      y++;  
    }
    if (this.initializing) {
      // Initialize Year1 to the first year
      // Initialize Year2 to the last year
      this.internallyForced = 1;
      if (name == 'Year1') {
        options[0].selected=true;
      } else {
        options[length-1].selected=true;
      }
      this.initializing = 0;
    } else {
      y = loYear;
      for (i=0; i<n; i++) {
        if (y == currentYear) {
          options[i].selected=true;
        }
        y++;  
      }
    }
  }
  YearMenu.onchange = this.selectChange;
  this.selectYear(YearMenu);

}


/**
 * Resets the valid date range to the specified values.
 * <p>
 * Frst the years are set and then the month and day selectors are 
 * re-initialized.
 * @param {string} String1 lo date value of the form 'YYYY-MM-DD [HH:mm:SS]'
 * @param {string} String2 hi date value of the form 'YYYY-MM-DD [HH:mm:SS]'
 * @param {int} add_sub_1 minutes to adjust if String1 is 'TODAY' or 'NOW'
 * @param {int} add_sub_2 minutes to adjust if String2 is 'TODAY' or 'NOW'
 */
function DateWidget_setDateRange(String1,String2,add_sub_1,add_sub_2) {
  this.resetDateRange();
  this.setDate1(String1,add_sub_1);
  this.setDate2(String2,add_sub_2);
}

/**
 * Sets the date to appear in the Date1 DateWidget.
 * <p>
 * This need not be identical to the lowest valid date.
 * <p>
 * Frst the year is set and then the month and day selectors are re-initialized.
 * <p>
 * <b>NOTE:</b> If (! Date1 <= String1 <= Date2 ) this routine
 * will not work properly.  The internal logic will
 * prevent this from happening but any resetting of
 * dates from external javascript code should ALWAYS use setDateRange().
 * @param {string} String1 Date1 date value of the form 'YYYY-MM-DD [HH:mm:SS]'
 * @param {int} add_sub_1 minutes by which to adjust the date
 */
function DateWidget_setDate1(String1,add_sub_1) {
// TODO:  setDate1() needs to throw exceptions
  var date1 = this.parseDate(String1,add_sub_1);
  this.year1 = date1[0];
  this.month1 = date1[1];
  this.day1 = date1[2];
  this.hour1 = date1[3];
  this.minute1 = date1[4];
  this.second1 = date1[5];
  if (!this.climatology) {
    this.initializeYearMenu(this.Year1);
  } else {
    if (this.Month1) {
      this.selectMonth(this.Month1);
    }
  }
}

/**
 * Resets the date to appear in the Date2 DateWidget.
 * <p>
 * This need not be identical to the highest valid date.
 * <p>
 * Frst the year is set and then the month and day selectors are re-initialized.
 * @param {string} String2 Date2 date value of the form 'YYYY-MM-DD [HH:mm:SS]'
 * @param {int} add_sub_2 minutes by which to adjust the date
 */
function DateWidget_setDate2(String2,add_sub_2) {
// TODO:  setDate2() needs to throw exceptions
  var date2 = this.parseDate(String2,add_sub_2);
  this.year2 = date2[0];
  this.month2 = date2[1];
  this.day2 = date2[2];
  this.hour2 = date2[3];
  this.minute2 = date2[4];
  this.second2 = date2[5];
  if (this.Year2) {
    this.initializeYearMenu(this.Year2);
  } else {
    if (this.Month2) {
      this.selectMonth(this.Month2);
    }
  }
}

/**
 * Returns an ascii representation of the current state. 
 * @return {string} string ascii representation of DateWidget object
 */
function DateWidget_toString() {
  var message = 'dateLo = ' + this.getDateLo() + '\n' +
                'dateHi = ' + this.getDateHi() + '\n' +
                'date1 = ' + this.getDate1() + '\n' +
                'date2 = ' + this.getDate2() + '\n' +
                'this.deltaMinutes = ' + this.deltaMinutes + '\n' +
                'this.offsetMinutes = ' + this.offsetMinutes + '\n';
  return message;
}

/**
 * Returns an ascii representation of DateLo.
 * <p>
 * DateLo is the low end of the valid range of dates.
 * @return {string} date string in the form 'YYYY-MM-DD [HH:mm:SS]'
 */
function DateWidget_getDateLo() {
  var monthLo = (this.monthLo == '00') ? '' : '-' + this.monthLo;
  var dayLo = (this.dayLo == '00') ? '' : '-' + this.dayLo;
  var timeLo = (this.Time1) ? ' ' + this.twoDigit(this.hourLo) + ':' + this.twoDigit(this.minuteLo) + ':' + this.twoDigit(this.secondLo) : '';
  var message = this.fourDigit(this.yearLo) + monthLo + dayLo + timeLo;
  return message;
}

/**
 * Returns an ascii representation of DateHi.
 * <p>
 * DateHi is the high end of the valid range of dates.
 * @return {string} date string in the form 'YYYY-MM-DD [HH:mm:SS]'
 */
function DateWidget_getDateHi() {
  var monthHi = (this.monthHi == '00') ? '' : '-' + this.monthHi;
  var dayHi = (this.dayHi == '00') ? '' : '-' + this.dayHi;
  var timeHi = (this.Time2) ? ' ' + this.twoDigit(this.hourHi) + ':' + this.twoDigit(this.minuteHi) + ':' + this.twoDigit(this.secondHi) : '';
  var message = this.fourDigit(this.yearHi) + monthHi + dayHi + timeHi;
  return message;
}

/**
 * Returns an ascii representation of Date1.
 * <p>
 * Date1 is the lower of the user specified dates.
 * @return {string} date string in the form 'YYYY-MM-DD [HH:mm:SS]'
 */
function DateWidget_getDate1() {
  var month1 = (this.month1 == '00') ? '' : '-' + this.month1;
  var day1 = (this.day1 == '00') ? '' : '-' + this.day1;
  var time1 = (this.Time1) ? ' ' + this.twoDigit(this.hour1) + ':' + this.twoDigit(this.minute1) + ':' + this.twoDigit(this.second1) : '';
  var message = this.fourDigit(this.year1) + month1 + day1 + time1;
  return message;
}

/**
 * Returns an ascii representation of Date2.
 * <p>
 * Date2 is the higher of the user specified dates.
 * @return {string} date string in the form 'YYYY-MM-DD [HH:mm:SS]'
 */
function DateWidget_getDate2() {
  var month2 = (this.month2 == '00') ? '' : '-' + this.month2;
  var day2 = (this.day2 == '00') ? '' : '-' + this.day2;
  var time2 = (this.Time2) ? ' ' + this.twoDigit(this.hour2) + ':' + this.twoDigit(this.minute2) + ':' + this.twoDigit(this.second2) : '';
  var message = (this.Year2) ? this.fourDigit(this.year2) + month2 + day2 + time2 : '';
  return message;
}

/**
 * Returns an ascii representation of Date1 for use by Ferret.
 * <p>
 * This is the format that the Ferret analysis and visualization tool requires.
 * @return {string} date string in the form 'DD-MMM-YYYY [HH:mm:SS]'
 *                  (e.g. '15-JAN-2006 00:00:00')
 */
function DateWidget_getDate1_Ferret() {
  var monthNames = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
  var month1 = (this.month1 == '00') ? '' : '-' + monthNames[Number(this.month1)-1];
  var day1 = (this.day1 == '00') ? '' : this.day1;
  if( this.menu_set_1.indexOf('T')!=-1)
	var time1 = (this.Time1) ? ' ' + this.twoDigit(this.hour1) + ':' + this.twoDigit(this.minute1) + ':' + this.twoDigit(this.second1) : '';
  else
	var time1 = "";
  var message = day1 + month1 + '-' + this.fourDigit(this.year1) + time1;
  return message;
}

/**
 * Returns an ascii representation of Date2 for use by Ferret.
 * <p>
 * This is the format that the Ferret analysis and visualization tool requires.
 * @return {string} date string in the form 'DD-MMM-YYYY [HH:mm:SS]'
 *                  (e.g. '15-JAN-2006 00:00:00')
 */
function DateWidget_getDate2_Ferret() {
  var monthNames = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
  var month2 = (this.month2 == '00') ? '' : '-' + monthNames[Number(this.month2)-1];
  var day2 = (this.day2 == '00') ? '' : this.day2;
  if(this.menu_set_2.indexOf('T')!=-1)
	  var time2 = (this.Time2) ? ' ' + this.twoDigit(this.hour2) + ':' + this.twoDigit(this.minute2) + ':' + this.twoDigit(this.second2) : '';
  else
	 var time2 = "";
  //var message = (this.Year2) ? day2 + month2 + '-' + this.fourDigit(this.year2) + time2 : '';
  var message = day2 + month2 + '-' + this.fourDigit(this.year2) + time2;
  return message;
}

/** 
 * Returns an ascii representation of the entire DateWidget for use 
 * in building LASRequests with the ImageSlideSorter.
 * @return {string} Date1 and Date2 strings (separated by a comma) for use in an LASRequest
 */
function DateWidget_getValue() {
  var result = this.getDate1_Ferret();
  if (this.menu_set_2) {
    result = result + "," + this.getDate2_Ferret();
  }
  return result;
}

/** 
 * Sets the value of the DateWidget.
 * @param {string} Date string
 * @param {int} index [1 | 2] identifying which Date to set (optional)
 * @return {string} Date1 and Date2 strings (separated by a comma) for use in an LASRequest
 */
function DateWidget_setValue(value,index) {
  var index = index ? index : 1;
  if (index == 1) {
    this.setDate1(value);
  } else {
    this.setDate2(value);
  }
}

/**
 * Displays an alert box with the ascii representation of the DateWidget object.
 */
function DateWidget_alert() {
  alert(this.toString());
}

/**
 * Resets Date1 to DateLo.
 */
function DateWidget_resetDate1() {
  this.setDate1(this.getDateLo());
}

/**
 * Resets Date2 to DateHi.
 */
function DateWidget_resetDate2() {
  this.setDate2(this.getDateHi());
}

/**
 * Resets Date1 to DateLo.
 * Resets Date2 to DateHi.
 */
function DateWidget_resetDateRange() {
  this.resetDate1();
  this.resetDate2();
}

/**
 * Sets the deltaMinutes property.
 * @param {int} deltaMintues interval in the time selector 
 */
function DateWidget_setDeltaMinutes(min) {
  this.deltaMinutes = min;
}

/**
 * Sets the offsetMinutes property.
 * @param {int} offsetMinutes offset from 00:00:00 in the time selector
 */
function DateWidget_setOffsetMinutes(min) {
  this.offsetMinutes = min;
}

/**
 * Disables all Select objects in the Widget.
 */
function DateWidget_disable(hilo) {
  var node = document.getElementById(this.element_id);
  var select_nodes = node.getElementsByTagName('select');
  for (var i=0; i<select_nodes.length; i++) 
  	switch(hilo) {
		case 'hi' :
			if(select_nodes[i].id[select_nodes[i].id.length-1]=='2')
				select_nodes[i].disabled= 1;
			else

			break;
		case 'lo' :
			if(select_nodes[i].id[select_nodes[i].id.length-1]=='1')
				select_nodes[i].disabled= 1;
			break;
		default :    
			select_nodes[i].disabled = 1;
  		
	}  
  this.disabled = 1;
}

/**
 * Enables all Select objects in the Widget.
 */
function DateWidget_enable(hilo) {
  var node = document.getElementById(this.element_id);
  var select_nodes = node.getElementsByTagName('select');
  for (var i=0; i<select_nodes.length; i++) 
	switch(hilo) {
		case 'hi' :
			if(select_nodes[i].id[select_nodes[i].id.length-1]=='2')
				select_nodes[i].disabled= 0;
			else

			break;
		case 'lo' :
			if(select_nodes[i].id[select_nodes[i].id.length-1]=='1')
				select_nodes[i].disabled= 0;
			break;
		default :    
			select_nodes[i].disabled = 0;
  		
	}
  this.disabled = 0;
}

/**
 * Set's the Widget container's visibility to 'visible'
 */
function DateWidget_show() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'visible';
  this.visible = 1;
}

/**
 * Set's the Widget container's visibility to 'hidden'
 */
function DateWidget_hide() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'hidden';
  this.visible = 0;
}

/**
 * Attaches a callback function to the DateWidget that will be executed after
 * every interaction with the widget.
 * @param {function} external callback function
 */
function DateWidget_setCallback(callback) {
  this.callback = callback;
}

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Private methods                                                            //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////


/**
 * Parses a date string in ISO 8601 format 'YYYY-MM-DD [HH:mm:SS]'.
 * <p>
 * Also accepts the strings 'TODAY' and 'NOW' or Ferret-style '15-Jan-2002'
 * <dl>
 *   <dt>TODAY</dt>
 *   <dd>Sets the date elements of the returning array to today's
 *   date but leaves the time portion at 00:00:00.</dd>
 *   <dt>NOW</dt>
 *   <dd>Sets all elements of the returning array to the values
 *   returned by the javascrpit Date() method.</dd>
 *   <dt>15-Jan</dt>
 *   <dd>Ferret climatological dates will be converted to
 *   0001-MM-DD.</dd>
 * </dl>
 * An optional argument may be passed in describing the number
 * of minutes to add or subtract from the date.
 * @private
 * @param {string} dateString date string to parse
 * @param {int} add_sub mintues to add or subtract from the date
 * @return YMDHMS array of integers representing the date and time. 
 */
// TODO:  Fix support for Climatologies.
// TODO:  Firefox 1.0.8 on Linux will not allow me to specify '0000'
// TODO:  as the year in new Date(...).  It converts this to an integer,
// TODO:  determines it has less than two digits and adds 1900.

function DateWidget_parseDate(dateString,add_sub) {
  var YMDHMS; 
  var milli;
  var date = new Date();
  var newDate = new Date();

  switch (dateString) {
    case 'NOW':
      break;
    case 'TODAY':
      var dummy = new Date();
      date = new Date(dummy.getFullYear(),dummy.getMonth(),dummy.getDate(),0,0,0);
      newDate = new Date(dummy.getFullYear(),dummy.getMonth(),dummy.getDate(),0,0,0);
      break;
    default:
      var dateTime = String(dateString).split(' ');
      var YMD = String(dateTime[0]).split('-');  
      var YMDHMS = String(dateTime[0]).split('-');  

      // Ferret format: 'D?-Mon[-YYYY]'
      if (String(YMD[1]).length == 3) {
        var months = {jan:'01',feb:'02',mar:'03',apr:'04',may:'05',jun:'06',jul:'07',aug:'08',sep:'09',oct:'10',nov:'11',dec:'12'};
        var mon = String(YMD[1].toLowerCase());
        var MM = months[mon];
        if (YMD[2]) { 
          YMDHMS[0] = YMD[2];
        } else {
          YMDHMS[0] = '0001';
        }
        YMDHMS[1] = MM;
        YMDHMS[2] = YMD[0];
      }

      if (YMDHMS[0]) {
        // NOTE:  The javascript Date object in most browsers treats all 0000 < years < 0100 as if they were two-digit 
        // NOTE:  representations of '19YY'.  To account for this we will add 8000 to all 
        // NOTE:  years < 0100 for internal Date object calculations and then subtract 8000
        // NOTE:  at the end.
        if (Number(YMDHMS[0]).valueOf() >= 8000) {
          error_msg = 'The DateWidget cannot handle years >= 8000.  Incoming date is "' +  dateString + '"';
          throw(error_msg);
        }
        var year = Number(YMDHMS[0]).valueOf();
        if (year < 100) {
          year += 8000;
          YMDHMS[0] = String(year);
        }
        // NOTE:  Test for restricted Safari range as described here:
        // NOTE:  http://www.merlyn.demon.co.uk/js-datex.htm
        var in_year = Number(YMDHMS[0]);
        var Safari_date = new Date(in_year,1,1);
        var Safari_year = Safari_date.getFullYear();
        if (Safari_year != in_year) {
          if (in_year > 8000) { in_year -= 8000 } // convert back to original for error message
          error_msg = 'Browser Issue!  Some browsers, eg Safari, only support years within the restricted range of "1902" to "2037".  Incoming year is "' + in_year + '".';
          //throw(error_msg);
        }

      } else {
        error_msg = 'DateWidget ERROR:  parseDate: bad date "' + dateString + '"';
        //throw(error_msg);
      }

       
      if (dateTime[1]) {
        var HMS = String(dateTime[1]).split(':');
        YMDHMS[3] = HMS[0];
        YMDHMS[4] = HMS[1];
        if ( HMS[2] ) {
           YMDHMS[5] = HMS[2];
        } else {
           YMDHMS[5] = '00';
        }
      } else {
        YMDHMS[3] = '00';
        YMDHMS[4] = '00';
        YMDHMS[5] = '00';
      }
      // Throw exceptions if appropriate
      var error_msg;
      if (Number(YMDHMS[1]).valueOf() < 1 || Number(YMDHMS[1]).valueOf() > 12) {
        error_msg = "DateWidget ERROR:  parseDate:  month \"" + YMDHMS[1] + "\" must be in the range 1:12";
        throw(error_msg);
      }
      if (Number(YMDHMS[2]).valueOf() < 1 || Number(YMDHMS[2]).valueOf() > 31) {
        error_msg = "DateWidget ERROR:  parseDate:  day \"" + YMDHMS[2] + "\" must be in the range 1:31";
        throw(error_msg);
      }
      if (Number(YMDHMS[3]).valueOf() < 0 || Number(YMDHMS[3]).valueOf() > 23) {
        error_msg = "DateWidget ERROR:  parseDate:  hour \"" + YMDHMS[3] + "\" must be in the range 0:23";
        throw(error_msg);
      }
      if (Number(YMDHMS[4]).valueOf() < 0 || Number(YMDHMS[4]).valueOf() > 59) {
        error_msg = "DateWidget ERROR:  parseDate:  minute \"" + YMDHMS[4] + "\" must be in the range 0:59";
        throw(error_msg);
      }
      if (Number(YMDHMS[5]).valueOf() < 0 || Number(YMDHMS[5]).valueOf() > 59) {
        error_msg = "DateWidget ERROR:  parseDate:  second \"" + YMDHMS[5] + "\" must be in the range 0:59";
        throw(error_msg);
      }
      date = new Date(YMDHMS[0],YMDHMS[1]-1,YMDHMS[2],YMDHMS[3],YMDHMS[4],YMDHMS[5]);
      newDate = new Date(YMDHMS[0],YMDHMS[1]-1,YMDHMS[2],YMDHMS[3],YMDHMS[4],YMDHMS[5]);
      break;
  }

  if (add_sub) {
    milli = date.valueOf() + 60 * 1000 * add_sub;
    newDate = new Date(milli);
  }

  // NOTE:  Store integer values (not Strings) in YMDHMS.
  with (newDate) {
    YMDHMS = new Array(getFullYear(),getMonth()+1,getDate(),getHours(),getMinutes(),getSeconds());
  }

// NOTE:  No support for seconds at this time
  YMDHMS[5] = 0;
// NOTE:  Return years > 8000 to their original 
  if (YMDHMS[0] >= 8000) {
    YMDHMS[0] -= 8000;
  }
  return YMDHMS;
}

/**
 * Returns the number of days in the February of a particular year.
 * @private
 * @param {int} year four digit year
 * @return {int} febDays number of days in the February of that year.
 */
function DateWidget_febDays(year) {
  var febDays = 28 
  if ( (year%4) == 0 ) {
    febDays = 29;
    if ( (year%100) == 0 ) {
      febDays = 28;
      if ( (year%400) == 0 ) {
        febDays = 29;
      }
    }
  }
  return febDays;
}

/**
 * Forces all MM, DD, HH, mm, SS integers to two digit strings.
 * @private
 * @param {int} num value to test
 * @return {int} num two digit version of the value
 */
function DateWidget_twoDigit(num) {
  if (String(num).length == 1) {
    num = '0' + String(num);
  }
  return num;
}

/**
 * Forces YYYY integers into four digits strings.
 * @private
 * @param {int} num value to test
 * @return {int} num two digit version of the value
 */
function DateWidget_fourDigit(num) {
  while (String(num).length < 4) {
    num = '0' + String(num);
  }
  return num;
}

/**
 * Checks to see if Date1 < Date2.
 * @private
 * @return {int} 0 (FALSE) | 1 (TRUE)
 */
function DateWidget_correctOrder() {
  var order = 1;
  if (this.year1 > this.year2) {
    order = 0;
  } else {
    if (this.year1 == this.year2) {
      if (this.month1 > this.month2) {
        order = 0;
      } else {
        if (this.month1 == this.month2) {
          if (this.day1 > this.day2) {
            order = 0;
          } else {
            if (this.day1 == this.day2) {
              if (this.hour1 > this.hour2) {
                order = 0;
              }
            }
          }
        }
      }
    }
  }
  return order;
}

/**
 * Cross-browser event handling for IE5+, NS6+ and Mozilla/Gecko
 * By Scott Andrew
 * @private
 * @param elm element listening for the event
 * @param evType type of event
 * @param fn function to invoke when an event is triggered
 * @param useCapture flag
 */
// NOTE:  Based on advice from Quirksmode (http://www.quirksmode.org) I will not use the
// NOTE:  W3C method of event registration in this version (March, 2007) of the code.
// NOTE:  Instead, look for the traditional method (e.g. YearMenu.onchange = selectChange;)
// NOTE:  in the code.
function DateWidget_addEvent(elm, evType, fn, useCapture) {
  if (elm.addEventListener) {
    elm.addEventListener(evType, fn, useCapture);
    return true;
  } else if (elm.attachEvent) {
    var r = elm.attachEvent('on' + evType, fn);
    return r;
  } else {
    elm['on' + evType] = fn;
  }
}

/**
 * Flash a Select menu to indicate that it has been changed internally.
 *
 * @param Menu Select object that is being changed
 */
function DateWidget_flash(Menu) {
  var name = Menu.id;
  var command = "document.getElementById(\'" + name + "\').style.backgroundColor = '#fff'";
  Menu.style.backgroundColor = '#fcc';
  window.setTimeout(command,750);
}


////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Private methods that are called by the event handler.                      //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * Method called by event handler for the 'Year1' and 'Year2' selectors.
 * @param YearMenu document select object for year
 */
function DateWidget_selectYear(YearMenu) {
  var monthNames = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
  var monthVals = new Array('01','02','03','04','05','06','07','08','09','10','11','12');

  currentYear = YearMenu.options[YearMenu.selectedIndex].value;

  var currentMonth;
  var MonthMenu;
  if (YearMenu.getAttribute('id') == 'DW_Year1') {
    MonthMenu = this.Month1;
    this.year1 = currentYear;
    if (this.year1 == this.year2 && this.month1 > this.month2) {
      currentMonth = this.month2 - 1;
      this.flash(MonthMenu);
    } else {
      currentMonth = this.month1 - 1;
    }
  } else {
    MonthMenu = this.Month2;
    this.year2 = currentYear;
    if (this.year1 == this.year2 && this.month1 > this.month2) {
      currentMonth = this.month1 - 1;
      this.flash(MonthMenu);
    } else {
      currentMonth = this.month2 - 1;
    }
  }

// Recreate the Month menu
//   If widget #1, range = yearLo-monthLo:yearHi-monthHi
//   If widget #2, range = yearLo-monthLo:yearHi-monthHi

  var loMonth = 0;
  var hiMonth = 11;
  var OtherYearMenu;
  if (YearMenu.getAttribute('id') == 'DW_Year1') {
    OtherYearMenu = this.Year2;
    if (currentYear == this.yearLo) {
      loMonth = this.monthLo - 1; 
    }
    if (currentYear == this.yearHi) {
      hiMonth = this.monthHi - 1; 
    }
  } else {
    OtherYearMenu = this.Year1;
    if (currentYear == this.yearLo) {
      loMonth = this.monthLo - 1; 
    }
    if (currentYear == this.yearHi) {
      hiMonth = this.monthHi - 1; 
    }
  }

// Create a new set of options and then select
// a month: current selection or nearest available unless initializing.

  if (MonthMenu) {
    with (MonthMenu) {
      var n = hiMonth - loMonth;
      var m = loMonth;
      options.length=0;
      if (this.monthLo != '00') {
        for (i=0; i<=n; i++) {
          options[i]=new Option(monthNames[m],monthVals[m]);
          //this.addEvent(options[i], 'click', this.optionClick, false);
          m++;
        }
        if (currentMonth < loMonth || currentMonth > hiMonth) {
          if (currentMonth < loMonth) {
            options[0].selected = true;
           } else {
            var i = hiMonth - loMonth;
            if(options[i]) {
		options[i].selected = true; }
           }
        } else {
          var i = currentMonth - loMonth;
          if(options[i]) {
		options[i].selected = true;
		}
        }
      }
    }
    MonthMenu.onchange = this.selectChange;
    this.selectMonth(MonthMenu);
  } else { 
//No Month-Day-Time Menus
    if (this.internallyForced) {
      this.internallyForced = 0;
    } else {
      if (OtherYearMenu) {
        if (!this.correctOrder()) {
          this.internallyForced = 1;
          this.initializeYearMenu(OtherYearMenu);
        }
      }
    }
  }

}


/**
 * Method called by event handler for the 'Month1' and 'Month2' selectors.
 * @param MonthMenu document Select object for Month
 */
function DateWidget_selectMonth(MonthMenu) {

  var currentYear;
  var currentMonth; 
  var currentDay;
  var DayMenu;
  var OtherYearMenu;

  var dayVals = new Array('01','02','03','04','05','06','07','08','09','10',
                          '11','12','13','14','15','16','17','18','19','20',
                          '21','22','23','24','25','26','27','28','29','30','31');

  if (this.monthLo == '00') {
    currentMonth = '00';
  } else {
    currentMonth = MonthMenu.options[MonthMenu.selectedIndex].value;
  }

  // TODO:  Use matching instead of '==' when 'DW_Month1' is replaced by
  // TODO:  this.entry_id + "_Month1"
  if (MonthMenu.getAttribute('id') == 'DW_Month1') {
    OtherYearMenu = this.Year2;
    DayMenu = this.Day1;
    currentYear = this.year1;
    this.month1 = currentMonth;
    if (this.year1 == this.year2 && this.month1 == this.month2 && this.day1 > this.day2) {
      currentDay = this.day2;
      this.flash(DayMenu);
    } else {
      currentDay = this.day1;
    }
  } else {
    OtherYearMenu = this.Year1;
    DayMenu = this.Day2;
    currentYear = this.year2;
    this.month2 = currentMonth;
    if (this.year1 == this.year2 && this.month1 == this.month2 && this.day1 > this.day2) {
      currentDay = this.day1;
      this.flash(DayMenu);
    } else {
      currentDay = this.day2;
    }
  }

// Recreate the Day menu

  var loDay = 1;
  var hiDay = 31;

// Modify hiDay based on month

  switch (currentMonth) {
    case "02":
      hiDay = this.febDays(currentYear); break;
    case "04":
      hiDay = 30; break;
    case "06":
      hiDay = 30; break;
    case "09":
      hiDay = 30; break;
    case "11":
      hiDay = 30; break;
    default:
      hiDay = 31; break;
  }

// Recreate the Day menu
//   If widget #1, range = yearLo-monthLo-dayLo:yearHi-monthHi-dayHi
//   If widget #2, range = yearLo-monthLo-dayLo:yearHi-monthHi-dayHi

  if (MonthMenu.getAttribute('id') == 'DW_Month1') {
    if (currentYear == this.yearLo && currentMonth == this.monthLo) {
      loDay = this.dayLo; 
    }
    if (currentYear == this.yearHi && currentMonth == this.monthHi) {
      hiDay = this.dayHi; 
    }
  } else {
    if (currentYear == this.yearLo && currentMonth == this.monthLo) {
      loDay = this.dayLo; 
    }
    if (currentYear == this.yearHi && currentMonth == this.monthHi) {
      hiDay = this.dayHi; 
    }
  }

// Create a new set of options and then select
// a day: current selection or nearest available

  if (DayMenu) {
    with (DayMenu) {
      var n = hiDay - loDay + 1;
      var d = loDay - 1;
      options.length=0;
      if (this.dayLo != '00') { // No days widget
        for (i=0; i<n; i++) {
          options[i]=new Option(dayVals[d],dayVals[d]);
          //this.addEvent(options[i], 'click', this.optionClick, false);
          d++;
        }
        if (currentDay < loDay || currentDay > hiDay) {
          if (currentDay < loDay) {
            options[0].selected = true;
          } else {
            var i = hiDay - loDay;
            options[i].selected = true;
          }
          this.flash(DayMenu);
        } else {
          var i = currentDay - loDay;
          options[i].selected = true;
        }
      }
    }
    DayMenu.onchange = this.selectChange;
    this.selectDay(DayMenu);
  } else {
//No Day-Time Menus
    if (this.internallyForced) {
      this.internallyForced = 0;
    } else {
      if (OtherYearMenu) {
        if (!this.correctOrder()) {
          this.internallyForced = 1;
          this.initializeYearMenu(OtherYearMenu);
        }
      }
    }
  } 

}


/**
 * Method called by event handler for the 'Day1' and 'Day2' selectors.
 * @param DayMenu document select object for day
 */
function DateWidget_selectDay(DayMenu) {
  var currentYear;
  var currentMonth;
  var currentDay;
  var currentHour;
  var TimeMenu;
  var OtherYearMenu;

  if (this.dayLo == '00') { // No days
    currentDay = '00';
  } else {
    currentDay = DayMenu.options[DayMenu.selectedIndex].value;
  }
  // TODO:  Use matching instead of '==' when 'DW_Day1' is replaced by
  // TODO:  this.entry_id + "_Month1"
  if (DayMenu.getAttribute('id') == 'DW_Day1') {
    this.day1 = currentDay;
    TimeMenu = this.Time1;
    OtherYearMenu = this.Year2;
    currentYear = this.year1;
    currentMonth = this.month1;
    currentDay = this.day1;
/* 
    currentTime = (currentYear == this.year2 && 
                   currentMonth == this.month2 && 
                   currentDay == this.day2 &&
                   this.hour1 > this.hour2) ? 
                   this.hour2 + ':' + this.minute2 :
                   this.hour1 + ':' + this.minute1;
*/
    currentTime = this.hour1 + ':' + this.minute1;
  } else {
    this.day2 = currentDay;
    TimeMenu = this.Time2;
    OtherYearMenu = this.Year1;
    currentYear = this.year2;
    currentMonth = this.month2;
    currentDay = this.day2;
/* 
    currentTime = (currentYear == this.year1 && 
                   currentMonth == this.month1 && 
                   currentDay == this.day1 &&
                   this.hour1 > this.hour2) ? 
                   this.hour1 + ':' + this.minute1 :
                   this.hour2 + ':' + this.minute2;
*/  
    currentTime = this.hour2 + ':' + this.minute2;
  }
  var loMinute = this.offsetMinutes;
  var hiMinute = 1440;
// NOTE: use Number() to prevent '60' + '60' = '6060'
  if (currentYear == this.yearLo && 
     currentMonth == this.monthLo &&
     currentDay == this.dayLo) {
    loMinute = Number(this.minuteLo) + Number(this.hourLo) * 60;
  }
  if (currentYear == this.yearHi && 
     currentMonth == this.monthHi &&
     currentDay == this.dayHi) {
     hiMinute = Number(this.minuteHi) + Number(this.hourHi) * 60;
  }

// Create hours menu
// TODO:  Figure out why TimeMenu isn't working with nextDate()
  if (TimeMenu) {
    with (TimeMenu) {
      var time='00:00';
      var hr = Math.floor(loMinute/60);
      var min = loMinute - hr * 60;
      var minutes = loMinute;
      var n = Math.floor((hiMinute-loMinute)/this.deltaMinutes) + 1;
      for (i=0; i<n; i++) {
        time = this.twoDigit(hr) + ":" + this.twoDigit(min);
        options[i]=new Option(time,time);
        //this.addEvent(options[i], 'click', this.optionClick, false);
        // if (time == currentTime) { this line fails
        var tmp = String(currentTime).split(":");
        if(tmp[0]==hr && tmp[1]==min){
          options[i].selected = true;
        }
        minutes = Number(minutes) + Number(this.deltaMinutes);
        hr = Math.floor(minutes/60);
        min = minutes - hr * 60;
        if (hr >= 24) { break; }
      }
    }
    TimeMenu.onchange = this.selectChange;
  }

// Force update of other Years, Months & Days while avoiding an infinite loop

  if (this.internallyForced) {
    this.internallyForced = 0;
  } else {
    if (OtherYearMenu) {
      if (!this.correctOrder()) {
        this.internallyForced = 1;
        this.initializeYearMenu(OtherYearMenu);
      }
    }
  }

}

/**
 * Method called by event handler for the 'Time1' and 'Time2' selectors.
 * @param TimeMenu document select object for time
 */
function DateWidget_selectTime(TimeMenu) {
  var time = TimeMenu.options[TimeMenu.selectedIndex].value;
  var Time = String(time).split(':');
    
  // TODO:  Use matching instead of '==' when 'DW_Time1' is replaced by
  // TODO:  this.entry_id + "_Time1"
  if (TimeMenu.getAttribute('id') == 'DW_Time1') {
    this.hour1 = Time[0];    
    this.minute1 = Time[1];
  } else {
    this.hour2 = Time[0];    
    this.minute2 = Time[1];
  }
}

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Event handlers.                                                            //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * 'onchange' event handler for all DateWidget selectors.
 * This event handler tests for the 'id' of the Selector and then calls
 * the appropriate selectYear(), selectMonth(), selectDay() or selectTime() method
 * of the DateWidget.
 * @param e event
 */
function DateWidget_selectChange(e) {

  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('DateWidget ERROR:\n> selectChange:  This browser does not support standard javascript events.');
    return;
  }

  if (target.nodeName.toLowerCase() != 'select') {
    alert('DateWidget ERROR:\n> selectChange:  event target [' + target.nodeName.toLowerCase() + '] should instead be [select]');
    return;
  }
  var select = target;
  var DW = select.widget;
  // TODO:  Use matching instead of '==' when 'DW_~1' is replaced by
  // TODO:  this.entry_id + "_~1"
  switch (select.getAttribute('id')) {
    case "DW_Year1":
      DW.selectYear(select);
      break;
    case "DW_Month1":
      DW.selectMonth(select);
      break;
    case "DW_Day1":
      DW.selectDay(select);
      break;
    case "DW_Time1":
      DW.selectTime(select);
      break;
    case "DW_Year2":
      DW.selectYear(select);
      break;
    case "DW_Month2":
      DW.selectMonth(select);
      break;
    case "DW_Day2":
      DW.selectDay(select);
      break;
    case "DW_Time2":
      DW.selectTime(select);
      break;
  }
  if (DW.callback) {
    DW.callback(DW);
  }
}

// NOTE:  Compatibility wih Safari requires that we attach the event handler to the Select object.
// NOTE:  the optionClick method is not currently used.
/*
function DateWidget_optionClick(e) {

  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('DateWidget ERROR:\n> optionClick:  This browser does not support standard javascript events.');
    return;
  }

  if (target.nodeName.toLowerCase() != 'option') {
    alert('DateWidget ERROR:\n> optionClick:  event target [' + target.nodeName + '] should instead be [option]');
    return;
  }
  var select = target.parentNode;
  var DW = select.widget;
  // TODO:  Use matching instead of '==' when 'DW_~1' is replaced by
  // TODO:  this.entry_id + "_~1"
  switch (select.getAttribute('id')) {
    case "DW_Year1":
      DW.selectYear(select);
      break;
    case "DW_Month1":
      DW.selectMonth(select);
      break;
    case "DW_Day1":
      DW.selectDay(select);
      break;
    case "DW_Time1":
      DW.selectTime(select);
      break;
    case "DW_Year2":
      DW.selectYear(select);
      break;
    case "DW_Month2":
      DW.selectMonth(select);
      break;
    case "DW_Day2":
      DW.selectDay(select);
      break;
    case "DW_Time2":
      DW.selectTime(select);
      break;
  }
  if (DW.callback) {
    DW.callback(DW);
  }
}
*/
