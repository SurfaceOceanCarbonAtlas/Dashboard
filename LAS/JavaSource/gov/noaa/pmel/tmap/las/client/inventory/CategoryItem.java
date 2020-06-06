package gov.noaa.pmel.tmap.las.client.inventory;

import org.gwtbootstrap3.client.ui.LinkedGroupItem;

import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;

public class CategoryItem extends LinkedGroupItem {
	CategorySerializable category;
	public CategoryItem() {
		super();
	}

	public CategoryItem(CategorySerializable category) {
		super();
		this.category = category;
        setText(category.getName());
        setTitle(category.getName());
	}

	public CategorySerializable getCategory() {
		return category;
	}

	public void setCategory(CategorySerializable category) {
		this.category = category;
	}
	
}
