package ru.club.sfera.telegraph

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import im.delight.android.webview.AdvancedWebView
import kotlinx.android.synthetic.main.telegraph_activity.*
import ru.club.sfera.R
import ru.club.sfera.app.App
import java.net.URI

class TelegraphActivity : AppCompatActivity() {

	private val savePost = SavePost()


	private val viewer: TelegraphViewer? by lazy {
		findViewById<TelegraphViewer?>(R.id.viewer)?.apply {
			setListener(this@TelegraphActivity, object : AdvancedWebView.Listener {
				override fun onPageFinished(url: String?) {
					viewerPendingPage?.let { viewer?.showPage(it) }
					viewerPendingPage = null
				}

				override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
				}

				override fun onDownloadRequested(url: String?, suggestedFilename: String?, mimeType: String?, contentLength: Long, contentDisposition: String?, userAgent: String?) {
				}

				override fun onExternalPageRequest(url: String?) {
					AdvancedWebView.Browsers.openUrl(this@TelegraphActivity, url)
				}

				override fun onPageStarted(url: String?, favicon: Bitmap?) {
				}
			})
		}
	}
	private var viewerPendingPage: TelegraphApi.Page? = null
	private val editor: TelegraphEditor? by lazy {
		findViewById<TelegraphEditor?>(R.id.editor)?.apply {
			setListener(this@TelegraphActivity, object : AdvancedWebView.Listener {
				override fun onPageFinished(url: String?) {
					editorPendingPage?.let { editor?.setContent(it.content) }
					editorPendingPage = null
				}

				override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
				}

				override fun onDownloadRequested(url: String?, suggestedFilename: String?, mimeType: String?, contentLength: Long, contentDisposition: String?, userAgent: String?) {
				}

				override fun onExternalPageRequest(url: String?) {
					AdvancedWebView.Browsers.openUrl(this@TelegraphActivity, url)
				}

				override fun onPageStarted(url: String?, favicon: Bitmap?) {
				}
			})
		}
	}
	private var editorPendingPage: TelegraphApi.Page? = null

	private var currentPage: TelegraphApi.Page? = null
	private var editorMode = true
	private var canEdit = false
	private var isEdit = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.telegraph_activity)
		setSupportActionBar(toolbarTelegraph)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)
		supportActionBar?.title = getString(R.string.telegraph_name)



		if (accessToken.isBlank()) TelegraphApi.createAccount(shortName = "teleposter") { success, account, error ->
            if (success && account != null && account.accessToken != null) {
                accessToken = account.accessToken
            } else {
                showError(error)
            }
        }
		if (intent.action == Intent.ACTION_VIEW) {
			val uri = URI.create(intent.dataString)
			when (uri.host) {
				"telegra.ph", "graph.org" -> loadPage(uri.path)
				"edit.telegra.ph", "edit.graph.org" -> login(uri.toString())
			}
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		if (intent?.action == Intent.ACTION_VIEW) {
			val uri = URI.create(intent.dataString)
			when (uri.host) {
				"telegra.ph", "graph.org" -> loadPage(uri.path)
				"edit.telegra.ph", "edit.graph.org" -> login(uri.toString())
			}
		}
	}

	private fun loadEditor(path: String? = null) {
		editorMode = true
		canEdit = false
		isEdit = false
		invalidateOptionsMenu()
		editor?.visibility = View.VISIBLE
		viewer?.visibility = View.GONE
		currentPage = null
		// Load
		if (path != null) TelegraphApi.getPage(accessToken, path, true) { success, page, error ->
            if (success && page != null) {
                isEdit = true
                currentPage = page
                editorPendingPage = page
                editor?.prepare()
            } else {
                showError(error)
            }
        } else {
			editor?.prepare()
		}
	}

	private fun login(authUrl: String) {
        TelegraphApi.login(authUrl) { success, accessToken, account ->
            if (success && !accessToken.isNullOrEmpty()) {
                this.accessToken = accessToken
                this.authorName = account?.authorName
                showMessage(getString(R.string.telegraph_success), getString(R.string.telegraph_login_success))
            } else showError(getString(R.string.telegraph_login_failed))
        }
	}

	private fun loadPage(path: String) {
		editorMode = false
		canEdit = false
		invalidateOptionsMenu()
		viewer?.visibility = View.VISIBLE
		editor?.visibility = View.GONE
		currentPage = null
		// Load
        TelegraphApi.getPage(accessToken, path, true) { success, page, error ->
            if (success && page != null) {
                canEdit = page.canEdit ?: false
                invalidateOptionsMenu()
                currentPage = page
                viewerPendingPage = page
                viewer?.prepare()
            } else showError(error)
        }
	}

	private fun showError(message: String? = null) = showMessage(getString(R.string.telegraph_error), message
			?: getString(R.string.telegraph_error_desc))

	private fun showMessage(title: String? = null, message: String? = null) {
		MaterialDialog(this@TelegraphActivity)
				.title(text = title ?: "")
				.message(text = message ?: "")
				.positiveButton(android.R.string.ok)
				.show()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		editor?.onActivityResult(requestCode, resultCode, data)
	}

	override fun onBackPressed() {
		if (viewer?.onBackPressed() == false) return
		else super.onBackPressed()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		super.onCreateOptionsMenu(menu)
		menuInflater.inflate(R.menu.telegraph_menu, menu)
		return true
	}

	override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
		super.onPrepareOptionsMenu(menu)
		menu?.findItem(R.id.create)?.isVisible = !editorMode
		menu?.findItem(R.id.share)?.isVisible = !editorMode
		menu?.findItem(R.id.try_edit)?.isVisible = !editorMode && !canEdit
		menu?.findItem(R.id.image)?.isVisible = editorMode
		menu?.findItem(R.id.publish)?.isVisible = editorMode
		menu?.findItem(R.id.edit)?.isVisible = canEdit
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			android.R.id.home -> {
				finish()
				return true
			}
			R.id.create -> {
				loadEditor()
				true
			}
			R.id.publish -> {
				editor?.getText { json ->
					MaterialDialog(this@TelegraphActivity)
							.title(R.string.telegraph_title_question)
							.input(hintRes = R.string.telegraph_title_hint, prefill = currentPage?.title
									?: "", allowEmpty = false) { _, title ->

								val name = App.getInstance().userName
								if (!isEdit) authorName = name.toString()
								if (isEdit) TelegraphApi.editPage(accessToken, currentPage?.path
									?: "", authorName = name.toString(), title = title.toString(), content = json
									?: "", returnContent = true) { success, page, error ->
									if (success && page != null) loadPage(page.path)
									else showError(error)
								} else TelegraphApi.createPage(accessToken, content = json
									?: "", title = title.toString(), authorName = name.toString(), returnContent = true) { success, page, error ->
									if (success && page != null) loadPage(page.path)
									else showError(error)
								}
								/*MaterialDialog(this@TelegraphActivity)
										.title(R.string.telegraph_name_question)
										.input(hintRes = R.string.telegraph_name_hint, prefill = if (isEdit) currentPage?.authorName
												?: authorName ?: "" else authorName
												?: "", allowEmpty = true) { _, name ->
											if (!isEdit) authorName = name.toString()
											if (isEdit) TelegraphApi.editPage(accessToken, currentPage?.path
                                                    ?: "", authorName = name.toString(), title = title.toString(), content = json
                                                    ?: "", returnContent = true) { success, page, error ->
                                                if (success && page != null) loadPage(page.path)
                                                else showError(error)
                                            } else TelegraphApi.createPage(accessToken, content = json
                                                    ?: "", title = title.toString(), authorName = name.toString(), returnContent = true) { success, page, error ->
                                                if (success && page != null) loadPage(page.path)
                                                else showError(error)
                                            }
										}
										.show()*/
							}
							.show()
				}
				true
			}
			R.id.edit, R.id.try_edit -> {
				loadEditor(currentPage?.path)
				true
			}
			R.id.bookmarks -> {
				MaterialDialog(this@TelegraphActivity)
						.title(R.string.telegraph_bookmarks)
						.positiveButton(R.string.telegraph_open)
						.negativeButton(android.R.string.cancel)
						.listItemsSingleChoice(items = bookmarks().reversed().map { it.second }) { _, index, _ ->
							loadPage(bookmarks().reversed().map { it.first }[index])
						}
						.show()
				true
			}
			R.id.delete_bookmark -> {
				MaterialDialog(this@TelegraphActivity)
						.title(R.string.telegraph_delete_bookmark)
						.positiveButton(R.string.telegraph_delete)
						.negativeButton(android.R.string.cancel)
						.listItemsMultiChoice(items = bookmarks().reversed().map { it.second }) { _, indices, _ ->
							MaterialDialog(this@TelegraphActivity)
									.title(R.string.telegraph_delete)
									.message(R.string.telegraph_delete_question)
									.positiveButton(android.R.string.yes)
									.negativeButton(android.R.string.no)
									.positiveButton {
										val tmpBookmarks = bookmarks().reversed().map { it.first }
										for (index in indices) deleteBookmark(tmpBookmarks[index])
									}
									.show()
						}
						.show()
				true
			}
			R.id.published -> {
                TelegraphApi.getPageList(accessToken) { success, pageList, error ->
                    if (success && pageList != null && pageList.pages != null) {
                        MaterialDialog(this@TelegraphActivity)
                                .title(R.string.telegraph_published)
                                .positiveButton(R.string.telegraph_open)
                                .negativeButton(android.R.string.cancel)
                                .listItemsSingleChoice(items = pageList.pages.map { it.title }) { _, i, _ ->
                                    loadPage(pageList.pages[i].path)
                                }
                                .show()
                    } else showError(error)
                }
				true
			}
			R.id.bookmark -> {
				MaterialDialog(this@TelegraphActivity)
						.title(R.string.telegraph_title_question)
						.input(hintRes = R.string.telegraph_title_hint, prefill = currentPage?.title
								?: "", allowEmpty = false) { _, input ->
							val curPage = currentPage
							if (curPage?.url != null) addBookmark(curPage.url.split("/").last(), input.toString())
						}
						.show()
				true
			}
			R.id.share -> {
				//val shareIntent = Intent()
				//shareIntent.action = Intent.ACTION_SEND
				//shareIntent.type = "text/plain"
				//shareIntent.putExtra(Intent.EXTRA_TITLE, currentPage?.title)
				//shareIntent.putExtra(Intent.EXTRA_TEXT, currentPage?.url)
				//startActivity(Intent.createChooser(shareIntent, getString(R.string.telegraph_share)))
				if(currentPage?.content != null) savePost.newPost(currentPage!!.content!!, currentPage!!.imageUrl!!)
				true
			}
			R.id.about -> {
				val aboutIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jlelse/teleposter"))
				startActivity(aboutIntent)
				true
			}
			R.id.login -> {
				MaterialDialog(this@TelegraphActivity)
						.title(R.string.telegraph_login)
						.message(R.string.telegraph_login_desc)
						.positiveButton(android.R.string.ok)
						.positiveButton {
							startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/telegraph")))
						}
						.show()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}
