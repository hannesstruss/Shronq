package de.hannesstruss.shronq.ui.base

import androidx.lifecycle.ViewModelProvider

interface ViewModelFactory : ViewModelProvider.Factory {
  companion object {
    const val SERVICE_NAME = "ViewModelFactory"
  }
}

