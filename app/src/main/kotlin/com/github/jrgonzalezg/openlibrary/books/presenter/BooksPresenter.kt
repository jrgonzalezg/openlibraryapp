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

package com.github.jrgonzalezg.openlibrary.books.presenter

import com.github.jrgonzalezg.openlibrary.books.domain.BookSummary
import com.github.jrgonzalezg.openlibrary.books.usecase.GetBookSummariesUseCase
import com.github.jrgonzalezg.openlibrary.presenter.BasePresenter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class BooksPresenter @Inject constructor(
    val getBookSummariesUseCase: GetBookSummariesUseCase) : BasePresenter<BooksView>() {
  fun loadBookSummaries() {
    launch(job!! + UI) {
      val books: List<BookSummary> = getBookSummariesUseCase.getBookSummaries().await()
      view?.showBookSummaries(books)
    }
  }

  override fun takeView(view: BooksView) {
    super.takeView(view)

    loadBookSummaries()
  }
}

interface BooksView {
  fun showBookSummaries(bookSummaries: List<BookSummary>): Unit
}