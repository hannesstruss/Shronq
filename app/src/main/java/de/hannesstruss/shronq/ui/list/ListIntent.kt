package de.hannesstruss.shronq.ui.list

sealed class ListIntent {
  data class DeleteItem(val id: Int) : ListIntent()
}
