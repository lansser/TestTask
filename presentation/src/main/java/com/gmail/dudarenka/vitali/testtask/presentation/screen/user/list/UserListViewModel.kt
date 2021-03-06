package com.gmail.dudarenka.vitali.testtask.presentation.screen.user.list

import android.databinding.ObservableBoolean
import com.gmail.dudarenka.vitali.domain.entity.User
import com.gmail.dudarenka.vitali.domain.usecases.GetUsersUseCase
import com.gmail.dudarenka.vitali.testtask.presentation.app.App
import com.gmail.dudarenka.vitali.testtask.presentation.base.BaseViewModel
import com.gmail.dudarenka.vitali.testtask.presentation.screen.user.UserRouter
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class UserListViewModel : BaseViewModel<UserRouter>() {
    var adapter: UserListAdapter? = UserListAdapter()
    val isProgressEnabled = ObservableBoolean(false)
    @Inject
    lateinit var getUsersUseCase: GetUsersUseCase

    init {
        loadItems()
        adapter?.onItemClickListener = object : UserListAdapter.OnItemClickListener {
            override fun onItemClick(user: User) {
                router!!.goToUserDetails(user.login)
            }
        }

    }

    fun loadItems() {
        App.appComponent.inject(this)
        isProgressEnabled.set(true)
        val disposable = getUsersUseCase.get(adapter!!.getLastId()).subscribeBy(
                onNext = {
                    adapter?.listData!!.addAll(it.toMutableList())
                    adapter!!.notifyDataSetChanged()
                    isProgressEnabled.set(false)
                },
                onError = {
                    isProgressEnabled.set(false)
                    router?.showError(it)
                }
        )
        addToDisposable(disposable)
    }

}