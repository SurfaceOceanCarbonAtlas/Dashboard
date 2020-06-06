package gov.noaa.pmel.tmap.las.client.lastest;

import gov.noaa.pmel.tmap.las.client.HtmlSanitizerUtil;
import gov.noaa.pmel.tmap.las.client.serializable.TestDataset;
import gov.noaa.pmel.tmap.las.client.serializable.TestResult;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProductTestTable extends Composite {
	VerticalPanel panel = new VerticalPanel();

	public class HyperlinkCell extends AbstractCell<Hyperlink>
	{
		public void render(Context context, Hyperlink h, SafeHtmlBuilder sb)
		{
			sb.append(SafeHtmlUtils.fromTrustedString(h.toString()));
		}
	}
	public ProductTestTable(TestDataset[] ds, boolean failed) {
		for (int i = 0; i < ds.length; i++) {
			panel.add(new HTML("<h4>"+HtmlSanitizerUtil.sanitizeHtml(ds[i].getName())+"</h4>"));
			TestResult[] results;
			if (failed) {
				results = ds[i].getFailedResults();
			} else {
				results = ds[i].getResults();
			}
			 
			CellTable<TestResult> table = new CellTable<TestResult>();
			table.setWidth("100%", true);
			
			Column<TestResult, SafeHtml> url = new Column<TestResult, SafeHtml>(new SafeHtmlCell()) {

				@Override
				public SafeHtml getValue(TestResult result) {
					
					SafeHtmlBuilder builder = new SafeHtmlBuilder();
					builder.appendHtmlConstant("<a href=\"").appendEscaped(result.getUrl()).appendHtmlConstant("\">").appendEscaped(result.getUrl()).appendHtmlConstant("</a>");
					return builder.toSafeHtml();
				}
				
			};
			
			TextColumn<TestResult> view = new TextColumn<TestResult>() {

				@Override
				public String getValue(TestResult result) {
					return result.getView();
				}
				
			};
            
            TextColumn<TestResult> product = new TextColumn<TestResult>() {

				@Override
				public String getValue(TestResult result) {
					return result.getProduct();
				}
            	
            };
                       
			TextColumn<TestResult> time = new TextColumn<TestResult>() {
				
				@Override
				public String getValue(TestResult result) {
					return result.getTime();
				}
				
			};
			
			TextColumn<TestResult> status = new TextColumn<TestResult>() {

				@Override
				public String getValue(TestResult result) {
					return result.getStatus();
				}
				
				
			};
			
			table.addColumn(status, "Status");			
			table.addColumn(view, "View");
			table.addColumn(product, "Product");
			table.addColumn(time, "Time");
			table.addColumn(url, "URL");
           
			
			table.setColumnWidth(url, 700.0, Unit.PX);
			table.setColumnWidth(status, 120.0, Unit.PX);
			table.setColumnWidth(product, 120.0, Unit.PX);
			table.setColumnWidth(view, 90.0, Unit.PX);
			table.setColumnWidth(time, 120.0, Unit.PX);
			

			table.setRowCount(results.length, true);
			List<TestResult> resultsList = Arrays.asList(results);
			table.setRowData(0, resultsList);
			
			panel.add(table);
			
			
		}
		initWidget(panel);
	}

}
