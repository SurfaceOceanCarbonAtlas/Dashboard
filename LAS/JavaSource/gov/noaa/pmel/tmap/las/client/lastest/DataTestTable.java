package gov.noaa.pmel.tmap.las.client.lastest;

import gov.noaa.pmel.tmap.las.client.serializable.TestDataset;
import gov.noaa.pmel.tmap.las.client.serializable.TestResult;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;


public class DataTestTable extends Composite {
	VerticalPanel tablePanel = new VerticalPanel();
	public DataTestTable(TestDataset[] ds, boolean failed) {
		for (int i = 0; i < ds.length; i++) {

			List<TestResult> results;
			if ( failed ) {
				results = Arrays.asList(ds[i].getFailedResults());
			} else {
				results = Arrays.asList(ds[i].getResults());
			}
			String name = UriUtils.fromString(ds[i].getName()).asString();
			tablePanel.add(new HTML("<h4>"+name+"</h4>"));
			CellTable<TestResult> table = new CellTable<TestResult>();
			table.setWidth("100%", false);

			Column<TestResult, SafeHtml> url = new Column<TestResult, SafeHtml>(new SafeHtmlCell()) {

				@Override
				public SafeHtml getValue(TestResult result) {
					String s = "<a href=\""+result.getUrl()+"\">"+result.getUrl()+"</a>";
					return SafeHtmlUtils.fromTrustedString(s);
				}

			};


			TextColumn<TestResult> status = new TextColumn<TestResult>() {

				@Override
				public String getValue(TestResult result) {
					return result.getStatus();
				}

			};
			table.setColumnWidth(url, 65, Unit.PCT);
			table.setColumnWidth(status, 30, Unit.PX);
			table.addColumn(status, "Status");
			table.addColumn(url, "URL");

			table.setRowCount(results.size(), true);

			table.setRowData(0, results);

			tablePanel.add(table);
		}
		initWidget(tablePanel);
	}
}
