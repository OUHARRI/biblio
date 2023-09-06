package com.biblio.app.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.biblio.libs.Model;
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Category extends Model {

	public Category() {
		super("categories", new String[]{"id"});
	}

	private int id;
	private Book[] book = null;
	private String category;
	private String description;
	private java.sql.Timestamp delete_at;

}