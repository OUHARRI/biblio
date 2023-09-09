package com.biblio.dao;

import com.biblio.libs.Model;

public final class BooksAuthorsDao extends Model {


    public BooksAuthorsDao() {
        super("books_authors",new String[]{"book","author"});
        this._softDelete = false;
    }
}
