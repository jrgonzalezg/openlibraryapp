/*
 * Copyright (C) 2017 Juan Ramón González González (https://github.com/jrgonzalezg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jrgonzalezg.openlibrary.features.books.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.jrgonzalezg.openlibrary.R
import com.github.jrgonzalezg.openlibrary.app.BaseActivity
import com.github.jrgonzalezg.openlibrary.app.MyApplication
import com.github.jrgonzalezg.openlibrary.features.books.BookActivityModule
import com.github.jrgonzalezg.openlibrary.features.books.BookActivitySubcomponent
import com.github.jrgonzalezg.openlibrary.features.books.domain.Book
import com.github.jrgonzalezg.openlibrary.features.books.domain.BookError
import com.github.jrgonzalezg.openlibrary.features.books.presenter.BookPresenter
import com.github.jrgonzalezg.openlibrary.features.books.presenter.BookView
import kotlinx.android.synthetic.main.book_activity.coordinatorLayout
import kotlinx.android.synthetic.main.book_activity.toolbar
import kotlinx.android.synthetic.main.book_details.bookCoverImageView
import kotlinx.android.synthetic.main.book_details.bookDescriptionTextView
import kotlinx.android.synthetic.main.book_details.bookNumberOfPagesTextView
import kotlinx.android.synthetic.main.book_details.bookPhysicalFormatTextView
import kotlinx.android.synthetic.main.book_details.bookTitleTextView
import javax.inject.Inject

class BookActivity : BaseActivity(), BookView {
  private var bookKey: String? = null

  @Inject
  lateinit var bookPresenter: BookPresenter

  override fun initializeDependencyInjection() {
    val bookActivitySubcomponent: BookActivitySubcomponent =
        MyApplication.myApplicationComponent.newBookActivitySubcomponent(BookActivityModule(this))

    bookActivitySubcomponent.inject(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.book_activity)

    setSupportActionBar(toolbar)
    toolbar.title = title

    // Show the Up button in the action bar
    val actionBar = supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(true)

    bookKey = intent.getStringExtra(BOOK_KEY_EXTRA)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    if (id == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this)
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onResumeFragments() {
    super.onResumeFragments()

    this.bookPresenter.bookKey = bookKey
    this.bookPresenter.takeView(this)
  }

  override fun onPause() {
    super.onPause()

    this.bookPresenter.dropView(this)
  }

  fun getFirstCoverUrl(covers: List<Int>?): String? {
    val firstCoverId: Int? = covers?.firstOrNull()

    if (firstCoverId != null) {
      return "http://covers.openlibrary.org/b/id/${firstCoverId}-L.jpg"
    } else {
      return null
    }
  }

  override fun showBook(book: Book) {
    // TODO: Improve layout / views for the book details
    val firstCoverUrl = getFirstCoverUrl(book.covers)
    if (firstCoverUrl != null) {
      Glide.with(this).load(firstCoverUrl).apply(
          RequestOptions().placeholder(R.drawable.ic_book)).into(bookCoverImageView)
    } else {
      bookCoverImageView.setImageResource(R.drawable.ic_book)
    }

    bookTitleTextView.text = book.title
    bookDescriptionTextView.text = book.description
    bookNumberOfPagesTextView.text = book.numberOfPages.toString()
    bookPhysicalFormatTextView.text = book.physicalFormat
  }

  override fun showBookError(bookError: BookError) {
    // TODO: Add proper views for the book error
    Snackbar.make(coordinatorLayout, "Should show book error: $bookError}",
        Snackbar.LENGTH_LONG).show()
  }

  companion object {
    const val BOOK_KEY_EXTRA: String = "book_key_extra"

    fun getCallingIntent(context: Context, bookKey: String): Intent {
      val intent = Intent(context, BookActivity::class.java)
      intent.putExtra(BOOK_KEY_EXTRA, bookKey)
      return intent
    }
  }
}
