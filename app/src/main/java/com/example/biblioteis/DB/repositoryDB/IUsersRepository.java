package com.example.biblioteis.DB.repositoryDB;

import androidx.lifecycle.LiveData;

public interface IUsersRepository<T> {
    LiveData<T> getUser(int i);
}
